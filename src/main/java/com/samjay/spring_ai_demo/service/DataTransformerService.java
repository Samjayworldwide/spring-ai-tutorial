package com.samjay.spring_ai_demo.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DataTransformerService {

    List<Document> transformDocuments(List<Document> documents);
}
