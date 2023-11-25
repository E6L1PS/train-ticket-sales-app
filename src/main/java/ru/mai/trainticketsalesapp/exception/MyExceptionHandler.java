package ru.mai.trainticketsalesapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handleNotFoundException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ResponseError(e.getMessage(), HttpStatus.NOT_FOUND);
    }

}
