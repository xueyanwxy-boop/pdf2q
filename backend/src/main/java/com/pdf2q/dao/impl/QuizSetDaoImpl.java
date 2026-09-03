package com.pdf2q.dao.impl;

import com.pdf2q.dao.QuizSetDao;
import com.pdf2q.domain.QuizSet;
import com.pdf2q.mapper.QuizSetMapper;
import java.util.List;
import org.springframework.stereotype.Repository;

/** 题库 Dao 实现：委托 MyBatis Mapper。 */
@Repository
public class QuizSetDaoImpl implements QuizSetDao {

  private final QuizSetMapper quizSetMapper;

  public QuizSetDaoImpl(QuizSetMapper quizSetMapper) {
    this.quizSetMapper = quizSetMapper;
  }

  @Override
  public int insert(QuizSet quizSet) {
    return quizSetMapper.insert(quizSet);
  }

  @Override
  public List<QuizSet> selectByOwnerToken(String ownerToken) {
    return quizSetMapper.selectByOwnerToken(ownerToken);
  }

  @Override
  public QuizSet selectByIdAndOwnerToken(Long id, String ownerToken) {
    return quizSetMapper.selectByIdAndOwnerToken(id, ownerToken);
  }

  @Override
  public int deleteById(Long id) {
    return quizSetMapper.deleteById(id);
  }
}
