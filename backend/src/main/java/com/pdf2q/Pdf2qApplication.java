package com.pdf2q;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Pdf2qApplication {

  public static void main(String[] args) {
    SpringApplication.run(Pdf2qApplication.class, args);
  }
}
