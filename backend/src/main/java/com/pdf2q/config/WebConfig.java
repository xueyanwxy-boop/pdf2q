package com.pdf2q.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Web 相关 Bean：HTTP 客户端、跨域配置。 */
@Configuration
public class WebConfig {

  /** 用于调用 DeepSeek 等外部 HTTP API。 */
  @Bean
  public RestClient.Builder restClientBuilder() {
    return RestClient.builder();
  }

  /** 允许前端（Vite 开发服等）跨域访问 /api/**。 */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*");
      }
    };
  }
}
