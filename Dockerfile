FROM jenkins/jenkins:alpine

# Set labels
LABEL version="2.238"
LABEL maintainer="Said Sef said@saidsef.co.uk (saidsef.co.uk/)"
LABEL description="Containerised Jenkins CI/CD Server With Plugins"

ARG BUILD_ID=""

ENV BUILD_ID ${BUILD_ID:-'0.0.0.0-boo!'}
ENV JAVA_OPTS="-Dhudson.footerURL=https://saidsef.co.uk -Djenkins.install.runSetupWizard=false -Dhudson.remoting.ClassFilter=java.security.KeyPair,sun.security.rsa.RSAPrivateCrtKeyImpl -Dpermissive-script-security.enabled=true -Djdk.tls.client.protocols=TLSv1.2 -Xmx2g"
ENV PORT ${PORT:-8080}

# Copy plugins, groovy and css to container
COPY files/plugins.txt /var/jenkins_home/plugins.txt
COPY groovy/custom.groovy /var/jenkins_home/init.groovy.d/

# Disable plugin banner on startup
RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state

USER root

RUN apk --no-cache add graphviz && \
    chown jenkins:jenkins -R /usr/share/jenkins && \
    chown jenkins:jenkins -R /var/jenkins_home

USER jenkins

# Install plugins
RUN /usr/local/bin/install-plugins.sh < /var/jenkins_home/plugins.txt
RUN echo ${BUILD_ID} | tee -a /tmp/build_id.txt

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=10s CMD curl --fail 'http://localhost:${PORT}/login?from=login' || exit 1

VOLUME ["/var/jenkins_home/logs", "/var/jenkins_home/cache"]
VOLUME ["/var/jenkins_home/jobs", "/var/jenkins_home/jenkins-jobs"]
VOLUME ["/var/jenkins_home/secrets"]
