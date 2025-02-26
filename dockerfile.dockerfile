FROM centos:7

RUN mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup

RUN curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo

RUN rpm --rebuilddb && yum install -y wget which

RUN yum clean all && mkdir -p /home/opt


WORKDIR /home/opt

#MYSQL
RUN wget http://repo.mysql.com/mysql80-community-release-el7.rpm
RUN yum -y install ./mysql80-community-release-el7.rpm
RUN yum install -y mysql-community-server && yum clean all

RUN mysqld --initialize-insecure --user=mysql

#JDK17
RUN wget https://download.oracle.com/java/17/archive/jdk-17.0.12_linux-x64_bin.rpm
RUN yum -y install ./jdk-17.0.12_linux-x64_bin.rpm

#nginx
RUN wget http://nginx.org/packages/centos/7/x86_64/RPMS/nginx-1.20.2-1.el7.ngx.x86_64.rpm
RUN yum -y install  ./nginx-1.20.2-1.el7.ngx.x86_64.rpm

#minio
RUN wget https://dl.min.io/server/minio/release/linux-amd64/minio && chmod +x minio
RUN chmod +x minio

#file
RUN echo "#!/bin/bash" >> /home/opt/start.sh \
    && echo "export MINIO_ROOT_PASSWORD=password" >> /home/opt/start.sh \
    && echo "export MINIO_ROOT_USER=admin" >> /home/opt/start.sh \
    && echo "nohup mysqld --user=mysql &> /home/opt/nohup.log &" >> /home/opt/start.sh \
    && echo "nginx" >> /home/opt/start.sh \
    && echo "nohup /home/opt/minio server /data --console-address :9001 &>/home/opt/nohupp2.log &" >> /home/opt/start.sh \
    && echo "sleep 4" >> /home/opt/start.sh \
    && echo "mysql -uroot -e 'source /home/opt/init.sql' && sed -i '/mysql -uroot -e/d' /home/opt/start.sh" >> /home/opt/start.sh \
    && echo "/home/opt/helm/bin/start-server.sh" >> /home/opt/start.sh
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
    && echo "        proxy_pass http://127.0.0.1:18080/; " >> /etc/nginx/conf.d/helm.conf \
    && echo "    }" >> /etc/nginx/conf.d/helm.conf \
    && echo "}" >> /etc/nginx/conf.d/helm.conf


COPY dist/ /usr/share/nginx/html/dist
COPY helm/ /home/opt/helm
COPY init.sql /home/opt
RUN echo "/home/opt/start.sh" >> /etc/rc.d/rc.local
RUN rm -rf '/etc/nginx/conf.d/default.conf'

RUN chmod +x /etc/rc.d/rc.local ./start.sh ./helm/bin/start-server.sh ./helm/bin/stop-server.sh

EXPOSE 80 3306 18080 9000 9001

CMD /usr/sbin/init
#docker build -f dockerfile -t helm-harbortek .
#docker run -d --name helm -p 80:80 -p 3306:3306 -p 9000:9000 -p 9001:9001 --privileged=true helm-harbortek:latest
