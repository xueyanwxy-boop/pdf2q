package com.pdf2q;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * pdf2q 后端启动入口（Spring Boot + MyBatis）。
 * <p>默认端口见 application.yml（3001）。
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("com.pdf2q.mapper")
public class Pdf2qApplication {

  public static void main(String[] args) {
    SpringApplication.run(Pdf2qApplication.class, args);
  }
}
