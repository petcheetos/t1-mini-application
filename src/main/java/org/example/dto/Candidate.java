package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Candidate(
    @JsonProperty("last_name")
    String lastName,
    @JsonProperty("first_name")
    String firstName,
    @JsonProperty("email")
    String email,
    @JsonProperty("role")
    String role
) {
}
