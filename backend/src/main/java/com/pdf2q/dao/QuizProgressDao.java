package com.pdf2q.dao;

import com.pdf2q.domain.QuizProgress;
import java.util.List;

/** 答题进度数据访问接口。 */
public interface QuizProgressDao {

  QuizProgress selectByOwnerAndQuizSet(String ownerToken, Long quizSetId);

  List<QuizProgress> selectByOwnerAndQuizSetIds(String ownerToken, List<Long> quizSetIds);

  int insert(QuizProgress progress);

  int update(QuizProgress progress);

  int deleteByOwnerAndQuizSet(String ownerToken, Long quizSetId);

  int deleteByQuizSetId(Long quizSetId);
}
