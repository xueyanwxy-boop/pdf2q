package com.pdf2q.dao.impl;

import com.pdf2q.dao.QuestionDao;
import com.pdf2q.domain.Question;
import com.pdf2q.mapper.QuestionMapper;
import java.util.List;
import org.springframework.stereotype.Repository;

/** 题目 Dao 实现：委托 MyBatis Mapper。 */
@Repository
public class QuestionDaoImpl implements QuestionDao {

  private final QuestionMapper questionMapper;

  public QuestionDaoImpl(QuestionMapper questionMapper) {
    this.questionMapper = questionMapper;
  }

  @Override
  public int insertBatch(List<Question> list) {
    if (list == null || list.isEmpty()) {
      return 0;
    }
    return questionMapper.insertBatch(list);
  }

  @Override
  public List<Question> selectByQuizSetId(Long quizSetId) {
    return questionMapper.selectByQuizSetId(quizSetId);
  }

  @Override
  public int deleteByQuizSetId(Long quizSetId) {
    return questionMapper.deleteByQuizSetId(quizSetId);
  }
}
