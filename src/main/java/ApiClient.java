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
        String json = objectMapper.writeValueAsString(body);//Json Serialization convert object into json
        System.out.println("→ POST " + BASE_URL + endpoint);

        HttpRequest request = HttpRequest.newBuilder() // Http request builder of data
                .uri(URI.create(BASE_URL + endpoint)) //data url endpoint
                .header("Content-Type", "application/json") // add http header: means the data i am sending is JSOn
                .POST(HttpRequest.BodyPublishers.ofString(json)) // post requst->conerting json string into reuqst body stream
                .build(); // Finalizes and creates immutable HttpRequest object.

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());             //Send this HTTP request to backend and store returned response as String
        System.out.println("← Status: " + response.statusCode());
        return response.body(); //Gets actual response content from server.
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
                .header("Authorization", "Bearer " + authToken) // uses store token / Attach logged-in user's JWT token to request for authentication.
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println("← Status: " + response.statusCode());
        return response.body();
    }

    public void setAuthToken(String token) {
        this.authToken = token; //save token
    }

    public String getAuthToken() {
        return authToken; // retrieve token
    }

    public String prettyPrint(String json) {//make JSON look clean and readable.
        try {
            Object obj = objectMapper.readValue(json, Object.class); // parsing: Read JSON String → Convert Into Java Object
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj);
        } catch (Exception e) {
            return json; //if any error in try block just return my original Json.
        }
    }
}