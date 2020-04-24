FROM httpd:2.4


RUN mkdir -p /usr/local/apache2/conf/sites/

COPY ./docker-resources/httpd.conf /usr/local/apache2/conf/httpd.conf
COPY ./docker-resources/host.conf /usr/local/apache2/conf/sites/host.conf
COPY ./docker-resources/htpasswd /etc/apache2/pass/htpasswd


ENTRYPOINT credentials=$(echo "Basic ")$(echo -n $(echo "elastic:")$(cat /run/secrets/elastic_bootstrap_password)| base64) && \
	export credentials && \
	httpd -D FOREGROUND