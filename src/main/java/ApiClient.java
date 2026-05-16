package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String authToken;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // ── POST without token ────────────────────────────
    public String post(String endpoint, Object body) throws Exception {
        String json = objectMapper.writeValueAsString(body);
        System.out.println("→ POST " + BASE_URL + endpoint);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("← Status: " + response.statusCode());
        return response.body();
    }

    // ── POST with token ───────────────────────────────
    public String postWithAuth(String endpoint, Object body) throws Exception {
        String json = objectMapper.writeValueAsString(body);
        System.out.println("→ POST " + BASE_URL + endpoint);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("← Status: " + response.statusCode());
        return response.body();
    }

    // ── GET with token ────────────────────────────────
    public String getWithAuth(String endpoint) throws Exception {
        System.out.println("→ GET " + BASE_URL + endpoint);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("← Status: " + response.statusCode());
        return response.body();
    }

    // ── PUT with token ────────────────────────────────
    public String putWithAuth(String endpoint) throws Exception {
        System.out.println("→ PUT " + BASE_URL + endpoint);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("← Status: " + response.statusCode());
        return response.body();
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String prettyPrint(String json) {
        try {
            Object obj = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj);
        } catch (Exception e) {
            return json;
        }
    }
}