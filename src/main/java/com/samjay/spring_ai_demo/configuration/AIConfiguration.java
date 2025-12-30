package com.samjay.spring_ai_demo.configuration;

import com.samjay.spring_ai_demo.advisors.TokenPrintAdvisors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
public class AIConfiguration {

    @Bean(name = "openAiChatClient")
    public ChatClient openAiChatModel(OpenAiChatModel openAiChatModel) {

        return ChatClient.builder(openAiChatModel).build();
    }

    @Bean(name = "ollamaChatClient")
    public ChatClient ollamaChatModel(OllamaChatModel ollamaChatModel) {

        return ChatClient.builder(ollamaChatModel).build();
    }

    /*
    Adding default system prompt and default options to OpenAi Chat Client for all prompts
     */

    @Bean(name = "openAiChatClient")
    public ChatClient settingDefaultSystemPromptAndDefaultConfiguration(OpenAiChatModel openAiChatModel) {

        return ChatClient.builder(openAiChatModel)
                .defaultSystem("You are a helpful assistant.")
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4o-mini")
                        .temperature(0.3)
                        .maxTokens(300)
                        .build())
                .build();
    }

    /*
    Adding default logging advisors to OpenAi Chat Client for all prompts
    This will log the request and response details for each chat interaction.
    This will also add safeguard advisor to filter out inappropriate content.
     */

    @Bean(name = "openAiChatClient")
    public ChatClient addingLoggingAdvisorAndSafeGuardAdvisor(OpenAiChatModel openAiChatModel) {

        return ChatClient
                .builder(openAiChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), new SafeGuardAdvisor(List.of("pedophilia", "pornography", "hate speech")))
                .build();
    }

    /*
    Adding custom advisors to Ollama Chat Client for all prompts, it can be applied to any other Chat Client as well.
    This will log the token usage for each chat interaction.
     */

    @Bean(name = "ollamaChatClient")
    public ChatClient addingCustomAdvisor(OllamaChatModel ollamaChatModel) {

        return ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(new TokenPrintAdvisors())
                .build();
    }


    /*
    Implementing chat memory for models to remember previous interactions.
    This example uses in-memory chat memory by default, but you can implement persistent storage as needed.
     */

    @Bean(name = "openAiChatClient")
    public ChatClient configuringInMemoryChatMemoryAdvisor(OpenAiChatModel openAiChatModel, ChatMemory chatMemory) {

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(messageChatMemoryAdvisor)
                .build();
    }

    /*
    Configuring JDBC Chat Memory Repository for persistent chat memory storage.
    This example uses a JDBC repository to store chat messages in a database.
    Here we set the maximum number of messages to retain in memory to 10.
     */

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @Bean
    public RestClient restClient() {
        return RestClient
                .builder()
                .baseUrl("http://api.weatherapi.com/v1")
                .build();
    }
}
