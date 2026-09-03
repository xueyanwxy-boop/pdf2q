package com.pdf2q.domain;

import java.time.Instant;

/** 题库表 quiz_sets 对应实体（MyBatis POJO）。 */
public class QuizSet {

  private Long id;
  private String ownerToken;
  /** 云端账号 id；未登录本地题为 null */
  private Long userId;
  private String title;
  private int questionCount;
  private Instant createdAt;

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
}
