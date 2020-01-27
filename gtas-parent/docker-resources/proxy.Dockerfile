FROM httpd:latest

COPY ./httpd.conf /usr/local/apache2/conf/httpd.conf
RUN mkdir -p /usr/local/apache2/conf/sites/
COPY ./host.conf /usr/local/apache2/conf/sites/host.conf
COPY ./htpasswd /etc/apache2/pass/htpasswd

ENTRYPOINT credentials=$(echo "Basic ")$(echo -n $(echo "elastic:")$(cat /run/secrets/elastic_bootstrap_password)| base64) && \
	export credentials && \
	httpd -D FOREGROUND