package com.pdf2q.dao.impl;

import com.pdf2q.dao.QuizProgressDao;
import com.pdf2q.domain.QuizProgress;
import com.pdf2q.mapper.QuizProgressMapper;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

/** 答题进度 Dao 实现：委托 MyBatis Mapper。 */
@Repository
public class QuizProgressDaoImpl implements QuizProgressDao {

  private final QuizProgressMapper quizProgressMapper;

  public QuizProgressDaoImpl(QuizProgressMapper quizProgressMapper) {
    this.quizProgressMapper = quizProgressMapper;
  }

  @Override
  public QuizProgress selectByOwnerAndQuizSet(String ownerToken, Long quizSetId) {
    return quizProgressMapper.selectByOwnerAndQuizSet(ownerToken, quizSetId);
  }

  @Override
  public List<QuizProgress> selectByOwnerAndQuizSetIds(String ownerToken, List<Long> quizSetIds) {
    if (quizSetIds == null || quizSetIds.isEmpty()) {
      return Collections.emptyList();
    }
    return quizProgressMapper.selectByOwnerAndQuizSetIds(ownerToken, quizSetIds);
  }

  @Override
  public int insert(QuizProgress progress) {
    return quizProgressMapper.insert(progress);
  }

  @Override
  public int update(QuizProgress progress) {
    return quizProgressMapper.update(progress);
  }

  @Override
  public int deleteByOwnerAndQuizSet(String ownerToken, Long quizSetId) {
    return quizProgressMapper.deleteByOwnerAndQuizSet(ownerToken, quizSetId);
  }

  @Override
  public int deleteByQuizSetId(Long quizSetId) {
    return quizProgressMapper.deleteByQuizSetId(quizSetId);
  }
}
