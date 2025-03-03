## 安装指南
#### 一、环境准备

- ‌**Java**‌：JDK 17+
- ‌**MySQL**‌：8
- ‌**Maven**‌：3.6+
- ‌**Node.js**‌：14.x+

#### 二、克隆项目

1. 通过 Git 克隆项目代码到本地

   ``` bash
   git clone https://github.com/Harbortek/helm.git
   cd helm
   ```

#### 二、后端部署(Springboot)

1. 配置数据库

   ``` shell
   #1.创建数据库
   CREATE DATABASE `helm` DEFAULT CHARACTER SET utf8mb4;
   
   #2.修改数据库配置文件/etc/mysql/mysql.conf.d
   # 	windows环境下 默认修改C:\ProgramData\MySQL\MySQL Server 8.0\my.ini
   #	增加 log_bin_trust_function_creators = 1
   ```

2. 在 `server/helm-assembly/src/main/conf/application.properties` 中修改配置：

   ```yaml
   spring:  
     datasource:  
       url: jdbc:mysql://localhost:3306//helm?autoReconnect=true&socketTimeout=60000
       username: root  
       password: your_password  
   ```

3. 执行 Maven 打包：

   ```shell
   # cd ./server
   mvn clean package -DskipTests  
   ```

4. 启动服务：

   ``` shell
   # cd ./helm-assembly/target/helm-0.1.0-SNAPSHOT/helm
   ./bin/start-server.sh
   #后端服务默认运行在 http://localhost:8080
   ```

5. 如果使用IntelliJ IDEA 等编辑器运行

   ``` shell
   #请拷贝helm-assembly/src/main/conf/application.properties
   #到helm-start/src/main/resources/application-dev.properties
   #并配置VM options -DHELM_HOME=/File/opt #项目模板存放路径
   #运行HelmServer
   ```

   

#### 三、前端部署(Vue)

1. 安装依赖

   ``` shell
   cd ../web
   npm install
   ```

2. 启动前端服务

   ``` shell
   npm run serve
   #前端服务默认运行在 http://localhost:3000
   ```

3. 使用项目

   ``` shell
   浏览器访问 `http://localhost:3000` 
   # 系统默认账号密码: admin / admin
   ```

#### 四、常见问题

1 数据库连接失败

- 确保 MySQL 服务已启动。
- 检查 `application.properties` 中的数据库配置是否正确。
- 确保数据库用户和密码正确。

2 前端代理失败

- 确保后端服务已启动。
- 检查 `vue.config.js` 中的代理配置是否正确。

3 Maven 构建失败

- 确保网络连接正常，Maven 能够下载依赖。
- 检查 `pom.xml` 文件是否有拼写错误。
