# 环境搭建与启动

## 环境要求

| 工具 | 版本建议 | 用途 |
|------|----------|------|
| JDK | 17+ | 后端 |
| Maven | 3.9+ | 后端构建 |
| Node.js | 18+ | 前端 |
| IntelliJ IDEA | 任意较新版 | 推荐跑后端 |

本机已验证过：Java 17、Maven 3.9、Node 可用。

## 1. 获取代码

```powershell
git clone https://github.com/xueyanwxy-boop/pdf2q.git
cd pdf2q
```

## 2. 配置 DeepSeek Key

复制示例文件：

```powershell
copy backend\src\main\resources\application-local.yml.example `
     backend\src\main\resources\application-local.yml
```

内容示例：

```yaml
deepseek:
  api-key: sk-你的密钥
```

也可在 IDEA Run Configuration 里设置环境变量 `DEEPSEEK_API_KEY`（会覆盖空默认值）。

**不要**把带真实 Key 的 `application-local.yml` 提交到 Git。

## 3. 启动后端

### 方式 A：IntelliJ IDEA（推荐）

1. 打开 `backend` 目录（或打开 `pom.xml`）
2. 等待 Maven 依赖下载完成
3. 运行 `com.pdf2q.Pdf2qApplication`
4. 控制台出现端口 `3001` 即成功

### 方式 B：命令行

```powershell
cd backend
mvn spring-boot:run
```

验证：

```powershell
curl http://localhost:3001/api/health
```

期望返回：`{"ok":true}`

## 4. 启动前端

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

浏览器打开：http://localhost:5173

Vite 会把 `/api/*` 代理到 `http://localhost:3001`。

### PowerShell 执行策略报错

若出现「无法加载 npm.ps1 / 禁止运行脚本」：

```powershell
npm.cmd run dev
```

或（当前用户永久放开）：

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

## 5. 局域网访问

1. 确认 `frontend/vite.config.js` 中 `server.host` 为 `true`（已默认开启）
2. 重启前端后，终端会打印 Network 地址，例如 `http://172.16.x.x:5173`
3. 同一路由器下的设备用该地址访问
4. 若打不开：检查 Windows 防火墙是否放行 5173；双方需同一网段且无 AP 隔离

## 6. 常见问题

| 现象 | 处理 |
|------|------|
| 生成失败，提示未设置 Key | 检查 `application-local.yml` 是否存在且 `api-key` 正确，重启后端 |
| PDF 提示抽不到字 | 换可选中文字的 PDF；扫描件暂不支持 |
| 本机能开、别人 IP 打不开 | 重启前端；检查 host / 防火墙 / 是否同一局域网 |
| 前端 API 404 | 确认后端已在 3001 运行 |

## 端口一览

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:5173 |
| 后端 | http://localhost:3001 |
| 健康检查 | http://localhost:3001/api/health |
