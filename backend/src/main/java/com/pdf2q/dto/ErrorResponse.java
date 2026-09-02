package com.pdf2q.dto;

import java.util.Map;

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
