package com.pdf2q.domain;

/** 题目表 questions 对应实体（MyBatis POJO）。 */
public class Question {

  private Long id;
  private Long quizSetId;
  private int seq;
  private QuestionType type;
  private String stem;
  private String optionA;
  private String optionB;
  private String optionC;
  private String optionD;
  private String answer;
  private String explanation;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getQuizSetId() {
    return quizSetId;
  }

  public void setQuizSetId(Long quizSetId) {
    this.quizSetId = quizSetId;
  }

  public int getSeq() {
    return seq;
  }

  public void setSeq(int seq) {
    this.seq = seq;
  }

  public QuestionType getType() {
    return type;
  }

  public void setType(QuestionType type) {
    this.type = type;
  }

  public String getStem() {
    return stem;
  }

  public void setStem(String stem) {
    this.stem = stem;
  }

  public String getOptionA() {
    return optionA;
  }

  public void setOptionA(String optionA) {
    this.optionA = optionA;
  }

  public String getOptionB() {
    return optionB;
  }

  public void setOptionB(String optionB) {
    this.optionB = optionB;
  }

  public String getOptionC() {
    return optionC;
  }

  public void setOptionC(String optionC) {
    this.optionC = optionC;
  }

  public String getOptionD() {
    return optionD;
  }

  public void setOptionD(String optionD) {
    this.optionD = optionD;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }
}
