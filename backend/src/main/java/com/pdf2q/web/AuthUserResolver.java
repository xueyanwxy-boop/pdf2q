package com.pdf2q.web;

import com.pdf2q.domain.User;
import com.pdf2q.service.AuthService;
import com.pdf2q.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** 解析可选/必选的云端登录用户。 */
@Component
public class AuthUserResolver {

  private final AuthService authService;

  public AuthUserResolver(AuthService authService) {
    this.authService = authService;
  }

  /** 已登录返回用户，未登录返回 null。 */
  public User optional(HttpServletRequest request) {
    return authService.resolveUser(request.getHeader("Authorization"));
  }

  /** 必须登录。 */
  public User require(HttpServletRequest request) {
    User user = optional(request);
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录云端账号");
    }
    return user;
  }

  public String rawToken(HttpServletRequest request) {
    return AuthServiceImpl.extractBearer(request.getHeader("Authorization"));
  }
}
