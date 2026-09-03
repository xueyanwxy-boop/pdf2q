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

/**
 * 基础接口：健康检查，以及「只出题不落库」的调试接口。
 * <p>正式创建题库请使用 {@link QuizSetController}。
 */
@RestController
@RequestMapping("/api")
public class GenerateController {

  private final DeepSeekService deepSeekService;

  public GenerateController(DeepSeekService deepSeekService) {
    this.deepSeekService = deepSeekService;
  }

  /** 健康检查，确认后端已启动。GET /api/health */
  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of("ok", true);
  }

  /**
   * 根据文本生成选择题，结果不写入数据库（调试用）。
   * POST /api/generate
   */
  @PostMapping("/generate")
  public GenerateResponse generate(@Valid @RequestBody GenerateRequest request) {
    int single = request.getSingleCount() == null ? 0 : request.getSingleCount();
    int multiple = request.getMultipleCount() == null ? 0 : request.getMultipleCount();
    int judge = request.getJudgeCount() == null ? 0 : request.getJudgeCount();
    return new GenerateResponse(
        deepSeekService.generateQuestions(request.getText(), single, multiple, judge));
  }
}
