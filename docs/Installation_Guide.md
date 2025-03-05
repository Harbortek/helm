## 安装指南
### 1、环境要求

**部署服务器要求：**

- 操作系统: Ubuntu 22.04 / CentOS 7.6 64 位系统
- CPU/内存: 2核4G
- 磁盘空间: 40G
- **可访问互联网**

### 2、配置环境

- 本指南以CentOS 7为操作示例，Ubuntu系统可参考对应命令调整

##### 2.1、创建新用户

1. 创建用户

   ``` shell
   sudo useradd -m username
   ```

2. 设置用户密码

   ``` shell
   sudo passwd username
   ```

3. 将用户加入wheel组

   ``` shell
   sudo usermod -aG wheel username
   su username
   ```

##### 2.2、安装 JDK 17

1. 修改yum源

   ``` shell
   sudo mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup
   sudo curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
   ```

2. 安装wget

   ``` shell
   sudo yum install -y wget which
   ```

3. 下载 JDK17 RPM 安装包

   ``` shell
   cd /opt/
   sudo wget https://download.oracle.com/java/17/archive/jdk-17.0.12_linux-x64_bin.rpm
   ```

4. 安装 RPM 安装包

   ``` shell
   sudo yum -y install ./jdk-17.0.12_linux-x64_bin.rpm
   ```

5. 验证 JDK 正确安装

   ``` shell
   java -version
   ```


##### 2.3、安装配置 Mysql

1. 下载 Mysql

   ``` shell
   sudo wget http://repo.mysql.com/mysql80-community-release-el7.rpm
   ```

2. 安装Mysql

   ``` shell
   sudo yum -y install mysql80-community-release-el7.rpm
   sudo yum install -y mysql-community-server && yum clean all
   ```

3. 初始化mysql

   ``` shell
   sudo mysqld --initialize-insecure --user=mysql
   ```

4. 后台运行mysql

   ``` shell
   nohup sudo mysqld --user=mysql >> nohup.log 2>&1 &
   ```

5. 登录mysql，初始密码为空

   ``` shell
   mysql -uroot
   ```

6. 修改密码并创建helm用户，初始密码为空

   ``` shell
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'helm';
   CREATE USER 'helm'@'%' IDENTIFIED BY 'helm';
   GRANT ALL PRIVILEGES ON *.* TO 'helm'@'%';
   set global log_bin_trust_function_creators=1;
   FLUSH PRIVILEGES;
   CREATE DATABASE IF NOT EXISTS helm;
   exit;
   ```

##### 2.4、安装配置 Maven

1. 下载 Maven

   ``` shell
   sudo wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
   ```

2. 安装

   ``` shell
   tar zxvf apache-maven-3.9.6-bin.tar.gz
   mv apache-maven-3.9.6 /opt
   
   echo "export M2_HOME=/opt/apache-maven-3.9.6" >> ~/.bashrc
   echo "export PATH=\$PATH:\$M2_HOME/bin" >> ~/.bashrc
   source ~/.bashrc
   ```

3. 验证

   ``` shell
   mvn -v
   ```

##### 2.5、安装配置 Nodejs

1. 下载 Nodejs

   ``` shell
   sudo wget https://nodejs.org/dist/v16.15.0/node-v16.15.0-linux-x64.tar.xz
   ```

2. 安装

   ``` shell
   tar xvf node-v16.15.0-linux-x64.tar.xz
   mv node-v16.15.0-linux-x64 /opt
   
   echo "export PATH=\$PATH:/opt/node-v16.15.0-linux-x64/bin" >> ~/.bashrc
   
   source ~/.bashrc
   ```

3. 验证

   ``` shell
   node --version
   ```

##### 2.6、安装 Nginx

1. 下载Nginx

   ``` shell
   sudo wget http://nginx.org/packages/centos/7/x86_64/RPMS/nginx-1.20.2-1.el7.ngx.x86_64.rpm
   ```

2. 安装

   ``` shell
   sudo yum -y install  ./nginx-1.20.2-1.el7.ngx.x86_64.rpm
   ```

3. 启动并验证

   ``` shell
   nginx
   nginx -t
   ```

##### 2.7、安装git

1. 安装git

   ``` shell
   yum install -y git
   ```

2. 验证

   ``` shell
   git --version
   ```

### 3、项目运行

##### 3.1、源码准备

1. 下载源码到本地

   ``` shell
   cd /opt
   git clone https://github.com/Harbortek/helm.git
   cd ./helm
   ```


##### 3.2、后端运行

1.  在`./server/helm-assembly/src/main/conf/application.properties` 确认mysql配置：

   ``` yaml
   spring:  
     datasource:  
       url: jdbc:mysql://localhost:3306//helm?autoReconnect=true&socketTimeout=60000
       username: helm 
       password: helm 
   ```

2.  修改maven配置源 /opt/apache-maven-3.9.6/conf/settings.xml

    ``` shell
    <settings xmlns='http://maven.apache.org/SETTINGS/1.0.0'
                    xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
                    xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0
                                        https://maven.apache.org/xsd/settings-1.0.0.xsd'>
        <mirrors>
            <mirror>
                <id>alimaven</id>
                <mirrorOf>central</mirrorOf>
                <name>aliyun maven</name>
                <url>https://maven.aliyun.com/repository/public/</url>
            </mirror>
        </mirrors>
    </settings>
    ```

3.  执行Maven打包后端项目:

   ``` shell
   cd ./server
   mvn clean package -DskipTests  
   ```

4.  启动项目

   ``` shell
   ./helm-assembly/target/helm-0.1.0-SNAPSHOT/helm/bin/start-server.sh
   ```

- 后端服务默认运行在 http://localhost:8080
- 启动日志在./helm-assembly/src/main/logs/

##### 3.3、前端运行

1. 安装项目依赖：

   ``` yaml
   cd ../web
   npm install --registry=https://registry.npmmirror.com
   ```

2. 构建项目:

   ``` shell
   npm run build:dev
   ```

3. 修改nginx配置文件/etc/nginx/conf.d/default.conf

   ``` shell
   server {
       listen       80;
       server_name  localhost;
       location / {
           root   /usr/share/nginx/html/dist;
           index  index.html index.htm;
       }
       location /api/ {
           proxy_set_header Host $http_host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header REMOTE-HOST $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_pass http://127.0.0.1:8080/api/;
       }
   }
   ```

4. 拷贝项目到nginx

   ``` shell
   cp -r dist/ /usr/share/nginx/html/dist
   ```

5. 验证nginx配置文件并重启nginx

   ``` shell
   nginx -t
   nginx -s reload
   ```

##### 3.4、项目使用

``` shell
`http://localhost` 
```

- 系统默认账号密码: admin / admin
