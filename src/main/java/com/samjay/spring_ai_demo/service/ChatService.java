package com.samjay.spring_ai_demo.service;

import com.samjay.spring_ai_demo.entity.Job;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    String chat(String message);

    String chatWithPromptClass(String message);

    String getChatResponse(String message);

    void viewMetaData(String message);

    Job convertAiResponseToJob(String message);

    List<Job> convertAiResponseToJobList(String message);

    String addingSpecificConfigurationsForAPrompt(String message);

    String addingPromptTemplating(String name);

    String moreAboutPromptTemplating(String name, String role);

    String configuringBothSystemAndUserPromptUsingPromptTemplating(String name, String role);

    String readingFromFileAndUsingItAsPrompt(String name, String role);

    Flux<String> streamChatResponseFromAiModel(String message);

    String addingMemoryForASpecificChatConversation(String message, String userId);

    void addingDataToVectorDB(List<String> dataList);

    String chatWithDataFromVectorDB(String message);

    String chatWithDataFromVectorDBWithQuestionAnswerAdvisor(String message);

    String chatWithDataFromVectorDBWithRetrievalAugmentationAdvisor(String message);

    String usingAdvancedRagFeaturesToChatWithLLM(String message);

    void howToLoadPdfDocumentIntoAVectorDB();

    void howToLoadJsonDocumentIntoAVectorDB();

    String howToConfigureLLmForToolCalling(String message);

    String howToCallWeatherApiTool(String message);
}
