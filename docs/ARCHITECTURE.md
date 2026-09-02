# 架构说明（v0.1）

## 总体流程

```text
用户上传 PDF
    ↓
浏览器 pdf.js 抽取纯文本
    ↓
POST /api/generate  { text, count }
    ↓
Spring Boot 后端拼装 Prompt
    ↓
DeepSeek Chat API
    ↓
校验并返回 questions[]
    ↓
Vue 答题页：选题 → 反馈 → 解析 → 计分
```

## 模块职责

| 模块 | 职责 | 不负责 |
|------|------|--------|
| frontend | 上传、抽字、答题 UI | 保存 API Key、调 DeepSeek |
| backend | 藏 Key、调模型、规范化 JSON | PDF 二进制解析 |
| DeepSeek | 根据文本出题 | 持久化 |

## 为何前后端分离

- Key 不能进前端打包产物
- 前端可继续用 Vue 做交互；后端用 Java，方便 IDEA 开发与后续接库

## 配置

| 配置项 | 位置 | 说明 |
|--------|------|------|
| deepseek.api-key | application-local.yml 或环境变量 | 必填 |
| deepseek.base-url | application.yml | 默认官方地址 |
| deepseek.model | application.yml | 默认 deepseek-chat |
| deepseek.max-text-chars | application.yml | 送模型的最大字符数 |
| server.port | application.yml | 默认 3001 |

## 与下一版的关系

下一版若要做「一次解析、永久复用」，建议在后端增加：

1. 对规范化文本做 hash
2. 命中则直接读库中的 questions
3. 未命中再调 DeepSeek 并落库

当前版本有意不做数据库，先把闭环跑通。
