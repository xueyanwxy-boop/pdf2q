package com.pdf2q.dao;

import com.pdf2q.domain.QuizSet;
import java.util.List;

/** 题库数据访问接口。 */
public interface QuizSetDao {

  int insert(QuizSet quizSet);

  List<QuizSet> selectByOwnerToken(String ownerToken);

  List<QuizSet> selectByUserId(Long userId);

  QuizSet selectByIdAndOwnerToken(Long id, String ownerToken);

  QuizSet selectByIdAndUserId(Long id, Long userId);

  int bindUserForOwner(String ownerToken, Long userId);

  int deleteById(Long id);
}
