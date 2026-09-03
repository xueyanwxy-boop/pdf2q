package com.pdf2q.dto;

/** 登录/注册成功响应。 */
public class AuthResponse {

  private String token;
  private Long userId;
  private String phone;
  private String nickname;

  public AuthResponse() {
  }

  public AuthResponse(String token, Long userId, String phone, String nickname) {
    this.token = token;
    this.userId = userId;
    this.phone = phone;
    this.nickname = nickname;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
}
