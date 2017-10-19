FROM jenkins/jenkins:alpine

# Copy plugins list to container
COPY plugins.txt /var/jenkins_home/plugins.txt
COPY groovy/init.groovy /var/jenkins_home/init.groovy.d/

# health check endpoint
HEALTHCHECK --interval=30s --timeout=10s CMD curl --fail 'http://localhost:8080/login?from=login' || exit 1

# Install plugin
RUN /usr/local/bin/install-plugins.sh < /var/jenkins_home/plugins.txt
