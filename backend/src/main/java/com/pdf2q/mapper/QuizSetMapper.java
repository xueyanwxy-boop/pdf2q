package com.pdf2q.mapper;

import com.pdf2q.domain.QuizSet;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper：题库表 SQL 映射（见 QuizSetMapper.xml）。 */
@Mapper
public interface QuizSetMapper {

  int insert(QuizSet quizSet);

  List<QuizSet> selectByOwnerToken(@Param("ownerToken") String ownerToken);

  QuizSet selectByIdAndOwnerToken(@Param("id") Long id, @Param("ownerToken") String ownerToken);

  int deleteById(@Param("id") Long id);
}
