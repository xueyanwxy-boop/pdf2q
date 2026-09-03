package com.pdf2q.service;

import com.pdf2q.dto.SyncResult;

/** 云端一键同步。 */
public interface CloudSyncService {

  /**
   * 将当前浏览器本地题库绑定到账号，并按「题库叠加、进度以云端为准」合并。
   *
   * @param ownerToken 当前设备弱登录 token
   * @param userId     云端用户 id
   */
  SyncResult sync(String ownerToken, Long userId);
}
