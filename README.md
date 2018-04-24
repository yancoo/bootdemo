# bootdemo 学习演示项目

### 功能
- 集成SpringBoot+SpringSecurity+Postgresql
- 不使用SpringMVC，RESTful API，json
- 统一ServiceException异常处理，便于业务逻辑异常编写
- 支持用户User和角色Role认证授权
- 上下线Session维护与日志记录
- 自动DDL，创建Table和Column，不增加Schema等时可减少sql维护
- 使用spring-boot-devtools，热部署，IDE环境修改代码立即部署生效
- 推荐IDE为STS

### 安装
- 安装Maven、JDK、Postgresql
- create database demo;
- 顺序执行db/init下sql创建库表
- 编辑application.properties，确认spring.datasource.*连接参数
- 编译
  - mvn install
  - application.properties如有修改，也须重复执行mvn install
- 运行
  - mvn spring-boot:run
  - 或 IDE中运行 BootdemoApplication（IDE环境修改代码不必mvn install）

### JMeter测试
- 使用JMeter容易回归测试功能API，也容易进行性能测试
- 安装JMeter，打开test/jmeter/springboottest.jmx
- 编辑 User Defined Variables，设置参数
- Ctrl+R运行，结果查看View Results Tree