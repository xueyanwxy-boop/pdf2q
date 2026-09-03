package com.pdf2q.web;

import com.pdf2q.dto.ErrorResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * 全局异常处理：把各类异常统一成 {@code {"error":"..."}}，方便前端展示。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** 处理请求体校验失败（如 @NotBlank）。 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining("; "));
    return ResponseEntity.badRequest().body(new ErrorResponse(message));
  }

  /** 处理业务里主动抛出的 ResponseStatusException（含 4xx/5xx 原因文案）。 */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleStatus(ResponseStatusException ex) {
    HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
    if (status == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    String reason = ex.getReason() == null ? status.getReasonPhrase() : ex.getReason();
    return ResponseEntity.status(status).body(new ErrorResponse(reason));
  }

  /** 兜底：未分类异常返回 500。 */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleOther(Exception ex) {
    return ResponseEntity.internalServerError()
        .body(new ErrorResponse(ex.getMessage() == null ? "Internal Server Error" : ex.getMessage()));
  }
}
