package com.pdf2q.mapper;

import com.pdf2q.domain.Question;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper：题目表 SQL 映射（见 QuestionMapper.xml）。 */
@Mapper
public interface QuestionMapper {

  int insertBatch(@Param("list") List<Question> list);

  List<Question> selectByQuizSetId(@Param("quizSetId") Long quizSetId);

  int deleteByQuizSetId(@Param("quizSetId") Long quizSetId);
}
