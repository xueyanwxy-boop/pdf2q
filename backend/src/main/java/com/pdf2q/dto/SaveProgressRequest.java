package com.pdf2q.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/** 保存答题进度请求体：当前题号 + 已答列表。 */
public class SaveProgressRequest {

  @Min(0)
  private int currentIndex;

  @NotNull
  @Valid
  private List<AnswerItem> answers = new ArrayList<>();

  public int getCurrentIndex() {
    return currentIndex;
  }

  public void setCurrentIndex(int currentIndex) {
    this.currentIndex = currentIndex;
  }

  public List<AnswerItem> getAnswers() {
    return answers;
  }

  public void setAnswers(List<AnswerItem> answers) {
    this.answers = answers;
  }

  /** 单题作答记录。 */
  public static class AnswerItem {
    @Min(0)
    private int index;

    @NotNull
    private String selected;

    private boolean correct;

    public int getIndex() {
      return index;
    }

    public void setIndex(int index) {
      this.index = index;
    }

    public String getSelected() {
      return selected;
    }

    public void setSelected(String selected) {
      this.selected = selected;
    }

    public boolean isCorrect() {
      return correct;
    }

    public void setCorrect(boolean correct) {
      this.correct = correct;
    }
  }
}
