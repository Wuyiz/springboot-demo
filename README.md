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

### 数据库基础字段以及规约

1. id如果是varchar，长度统一设置为100；如果为数字，统一为无符号的bigint类型
2. name、code等varchar类型，长度统一为100
3. introduction、remarks等短文本字段，统一为varchar类型，默认500~1000长度
4. description等长文本字段，统一为varchar类型，默认2000~4000长度
5. 超过5000长度，使用text、longtext等文本类型（如果表数据量预计将来增长较大时，新开表存储文本，避免影响查询效率）
6. 如果要记录时区信息，应该使用timestamp类型，而不是datetime类型
7. 创建时间和更新时间字段的精度设置为3|6位，针对同一秒插入的数据也可以正确排序
8. 索引命名方式：普通索引用idx_，唯一约束用uk_，主键约束用pk_
9. 涉及表的状态字段时，如果字段是tinyint等数字类型，不需要设置成无符号，因为状态值可以存在负值

```sql
CREATE TABLE `table_name`
(
  `id`           bigint unsigned NOT NULL COMMENT '主键ID',
  `is_deleted`   tinyint                                                       DEFAULT '0' COMMENT '逻辑删除（0：未删除；1：已删除）',
  `created_by`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `created_time` datetime(3)                                                   DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_by`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime(3)                                                   DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
```

```java
import java.io.Serializable;

@Data
@TableName(value = "table_name")
public class TableName implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private BigInteger id;

    @TableLogic
    private Boolean isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @Version
    private static final long serialVersionUID = 1L;
}
```

```yaml
# spring-log配置
server:
  port: 18080
  servlet:
    context-path: /service-path
  tomcat:
    # tomcat允许的请求大小，如果上传文件大于该配置数额，请求会直接返回错误，而不会被全局异常捕获
    # 可以设置为-1表示无限大，避免全局异常无法捕获
    # 一般会和springboot的文件上传大小的配置搭配使用
    max-swallow-size: -1

spring:
  servlet:
    multipart:
      # 配置上传的单个文件大小
      max-file-size: 100MB
      # 配置单次请求允许上传的所有文件的总大小（单词请求上传10个文件的总大小）
      max-request-size: 100MB

logging:
  config: classpath:log4j2.xml
  file:
    # name是指日志路径+日志文件名称
    name: ${logging.file.path}/${spring.application.name}.log
    # path是日志路径
    path: /home/api/log/${spring.application.name}

# spring-admin监控配置
management:
  info:
    defaults:
      enabled: true
  endpoint:
    health:
      show-details: ALWAYS
    logfile:
      # SpringAdmin显示日志
      external-file: ${logging.file.name}
      enabled: true
  endpoints:
    web:
      exposure:
        include: '*'

# mybatis-plus配置
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-value: 1
      logic-not-delete-value: 0
  mapper-locations: classpath*:mapper/**/*Mapper.xml
```