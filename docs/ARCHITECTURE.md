# 架构说明（v0.2）

## 流程

```text
打开站点 → 我的题库（按 X-Owner-Token 隔离）
添加问答 → PDF 浏览器抽字 → DeepSeek 出题 → MySQL 落库
答题 / 退出 → 写入 quiz_progress
继续 → 读取 progress 从断点恢复
重新作答 → 清空 progress
```

## 分层（后端）

```text
Controller
  → Service 接口 / ServiceImpl
    → Dao 接口 / DaoImpl
      → Mapper 接口 + Mapper XML（真正 SQL）
```


- 身份 = `owner_token`（前端生成，长期存 localStorage）
- 不做 IP 识别（同网段会串号）
- 后续可在同一 token 上绑定用户名/密码

## 表

| 表 | 用途 |
|----|------|
| quiz_sets | 题库元数据 |
| questions | 题目 |
| quiz_progress | 每用户每套题的进度与已答 JSON |

当前不存 PDF 原文，只存题目。

## 本机部署注意

前端 `localhost:5173` 代理到本机 `3001`。仅本机使用时无需改 API 基址。  
若以后局域网分享，需让后端对局域网可访问，并调整前端 API 地址。
