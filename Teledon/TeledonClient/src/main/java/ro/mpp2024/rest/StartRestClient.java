package ro.mpp2024.rest;

import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ro.mpp2024.model.CharityCase;

public class StartRestClient {
    private static final String BASE_URL = "http://localhost:8080/teledon/charity-cases";

    public static void main(String[] args) {

        RestClient restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .requestInterceptor(new LoggingInterceptor())
                .build();

        try {
            System.out.println("TEST: CREATE");
            CharityCase newCase = new CharityCase("Caz Test RestClient", 3500.0);

            CharityCase result = restClient.post()
                    .body(newCase)
                    .retrieve()
                    .body(CharityCase.class);

            Long id = result.getId();
            System.out.println();

            System.out.println("TEST: GET BY ID");
            restClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .body(CharityCase.class);
            System.out.println();

            System.out.println("TEST: UPDATE");
            result.setName("Nume Actualizat RestClient");
            restClient.put()
                    .uri("/{id}", id)
                    .body(result)
                    .retrieve()
                    .toBodilessEntity();
            System.out.println();

            System.out.println("TEST: GET ALL");
            restClient.get()
                    .retrieve()
                    .body(CharityCase[].class);
            System.out.println();

            System.out.println("TEST: GET FILTERED (maxAmount=4000)");
            restClient.get()
                    .uri("?maxAmount=4000")
                    .retrieve()
                    .body(CharityCase[].class);
            System.out.println();

            System.out.println("TEST: DELETE");
            restClient.delete()
                    .uri("/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
            System.out.println();

            System.out.println("TEST: VERIFICARE FINALA");
            try {
                restClient.get()
                        .uri("/{id}", id)
                        .retrieve()
                        .body(CharityCase.class);
            } catch (HttpClientErrorException.NotFound e) {
                System.out.println("Verificare OK: Resursa nu mai exista (Eroare 404 prinsa corect).");
            }
            System.out.println();

        } catch (Exception e) {
            System.err.println("Eroare generala: " + e.getMessage());
        }
    }
}