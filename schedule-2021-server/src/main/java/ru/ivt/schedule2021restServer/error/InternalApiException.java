package ru.ivt.schedule2021restServer.error;

import org.springframework.http.HttpStatus;

public class InternalApiException extends ApiRequestException {
    private final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public InternalApiException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
