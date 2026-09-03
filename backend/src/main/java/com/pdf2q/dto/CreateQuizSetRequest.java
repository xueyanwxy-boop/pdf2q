package com.pdf2q.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 创建题库请求体：名称 + 文本 + 各题型数量（每种 0–20，至少一种 &gt; 0）。 */
public class CreateQuizSetRequest {

  @NotBlank
  @Size(max = 200)
  private String title;

  @NotBlank
  private String text;

  /** 单选题数量，0–20 */
  @Min(0)
  @Max(20)
  private Integer singleCount = 0;

  /** 多选题数量，0–20 */
  @Min(0)
  @Max(20)
  private Integer multipleCount = 0;

  /** 判断题数量，0–20 */
  @Min(0)
  @Max(20)
  private Integer judgeCount = 0;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Integer getSingleCount() {
    return singleCount;
  }

  public void setSingleCount(Integer singleCount) {
    this.singleCount = singleCount;
  }

  public Integer getMultipleCount() {
    return multipleCount;
  }

  public void setMultipleCount(Integer multipleCount) {
    this.multipleCount = multipleCount;
  }

  public Integer getJudgeCount() {
    return judgeCount;
  }

  public void setJudgeCount(Integer judgeCount) {
    this.judgeCount = judgeCount;
  }
}
