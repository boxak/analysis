package com.javas.analysis.controller;

import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.javas.analysis.service.AnalyzeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyzeController {

  @Autowired
  AnalyzeService analyzeService;

  @GetMapping("/analyze")
  public String analyze() {
    return analyzeService.analyze();
  }
}
