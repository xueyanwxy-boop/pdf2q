package com.pdf2q.dto;

import java.time.Instant;
import java.util.List;

/** 题库相关响应 DTO 集合（列表摘要、详情、进度）。 */
public class QuizDtos {

  private QuizDtos() {
  }

  /** 题库列表中的一条摘要（含进度）。 */
  public static class QuizSetSummary {
    private Long id;
    private String title;
    private int questionCount;
    private Instant createdAt;
    private int answeredCount;
    private int currentIndex;
    private boolean finished;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public int getQuestionCount() {
      return questionCount;
    }

    public void setQuestionCount(int questionCount) {
      this.questionCount = questionCount;
    }

    public Instant getCreatedAt() {
      return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
      this.createdAt = createdAt;
    }

    public int getAnsweredCount() {
      return answeredCount;
    }

    public void setAnsweredCount(int answeredCount) {
      this.answeredCount = answeredCount;
    }

    public int getCurrentIndex() {
      return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
      this.currentIndex = currentIndex;
    }

    public boolean isFinished() {
      return finished;
    }

    public void setFinished(boolean finished) {
      this.finished = finished;
    }
  }

  /** 答题进度。 */
  public static class ProgressDto {
    private int currentIndex;
    private List<SaveProgressRequest.AnswerItem> answers;
    private boolean finished;

    public int getCurrentIndex() {
      return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
      this.currentIndex = currentIndex;
    }

    public List<SaveProgressRequest.AnswerItem> getAnswers() {
      return answers;
    }

    public void setAnswers(List<SaveProgressRequest.AnswerItem> answers) {
      this.answers = answers;
    }

    public boolean isFinished() {
      return finished;
    }

    public void setFinished(boolean finished) {
      this.finished = finished;
    }
  }

  /** 题库详情：元数据 + 全部题目 + 进度。 */
  public static class QuizSetDetail {
    private Long id;
    private String title;
    private int questionCount;
    private List<GenerateResponse.QuestionDto> questions;
    private ProgressDto progress;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public int getQuestionCount() {
      return questionCount;
    }

    public void setQuestionCount(int questionCount) {
      this.questionCount = questionCount;
    }

    public List<GenerateResponse.QuestionDto> getQuestions() {
      return questions;
    }

    public void setQuestions(List<GenerateResponse.QuestionDto> questions) {
      this.questions = questions;
    }

    public ProgressDto getProgress() {
      return progress;
    }

    public void setProgress(ProgressDto progress) {
      this.progress = progress;
    }
  }

  /** 将数据库题目转为前端使用的 QuestionDto。 */
  public static GenerateResponse.QuestionDto toQuestionDto(com.pdf2q.domain.Question q) {
    GenerateResponse.QuestionDto dto = new GenerateResponse.QuestionDto();
    dto.setType(q.getType());
    dto.setQuestion(q.getStem());
    java.util.Map<String, String> options = new java.util.LinkedHashMap<>();
    options.put("A", q.getOptionA());
    options.put("B", q.getOptionB());
    if (q.getType() != com.pdf2q.domain.QuestionType.judge) {
      options.put("C", q.getOptionC());
      options.put("D", q.getOptionD());
    }
    dto.setOptions(options);
    dto.setAnswer(q.getAnswer());
    dto.setExplanation(q.getExplanation());
    return dto;
  }
}
