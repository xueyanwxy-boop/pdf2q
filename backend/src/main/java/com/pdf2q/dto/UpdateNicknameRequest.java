package com.pdf2q.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 修改昵称请求。 */
public class UpdateNicknameRequest {

  @NotBlank(message = "昵称不能为空")
  @Size(min = 2, max = 16, message = "昵称长度为 2–16 字")
  private String nickname;

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
}
