FROM jenkins/jenkins:alpine
MAINTAINER Said Sef <saidsef@gmail.com>

# Set labels
LABEL version="2.0"
LABEL description="Containerised Jenkins CI/CD Server With Plugins"

ARG BUILD_ID=""

ENV BUILD_ID ${BUILD_ID:-'0.0.0.0-boo!'}
ENV JENKINS_OPTS -Dpermissive-script-security.enabled=true ${JENKINS_OPTS:-''}

# Install graphviz and build information
USER root
RUN apk add --update graphviz
RUN echo $BUILD_ID > /tmp/build_id.txt

# Copy plugins, groovy and css to container
USER jenkins
COPY files/plugins.txt /var/jenkins_home/plugins.txt
COPY groovy/custom.groovy /var/jenkins_home/init.groovy.d/

# Install plugins
RUN /usr/local/bin/install-plugins.sh < /var/jenkins_home/plugins.txt

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=10s CMD curl --fail 'http://localhost:8080/login?from=login' || exit 1

VOLUME ["/var/jenkins_home/workspace", "/var/jenkins_home/logs"]
VOLUME ["/var/jenkins_home/jobs", "/var/jenkins_home/jenkins-jobs"]
VOLUME ["/var/jenkins_home/secrets", "/var/jenkins_home/cache"]
