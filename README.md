# 导入项目  
 - 项目地址：https://github.com/hsshhssh/financial-pay
 - git clone https://github.com/hsshhssh/financial-pay.git  
   或者直接下载zip包

# 引入依赖  
 - sql日志工具：print-sql-starter-0.0.1.jar  
   源码地址：https://github.com/hsshhssh/print-sql-starter  
   打包命令：mvn install:install-file -Dfile=print-sql-starter-0.0.1.jar -DgroupId=org.hssh.common -DartifactId=print-sql-starter -Dversion=0.0.1
   
 - 数据源：zkdb-starter-0.0.2.jar   
   源码地址：https://github.com/hsshhssh/zkdb-starter  
   打包命令： mvn install:install-file -Dfile=zkdb-starter-0.0.2.jar -DgroupId=org.hssh.common -DartifactId=zkdb-starter -Dversion=0.0.2

 - 平安银行json-lib包： json-lib-2.4-jdk15.jar 
   打包命令：mvn install:install-file -Dfile=json-lib-2.4-jdk15.jar -DgroupId=net.sf.json-lib -DartifactId=json-lib -Dversion=2.4

 - 平安银行加密兼容：替换security包下的jar包   
   路径：JAVA_HOME \jre\lib\security  
   jdk1.7: UnlimitedJCEPolicyJDK7.zip  
   jdk1.8: jce_policy-8.zip  
   （解压后再替换）

# zookeeper配置、自动
 - 安装zookeeper 安装部署启动教程请自行谷歌百度
 - 安装zkui，用于操作zookeeper。 地址：https://github.com/echoma/zkui  
 - zk配置中心配置：根据业务需要配置

# 数据库配置 
 - 表结构文件：pay_app.sql

# 配置环境变量 
 - ZK_HOST = 127.0.0.1:2181 