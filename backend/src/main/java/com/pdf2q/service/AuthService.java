package com.pdf2q.service;

import com.pdf2q.dto.AuthResponse;
import com.pdf2q.dto.LoginRequest;
import com.pdf2q.dto.RegisterRequest;
import com.pdf2q.domain.User;

/** 云端账号：注册 / 登录 / 令牌校验。 */
public interface AuthService {

  AuthResponse register(RegisterRequest request);

  AuthResponse login(LoginRequest request);

  void logout(String token);

  /** 修改昵称；允许重名。返回更新后的昵称。 */
  String updateNickname(Long userId, String nickname);

  /** 根据 Bearer token 解析用户；无效返回 null。 */
  User resolveUser(String rawAuthorizationHeader);
}
