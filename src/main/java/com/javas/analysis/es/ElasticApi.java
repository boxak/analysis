package com.javas.analysis.es;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.javas.analysis.config.ElasticsearchClientConfig;
import com.javas.analysis.dto.Result;
import com.javas.analysis.utils.ElasticUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@ComponentScan(basePackageClasses = {ElasticsearchClientConfig.class})
public class ElasticApi {

    @Autowired
    private RestClient client;

    public String extractKeywords(String text) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("text", text);
        jsonObj.addProperty("analyzer", "korean_analyzer");

        String query = jsonObj.toString();
        log.info("jsonStr : {}", query);

        String endPoint = "/nori_analyzer/_analyze";
        String entityString = callElasticApi("POST", endPoint, query);

        return getKeywords(entityString);
    }

    public String insertAnalysisResult(Result result) {
        if (ObjectUtils.isEmpty(result)) return "result object is null...";

        result.setPubDate(ElasticUtil.convertPubDate(result.getPubDate()));
        result.setRegDate(ElasticUtil.convertRegDate(result.getRegDate()));

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("title", result.getTitle());
        jsonObj.addProperty("content", result.getContent());
        jsonObj.addProperty("summary", result.getSummary());
        jsonObj.addProperty("mediaName", result.getMediaName());
        jsonObj.addProperty("sid1", result.getSid1());
        jsonObj.addProperty("sid2", result.getSid2());
        jsonObj.addProperty("class1", result.getClass1());
        jsonObj.addProperty("class2", result.getClass2());
        jsonObj.addProperty("uri", result.getUri());
        jsonObj.addProperty("rootDomain", result.getRootDomain());
        jsonObj.addProperty("pubDate", result.getPubDate());
        jsonObj.addProperty("regDate", result.getRegDate());
        jsonObj.addProperty("keywords", result.getKeywords());

        String query = jsonObj.toString();

        String endPoint = "/analysis_result/_doc";
        String entityString = callElasticApi("POST", endPoint, query);

        return entityString;
    }

    private String getKeywords(String entityString) {
        JsonObject jsonObj = (JsonObject) JsonParser.parseString(entityString);
        JsonArray jsonArray = (JsonArray) jsonObj.get("tokens");
        StringBuilder sb = new StringBuilder();

        for (JsonElement element : jsonArray) {
            JsonObject tempObj = (JsonObject) element;
            sb.append(String.valueOf(tempObj.get("token"))+",");
        }

        String str = sb.toString();

        if (StringUtils.isNotEmpty(str)) {
            str = str.substring(0, str.length()-1);
        }

        return str;
    }

    private String callElasticApi(String method, String endPoint, String query) {
        Request request = new Request(method, endPoint);
        request.setJsonEntity(query);
        Response response = null;
        String entityString = "";

        try {
            response = client.performRequest(request);
            entityString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entityString;
    }
}
