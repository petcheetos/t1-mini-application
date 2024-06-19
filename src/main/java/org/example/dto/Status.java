package org.example.dto;

public enum Status {
    INCREASED("increased");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getStatus() {
        return value;
    }
}
