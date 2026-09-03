# API 说明

基础地址：`http://localhost:3001`  
前端开发经 Vite 代理访问 `/api/*`。

除特别说明外，题库相关接口均需请求头：

```http
X-Owner-Token: <浏览器 localStorage 中的 UUID>
```

## GET `/api/health`

```json
{ "ok": true }
```

不需要 Owner Token。

## GET `/api/quiz-sets`

我的题库列表。

```json
[
  {
    "id": 1,
    "title": "第三章复习",
    "questionCount": 10,
    "createdAt": "2026-09-03T02:00:00Z",
    "answeredCount": 3,
    "currentIndex": 3,
    "finished": false
  }
]
```

## POST `/api/quiz-sets`

创建题库（按题型分别调 DeepSeek 出题并落库）。允许同一 PDF 重复创建。

```json
{
  "title": "第三章复习",
  "text": "PDF 抽出的文本",
  "singleCount": 5,
  "multipleCount": 3,
  "judgeCount": 2
}
```

| 字段 | 说明 |
|------|------|
| singleCount | 单选题数量 0–20 |
| multipleCount | 多选题数量 0–20 |
| judgeCount | 判断题数量 0–20 |

至少一种数量 &gt; 0。合计最多 60 题。题目顺序：单选 → 多选 → 判断。

多选答案形如 `"A,C"`（字母排序），须完全一致才得分。判断题选项为 A=对、B=错。

返回 `QuizSetDetail`（含 questions 与空 progress）。每题含 `type`: `single` | `multiple` | `judge`。

## GET `/api/quiz-sets/{id}`

题库详情 + 当前进度。

## PUT `/api/quiz-sets/{id}/progress`

保存进度。

```json
{
  "currentIndex": 2,
  "answers": [
    { "index": 0, "selected": "B", "correct": true },
    { "index": 1, "selected": "A", "correct": false }
  ]
}
```

## POST `/api/quiz-sets/{id}/progress/reset`

清空进度（重新作答）。

## DELETE `/api/quiz-sets/{id}`

删除题库及其进度。

## POST `/api/generate`（调试用）

仅出题不落库，请求体与创建题库类似：`{ "text", "singleCount", "multipleCount", "judgeCount" }`。正式流程请用 `POST /api/quiz-sets`。

## 错误格式

```json
{ "error": "说明" }
```
