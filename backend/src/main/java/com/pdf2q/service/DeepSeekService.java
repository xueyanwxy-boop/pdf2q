package com.pdf2q.service;

import com.pdf2q.dto.GenerateResponse.QuestionDto;
import java.util.List;

/** DeepSeek 出题服务接口。 */
public interface DeepSeekService {

  /**
   * 按各题型数量分别出题后合并（顺序：单选 → 多选 → 判断）。
   * 每种 0–20，且至少一种 &gt; 0。
   */
  List<QuestionDto> generateQuestions(
      String rawText, int singleCount, int multipleCount, int judgeCount);
}
