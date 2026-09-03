package com.pdf2q.service.impl;

import com.pdf2q.dao.UserDao;
import com.pdf2q.domain.User;
import com.pdf2q.domain.UserToken;
import com.pdf2q.dto.AuthResponse;
import com.pdf2q.dto.LoginRequest;
import com.pdf2q.dto.RegisterRequest;
import com.pdf2q.service.AuthService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

  private static final int TOKEN_DAYS = 30;

  private final UserDao userDao;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public AuthServiceImpl(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    String phone = request.getPhone().trim();
    if (userDao.selectByPhone(phone) != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "该手机号已注册");
    }
    User user = new User();
    user.setPhone(phone);
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    user.setNickname(request.getNickname().trim());
    user.setCreatedAt(Instant.now());
    userDao.insert(user);
    return issueToken(user);
  }

  @Override
  @Transactional
  public AuthResponse login(LoginRequest request) {
    User user = userDao.selectByPhone(request.getPhone().trim());
    if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "手机号或密码错误");
    }
    return issueToken(user);
  }

  @Override
  @Transactional
  public void logout(String token) {
    if (StringUtils.hasText(token)) {
      userDao.deleteToken(token);
    }
  }

  @Override
  @Transactional
  public String updateNickname(Long userId, String nickname) {
    String trimmed = nickname == null ? "" : nickname.trim();
    if (!StringUtils.hasText(trimmed) || trimmed.length() < 2 || trimmed.length() > 16) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "昵称长度为 2–16 字");
    }
    User user = userDao.selectById(userId);
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录云端账号");
    }
    userDao.updateNickname(userId, trimmed);
    return trimmed;
  }

  @Override
  public User resolveUser(String rawAuthorizationHeader) {
    String token = extractBearer(rawAuthorizationHeader);
    if (token == null) {
      return null;
    }
    UserToken found = userDao.selectToken(token);
    if (found == null) {
      return null;
    }
    return userDao.selectById(found.getUserId());
  }

  private AuthResponse issueToken(User user) {
    String token = UUID.randomUUID().toString().replace("-", "");
    UserToken row = new UserToken();
    row.setToken(token);
    row.setUserId(user.getId());
    row.setExpiresAt(Instant.now().plus(TOKEN_DAYS, ChronoUnit.DAYS));
    userDao.insertToken(row);
    return new AuthResponse(token, user.getId(), user.getPhone(), user.getNickname());
  }

  public static String extractBearer(String header) {
    if (!StringUtils.hasText(header)) {
      return null;
    }
    String value = header.trim();
    if (value.regionMatches(true, 0, "Bearer ", 0, 7)) {
      String token = value.substring(7).trim();
      return token.isEmpty() ? null : token;
    }
    return null;
  }
}
