FROM jenkins/jenkins:alpine
MAINTAINER Said Sef <saidsef@gmail.com>

ENV JENKINS_OPTS -Dpermissive-script-security.enabled=true ${JENKINS_OPTS:-''}

# Copy plugins, groovy and css to container
COPY files/plugins.txt /var/jenkins_home/plugins.txt
COPY groovy/init.groovy /var/jenkins_home/init.groovy.d/

# health check endpoint
HEALTHCHECK --interval=30s --timeout=10s CMD curl --fail 'http://localhost:8080/login?from=login' || exit 1

# Install plugin
RUN /usr/local/bin/install-plugins.sh < /var/jenkins_home/plugins.txt
