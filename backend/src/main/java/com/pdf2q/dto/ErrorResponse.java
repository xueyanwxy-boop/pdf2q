package com.pdf2q.dto;

import java.util.Map;

/** 统一错误响应体，字段名 {@code error} 供前端读取。 */
public class ErrorResponse {

  private String error;

  public ErrorResponse() {
  }

  public ErrorResponse(String error) {
    this.error = error;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public Map<String, String> asMap() {
    return Map.of("error", error);
  }
}
