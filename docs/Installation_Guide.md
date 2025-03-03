## 安装指南
#### 一、环境准备

1. 确保linux主机已安装Docker

2. 将helm项目拷贝到主机，或使用git获取helm项目

   ```shell
   git clone https://github.com/Harbortek/helm.git
   ```

3. 进入项目根目录

   ``` shell
   cd helm
   ```

#### 二、构建镜像

1. 在项目根目录执行构建命令


```shell
docker build -t helm .
```
2. 验证镜像构建成功

```shell
docker images | grep helm
```

3. 运行容器

``` shell
docker run -d -p 80:80 --name helm01 helm
```

4. 检查容器运行状态

``` shell
docker ps -a | grep helm01
```

#### 三、项目使用

``` shell
浏览器访问 `http://linux主机ip`
```

