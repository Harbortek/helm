FROM mysql:8.0-debian

RUN sed -i 's@deb.debian.org@mirror.nju.edu.cn@g' /etc/apt/sources.list.d/debian.sources
RUN apt-get update && \  
    apt-get install -y --no-install-recommends \  
    openjdk-17-jdk \  
    nginx \
    supervisor \
    && rm -rf /var/lib/apt/lists/ 

WORKDIR /home/opt

#MYSQL
ENV MYSQL_ROOT_PASSWORD=harbortek
COPY ./init.sql /docker-entrypoint-initdb.d/

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
    && echo "        proxy_pass http://127.0.0.1:18080/; " >> /etc/nginx/conf.d/helm.conf \
    && echo "    }" >> /etc/nginx/conf.d/helm.conf \
    && echo "}" >> /etc/nginx/conf.d/helm.conf

#supervisord
RUN echo "[program:mysql]" >> /etc/supervisor/conf.d/supervisord.conf \
    && echo "command=/usr/local/bin/docker-entrypoint.sh mysqld">>/etc/supervisor/conf.d/supervisord.conf \
    && echo "autorestart=true  ">>/etc/supervisor/conf.d/supervisord.conf \
    && echo "[program:nginx]  " >>/etc/supervisor/conf.d/supervisord.conf   \
    && echo "command=/usr/sbin/nginx -g 'daemon off;'" >>/etc/supervisor/conf.d/supervisord.conf  \
    && echo "autorestart=true  " >>/etc/supervisor/conf.d/supervisord.conf  \
    && echo "[program:java]" >>/etc/supervisor/conf.d/supervisord.conf \
    && echo "command=/home/opt/helm/bin/start-server.sh " >>/etc/supervisor/conf.d/supervisord.conf  \
    && echo "autorestart=true  " >>/etc/supervisor/conf.d/supervisord.conf

#
COPY dist/ /usr/share/nginx/html/dist
COPY helm/ /home/opt/helm
RUN : > /etc/nginx/sites-available/default

RUN chmod +x ./helm/bin/start-server.sh ./helm/bin/stop-server.sh

EXPOSE 80 3306 18080

CMD ["supervisord","-n"]

#docker build -f dockerfile -t helm-harbortek .
#docker run -d --name helm -p 80:80 -p 3306:3306 -p 9000:9000 -p 9001:9001 --privileged=true helm-harbortek:latest

