package com.javas.analysis;

import com.google.gson.*;
import com.javas.analysis.dto.Dict;
import com.javas.analysis.dto.News;
import com.javas.analysis.dto.Result;
import com.javas.analysis.repository.DictRepository;
import com.javas.analysis.repository.NewsRepository;
import com.javas.analysis.repository.ResultRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

@Slf4j
@DataMongoTest
public class AnalyzeServiceTest {

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    ResultRepository resultRepository;

    @Autowired
    DictRepository dictRepository;

    @Test
    void T1() throws Exception {
        List<News> newsList = newsRepository.findAllByReadCheck(0);

        News news = newsList.get(0);
        String keywords = "";

        keywords = extractKeywords(news);
        news.setReadCheck(1);
        Result result = appendNewsAndKeywords(news, keywords);
        resultRepository.save(result);
        newsRepository.save(news);

        String[] splitedKeywords = keywords.split(",");
        Map<String, Integer> wordMap = new HashMap<>();
        List<Dict> dictList = new ArrayList<>();

        for (String keyword : splitedKeywords) {
            keyword = keyword.trim();
            if (wordMap.containsKey(keyword)) {
                int count = wordMap.get(keyword);
                wordMap.put(keyword, count+1);
            } else {
                wordMap.put(keyword,1);
            }
        }

        for (String keyword : wordMap.keySet()) {
            keyword = keyword.trim();
            int countFromMap = wordMap.get(keyword);
            Dict dict = dictRepository.findByWord(keyword);
            if (ObjectUtils.isNotEmpty(dict)) {
                int countFromMongo = dict.getCount();
                dict.setCount(countFromMongo + countFromMap);
            } else {
                dict = new Dict();
                dict.setWord(keyword);
                dict.setCount(countFromMap);
            }
            dictList.add(dict);
        }
        dictRepository.saveAll(dictList);
    }

    private String extractKeywords(News news) throws Exception {
        String title = news.getTitle();
        String summary = news.getSummary();
        String content = news.getContent();

        title = nvl(title);
        summary = nvl(summary);
        content = nvl(content);

        String text = title+"\n"+summary+"\n"+content;

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("text", text);
        jsonObj.addProperty("analyzer", "korean_analyzer");

        String query = jsonObj.toString();
        log.info("jsonStr : {}",query);

        String endPoint = "/nori_analyzer/_analyze";
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

        String resultStr = jsonObject.toString();

        log.info("resultStr : {}",resultStr);
        Result result = gson.fromJson(resultStr, Result.class);

        return result;
    }

    private String getKeywords(String entityString) {
        JsonObject jsonObj = (JsonObject) JsonParser.parseString(entityString);
        JsonArray jsonArray = (JsonArray) jsonObj.get("tokens");
        StringBuilder sb = new StringBuilder();

        for (JsonElement element : jsonArray) {
            JsonObject tempObj = (JsonObject) element;
            sb.append(String.valueOf(tempObj.get("token")).replaceAll("\"","")+",");
        }

        String str = sb.toString();

        if (StringUtils.isNotEmpty(str)) {
            str = str.substring(0, str.length()-1);
        }

        return str;
    }

    private RestClient restClientBuild() throws Exception {
        RestClientBuilder builder = null;
        try {
            String server = "127.0.0.1";
            HttpHost host = new HttpHost(server,9200,"http");
            builder = RestClient.builder(host);
            builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
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

    private String nvl(String str) {
        return StringUtils.isEmpty(str) ? "" : str;
    }
}
