# MemeForge 后端服务

MemeForge后端采用微服务架构，基于Spring Cloud生态系统构建，提供用户管理、代币记录、交易历史等核心功能。

## 项目结构
backend/
├── api-gateway/ # API网关服务
├── user-service/ # 用户服务
├── token-service/ # 代币服务
└── transaction-service/ # 交易服务


## 技术栈

- **框架**: Spring Boot, Spring Cloud
- **服务注册与发现**: Nacos
- **安全**: Spring Security
- **数据库**: MySQL
- **缓存**: Redis
- **构建工具**: Maven
- **ORM**: MyBatis-Plus
- **区块链交互**: Web3j

## 服务模块

### API网关 (api-gateway)

- 路由转发
- 安全验证
- 请求过滤与限流
- 跨域资源共享(CORS)配置

### 用户服务 (user-service)

- Web3钱包登录认证
- 用户信息管理
- 权限控制
- 会话管理

### 代币服务 (token-service)

- 代币信息记录与存储
- 代币元数据管理
- 代币创建事件监听
- 代币列表查询服务

### 交易服务 (transaction-service)

- 交易记录存储
- 交易历史查询
- 区块链交易同步
- 价格数据收集与统计

## 开发指南

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.0+


