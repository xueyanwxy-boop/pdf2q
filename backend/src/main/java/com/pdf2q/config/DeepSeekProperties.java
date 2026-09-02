package com.pdf2q.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekProperties {

  private String apiKey = "";
  private String baseUrl = "https://api.deepseek.com";
  private String model = "deepseek-chat";
  private int maxTextChars = 60000;

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public int getMaxTextChars() {
    return maxTextChars;
  }

  public void setMaxTextChars(int maxTextChars) {
    this.maxTextChars = maxTextChars;
  }
}
