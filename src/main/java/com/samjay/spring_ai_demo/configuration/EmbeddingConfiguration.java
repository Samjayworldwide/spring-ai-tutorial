package com.samjay.spring_ai_demo.configuration;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmbeddingConfiguration {

    /*
    This configuration is done because we have two EmbeddingModel beans in the application context
    OpenAIEmbeddingModel and OllamaEmbeddingModel. VectorStore needs a primary EmbeddingModel bean to function properly.
    By marking OpenAiEmbeddingModel as primary, we ensure that it is the default choice for dependency injection
    when an EmbeddingModel is required.
     */

    @Bean
    @Primary
    public EmbeddingModel primaryEmbeddingModel(OpenAiEmbeddingModel openAiEmbeddingModel) {
        return openAiEmbeddingModel;
    }
}

