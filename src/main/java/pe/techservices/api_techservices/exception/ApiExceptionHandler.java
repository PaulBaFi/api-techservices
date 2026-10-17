package pe.techservices.api_techservices.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.techservices.api_techservices.dto.ErrorResponse;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorResponse> handle(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new ErrorResponse(ex.getStatus().value(), ex.getStatus().getReasonPhrase(), ex.getMessage()));
    }
}
