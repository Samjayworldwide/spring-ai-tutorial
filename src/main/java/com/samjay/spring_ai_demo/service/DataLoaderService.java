package com.samjay.spring_ai_demo.service;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DataLoaderService {

    List<Document> loadDocumentsFromJsonFile(Resource resource);

    List<Document> loadDocumentsFromPDFByPage(Resource resource);

    List<Document> loadDocumentsFromPdfByParagraph(Resource resource);
}
