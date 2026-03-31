# User 用户管理接口文档

> 项目：Spring Boot Demo  
> 版本：v1.0.0  
> 基础地址：`http://127.0.0.1:8081`  
> 数据格式：`application/json`  
> 更新时间：2026-03-31

---

## 目录

- [数据模型](#数据模型)
- [统一响应说明](#统一响应说明)
- [接口列表](#接口列表)
  - [1. 新增用户](#1-新增用户)
  - [2. 查询全部用户](#2-查询全部用户)
  - [3. 根据 ID 查询用户](#3-根据-id-查询用户)
  - [4. 修改用户](#4-修改用户)
  - [5. 删除用户](#5-删除用户)
- [数据库表结构](#数据库表结构)
- [错误码说明](#错误码说明)

---

## 数据模型

### User 用户对象

| 字段名       | 类型     | 说明                        | 是否必填（请求） |
|------------|--------|---------------------------|----------|
| id         | Long   | 用户 ID，自增主键                | 否（由数据库生成）|
| username   | String | 用户名，最大 50 字符              | 是        |
| password   | String | 密码，最大 50 字符               | 是        |
| email      | String | 邮箱地址，最大 100 字符            | 是        |
| createTime | String | 创建时间，格式 `yyyy-MM-ddTHH:mm:ss` | 否（由服务端生成）|

**示例：**

```json
{
  "id": 1,
  "username": "zhangsan",
  "password": "123456",
  "email": "zhangsan@example.com",
  "createTime": "2026-03-31T10:00:00"
}
```

---

## 统一响应说明

| HTTP 状态码 | 说明                   |
|-----------|----------------------|
| 200       | 请求成功                 |
| 201       | 创建成功                 |
| 204       | 删除成功，无返回体            |
| 500       | 服务器错误（如用户不存在等业务异常）   |

> 当发生业务异常时，响应体为纯文本错误信息，例如：`用户不存在，id: 999`

---

## 接口列表

---

### 1. 新增用户

**接口描述：** 创建一个新用户，服务端自动填充 `createTime`。

| 项目   | 内容           |
|------|--------------|
| 请求方法 | `POST`       |
| 请求路径 | `/users`     |
| 请求格式 | `application/json` |
| 响应格式 | `application/json` |

#### 请求体参数

| 字段名      | 类型     | 是否必填 | 说明      |
|----------|--------|------|---------|
| username | String | 是    | 用户名     |
| password | String | 是    | 密码      |
| email    | String | 是    | 邮箱地址    |

#### 请求示例

```http
POST /users HTTP/1.1
Host: 127.0.0.1:8081
Content-Type: application/json

{
  "username": "zhangsan",
  "password": "123456",
  "email": "zhangsan@example.com"
}
```

**cURL 示例：**

```bash
curl -X POST http://127.0.0.1:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhangsan",
    "password": "123456",
    "email": "zhangsan@example.com"
  }'
```

#### 响应示例

**成功 `201 Created`：**

```json
{
  "id": 1,
  "username": "zhangsan",
  "password": "123456",
  "email": "zhangsan@example.com",
  "createTime": "2026-03-31T10:00:00"
}
```

---

### 2. 查询全部用户

**接口描述：** 获取数据库中所有用户列表，无数据时返回空数组。

| 项目   | 内容           |
|------|--------------|
| 请求方法 | `GET`        |
| 请求路径 | `/users`     |
| 请求格式 | 无            |
| 响应格式 | `application/json` |

#### 请求示例

```http
GET /users HTTP/1.1
Host: 127.0.0.1:8081
```

**cURL 示例：**

```bash
curl http://127.0.0.1:8081/users
```

#### 响应示例

**成功 `200 OK`（有数据）：**

```json
[
  {
    "id": 1,
    "username": "zhangsan",
    "password": "123456",
    "email": "zhangsan@example.com",
    "createTime": "2026-03-31T10:00:00"
  },
  {
    "id": 2,
    "username": "lisi",
    "password": "654321",
    "email": "lisi@example.com",
    "createTime": "2026-03-31T11:00:00"
  }
]
```

**成功 `200 OK`（无数据）：**

```json
[]
```

---

### 3. 根据 ID 查询用户

**接口描述：** 根据用户 ID 查询单个用户信息。

| 项目   | 内容                  |
|------|---------------------|
| 请求方法 | `GET`               |
| 请求路径 | `/users/{id}`       |
| 请求格式 | 无                   |
| 响应格式 | `application/json`  |

#### 路径参数

| 参数名 | 类型   | 是否必填 | 说明      |
|-----|------|------|---------|
| id  | Long | 是    | 用户的主键 ID |

#### 请求示例

```http
GET /users/1 HTTP/1.1
Host: 127.0.0.1:8081
```

**cURL 示例：**

```bash
curl http://127.0.0.1:8081/users/1
```

#### 响应示例

**成功 `200 OK`：**

```json
{
  "id": 1,
  "username": "zhangsan",
  "password": "123456",
  "email": "zhangsan@example.com",
  "createTime": "2026-03-31T10:00:00"
}
```

**失败 `500 Internal Server Error`（用户不存在）：**

```
用户不存在，id: 1
```

---

### 4. 修改用户

**接口描述：** 根据用户 ID 修改用户信息，`createTime` 不会被更新。

| 项目   | 内容                  |
|------|---------------------|
| 请求方法 | `PUT`               |
| 请求路径 | `/users/{id}`       |
| 请求格式 | `application/json`  |
| 响应格式 | `application/json`  |

#### 路径参数

| 参数名 | 类型   | 是否必填 | 说明        |
|-----|------|------|-----------|
| id  | Long | 是    | 要修改的用户 ID |

#### 请求体参数

| 字段名      | 类型     | 是否必填 | 说明   |
|----------|--------|------|------|
| username | String | 是    | 新用户名 |
| password | String | 是    | 新密码  |
| email    | String | 是    | 新邮箱  |

#### 请求示例

```http
PUT /users/1 HTTP/1.1
Host: 127.0.0.1:8081
Content-Type: application/json

{
  "username": "zhangsan_new",
  "password": "newpass",
  "email": "new@example.com"
}
```

**cURL 示例：**

```bash
curl -X PUT http://127.0.0.1:8081/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhangsan_new",
    "password": "newpass",
    "email": "new@example.com"
  }'
```

#### 响应示例

**成功 `200 OK`：**

```json
{
  "id": 1,
  "username": "zhangsan_new",
  "password": "newpass",
  "email": "new@example.com",
  "createTime": "2026-03-31T10:00:00"
}
```

**失败 `500 Internal Server Error`（用户不存在）：**

```
用户不存在，id: 1
```

---

### 5. 删除用户

**接口描述：** 根据用户 ID 删除用户，删除成功后无响应体。

| 项目   | 内容              |
|------|-----------------|
| 请求方法 | `DELETE`        |
| 请求路径 | `/users/{id}`   |
| 请求格式 | 无               |
| 响应格式 | 无               |

#### 路径参数

| 参数名 | 类型   | 是否必填 | 说明        |
|-----|------|------|-----------|
| id  | Long | 是    | 要删除的用户 ID |

#### 请求示例

```http
DELETE /users/1 HTTP/1.1
Host: 127.0.0.1:8081
```

**cURL 示例：**

```bash
curl -X DELETE http://127.0.0.1:8081/users/1
```

#### 响应示例

**成功 `204 No Content`：**

> 响应体为空。

---

## 数据库表结构

```sql
CREATE TABLE user (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username    VARCHAR(50)  COMMENT '用户名',
    password    VARCHAR(50)  COMMENT '密码',
    email       VARCHAR(100) COMMENT '邮箱',
    create_time DATETIME     COMMENT '创建时间'
);
```

---

## 错误码说明

| HTTP 状态码 | 场景描述                         | 响应体示例                 |
|-----------|------------------------------|----------------------|
| 201       | 新增用户成功                       | 返回完整用户 JSON 对象        |
| 204       | 删除用户成功                       | 无响应体                  |
| 200       | 查询 / 修改成功                    | 返回完整用户 JSON 对象        |
| 500       | 查询或修改时用户不存在，或其他服务端运行时异常 | `用户不存在，id: {id}` 纯文本 |

---

## 快速测试脚本

以下脚本可在终端中顺序执行，完整验证所有接口：

```bash
# 1. 新增用户
curl -X POST http://127.0.0.1:8081/users \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan","password":"123456","email":"zhangsan@example.com"}'

# 2. 查询全部用户
curl http://127.0.0.1:8081/users

# 3. 根据 ID 查询（替换 {id} 为实际 ID）
curl http://127.0.0.1:8081/users/1

# 4. 修改用户（替换 {id} 为实际 ID）
curl -X PUT http://127.0.0.1:8081/users/1 \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan_new","password":"newpass","email":"new@example.com"}'

# 5. 删除用户（替换 {id} 为实际 ID）
curl -X DELETE http://127.0.0.1:8081/users/1
```

---

*文档由 Spring Boot Demo 项目自动整理生成*