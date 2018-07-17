FROM jenkins/jenkins:alpine
MAINTAINER Said Sef <said@saidsef.co.uk> (saidsef.co.uk/)

# Set labels
LABEL version="2.9"
LABEL description="Containerised Jenkins CI/CD Server With Plugins"

ARG BUILD_ID=""

ENV BUILD_ID ${BUILD_ID:-'0.0.0.0-boo!'}
ENV JENKINS_OPTS -Dpermissive-script-security.enabled=true ${JENKINS_OPTS:-''}

# Install graphviz and build information
USER root
RUN apk --update add --no-cache graphviz && \
    apk del build-base linux-headers pcre-dev openssl-dev && \
    rm -rf /var/cache/apk/*

# Copy plugins, groovy and css to container
#USER jenkins
COPY files/plugins.txt /var/jenkins_home/plugins.txt
COPY groovy/custom.groovy /var/jenkins_home/init.groovy.d/

# Disable plugin banner on startup
RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state

# Install plugins
RUN /usr/local/bin/install-plugins.sh < /var/jenkins_home/plugins.txt
RUN echo ${BUILD_ID} > /tmp/build_id.txt

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=10s CMD curl --fail 'http://localhost:8080/login?from=login' || exit 1

VOLUME ["/var/jenkins_home/logs", "/var/jenkins_home/cache"]
VOLUME ["/var/jenkins_home/jobs", "/var/jenkins_home/jenkins-jobs"]
