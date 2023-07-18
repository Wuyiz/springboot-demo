### port端口

| 服务名称               | 端口    |
|--------------------|-------|
| temp               | 12580 |
| netty              | none  |
| spring-kafka       | none  |
| websocket-rabbitmq | 12600 |

### maven 启动命令记录

- maven跳过单元测试进行打包
    - `mvn clean package -Dmaven.test.skip -f pom.xml`
- maven使用-P指定环境打包
    - `mvn clean package -Dmaven.test.skip=true -P prod -f pom.xml`

### 数据库基础字段sql
```sql
CREATE TABLE `table_name` (
  `id` VARCHAR(50) NOT NULL COMMENT '主键id',
--   `id` bigint NOT NULL COMMENT '主键id',
  `engineering_id` varchar(50) NOT NULL COMMENT '项目空间id',
  `is_deleted` int NOT NULL DEFAULT '0' COMMENT '逻辑删除（0：未删除；1：已删除）',
  `creator_id` int DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater_id` int DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
--   `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```