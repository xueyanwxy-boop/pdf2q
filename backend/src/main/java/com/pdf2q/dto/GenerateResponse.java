package com.pdf2q.dto;

import java.util.List;
import java.util.Map;

public class GenerateResponse {

  private List<QuestionDto> questions;

  public GenerateResponse() {
  }

  public GenerateResponse(List<QuestionDto> questions) {
    this.questions = questions;
  }

  public List<QuestionDto> getQuestions() {
    return questions;
  }

  public void setQuestions(List<QuestionDto> questions) {
    this.questions = questions;
  }

  public static class QuestionDto {
    private String question;
    private Map<String, String> options;
    private String answer;
    private String explanation;

    public String getQuestion() {
      return question;
    }

    public void setQuestion(String question) {
      this.question = question;
    }

    public Map<String, String> getOptions() {
      return options;
    }

    public void setOptions(Map<String, String> options) {
      this.options = options;
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
}
