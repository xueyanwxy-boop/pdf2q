# pdf2q

把 PDF（教材、技术笔记等）转成四选一选择题，支持在线作答与即时反馈。

> 当前版本：本地可运行的前后端分离 MVP（无数据库）。  
> 仓库：https://github.com/xueyanwxy-boop/pdf2q（私有）

## 功能

- 浏览器上传 PDF，本地用 pdf.js 抽取文本（不上传原文件到 AI）
- 后端调用 DeepSeek 生成选择题（题干 / A-D / 答案 / 解析）
- 逐题作答、对错反馈、查看解析、汇总正确率
- API Key 只保存在后端，不进入前端代码

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + pdf.js |
| 后端 | Java 17 + Spring Boot 3 + Maven |
| AI | DeepSeek Chat API |

## 目录结构

```text
pdf2q/
├── README.md
├── docs/
│   ├── SETUP.md      # 环境与启动
│   └── API.md        # 接口说明
├── backend/          # Spring Boot
└── frontend/         # Vue
```

## 快速开始

详细步骤见 [docs/SETUP.md](docs/SETUP.md)。最短路径：

1. **配置 Key**（勿提交到 Git）

```powershell
copy backend\src\main\resources\application-local.yml.example `
     backend\src\main\resources\application-local.yml
```

编辑 `application-local.yml`，填入 `deepseek.api-key`。

2. **启动后端**（IDEA 运行 `Pdf2qApplication`，或）

```powershell
cd backend
mvn spring-boot:run
```

访问健康检查：http://localhost:3001/api/health

3. **启动前端**

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

打开：http://localhost:5173

> PowerShell 若报「禁止运行脚本」，请用 `npm.cmd` 代替 `npm`。

## 局域网给同事看

前端已开启 `host: true`。同一路由器下可用：

`http://<你的局域网IP>:5173`

本机后端需保持运行；必要时放行防火墙 5173 端口。

## 安全说明

- `application-local.yml`、`.env` 已在 `.gitignore` 中，**不要**把真实 Key 提交进仓库
- 示例配置见 `application-local.yml.example` / `.env.example`

## 已知限制（v0.1）

- 仅支持可选中文字的 PDF（扫描件 / 纯图片 OCR 未做）
- 无登录、无题库持久化；刷新页面题目会丢失
- 长文档会截断后再出题（见后端 `deepseek.max-text-chars`）

## 后续可做

- 文本 hash 去重缓存（SQLite / 数据库）
- 扫描件 OCR
- 部署到 Serverless（如 Cloudflare + 国内可访问 DB）
