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

  List<QuizSet> selectByUserId(@Param("userId") Long userId);

  QuizSet selectByIdAndOwnerToken(@Param("id") Long id, @Param("ownerToken") String ownerToken);

  QuizSet selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

  int bindUserForOwner(@Param("ownerToken") String ownerToken, @Param("userId") Long userId);

  int deleteById(@Param("id") Long id);
}
