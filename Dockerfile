FROM jenkins/jenkins:alpine

# Set labels
LABEL version="2.201"
LABEL maintainer="Said Sef said@saidsef.co.uk (saidsef.co.uk/)"
LABEL description="Containerised Jenkins CI/CD Server With Plugins"

ARG BUILD_ID=""

ENV BUILD_ID ${BUILD_ID:-'0.0.0.0-boo!'}
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Dpermissive-script-security.enabled=true"
ENV PORT ${PORT:-8080}

# Install graphviz and build information
USER root
RUN apk --update add graphviz && \
    apk del build-base linux-headers pcre-dev openssl-dev && \
    rm -rfv /var/cache/apk/* && \
    rm -rfv /tmp/*

# Copy plugins, groovy and css to container
COPY files/plugins.txt /var/jenkins_home/plugins.txt
COPY groovy/custom.groovy /var/jenkins_home/init.groovy.d/

# Disable plugin banner on startup
RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state

# Install plugins
RUN /usr/local/bin/install-plugins.sh < /var/jenkins_home/plugins.txt
RUN echo ${BUILD_ID} | tee -a /tmp/build_id.txt

# first fix dir/file permission issues
#USER jenkins

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=10s CMD curl --fail 'http://localhost:${PORT}/login?from=login' || exit 1

VOLUME ["/var/jenkins_home/logs", "/var/jenkins_home/cache"]
VOLUME ["/var/jenkins_home/jobs", "/var/jenkins_home/jenkins-jobs"]
VOLUME ["/var/jenkins_home/secrets"]
