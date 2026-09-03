package com.pdf2q.service;

import com.pdf2q.dto.CreateQuizSetRequest;
import com.pdf2q.dto.QuizDtos.ProgressDto;
import com.pdf2q.dto.QuizDtos.QuizSetDetail;
import com.pdf2q.dto.QuizDtos.QuizSetSummary;
import com.pdf2q.dto.SaveProgressRequest;
import java.util.List;

/** 题库业务接口：创建/查询/删除题库，以及答题进度。 */
public interface QuizSetService {

  /** 列出题库：已登录按 userId，否则按 ownerToken。 */
  List<QuizSetSummary> list(String ownerToken, Long userId);

  /** 调用 DeepSeek 出题并落库。 */
  QuizSetDetail create(String ownerToken, Long userId, CreateQuizSetRequest request);

  /** 题库详情 + 进度。 */
  QuizSetDetail get(String ownerToken, Long userId, Long id);

  /** 保存答题进度。 */
  ProgressDto saveProgress(String ownerToken, Long userId, Long id, SaveProgressRequest request);

  /** 清空进度（重新作答）。 */
  ProgressDto resetProgress(String ownerToken, Long userId, Long id);

  /** 删除题库、题目与进度。 */
  void delete(String ownerToken, Long userId, Long id);
}
