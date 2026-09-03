package com.pdf2q.service.impl;

import com.pdf2q.dao.QuizProgressDao;
import com.pdf2q.dao.QuizSetDao;
import com.pdf2q.dto.SyncResult;
import com.pdf2q.service.CloudSyncService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 同步策略：
 * <ul>
 *   <li>本地未绑定题库 → 挂到当前 userId（叠加进账号）</li>
 *   <li>进度：若账号侧已有同题库进度，丢掉仅本地的进度（云端为准）；否则把本地进度绑到账号</li>
 * </ul>
 */
@Service
public class CloudSyncServiceImpl implements CloudSyncService {

  private final QuizSetDao quizSetDao;
  private final QuizProgressDao quizProgressDao;

  public CloudSyncServiceImpl(QuizSetDao quizSetDao, QuizProgressDao quizProgressDao) {
    this.quizSetDao = quizSetDao;
    this.quizProgressDao = quizProgressDao;
  }

  @Override
  @Transactional
  public SyncResult sync(String ownerToken, Long userId) {
    // 云端已有进度优先：删掉「同题库上仅本地、且云端已有进度」的本地进度行
    quizProgressDao.deleteOwnerProgressWhenUserExists(ownerToken, userId);
    // 其余本地进度绑到账号
    quizProgressDao.bindUserForOwner(ownerToken, userId);
    // 本地题库绑到账号（叠加）
    int migrated = quizSetDao.bindUserForOwner(ownerToken, userId);
    int total = quizSetDao.selectByUserId(userId).size();
    return new SyncResult(
        migrated,
        total,
        "同步完成：新绑定 " + migrated + " 套本地题库，账号共 " + total + " 套");
  }
}
