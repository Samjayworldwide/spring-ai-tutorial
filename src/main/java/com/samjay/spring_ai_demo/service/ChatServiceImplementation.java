package com.samjay.spring_ai_demo.service;

import com.samjay.spring_ai_demo.entity.Job;
import com.samjay.spring_ai_demo.toolcalling.SimpleDateTimeTool;
import com.samjay.spring_ai_demo.toolcalling.WeatherApiTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ChatServiceImplementation implements ChatService {

    private final ChatClient openAiChatClient;

    @Value("classpath:/prompts/userMessage.st")
    private Resource userMessage;

    @Value("classpath:/prompts/systemMessage.st")
    private Resource systemMessage;

    @Value("classpath:sample_data.json")
    private Resource jsonFileResource;

    @Value("classpath:Samuel Nwachukwu Mbanisi CV.pdf")
    private Resource pdfFileResource;

    private final VectorStore vectorStore;

    private final DataLoaderService dataLoaderService;

    private final DataTransformerService dataTransformerService;

    private final WeatherApiTool weatherApiTool;

    private final Logger logger = LoggerFactory.getLogger(ChatServiceImplementation.class);

    public ChatServiceImplementation(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                                     VectorStore vectorStore, DataLoaderService dataLoaderService,
                                     DataTransformerService dataTransformerService,
                                     WeatherApiTool weatherApiTool) {

        this.weatherApiTool = weatherApiTool;

        this.dataLoaderService = dataLoaderService;

        this.dataTransformerService = dataTransformerService;

        this.vectorStore = vectorStore;

        this.openAiChatClient = openAiChatClient;
    }


    /*
    To add a system prompt along with user prompt
     */

    @Override
    public String chat(String message) {

        return openAiChatClient.prompt()
                .user(message)
                .system("You are a helpful assistant.")
                .call()
                .content();
    }

    /*
    To construct prompt using Prompt class
     */

    @Override
    public String chatWithPromptClass(String message) {

        Prompt prompt = new Prompt(message);

        return openAiChatClient.prompt(prompt)
                .call()
                .content();
    }

    /*
    To get detailed chat response
     */

    public String getChatResponse(String message) {

        return Objects.requireNonNull(openAiChatClient.prompt(message)
                        .call()
                        .chatResponse())
                .getResult()
                .getOutput()
                .getText();
    }

    /*
    To view metadata of the response
     */
    @Override
    public void viewMetaData(String message) {

        var metadata = Objects.requireNonNull(openAiChatClient.prompt(message)
                        .call()
                        .chatResponse())
                .getMetadata();

        logger.info("Model Used: {}", metadata.getModel());

        logger.info("Response ID: {}", metadata.getId());

        logger.info("Created At: {}", metadata.getPromptMetadata());

    }

    /*
    How to convert AI response to a Java Object
     */

    @Override
    public Job convertAiResponseToJob(String message) {

        return openAiChatClient.prompt(message)
                .call()
                .entity(Job.class);
    }

    /*
    How to convert AI response to a List of Java Objects
     */

    @Override
    public List<Job> convertAiResponseToJobList(String message) {

        return openAiChatClient.prompt(message)
                .call()
                .entity(new ParameterizedTypeReference<>() {
                });
    }

    /*
    Adding specific configurations for a prompt
     */

    @Override
    public String addingSpecificConfigurationsForAPrompt(String message) {

        var prompt = new Prompt(message, OpenAiChatOptions
                .builder()
                .model("gpt-4")
                .temperature(0.7)
                .maxTokens(150)
                .build());

        return openAiChatClient.prompt(prompt)
                .call()
                .content();
    }

    /*
    Adding prompt templating, when you have only user prompt with dynamic parameters
     */

    @Override
    public String addingPromptTemplating(String name) {

        String message = "Create a welcome message for {name} who just registered on our platform.";

        return openAiChatClient.prompt()
                .user(u -> u.text(message).param("name", name))
                .call()
                .content();
    }

    /*
    More about prompt templating with PromptTemplate class with dynamic parameters
     */

    @Override
    public String moreAboutPromptTemplating(String name, String role) {

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template("Create a professional bio for {name} who works as a {role}.")
                .build();

        String message = promptTemplate.render(
                Map.of(
                        "name", name,
                        "role", role
                )
        );

        Prompt prompt = new Prompt(message);

        return openAiChatClient.prompt(prompt)
                .call()
                .content();
    }

    /*
    Configuring both system and user prompt using prompt templating with dynamic parameters
     */

    @Override
    public String configuringBothSystemAndUserPromptUsingPromptTemplating(String name, String role) {

        var systemPromptTemplate = SystemPromptTemplate.builder().template("You are an expert career counselor.").build();

        var systemPrompt = systemPromptTemplate.createMessage();

        var userPromptTemplate = PromptTemplate.builder()
                .template("Provide career advice for {name} who is working as a {role}.")
                .build();

        var userPrompt = userPromptTemplate.createMessage(
                Map.of(
                        "name", name,
                        "role", role
                )
        );

        var prompt = new Prompt(systemPrompt, userPrompt);

        return openAiChatClient.prompt(prompt)
                .call()
                .content();
    }

    /*
    Reading from file and using it as prompt with dynamic parameters
     */

    @Override
    public String readingFromFileAndUsingItAsPrompt(String name, String role) {

        return openAiChatClient.prompt()
                .system(system -> system.text(systemMessage))
                .user(user -> user.text(userMessage).param("name", name).param("role", role))
                .call()
                .content();
    }

    /*
    Streaming chat response from AI model to make it non-blocking
     */

    @Override
    public Flux<String> streamChatResponseFromAiModel(String message) {

        return openAiChatClient.prompt()
                .system(system -> system.text(systemMessage))
                .user(message)
                .stream()
                .content();
    }

    /*
    Adding chat memory for a specific chat conversation to avoid losing context
     */

    @Override
    public String addingMemoryForASpecificChatConversation(String message, String userId) {

        return openAiChatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, userId))
                .user(message)
                .call()
                .content();
    }

    /*
    Adding data to vector database
     */

    @Override
    public void addingDataToVectorDB(List<String> dataList) {

        List<Document> document = dataList.stream().map(Document::new).toList();

        vectorStore.add(document);
    }

    /*
    Chat with llm using data from vector database manually
     */

    @Override
    public String chatWithDataFromVectorDB(String message) {


        SearchRequest searchRequest = SearchRequest
                .builder()
                .topK(3)
                .similarityThreshold(0.7)
                .query(message)
                .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest);

        List<String> contextList = documents.stream().map(Document::getText).toList();

        String context = String.join("\n", contextList);

        return openAiChatClient.prompt()
                .system(system -> system.text(systemMessage).param("documents", context))
                .user(message)
                .call()
                .content();
    }

    /*
    Chat with llm using data from vector database with Question Answer Advisor
     */

    @Override
    public String chatWithDataFromVectorDBWithQuestionAnswerAdvisor(String message) {

        var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().similarityThreshold(0.75).topK(3).build())
                .build();

        return openAiChatClient.prompt()
                .advisors(qaAdvisor)
                .user(message)
                .call()
                .content();
    }

    /*
    Chat with llm using data from vector database with Retrieval Augmentation Advisor
     */

    @Override
    public String chatWithDataFromVectorDBWithRetrievalAugmentationAdvisor(String message) {

        var advisor = RetrievalAugmentationAdvisor
                .builder()
                .documentRetriever(VectorStoreDocumentRetriever
                        .builder()
                        .vectorStore(vectorStore)
                        .topK(3)
                        .similarityThreshold(0.75)
                        .build()
                )
                .queryAugmenter(ContextualQueryAugmenter.builder().allowEmptyContext(true).build())
                .build();

        return openAiChatClient.prompt()
                .advisors(advisor)
                .user(message)
                .call()
                .content();
    }

    /*
    Using advanced RAG features to chat with LLM, including query rewriting, translation, multi-query expansion,
    document retrieval, and contextual query augmentation
     */

    @Override
    public String usingAdvancedRagFeaturesToChatWithLLM(String message) {

        /*
        This rewrites the user query to make it more effective for retrieval.
         */
        var rewriteQueryTransformer = RewriteQueryTransformer
                .builder()
                .chatClientBuilder(openAiChatClient.mutate().clone())
                .build();

        /*
        This translates the user query to the target language for better retrieval.
         */

        var translationQueryTransformer = TranslationQueryTransformer
                .builder()
                .chatClientBuilder(openAiChatClient.mutate().clone())
                .targetLanguage("English")
                .build();

        /*
        This expands the user query into multiple queries to improve retrieval.
        Generates 3 different variations of the query.
         */

        var multiQueryExpander = MultiQueryExpander
                .builder()
                .chatClientBuilder(openAiChatClient.mutate().clone())
                .numberOfQueries(3)
                .build();

        /*
        This retrieves documents from the vector store based on the transformed and expanded queries.
         */

        var vectorStoreDocumentRetriever = VectorStoreDocumentRetriever
                .builder()
                .vectorStore(vectorStore)
                .topK(5)
                .similarityThreshold(0.8)
                .build();

        /*
        This augments the user query with contextual information from the retrieved documents.
        Adds the context to the user query even if no documents are found.
         */

        var contextualQueryAugmenter = ContextualQueryAugmenter
                .builder()
                .allowEmptyContext(true)
                .build();

        var retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor
                .builder()
                .queryTransformers(rewriteQueryTransformer, translationQueryTransformer)
                .queryExpander(multiQueryExpander)
                .documentRetriever(vectorStoreDocumentRetriever)
                .documentJoiner(new ConcatenationDocumentJoiner())
                .queryAugmenter(contextualQueryAugmenter)
                .build();

        return openAiChatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(message)
                .call()
                .content();
    }

    /*
    How to load PDF document into a vector database
     */

    @Override
    public void howToLoadPdfDocumentIntoAVectorDB() {

        //First load the PDF document by either paragraph or by page

        List<Document> pdfDocuments = dataLoaderService.loadDocumentsFromPdfByParagraph(pdfFileResource);

        //List<Document> pdfDocumentsByPage = dataLoaderService.loadDocumentsFromPDFByPage(pdfFileResource);

        //Then transform the loaded documents

        List<Document> transformedDocuments = dataTransformerService.transformDocuments(pdfDocuments);

        //Finally add the transformed documents to the vector store

        vectorStore.add(transformedDocuments);
    }

    /*
    How to load JSON document into a vector database
     */

    @Override
    public void howToLoadJsonDocumentIntoAVectorDB() {

        //First load the JSON document

        List<Document> jsonDocuments = dataLoaderService.loadDocumentsFromJsonFile(jsonFileResource);

        //Then transform the loaded documents

        List<Document> transformedDocuments = dataTransformerService.transformDocuments(jsonDocuments);

        //Finally add the transformed documents to the vector store

        vectorStore.add(transformedDocuments);
    }

    /*
    How to configure LLM for tool calling, this example gets the current date and time
     */

    @Override
    public String howToConfigureLLmForToolCalling(String message) {

        return openAiChatClient.prompt()
                .tools(new SimpleDateTimeTool())
                .user(message)
                .call()
                .content();
    }

    /*
    How to call Weather API tool to get current weather information for a specified city
     */

    @Override
    public String howToCallWeatherApiTool(String message) {

        return openAiChatClient.prompt()
                .tools(weatherApiTool)
                .user(message)
                .call()
                .content();
    }
}
