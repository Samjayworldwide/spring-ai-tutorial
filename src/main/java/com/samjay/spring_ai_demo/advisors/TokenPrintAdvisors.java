package com.samjay.spring_ai_demo.advisors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

@SuppressWarnings("NullableProblems")
@Slf4j
public class TokenPrintAdvisors implements CallAdvisor, StreamAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        log.info("My token print advisor called");

        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

        log.info("Response received in advisor: {}", chatClientResponse);

        assert chatClientResponse.chatResponse() != null;

        log.info("Number of prompt tokens used: {}", chatClientResponse.chatResponse().getMetadata().getUsage().getPromptTokens());

        log.info("Number of completion tokens used: {}", chatClientResponse.chatResponse().getMetadata().getUsage().getCompletionTokens());

        log.info("Number of tokens used: {}", chatClientResponse.chatResponse().getMetadata().getUsage().getTotalTokens());

        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {

        return streamAdvisorChain.nextStream(chatClientRequest);
    }

    @Override
    public String getName() {

        return this.getClass().getName();
    }

    @Override
    public int getOrder() {

        return 0;
    }
}
