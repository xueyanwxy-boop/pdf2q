# pdf2q

把 PDF 转成选择题，支持个人题库、弱登录与答题进度续答。

## 功能

- 首页即「我的题库」；右上角添加问答
- 填写名称 → 导入 PDF → DeepSeek 出题并入库
- 列表显示作答进度；可继续 / 重新作答（清空进度）/ 删除
- 答题中可「退出答题」，进度写入 MySQL，下次接着做
- 弱登录：浏览器 `localStorage` 中的 `ownerToken`（请求头 `X-Owner-Token`）

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Vue Router + pdf.js |
| 后端 | Java 17 + Spring Boot 3 + MyBatis |
| 数据库 | MySQL |
| AI | DeepSeek Chat API |

## 快速开始

见 [docs/SETUP.md](docs/SETUP.md)。本地最短路径：

1. 复制并填写 `backend/src/main/resources/application-local.yml`（DeepSeek Key + MySQL）
2. IDEA 运行 `Pdf2qApplication`（端口 3001）
3. `cd frontend && npm.cmd install && npm.cmd run dev` → http://localhost:5173

## 文档

- [环境与启动](docs/SETUP.md)
- [API](docs/API.md)
- [架构](docs/ARCHITECTURE.md)
