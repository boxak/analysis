package com.javas.analysis.controller;

import com.javas.analysis.dto.News;
import com.javas.analysis.mongo_repository.NewsRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReadController {

    @Autowired
    NewsRepository newsRepository;

    // 해당 미디어의 첫번째 기사를 몽고DB에서 가져오는 메서드
    @GetMapping("/mediaFirstArticle")
    @ResponseBody
    public News mediaFirstArticle(String mediaName) {
        List<News> newsList = newsRepository.findAllByMediaName(mediaName);
        News news = new News();

        if (!CollectionUtils.isEmpty(newsList)) {
            news = newsList.get(0);
        }

        return news;
    }
}
