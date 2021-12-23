package com.javas.analysis.mongo_repository;

import com.javas.analysis.config.MongoDBConfig;
import com.javas.analysis.dto.News;
import java.util.List;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Import(MongoDBConfig.class)
public interface NewsRepository extends MongoRepository<News, String> {
  News findFirstByUri(String uri);
  List<News> findAllByReadCheck(int readCheck);
}
