package com.samjay.spring_ai_demo.toolcalling;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherApiTool {

    @Value("${weather.api.key}")
    private String weatherApiKey;

    private final RestClient restClient;

    @Tool(description = "Get the current weather information for a specified city")
    public String getWeatherForCity(@ToolParam(description = "This is the city we want to get current weather information.") String city) {

        var response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/current.json")
                        .queryParam("q", city)
                        .queryParam("key", weatherApiKey)
                        .build()
                )
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {
                });

        assert response != null;

        return response.toString();
    }
}
