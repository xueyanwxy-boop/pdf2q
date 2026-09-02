package com.pdf2q.controller;

import com.pdf2q.dto.GenerateRequest;
import com.pdf2q.dto.GenerateResponse;
import com.pdf2q.service.DeepSeekService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GenerateController {

  private final DeepSeekService deepSeekService;

  public GenerateController(DeepSeekService deepSeekService) {
    this.deepSeekService = deepSeekService;
  }

  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of("ok", true);
  }

  @PostMapping("/generate")
  public GenerateResponse generate(@Valid @RequestBody GenerateRequest request) {
    int count = request.getCount() == null ? 10 : request.getCount();
    return new GenerateResponse(deepSeekService.generateQuestions(request.getText(), count));
  }
}
