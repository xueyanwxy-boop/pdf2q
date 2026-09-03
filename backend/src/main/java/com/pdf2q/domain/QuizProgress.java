package com.pdf2q.domain;

import java.time.Instant;

/** 答题进度表 quiz_progress 对应实体（MyBatis POJO）。 */
public class QuizProgress {

  private Long id;
  private String ownerToken;
  private Long userId;
  private Long quizSetId;
  private int currentIndex;
  private String answersJson;
  private Instant updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOwnerToken() {
    return ownerToken;
  }

  public void setOwnerToken(String ownerToken) {
    this.ownerToken = ownerToken;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getQuizSetId() {
    return quizSetId;
  }

  public void setQuizSetId(Long quizSetId) {
    this.quizSetId = quizSetId;
  }

  public int getCurrentIndex() {
    return currentIndex;
  }

  public void setCurrentIndex(int currentIndex) {
    this.currentIndex = currentIndex;
  }

  public String getAnswersJson() {
    return answersJson;
  }

  public void setAnswersJson(String answersJson) {
    this.answersJson = answersJson;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
