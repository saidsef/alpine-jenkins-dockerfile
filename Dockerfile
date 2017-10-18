FROM jenkins/jenkins:alpine

# Copy plugins list to container
COPY plugins.txt /var/jenkins_home/plugins.txt
COPY groovy/init.groovy /var/jenkins_home/init.groovy.d/

# health check endpoint
HEALTHCHECK --interval=30s --timeout=10s CMD curl --fail http://localhost:8080/ -L || exit 1

# Jenkins dirs and configs mounts
#VOLUME ["/var/jenkins_home/logs", "/var/jenkins_home/workspace", "/var/jenkins_home/config.xml", "/var/jenkins_home/credentials.xml"]

# Install plugin
RUN /usr/local/bin/install-plugins.sh < /var/jenkins_home/plugins.txt
