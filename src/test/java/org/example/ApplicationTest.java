package org.example;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.example.clients.ApiClient;
import org.example.dto.CandidateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = 8080)
public class ApplicationTest {

    private final ApiClient apiClient = new ApiClient(WebClient.builder(), "http://localhost:8080");
    private final Application application = new Application(apiClient);

    @BeforeEach
    public void setUp() {
        stubFor(post(urlEqualTo("/sign-up"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("success")));

        stubFor(get(urlEqualTo("/get-code?email=new@example.ru"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("someCode")));

        stubFor(post(urlEqualTo("/set-status"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("success")));
    }

    @Test
    public void testRegisterSuccess() {
        CandidateRequest candidate = new CandidateRequest("Новый", "Пользователь", "new@example.ru", "string");
        assertEquals("success", application.register(candidate));
    }
}
