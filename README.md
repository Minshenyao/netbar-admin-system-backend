# 基于SpringBoot3的网吧计时管理系统

## 项目介绍

本项目是一个基于SpringBoot3开发的网吧计时管理系统，作为课程设计作品。系统采用前后端分离架构，实现了用户管理、电脑管理、计时收费、充值管理等核心功能。项目遵循软件工程规范，采用标准的多层架构设计。

### 主要功能
- 用户管理（普通用户、包月用户、包年用户、VIP用户）
- 电脑管理（电脑状态监控、分配管理）
- 上机记录管理（计时收费、使用统计）
- 充值记录管理（余额充值、消费记录）
- 权限管理系统（基于JWT的身份认证和权限控制）

### 计费规则
- 基础计费：0.1元/分钟
- 智能计费：系统会根据用户余额自动计算可上机时长
- 自动下机：到达预计下机时间后，如果用户未主动下机，系统将自动执行下机操作
- 余额预警：上机前会检查用户余额，确保有足够的余额支持上机时长

例如：用户余额为10元时，系统会自动计算出可上机100分钟（约1小时40分钟），到时间后自动下机。

## 技术栈
- 后端框架：Spring Boot 3.5.0
- 数据持久层：Spring Data JPA
- 数据库：MySQL 9.3
- 安全认证：JWT (JSON Web Token)
- 开发语言：Java 17
- 构建工具：Maven 3.9.9

## 开发环境要求
- JDK 17+
- MySQL 9.3+
- Maven 3.9.9+
- IDE推荐：IntelliJ IDEA 2023.1+

## 快速开始

### 1. 克隆仓库
```bash
git clone https://github.com/Minshenyao/netbar-admin-system-backend.git
cd netbar-admin-system-backend
```

### 2. 配置项目

#### 2.1 配置文件说明
项目使用YAML格式的配置文件，主要配置文件位于 `src/main/resources/` 目录下：
- `application.yml.example`: 示例配置文件，包含所有需要的配置项
- `application.yml`: 实际运行的配置文件（需要自行创建）

#### 2.2 创建配置文件
1. 复制示例配置文件创建实际配置文件：
```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

2. 修改 `application.yml` 中的配置项：
```yaml
spring:
  application:
    name: netbarAdminSystem
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/netbar_db    # 数据库连接URL
    username: your_username                        # 数据库用户名
    password: your_password                        # 数据库密码
#  jpa:
#    hibernate:
#      ddl-auto: create                           # 首次运行时可以设置为create，之后改为update

jwt:
  secret: your_jwt_secret_key                     # JWT加密密钥（建议使用32位随机字符串）
  expiration: 86400000                            # JWT过期时间（毫秒，默认24小时）

server:
  port: 8080                                      # 服务器端口号
```

### 3. 创建数据库
```sql
CREATE DATABASE netbar_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 运行项目
1. 安装项目依赖
```bash
# 清理并安装依赖
mvn clean install
```

2. 启动项目
```bash
# 运行Spring Boot应用
mvn spring-boot:run
```

注意：首次运行时，Maven会自动下载所有依赖包，可能需要一些时间。

## 初始账号

系统初始化后会自动创建管理员账号:

- 用户名: admin
- 密码: admin123

请在首次登录后及时修改密码以确保系统安全。

## 数据库设计

### 用户表（users）
| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR | 用户名 |
| password | VARCHAR | 密码 |
| identity | VARCHAR | 身份证号 |
| phone | VARCHAR | 电话号码 |
| balance | DOUBLE | 账户余额 |
| status | INT | 状态（1-正常，0-禁用）|
| created_at | BIGINT | 创建时间 |
| updated_at | BIGINT | 更新时间 |
| permission | INT | 用户权限（99-管理员，0-普通用户，1-包月，2-包年，3-VIP）|

### 电脑表（computers）
| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | BIGINT | 主键 |
| computer_number | VARCHAR | 电脑编号 |
| status | INT | 状态（0-关机，1-开机，2-维修中）|
| current_user_id | BIGINT | 当前使用用户ID |
| location | VARCHAR | 位置信息 |
| created_at | BIGINT | 创建时间 |
| updated_at | BIGINT | 更新时间 |

### 上机记录表（operation_records）
| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| computer_id | BIGINT | 电脑ID |
| start_time | BIGINT | 开始时间 |
| end_time | BIGINT | 结束时间 |
| cost | DOUBLE | 消费金额 |

### 充值记录表（deposit_records）
| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| amount | DOUBLE | 充值金额 |
| created_at | BIGINT | 充值时间 |
| operator_id | BIGINT | 操作员ID |

## 开源协议

本项目采用 [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html) 开源协议。

### 重要说明
- 本项目为课程设计作品，仅供学习和参考。
- 禁止将本项目用于商业用途。
- 如果您对本项目进行了修改或改进，需要以相同的许可证开源。

## 联系方式

如果您有任何问题或建议，欢迎提出 Issue 或通过以下方式联系我：
- GitHub: [https://github.com/Minshenyao] 