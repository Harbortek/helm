# 第一阶段构建
FROM maven:3.8.4-openjdk-17 AS build

COPY ./server /app/server

RUN printf "<settings xmlns='http://maven.apache.org/SETTINGS/1.0.0'" > /usr/share/maven/conf/settings.xml \
    && printf "          xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"  >> /usr/share/maven/conf/settings.xml \
    && printf "          xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0"  >> /usr/share/maven/conf/settings.xml \
    && printf "                              https://maven.apache.org/xsd/settings-1.0.0.xsd'>"  >> /usr/share/maven/conf/settings.xml \
    && printf "    <mirrors>"  >> /usr/share/maven/conf/settings.xml \
    && printf "        <mirror>"  >> /usr/share/maven/conf/settings.xml \
    && printf "            <id>alimaven</id>"  >> /usr/share/maven/conf/settings.xml \
    && printf "<mirrorOf>central</mirrorOf>"  >> /usr/share/maven/conf/settings.xml \
    && printf "<name>aliyun maven</name>"  >> /usr/share/maven/conf/settings.xml \
    && printf "<url>https://maven.aliyun.com/repository/public/</url>"  >> /usr/share/maven/conf/settings.xml \
    && printf "</mirror>"  >> /usr/share/maven/conf/settings.xml \
    && printf "</mirrors>"  >> /usr/share/maven/conf/settings.xml \
    && printf "</settings>"  >> /usr/share/maven/conf/settings.xml

WORKDIR /app/server

RUN mvn dependency:go-offline

RUN mvn package -DskipTests

# 第二阶段构建
FROM node:18.20.2-buster-slim AS frontend-builder

COPY ./web /app/web

WORKDIR /app/web
RUN sed -i "s/vue-cli-service build --mode production/export NODE_OPTIONS=--openssl-legacy-provider \&\& vue-cli-service build --mode development/" ./package.json \
	&& sed -i "s/replacer\": \"1.5.2\"/replacer\": \"1.3.18\"/" ./package.json \
	&& npm config set -g registry https://registry.npmmirror.com

RUN npm install

RUN npm run build

#第三阶段
FROM mysql:8.0-debian

WORKDIR /app

COPY --from=build /app/server/helm-assembly/target/helm-0.1.0-SNAPSHOT/helm /app/helm
COPY --from=frontend-builder /app/web/dist /app/dist

RUN sed -i 's@deb.debian.org@mirror.nju.edu.cn@g' /etc/apt/sources.list.d/debian.sources
RUN apt-get update && \  
    apt-get install -y --no-install-recommends \  
    openjdk-17-jdk \  
    nginx \
    supervisor \
    && rm -rf /var/lib/apt/lists/ 

#MYSQL
ENV MYSQL_ROOT_PASSWORD=harbortek
RUN echo "CREATE USER 'helm'@'%' IDENTIFIED BY 'helm';" >> /docker-entrypoint-initdb.d/init.sql \
    && echo "GRANT ALL PRIVILEGES ON *.* TO 'helm'@'%';" >> /docker-entrypoint-initdb.d/init.sql \
    && echo "FLUSH PRIVILEGES;" >> /docker-entrypoint-initdb.d/init.sql \
    && echo "CREATE DATABASE IF NOT EXISTS helm;" >> /docker-entrypoint-initdb.d/init.sql \
    && echo "log_bin_trust_function_creators = 1" >> /etc/mysql/mysql.conf.d

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64  
ENV PATH="$JAVA_HOME/bin:$PATH"

#NGINX
RUN echo "server {" >> /etc/nginx/conf.d/helm.conf \
    && echo "    listen       80; " >> /etc/nginx/conf.d/helm.conf \
    && echo "    server_name  localhost; " >> /etc/nginx/conf.d/helm.conf \
    && echo "    location / { " >> /etc/nginx/conf.d/helm.conf \
    && echo "        root   /usr/share/nginx/html/dist; " >> /etc/nginx/conf.d/helm.conf \
    && echo "        index  index.html index.htm; " >> /etc/nginx/conf.d/helm.conf \
    && echo "    } " >> /etc/nginx/conf.d/helm.conf \
    && echo "    location /api/ { " >> /etc/nginx/conf.d/helm.conf \
    && echo "        proxy_set_header Host \$http_host; " >> /etc/nginx/conf.d/helm.conf \
    && echo "        proxy_set_header X-Real-IP \$remote_addr; " >> /etc/nginx/conf.d/helm.conf \
    && echo "        proxy_set_header REMOTE-HOST \$remote_addr; " >> /etc/nginx/conf.d/helm.conf \
    && echo "        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for; " >> /etc/nginx/conf.d/helm.conf \
    && echo "        proxy_pass http://127.0.0.1:8080/api/; " >> /etc/nginx/conf.d/helm.conf \
    && echo "    }" >> /etc/nginx/conf.d/helm.conf \
    && echo "}" >> /etc/nginx/conf.d/helm.conf \
    && mv ./dist /usr/share/nginx/html/dist/  \
    && : > /etc/nginx/sites-available/default \
    && chmod +x ./helm/bin/start-server.sh ./helm/bin/stop-server.sh

#supervisord
RUN echo "[program:mysql]" >> /etc/supervisor/conf.d/supervisord.conf \
    && echo "command=/usr/local/bin/docker-entrypoint.sh mysqld">>/etc/supervisor/conf.d/supervisord.conf \
    && echo "priority=10">>/etc/supervisor/conf.d/supervisord.conf \
    && echo "autorestart=true  ">>/etc/supervisor/conf.d/supervisord.conf \
    && echo "[program:nginx]  " >>/etc/supervisor/conf.d/supervisord.conf   \
    && echo "command=/usr/sbin/nginx -g 'daemon off;'" >>/etc/supervisor/conf.d/supervisord.conf  \
    && echo "autorestart=true  " >>/etc/supervisor/conf.d/supervisord.conf  \
    && echo "[program:helm]" >>/etc/supervisor/conf.d/supervisord.conf \
    && echo "command=/app/helm/bin/start-server.sh" >>/etc/supervisor/conf.d/supervisord.conf  \
    && echo "priority=1">>/etc/supervisor/conf.d/supervisord.conf \
    && echo "autorestart=true  " >>/etc/supervisor/conf.d/supervisord.conf \
    && echo "startsecs=3" >> /etc/supervisor/conf.d/supervisord.conf \
    && echo "startretries=5" >> /etc/supervisor/conf.d/supervisord.conf

EXPOSE 80 3306 8080

CMD ["supervisord","-n"]
