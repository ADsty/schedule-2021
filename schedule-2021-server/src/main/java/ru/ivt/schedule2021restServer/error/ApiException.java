package ru.ivt.schedule2021restServer.error;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiException {
    private boolean error = true;
    private String description;
    private HttpStatus httpStatus;

    public ApiException(String description, HttpStatus httpStatus) {
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
