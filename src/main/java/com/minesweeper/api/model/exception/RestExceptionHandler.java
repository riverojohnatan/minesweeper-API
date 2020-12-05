package com.minesweeper.api.model.exception;

import com.minesweeper.api.dto.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

   @ExceptionHandler(MinesweeperApiException.class)
    protected ResponseEntity<ApiError> handleSolicitudesApiException(final MinesweeperApiException ex) {
        log.error(ex.getMessage());
        final ApiError apiError = ApiError.builder().message(ex.getMessage()).build();
        return new ResponseEntity<ApiError>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiError> handleException(final Exception ex) {
        log.error(ex.getMessage());
        final ApiError apiError = ApiError.builder().message("Unexpected Error").build();
        return new ResponseEntity<ApiError>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
