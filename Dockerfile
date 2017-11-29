FROM jenkins/jenkins:alpine
MAINTAINER Said Sef <saidsef@gmail.com>

ENV JAVA_OPTS --ajp13Port=-1

# Copy plugins, groovy and css to container
COPY files/plugins.txt /var/jenkins_home/plugins.txt
COPY groovy/init.groovy /var/jenkins_home/init.groovy.d/
COPY files/*.css /var/jenkins_home/war/css/

# health check endpoint
HEALTHCHECK --interval=30s --timeout=10s CMD curl --fail 'http://localhost:8080/login?from=login' || exit 1

# Install plugin
RUN /usr/local/bin/install-plugins.sh < /var/jenkins_home/plugins.txt
