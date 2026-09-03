# 环境搭建与启动

## 环境要求

| 工具 | 版本建议 |
|------|----------|
| JDK | 17+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| MySQL | 8.x（远程或本地） |

## 配置（必做）

```powershell
copy backend\src\main\resources\application-local.yml.example `
     backend\src\main\resources\application-local.yml
```

填写：

- `deepseek.api-key`
- `spring.datasource.url / username / password`

`application-local.yml` 已 gitignore，**不要提交真实密码**。

表结构见 [docs/schema.sql](../docs/schema.sql)。当前访问方式为 **MyBatis**（非 JPA）：`Dao 接口 → DaoImpl → Mapper XML`。


## 启动后端

IDEA 运行 `com.pdf2q.Pdf2qApplication`，或：

```powershell
cd backend
mvn spring-boot:run
```

健康检查：http://localhost:3001/api/health

## 启动前端

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

打开 http://localhost:5173

PowerShell 若禁止脚本，请用 `npm.cmd`。

## 弱登录说明

首次访问会在浏览器生成 `pdf2q_owner_token` 并长期保存在 localStorage。  
清缓存 / 换浏览器会变成「另一个用户」，题库列表会空——这是当前弱登录的预期行为。
