package com.pdf2q.controller;

import com.pdf2q.dto.CreateQuizSetRequest;
import com.pdf2q.dto.QuizDtos.ProgressDto;
import com.pdf2q.dto.QuizDtos.QuizSetDetail;
import com.pdf2q.dto.QuizDtos.QuizSetSummary;
import com.pdf2q.dto.SaveProgressRequest;
import com.pdf2q.service.QuizSetService;
import com.pdf2q.web.OwnerTokenResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 题库相关 HTTP 接口。
 * <p>均需请求头 {@code X-Owner-Token}（弱登录身份）。
 */
@RestController
@RequestMapping("/api/quiz-sets")
public class QuizSetController {

  private final QuizSetService quizSetService;
  private final OwnerTokenResolver ownerTokenResolver;

  public QuizSetController(QuizSetService quizSetService, OwnerTokenResolver ownerTokenResolver) {
    this.quizSetService = quizSetService;
    this.ownerTokenResolver = ownerTokenResolver;
  }

  /** 我的题库列表（含作答进度摘要）。GET /api/quiz-sets */
  @GetMapping
  public List<QuizSetSummary> list(HttpServletRequest request) {
    return quizSetService.list(ownerTokenResolver.require(request));
  }

  /** 创建题库：调 DeepSeek 出题并写入 MySQL。POST /api/quiz-sets */
  @PostMapping
  public QuizSetDetail create(HttpServletRequest request, @Valid @RequestBody CreateQuizSetRequest body) {
    return quizSetService.create(ownerTokenResolver.require(request), body);
  }

  /** 题库详情（题目 + 当前进度），用于进入/继续答题。GET /api/quiz-sets/{id} */
  @GetMapping("/{id}")
  public QuizSetDetail get(HttpServletRequest request, @PathVariable Long id) {
    return quizSetService.get(ownerTokenResolver.require(request), id);
  }

  /** 保存答题进度（退出答题或每答一题时调用）。PUT /api/quiz-sets/{id}/progress */
  @PutMapping("/{id}/progress")
  public ProgressDto saveProgress(
      HttpServletRequest request,
      @PathVariable Long id,
      @Valid @RequestBody SaveProgressRequest body) {
    return quizSetService.saveProgress(ownerTokenResolver.require(request), id, body);
  }

  /** 清空进度（列表上「重新作答」）。POST /api/quiz-sets/{id}/progress/reset */
  @PostMapping("/{id}/progress/reset")
  public ProgressDto resetProgress(HttpServletRequest request, @PathVariable Long id) {
    return quizSetService.resetProgress(ownerTokenResolver.require(request), id);
  }

  /** 删除题库及其进度。DELETE /api/quiz-sets/{id} */
  @DeleteMapping("/{id}")
  public Map<String, Object> delete(HttpServletRequest request, @PathVariable Long id) {
    quizSetService.delete(ownerTokenResolver.require(request), id);
    return Map.of("ok", true);
  }
}
