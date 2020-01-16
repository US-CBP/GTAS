FROM httpd:latest

COPY httpd.conf /usr/local/apache2/conf/httpd.conf
RUN mkdir -p /usr/local/apache2/conf/sites/
COPY host.conf /usr/local/apache2/conf/sites/host.conf
COPY htpasswd /etc/apache2/pass/htpasswd

RUN find / -name "*"

CMD ["httpd", "-D", "FOREGROUND"]