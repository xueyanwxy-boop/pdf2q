package com.pdf2q.service;

import com.pdf2q.dto.CreateQuizSetRequest;
import com.pdf2q.dto.QuizDtos.ProgressDto;
import com.pdf2q.dto.QuizDtos.QuizSetDetail;
import com.pdf2q.dto.QuizDtos.QuizSetSummary;
import com.pdf2q.dto.SaveProgressRequest;
import java.util.List;

/** 题库业务接口：创建/查询/删除题库，以及答题进度。 */
public interface QuizSetService {

  /** 按 ownerToken 列出题库，并附带进度摘要。 */
  List<QuizSetSummary> list(String ownerToken);

  /** 调用 DeepSeek 出题并落库。 */
  QuizSetDetail create(String ownerToken, CreateQuizSetRequest request);

  /** 题库详情 + 进度。 */
  QuizSetDetail get(String ownerToken, Long id);

  /** 保存答题进度。 */
  ProgressDto saveProgress(String ownerToken, Long id, SaveProgressRequest request);

  /** 清空进度（重新作答）。 */
  ProgressDto resetProgress(String ownerToken, Long id);

  /** 删除题库、题目与进度。 */
  void delete(String ownerToken, Long id);
}
