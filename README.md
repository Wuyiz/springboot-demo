### port端口

| 服务名称               | 端口    |
|--------------------|-------|
| temp               | 12580 |
| netty              | none  |
| spring-kafka       | none  |
| websocket-rabbitmq | 12600 |

### maven 启动命令记录

- maven跳过测试打包
    - `clean package -Dmaven.test.skip -f pom.xml`
- maven使用-P指定环境打包
    - `clean package -Dmaven.test.skip=true -P prod -f pom.xml`