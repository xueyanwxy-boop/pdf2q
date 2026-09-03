package com.pdf2q.dao;

import com.pdf2q.domain.Question;
import java.util.List;

/** 题目数据访问接口。 */
public interface QuestionDao {

  int insertBatch(List<Question> list);

  List<Question> selectByQuizSetId(Long quizSetId);

  int deleteByQuizSetId(Long quizSetId);
}
