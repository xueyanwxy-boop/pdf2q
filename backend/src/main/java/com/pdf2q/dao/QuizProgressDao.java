package com.pdf2q.dao;

import com.pdf2q.domain.QuizProgress;
import java.util.List;

/** 答题进度数据访问接口。 */
public interface QuizProgressDao {

  QuizProgress selectByOwnerAndQuizSet(String ownerToken, Long quizSetId);

  QuizProgress selectByUserAndQuizSet(Long userId, Long quizSetId);

  List<QuizProgress> selectByOwnerAndQuizSetIds(String ownerToken, List<Long> quizSetIds);

  List<QuizProgress> selectByUserAndQuizSetIds(Long userId, List<Long> quizSetIds);

  int insert(QuizProgress progress);

  int update(QuizProgress progress);

  int bindUserForOwner(String ownerToken, Long userId);

  int deleteByOwnerAndQuizSet(String ownerToken, Long quizSetId);

  int deleteByUserAndQuizSet(Long userId, Long quizSetId);

  int deleteOwnerProgressWhenUserExists(String ownerToken, Long userId);

  int deleteByQuizSetId(Long quizSetId);
}
