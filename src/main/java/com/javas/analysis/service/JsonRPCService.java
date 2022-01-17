package com.javas.analysis.service;

import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import org.springframework.stereotype.Service;

@Service
@JsonRpcService("/analyze_jsonrpc")
public interface JsonRPCService {
    String analyze(@JsonRpcParam(value = "mediaName") String mediaName);
}
