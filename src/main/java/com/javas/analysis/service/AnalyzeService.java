package com.javas.analysis.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.javas.analysis.dto.News;
import com.javas.analysis.dto.Result;
import com.javas.analysis.repository.NewsRepository;
import com.javas.analysis.repository.ResultRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnalyzeService {

  @Autowired
  NewsRepository newsRepository;

  @Autowired
  ResultRepository resultRepository;

  public String analyze() {
    List<News> newsList = newsRepository.findAllByReadCheck(0);
    List<Result> resultList = new ArrayList<>();

    for (News news : newsList) {
      String keywords = extractKeywords(news);
      Result result = appendNewsAndKeywords(news, keywords);
    }

    return "analyze done...";
  }

  private String extractKeywords(News news) {
    return "";
  }

  private Result appendNewsAndKeywords(News news, String keywords) {
    Gson gson = new Gson();
    String jsonStr = gson.toJson(news);
    JsonObject jsonObject = (JsonObject)JsonParser.parseString(jsonStr);
    jsonObject.addProperty("keywords", keywords);

    String resultStr = jsonObject.getAsString();

    log.info(resultStr);
    Result result = gson.fromJson(resultStr, Result.class);

    return result;
  }
}
