package ro.mpp2024.rest;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import ro.mpp2024.model.CharityCase;
import java.util.Collections;

public class StartRestTemplate {
    private static final String URL = "http://localhost:8080/teledon/charity-cases";

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate(
                new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())
        );
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));

        try {
            System.out.println("TEST: CREATE");
            CharityCase newCase = new CharityCase("Caz Test Complet", 2500.0);
            CharityCase result = restTemplate.postForObject(URL, newCase, CharityCase.class);
            Long id = result.getId();
            System.out.println();

            System.out.println("TEST: GET BY ID");
            restTemplate.getForObject(URL + "/" + id, CharityCase.class);
            System.out.println();

            System.out.println("TEST: UPDATE");
            result.setName("Nume Actualizat");
            restTemplate.put(URL + "/" + id, result);
            System.out.println();

            System.out.println("TEST: GET ALL");
            restTemplate.getForObject(URL, CharityCase[].class);
            System.out.println();

            System.out.println("TEST: GET FILTERED (maxAmount=3000)");
            restTemplate.getForObject(URL + "?maxAmount=3000", CharityCase[].class);
            System.out.println();

            System.out.println("TEST: DELETE");
            restTemplate.delete(URL + "/" + id);
            System.out.println();

            System.out.println("TEST: VERIFICARE FINALA");
            try {
                restTemplate.getForObject(URL + "/" + id, CharityCase.class);
            } catch (Exception e) {
                System.out.println("Verificare OK: Resursa nu mai exista.");
            }
            System.out.println();

        } catch (Exception e) {
            System.err.println("Eroare: " + e.getMessage());
        }
    }
}