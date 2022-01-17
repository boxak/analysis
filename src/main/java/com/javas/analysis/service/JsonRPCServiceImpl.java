package com.javas.analysis.service;

import com.google.gson.*;
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import com.javas.analysis.dto.News;
import com.javas.analysis.dto.Result;
import com.javas.analysis.es.ElasticApi;
import com.javas.analysis.mongo_repository.NewsRepository;
import com.javas.analysis.mongo_repository.ResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AutoJsonRpcServiceImpl
@ComponentScan(basePackageClasses = {ElasticApi.class})
public class JsonRPCServiceImpl implements JsonRPCService {

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    ResultRepository resultRepository;

    @Autowired
    ElasticApi elasticApi;

    @Override
    public String analyze(String mediaName) {
        List<News> newsList = newsRepository.findAllByMediaNameAndReadCheck(mediaName, 0);
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
        String summary = news.getSummary();
        String content = news.getContent();

        title = nvl(title);
        summary = nvl(summary);
        content = nvl(content);

        String text = title+"\n"+summary+"\n"+content;

        return elasticApi.extractKeywords(text);
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

    private String nvl(String str) {
        return StringUtils.isEmpty(str) ? "" : str;
    }
}
