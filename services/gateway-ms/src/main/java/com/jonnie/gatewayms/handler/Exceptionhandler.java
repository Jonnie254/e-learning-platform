package com.jonnie.gatewayms.handler;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class Exceptionhandler {
    @ExceptionHandler(ExpiredJwtException.class)
   public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(Map.of("Error", e.getMessage())));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(Map.of("Error", e.getMessage())));
    }


}
