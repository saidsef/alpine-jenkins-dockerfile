FROM jenkins/jenkins:alpine
MAINTAINER Said Sef <saidsef@gmail.com>

# Set labels
LABEL version="2.0"
LABEL description="Containerised Jenkins CI/CD Server With Plugins"

ARG BUILD_ID=""

ENV BUILD_ID ${BUILD_ID:-'0.0.0.0-boo!'}
ENV JENKINS_OPTS -Dpermissive-script-security.enabled=true ${JENKINS_OPTS:-''}

# Copy plugins, groovy and css to container
COPY files/plugins.txt /var/jenkins_home/plugins.txt
COPY groovy/custom.groovy /var/jenkins_home/init.groovy.d/

# health check endpoint
HEALTHCHECK --interval=30s --timeout=10s CMD curl --fail 'http://localhost:8080/login?from=login' || exit 1

# Build information
RUN apk add --update graphviz
RUN echo $BUILD_ID > /tmp/build_id.txt

# Install plugin
RUN /usr/local/bin/install-plugins.sh < /var/jenkins_home/plugins.txt
