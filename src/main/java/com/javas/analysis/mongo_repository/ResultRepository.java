package com.javas.analysis.mongo_repository;

import com.javas.analysis.config.MongoDBConfig;
import com.javas.analysis.dto.Result;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Import(MongoDBConfig.class)
public interface ResultRepository extends MongoRepository<Result, String> {
}
