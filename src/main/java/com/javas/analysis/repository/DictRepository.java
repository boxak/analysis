package com.javas.analysis.repository;

import com.javas.analysis.config.MongoDBConfig;
import com.javas.analysis.dto.Dict;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Import(MongoDBConfig.class)
public interface DictRepository extends MongoRepository<Dict, String> {
  Dict findByWord(String word);
}
