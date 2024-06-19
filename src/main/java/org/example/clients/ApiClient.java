package org.example.clients;

import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.example.dto.ApiErrorResponse;
import org.example.dto.Candidate;
import org.example.dto.RoleResponse;
import org.example.dto.StatusRequest;
import org.example.exception.ApiErrorException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Log4j2
public class ApiClient {
    private static final String BASE_URL = "http://193.19.100.32:7000/api";
    private static final String GET_ROLES = "/get-roles";
    private static final String SIGN_UP = "/sign-up";
    private static final String GET_CODE = "/get-code";
    private static final String SET_STATUS = "/set-status";
    private final WebClient webClient;

    public ApiClient(WebClient.Builder builder, String baseUrl) {

        this.webClient = builder
            .baseUrl(Objects.requireNonNullElse(baseUrl, BASE_URL))
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create().followRedirect(true)
            ))
            .defaultStatusHandler(HttpStatusCode::is4xxClientError, clientResponse -> clientResponse
                .bodyToMono(ApiErrorResponse.class)
                .flatMap(apiErrorResponse -> Mono.error(new ApiErrorException(apiErrorResponse))))
            .defaultStatusHandler(HttpStatusCode::is5xxServerError, clientResponse -> clientResponse
                .bodyToMono(ApiErrorResponse.class)
                .flatMap(apiErrorResponse -> Mono.error(new ApiErrorException(apiErrorResponse))))
            .filter((request, next) -> {
                log.debug("Request: {} {}", request.method(), request.url());
                return next.exchange(request).doOnNext(response -> {
                    log.debug("Response: {}", response.statusCode());
                });
            })
            .build();
    }

    public RoleResponse getRoles() {
        return webClient
            .get()
            .uri(GET_ROLES)
            .retrieve()
            .bodyToMono(RoleResponse.class)
            .block();
    }

    public String signUp(Candidate candidate) {
        return webClient
            .post()
            .uri(SIGN_UP)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(candidate))
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    public String getCode(String email) {
        return webClient
            .get()
            .uri(GET_CODE + "?email=" + email)
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> response.replaceAll("^\"|\"$", ""))
            .block();
    }

    public String setStatus(StatusRequest status) {
        return webClient
            .post()
            .uri(SET_STATUS)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(status))
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }
}
