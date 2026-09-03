package com.pdf2q.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

/**
 * 弱登录：从请求头读取 {@code X-Owner-Token}。
 * <p>该 token 由前端写入 localStorage，用于隔离「我的题库」。
 */
@Component
public class OwnerTokenResolver {

  /** 弱登录身份请求头名称。 */
  public static final String HEADER = "X-Owner-Token";

  /**
   * 读取并校验 Owner Token；缺失或过长则抛 400。
   *
   * @return 修剪后的 token 字符串
   */
  public String require(HttpServletRequest request) {
    String token = request.getHeader(HEADER);
    if (!StringUtils.hasText(token) || token.length() > 64) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Missing or invalid header " + HEADER);
    }
    return token.trim();
  }
}
