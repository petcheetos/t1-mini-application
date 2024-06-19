package org.example.clients;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.example.dto.Candidate;
import org.example.dto.RoleResponse;
import org.example.dto.Status;
import org.example.dto.StatusRequest;
import org.example.exception.ApiErrorException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest(httpPort = 8080)
public class ApiClientTest {

    private final String roles = """
            {
                "roles": [
                    "Системный аналитик",
                    "Разработчик Java",
                    "Разработчик JS/React",
                    "Тестировщик",
                    "Прикладной администратор"
                ]
            }""";

    private final ApiClient apiClient = new ApiClient(WebClient.builder(), "http://localhost:8080");

    @Test
    void testGetRolesSuccess() {
        stubFor(get(urlEqualTo("/get-roles"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json")
                .withBody(roles)));

        RoleResponse response = apiClient.getRoles();
        assertThat(response.roles()).isNotEmpty();
        assertThat(response.roles()).containsExactly(
            "Системный аналитик",
            "Разработчик Java",
            "Разработчик JS/React",
            "Тестировщик",
            "Прикладной администратор"
        );
    }

    @Test
    void testGetRolesWithClientError() {
        stubFor(get(urlEqualTo("/get-roles"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json")
                .withBody(roles)));

        assertThrows(ApiErrorException.class, apiClient::getRoles);
    }

    @Test
    public void testSignUpSuccess() {
        Candidate candidate = new Candidate("Lastname", "Firstname", "new@example.ru", "role");

        stubFor(post(urlEqualTo("/sign-up"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json")
                .withBody("success")));

        String response = apiClient.signUp(candidate);
        assertEquals("success", response);
    }

    @Test
    public void testSignUpClientError() {
        Candidate candidate = new Candidate("Invalid", "User", "invalid-email", "role");

        stubFor(post(urlEqualTo("/sign-up"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"Bad Request\"}")));

        assertThrows(ApiErrorException.class, () -> {
            apiClient.signUp(candidate);
        });
    }

    @Test
    public void testGetCodeSuccess() {
        String email = "new@example.ru";

        stubFor(get(urlEqualTo("/get-code?email=new@example.ru"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json")
                .withBody("e073c14270e98ba5adbbb07e22ca831d")));

        String response = apiClient.getCode(email);
        assertEquals("e073c14270e98ba5adbbb07e22ca831d", response);
    }

    @Test
    public void testGetCodeClientError() {
        String email = "new@example.ru";

        stubFor(get(urlEqualTo("/get-code?email=new@example.ru"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"Bad Request\"}")));

        assertThrows(ApiErrorException.class, () -> {
            apiClient.getCode(email);
        });
    }

    @Test
    public void testSetStatusSuccess() {
        StatusRequest status = new StatusRequest("token", Status.INCREASED.getStatus());

        stubFor(post(urlEqualTo("/set-status"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json")
                .withBody("success")));

        String response = apiClient.setStatus(status);
        assertEquals("success", response);
    }

    @Test
    public void testSetStatusClientError() {
        StatusRequest status = new StatusRequest("token", Status.INCREASED.getStatus());

        stubFor(post(urlEqualTo("/set-status"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"Bad Request\"}")));

        assertThrows(ApiErrorException.class, () -> {
            apiClient.setStatus(status);
        });
    }
}
