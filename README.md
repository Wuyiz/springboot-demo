### port端口

| 服务名称               | 端口    |
|--------------------|-------|
| temp               | 12580 |
| netty              | none  |
| spring-kafka       | none  |
| spring-redis       | none  |
| websocket-rabbitmq | 12600 |

### maven 启动命令记录

- maven跳过单元测试进行打包
    - `mvn clean package -Dmaven.test.skip -f pom.xml`
- maven使用-P指定环境打包
    - `mvn clean package -Dmaven.test.skip=true -P prod -f pom.xml`

### 数据库约束

1. id varchar长度统一设置为50，如果为数字，统一为无符号为的bigint类型
2. name简短统一30
3. description等短文本，统一为varchar类型，默认255长度
4. text等长文本，统一为varchar类型，默认500~2000长度
5. 超过50000长度，使用text、longtext等文本类型（如果表数据量预计将来增长较大时，新开表存储文本，避免影响查询效率）
6. 如果要记录时区信息，datetime类型应该设置为timestamp

```sql
CREATE TABLE `table_name`
(
    `id`          bigint unsigned NOT NULL COMMENT '主键id',
--    `id`          VARCHAR(50)     NOT NULL COMMENT '主键ID',
    `is_deleted`  int             NOT NULL DEFAULT '0' COMMENT '逻辑删除（0：未删除；1：已删除）',
    `creator_id`  int                      DEFAULT NULL COMMENT '创建人',
    `create_time` datetime                 DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater_id`  int                      DEFAULT NULL COMMENT '更新人',
    `update_time` datetime                 DEFAULT NULL COMMENT '更新时间',
--    `update_time` datetime        DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
```