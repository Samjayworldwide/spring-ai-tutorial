package com.samjay.spring_ai_demo.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataTransformServiceImplementation implements DataTransformerService {

    @Override
    public List<Document> transformDocuments(List<Document> documents) {

        var tokenTextSplitter = new TokenTextSplitter();

        return tokenTextSplitter.transform(documents);
    }
}
