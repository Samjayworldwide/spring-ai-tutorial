package com.samjay.spring_ai_demo.controller;

import com.samjay.spring_ai_demo.helper.Helper;
import com.samjay.spring_ai_demo.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    /*
    This implementation is when you want to use a single AI model in your application.
     */

//    private final ChatClient chatClient;
//
//    public ChatController(ChatClient.Builder chatClientBuilder) {
//        chatClient = chatClientBuilder.build();
//    }
//
//    @GetMapping("/chat")
//    public ResponseEntity<String> sendMessage(String message) {
//
//        String aiResponse = chatClient.prompt(message).call().content();
//
//        return ResponseEntity.ok(aiResponse);
//    }


    /*
    This implementation is when you want to use multiple AI models in your application.

    In this case I am using open AI GPT model and Ollama model.
     */

    private final ChatClient openAiChatClient;

    private final ChatClient ollamaChatClient;

    private final ChatService chatService;

    public ChatController(@Qualifier("openAiChatClient") ChatClient openAiChatClient, @Qualifier("ollamaChatClient") ChatClient ollamaChatClient, ChatService chatService) {

        this.openAiChatClient = openAiChatClient;

        this.ollamaChatClient = ollamaChatClient;

        this.chatService = chatService;
    }

    @GetMapping("/chat/openai")
    public ResponseEntity<String> sendMessageToOpenAi(String message) {

        String aiResponse = openAiChatClient.prompt(message).call().content();

        return ResponseEntity.ok(aiResponse);
    }

    @GetMapping("/chat/ollama")
    public ResponseEntity<String> sendMessageToOllama(String message) {

        String aiResponse = ollamaChatClient.prompt(message).call().content();

        return ResponseEntity.ok(aiResponse);
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> streamMessageFromModel(String message) {

        return ResponseEntity.ok(chatService.streamChatResponseFromAiModel(message));
    }

    @GetMapping(value = "/chat/user")
    public ResponseEntity<String> chatUsingService(@RequestParam String message, @RequestHeader String userId) {

        String aiResponse = chatService.addingMemoryForASpecificChatConversation(message, userId);

        return ResponseEntity.ok(aiResponse);

    }

    @PostMapping("/chat/vector-db/add")
    public ResponseEntity<String> addToVectorDB() {

        chatService.addingDataToVectorDB(Helper.getData());

        return ResponseEntity.ok("Data added to Vector DB successfully.");
    }
}
