package com.javas.analysis;

import com.javas.analysis.dto.News;
import com.javas.analysis.repository.NewsRepository;
import java.util.List;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.util.common.model.Pair;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@Slf4j
@DataMongoTest
public class AnalysisTest {

  @Autowired
  NewsRepository newsRepository;

  @Test
  void T1() {
    Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
    KomoranResult result = komoran.analyze("문재인은 대한민국 19대 대통령이다.");
    List<Pair<String, String>> sentences = result.getList();
    for (Pair<String,String> token : sentences) {
      System.out.println(token.getFirst()+" "+token.getSecond());
    }
  }

  @Test
  void T2() {
    News news = new News();
    news = newsRepository.findFirstByUri("https://news.naver.com/main/read.nhn?mode=LS2D&mid=shm&sid1=102&sid2=250&oid=025&aid=0003083100");
    log.info(news.toString());
  }
}