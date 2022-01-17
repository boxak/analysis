package com.javas.analysis.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.javas.analysis.dto.News;
import com.javas.analysis.dto.Result;
import com.javas.analysis.es.ElasticApi;
import com.javas.analysis.mongo_repository.NewsRepository;
import com.javas.analysis.mongo_repository.ResultRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import org.elasticsearch.client.Request;

@Service
@Slf4j
@ComponentScan(basePackageClasses = {ElasticApi.class})
public class AnalyzeService {

  @Autowired
  NewsRepository newsRepository;

  @Autowired
  ResultRepository resultRepository;

  @Autowired
  ElasticApi elasticApi;

  public String analyze() {
    List<News> newsList = newsRepository.findAllByReadCheck(0);
    List<Result> resultList = new ArrayList<>();
    List<News> tempNewsList = new ArrayList<>();

    for (News news : newsList) {

      String keywords = "";
      try {
        keywords = extractKeywords(news);
      } catch (Exception e) {
        e.printStackTrace();
      }
      news.setReadCheck(1);
      Result result = appendNewsAndKeywords(news, keywords);
      resultList.add(result);
      elasticApi.insertAnalysisResult(result);
      tempNewsList.add(news);
    }

    resultRepository.insert(resultList);
    newsRepository.insert(tempNewsList);

    return "analyze done...";
  }

  private String extractKeywords(News news) throws Exception {

    String title = news.getTitle();
    String summary = StringUtils.isEmpty(news.getSummary()) ? "" : news.getSummary();
    String content = news.getContent();

    String text = title+"\n"+summary+"\n"+content;

    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("text", text);
    jsonObj.addProperty("analyzer", "nori_token_analyzer");

    String query = jsonObj.toString();
    log.info("jsonStr : {}",query);

    String endPoint = "/korean_analyzer/_analyze";
    Request request = new Request("POST", endPoint);
    request.setJsonEntity(query);
    RestClient client = restClientBuild();

    Response response = client.performRequest(request);
    String entityString = EntityUtils.toString(response.getEntity());

    return getKeywords(entityString);
  }

  private Result appendNewsAndKeywords(News news, String keywords) {
    Gson gson = new Gson();
    String jsonStr = gson.toJson(news);
    JsonObject jsonObject = (JsonObject)JsonParser.parseString(jsonStr);
    jsonObject.addProperty("keywords", keywords);

    String resultStr = jsonObject.getAsString();

    log.info("resultStr : {}",resultStr);
    Result result = gson.fromJson(resultStr, Result.class);

    return result;
  }

  private RestClient restClientBuild() throws Exception {
    RestClientBuilder builder = null;
    try {
      String server = "127.0.0.1";
      HttpHost host = new HttpHost(server,9200,"http");
      builder = RestClient.builder(host);
      builder.setRequestConfigCallback(new RequestConfigCallback() {
        @Override
        public Builder customizeRequestConfig(Builder builder) {
          return builder.setConnectTimeout(5000)
              .setSocketTimeout(5000)
              .setConnectionRequestTimeout(5000);
        }
      });
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return builder.build();
  }

  private String getKeywords(String entityString) {
    JsonObject jsonObj = (JsonObject) JsonParser.parseString(entityString);
    JsonArray jsonArray = (JsonArray) jsonObj.get("tokens");
    StringBuilder sb = new StringBuilder();

    for (JsonElement element : jsonArray) {
      JsonObject tempObj = (JsonObject) element;
      sb.append(String.valueOf(tempObj.get("token"))+",");
    }

    String str = sb.toString();

    if (StringUtils.isNotEmpty(str)) {
      str = str.substring(0, str.length()-1);
    }

    return str;
  }
}
