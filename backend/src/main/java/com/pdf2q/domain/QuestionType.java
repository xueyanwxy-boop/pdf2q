package com.pdf2q.domain;

/** 题目类型。 */
public enum QuestionType {
  /** 单选题（四选一） */
  single,
  /** 多选题（选项完全一致才得分） */
  multiple,
  /** 判断题（对 / 错） */
  judge
}
