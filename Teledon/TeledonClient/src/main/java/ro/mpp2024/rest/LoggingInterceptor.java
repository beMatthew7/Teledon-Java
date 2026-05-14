package ro.mpp2024.rest;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        System.out.println("TRIMITERE CERERE REST");
        System.out.println("Metoda: " + request.getMethod());
        System.out.println("URL: " + request.getURI());
        if (body.length > 0) {
            System.out.println("Request Body: " + new String(body, StandardCharsets.UTF_8));
        }

        ClientHttpResponse response = execution.execute(request, body);

        System.out.println("RASPUNS SERVER");
        System.out.println("Status: " + response.getStatusCode());

        String responseBody = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        System.out.println("Response Body: " + responseBody);
        System.out.println();

        return response;
    }
}