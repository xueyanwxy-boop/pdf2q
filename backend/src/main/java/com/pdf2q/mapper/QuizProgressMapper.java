package com.pdf2q.mapper;

import com.pdf2q.domain.QuizProgress;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** MyBatis Mapper：进度表 SQL 映射（见 QuizProgressMapper.xml）。 */
@Mapper
public interface QuizProgressMapper {

  QuizProgress selectByOwnerAndQuizSet(
      @Param("ownerToken") String ownerToken, @Param("quizSetId") Long quizSetId);

  List<QuizProgress> selectByOwnerAndQuizSetIds(
      @Param("ownerToken") String ownerToken, @Param("quizSetIds") List<Long> quizSetIds);

  int insert(QuizProgress progress);

  int update(QuizProgress progress);

  int deleteByOwnerAndQuizSet(
      @Param("ownerToken") String ownerToken, @Param("quizSetId") Long quizSetId);

  int deleteByQuizSetId(@Param("quizSetId") Long quizSetId);
}
