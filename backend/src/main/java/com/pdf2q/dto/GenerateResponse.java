package com.pdf2q.dto;

import com.pdf2q.domain.QuestionType;
import java.util.List;
import java.util.Map;

/** 出题接口响应：题目列表。 */
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

  /** 单道题目（含题型）。 */
  public static class QuestionDto {
    private QuestionType type;
    private String question;
    private Map<String, String> options;
    /** 单选/判断为 A；多选为排序后的 A,C */
    private String answer;
    private String explanation;

    public QuestionType getType() {
      return type;
    }

    public void setType(QuestionType type) {
      this.type = type;
    }

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
