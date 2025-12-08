# 后端认证接口规范（Android 客户端对接）

## 概览

- 基础地址（Base URL）：由服务端提供，客户端在 `backend/ApiConfig.kt` 中配置。
  - 开发（Android 模拟器）：`http://10.0.2.2:8080`（d:\homework_for_cs\Software Engineering\final project\RideApp\app\src\main\java\com\example\rideflow\backend\ApiConfig.kt:5）
  - 局域网调试：`http://<PC_IP>:<PORT>`
  - 线上：`https://<domain>`
- 路径：
  - 注册：`/api/v1/auth/register`（d:\homework_for_cs\Software Engineering\final project\RideApp\app\src\main\java\com\example\rideflow\backend\ApiConfig.kt:7）
  - 登录：`/api/v1/auth/login`（d:\homework_for_cs\Software Engineering\final project\RideApp\app\src\main\java\com\example\rideflow\backend\ApiConfig.kt:9）
- Content-Type：`application/json; charset=utf-8`
- 统一响应包：`ApiResponse<T>`（字段：`code`、`message`、`data`）

## 模型定义（客户端）

- 请求 DTO（d:\homework_for_cs\Software Engineering\final project\RideApp\app\src\main\java\com\example\rideflow\model\AuthModels.kt）
  - LoginRequest：
    ```json
    {
      "username": "string",
      "password": "string"
    }
    ```
  - RegisterRequest：
    ```json
    {
      "username": "string",
      "password": "string"
    }
    ```

- 响应 DTO（`ApiResponse<AuthResult>`）：
  - 包装结构（d:\homework_for_cs\Software Engineering\final project\RideApp\app\src\main\java\com\example\rideflow\model\ApiResponse.kt）
    ```json
    {
      "code": 0,
      "message": "ok",
      "data": { /* 见下 */ }
    }
    ```
  - `AuthResult`（d:\homework_for_cs\Software Engineering\final project\RideApp\app\src\main\java\com\example\rideflow\model\AuthModels.kt）
    ```json
    {
      "userId": 123456,
      "username": "string",
      "avatarUrl": "string|null",
      "token": "string"
    }
    ```

## API 说明

### 1. 用户注册

- Method：`POST`
- URL：`{BASE_URL}/api/v1/auth/register`
- Request Body：`RegisterRequest`
  ```json
  {
    "username": "user_or_email",
    "password": "******"
  }
  ```
- Response：`ApiResponse<AuthResult>`
  - 成功示例：
    ```json
    {
      "code": 0,
      "message": "ok",
      "data": {
        "userId": 1,
        "username": "user_or_email",
        "avatarUrl": null,
        "token": "jwt-token"
      }
    }
    ```
  - 失败示例：
    ```json
    {
      "code": 400,
      "message": "username already exists",
      "data": null
    }
    ```

### 2. 用户登录

- Method：`POST`
- URL：`{BASE_URL}/api/v1/auth/login`
- Request Body：`LoginRequest`
  ```json
  {
    "username": "user_or_email",
    "password": "******"
  }
  ```
- Response：`ApiResponse<AuthResult>`（成功/失败示例同注册）

## 客户端行为与契约

- 客户端调用位置：`AuthRepository`（d:\homework_for_cs\Software Engineering\final project\RideApp\app\src\main\java\com\example\rideflow\auth\AuthRepository.kt:27–49,57–83）
  - 使用 `AuthApi` 进行 HTTP 调用（d:\homework_for_cs\Software Engineering\final project\RideApp\app\src\main\java\com\example\rideflow\backend\AuthApi.kt）
  - 成功判断：当前客户端以 `data != null` 作为成功标志；`code` 未参与判断；错误文案取 `message`
  - 映射：`AuthResult` → `UserData`（昵称取 `username`，邮箱暂为 `""`）
- 注册字段来源：UI 有邮箱与昵称；仓库层将 `username` 取值为 `email` 非空优先，否则使用 `nickname`
- Token：接口返回 `token`，当前客户端未在后续请求中使用；后端可正常返回，但不影响当前功能

## 服务端实现建议

- HTTP 状态与 `code`：
  - 建议使用标准 HTTP 状态码（200/400/401 等），同时 `code` 以 0 表示成功，非 0 表示失败
  - 失败时将 `data` 置为 `null`，并提供明确的 `message`
- 安全：
  - 登录失败统一返回 401 + 业务 `code`（如 1001），`message` 提示不暴露具体原因
  - 注册时校验用户名（或邮箱）唯一性，失败返回 400 + 业务 `code`
- 序列化：JSON 字段名与大小写严格遵循上述 DTO
- 头像地址：`avatarUrl` 为可选字符串，返回完整可访问 URL
- 生产环境建议使用 HTTPS；如使用明文 HTTP，Android 9+ 需在客户端 `AndroidManifest` 的 `<application>` 里配置 `android:usesCleartextTraffic="true"`

## 网络与环境

- Android 模拟器访问宿主机地址：`10.0.2.2`
- 客户端依赖网络权限已声明：`uses-permission android:name="android.permission.INTERNET"`（d:\homework_for_cs\Software Engineering\final project\RideApp\app\src\main\AndroidManifest.xml:11）
- 客户端使用 OkHttp 发起 JSON 请求（d:\homework_for_cs\Software Engineering\final project\RideApp\app\src\main\java\com\example\rideflow\utils\HttpClient.kt）

## cURL 示例

```bash
# 注册
curl -X POST "{BASE_URL}/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"test@example.com","password":"123456"}'

# 登录
curl -X POST "{BASE_URL}/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"test@example.com","password":"123456"}'
```

## 扩展建议（可选）

- 若需在注册时区分邮箱与昵称：
  - 将 `RegisterRequest` 扩展为：`{"email":"string","nickname":"string","password":"string"}`
  - 客户端与服务端同时更新字段契约与校验逻辑

## 版本

- v1.0（认证模块）

