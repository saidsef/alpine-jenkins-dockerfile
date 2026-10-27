FROM docker.io/jenkins/jenkins:alpine-jdk25

# Set labels
LABEL version="3.6"
LABEL maintainer="Said Sef said@saidsef.co.uk (saidsef.co.uk/)"
LABEL description="Containerised Jenkins CI/CD Server With Plugins"

ARG BUILD_ID=""

ENV BUILD_ID=${BUILD_ID:-'0.0.0.0-boo!'}
ENV PROMETHEUS_JMX_JAR_VERSION=1.7.0
ENV PORT=${PORT:-8080}
ENV JENKINS_HOSTNAME=${JENKINS_HOSTNAME:-"jenkins.saidsef.co.uk"}

# Copy plugins, groovy and css to container
COPY files/plugins.txt /var/jenkins_home/plugins.txt
COPY groovy/custom.groovy /var/jenkins_home/init.groovy.d/
COPY files/prometheus-jmx-config.yaml /var/jenkins_home

# Disable plugin banner on startup
RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state

USER root

# Maven Central stops at JMX exporter 1.0.1, so the jar comes from the GitHub release
# Each VOLUME path must exist here, or Docker creates it owned by root and Jenkins cannot write to it
RUN apk --no-cache add curl graphviz && \
    JMX_URL=https://github.com/prometheus/jmx_exporter/releases/download/${PROMETHEUS_JMX_JAR_VERSION}/jmx_prometheus_javaagent-${PROMETHEUS_JMX_JAR_VERSION}.jar && \
    curl -fsSL "${JMX_URL}" -o /usr/share/jenkins/jmx_prometheus_javaagent.jar && \
    echo "$(curl -fsSL "${JMX_URL}.sha256" | cut -d ' ' -f 1)  /usr/share/jenkins/jmx_prometheus_javaagent.jar" | sha256sum -c - && \
    mkdir -p /var/jenkins_home/logs /var/jenkins_home/cache /var/jenkins_home/jobs /var/jenkins_home/jenkins-jobs /var/jenkins_home/secrets && \
    chown jenkins:jenkins -R /usr/share/jenkins && \
    chown jenkins:jenkins -R /var/jenkins_home

USER jenkins

# Install plugins
RUN /bin/jenkins-plugin-cli --plugin-file /var/jenkins_home/plugins.txt
RUN echo ${BUILD_ID} | tee -a /tmp/build_id.txt

ENV JAVA_OPTS="-Dhudson.footerURL=https://saidsef.co.uk -Djenkins.install.runSetupWizard=false -Dhudson.model.UsageStatistics.disabled=true -javaagent:/usr/share/jenkins/jmx_prometheus_javaagent.jar=8081:/var/jenkins_home/prometheus-jmx-config.yaml -XX:InitialRAMPercentage=40 -XX:MaxRAMPercentage=60 -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -Xss2048k -XX:MaxDirectMemorySize=256m"

# Health check endpoint
HEALTHCHECK --interval=60s --timeout=10s CMD curl --fail "http://localhost:${PORT}/login?from=login" || exit 1

VOLUME ["/var/jenkins_home/logs", "/var/jenkins_home/cache"]
VOLUME ["/var/jenkins_home/jobs", "/var/jenkins_home/jenkins-jobs"]
VOLUME ["/var/jenkins_home/secrets"]

EXPOSE ${PORT} 8081
