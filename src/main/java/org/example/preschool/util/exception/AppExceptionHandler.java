package org.example.preschool.util.exception;

import org.example.preschool.util.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException exception
    ) {
        List<String> errors = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + error.getDefaultMessage()
                        : error.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.badRequest()
                .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<BaseResponse<?>> handleServiceException(ServiceException exception) {
        BaseResponse<?> response = new BaseResponse<>(
                exception.getCode(),
                exception.getMessage(),
                exception.getData()
        );
        return new ResponseEntity<>(response, exception.getStatusCode());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse<Void>> handleAuthenticationException(AuthenticationException exception) {
        BaseResponse<Void> response = new BaseResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid username or password",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
