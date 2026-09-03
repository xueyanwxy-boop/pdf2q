package com.pdf2q.controller;

import com.pdf2q.dto.AuthResponse;
import com.pdf2q.dto.LoginRequest;
import com.pdf2q.dto.RegisterRequest;
import com.pdf2q.dto.SyncResult;
import com.pdf2q.dto.UpdateNicknameRequest;
import com.pdf2q.service.AuthService;
import com.pdf2q.service.CloudSyncService;
import com.pdf2q.web.AuthUserResolver;
import com.pdf2q.web.OwnerTokenResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 云端账号与同步接口。 */
@RestController
@RequestMapping("/api")
public class CloudController {

  private final AuthService authService;
  private final CloudSyncService cloudSyncService;
  private final AuthUserResolver authUserResolver;
  private final OwnerTokenResolver ownerTokenResolver;

  public CloudController(
      AuthService authService,
      CloudSyncService cloudSyncService,
      AuthUserResolver authUserResolver,
      OwnerTokenResolver ownerTokenResolver) {
    this.authService = authService;
    this.cloudSyncService = cloudSyncService;
    this.authUserResolver = authUserResolver;
    this.ownerTokenResolver = ownerTokenResolver;
  }

  /** 注册。POST /api/auth/register */
  @PostMapping("/auth/register")
  public AuthResponse register(@Valid @RequestBody RegisterRequest body) {
    return authService.register(body);
  }

  /** 登录。POST /api/auth/login */
  @PostMapping("/auth/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest body) {
    return authService.login(body);
  }

  /** 退出登录（作废服务端 token）。POST /api/auth/logout */
  @PostMapping("/auth/logout")
  public Map<String, Object> logout(HttpServletRequest request) {
    authService.logout(authUserResolver.rawToken(request));
    return Map.of("ok", true);
  }

  /** 修改昵称。PUT /api/auth/nickname */
  @PutMapping("/auth/nickname")
  public Map<String, Object> updateNickname(
      HttpServletRequest request, @Valid @RequestBody UpdateNicknameRequest body) {
    var user = authUserResolver.require(request);
    String nickname = authService.updateNickname(user.getId(), body.getNickname());
    return Map.of("nickname", nickname);
  }

  /** 一键同步。POST /api/cloud/sync */
  @PostMapping("/cloud/sync")
  public SyncResult sync(HttpServletRequest request) {
    var user = authUserResolver.require(request);
    String ownerToken = ownerTokenResolver.require(request);
    return cloudSyncService.sync(ownerToken, user.getId());
  }
}
