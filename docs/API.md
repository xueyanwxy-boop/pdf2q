# API 说明

后端基础地址（本地）：`http://localhost:3001`  
前端开发时请走 Vite 代理：`/api/...` → `http://localhost:3001/api/...`

## GET `/api/health`

健康检查。

**响应示例**

```json
{ "ok": true }
```

## POST `/api/generate`

根据纯文本生成四选一选择题。

### 请求体

```json
{
  "text": "从 PDF 提取出的学习材料全文",
  "count": 10
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| text | string | 是 | PDF 抽出的文本，不能为空 |
| count | number | 否 | 题目数量，默认 10，范围 1–20 |

### 成功响应

```json
{
  "questions": [
    {
      "question": "题干",
      "options": {
        "A": "选项A",
        "B": "选项B",
        "C": "选项C",
        "D": "选项D"
      },
      "answer": "B",
      "explanation": "依据原文……"
    }
  ]
}
```

### 错误响应

HTTP 状态码非 2xx 时：

```json
{ "error": "错误说明" }
```

常见情况：

| 状态码 | 含义 |
|--------|------|
| 400 | 参数校验失败（如 text 为空） |
| 500 | 未配置 DeepSeek Key 等服务端配置问题 |
| 502 | 调用 DeepSeek 失败或模型返回无法解析 |

## 设计说明

- PDF 解析在**浏览器**完成；后端只接收文本，降低带宽与隐私风险
- 过长文本会在后端按 `deepseek.max-text-chars`（默认 60000）截断后再送模型
- 当前无鉴权、无限流；仅适合受信局域网 / 本机调试
