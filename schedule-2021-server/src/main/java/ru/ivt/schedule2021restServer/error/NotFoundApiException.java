package ru.ivt.schedule2021restServer.error;

import org.springframework.http.HttpStatus;

public class NotFoundApiException extends ApiRequestException {

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public NotFoundApiException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
