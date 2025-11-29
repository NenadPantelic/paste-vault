package com.pastevault.common.api;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pastevault.common.exception.ApiException;
import com.pastevault.common.exception.ErrorReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public final ResponseEntity<ErrorReport> handleApiException(ApiException e) {
        log.warn("Handling APIException {}", e.getErrorReport());

        return new ResponseEntity<>(
                e.getErrorReport(),
                HttpStatusCode.valueOf(e.getErrorReport().status())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorReport> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Handling MethodArgumentNotValidException...", e);
        List<String> errors = new ArrayList<>();

        e.getBindingResult().getAllErrors().forEach(
                error -> errors.add(
                        String.format("%s: %s", ((FieldError) error).getField(), error.getDefaultMessage()
                        )
                )
        );

        return new ResponseEntity<>(
                ErrorReport.BAD_REQUEST.withErrors(errors),
                HttpStatusCode.valueOf(ErrorReport.BAD_REQUEST.status())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorReport> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Handling IllegalArgumentException...", e);

        return new ResponseEntity<>(
                ErrorReport.BAD_REQUEST.withErrors(e.getMessage()),
                HttpStatusCode.valueOf(ErrorReport.BAD_REQUEST.status())
        );
    }

    @ExceptionHandler({
            JsonParseException.class,
            HttpMessageNotReadableException.class,
            JsonMappingException.class
    })
    public ResponseEntity<ErrorReport> handleJsonParseException(JsonParseException e) {
        log.warn("Handling JSON request body exception...", e);

        return new ResponseEntity<>(
                ErrorReport.UNPROCESSABLE_ENTITY,
                HttpStatusCode.valueOf(ErrorReport.UNPROCESSABLE_ENTITY.status())
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ErrorReport> handleException(final RuntimeException e) {
        log.warn("Handling RuntimeException...", e);

        return new ResponseEntity<>(
                ErrorReport.INTERNAL_SERVER_ERROR,
                HttpStatusCode.valueOf(ErrorReport.INTERNAL_SERVER_ERROR.status())
        );
    }
}