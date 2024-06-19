package org.example.exception;

import org.example.dto.ApiErrorResponse;

public class ApiErrorException extends RuntimeException {

    public ApiErrorException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse.exceptionMessage());
    }
}
