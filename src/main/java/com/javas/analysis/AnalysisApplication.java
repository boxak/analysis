package com.javas.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.javas.analysis.mongo_repository")
public class AnalysisApplication {

  public static void main(String[] args) {
    SpringApplication.run(AnalysisApplication.class, args);
  }

}
