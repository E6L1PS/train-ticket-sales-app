package ru.mai.trainticketsalesapp.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ResponseError {
    private final String message;
    private final HttpStatus httpStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime time = LocalDateTime.now();

    public ResponseError(String message, HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
