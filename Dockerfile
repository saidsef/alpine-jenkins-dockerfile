FROM jenkins:2.7.4-alpine

# Copy plugins list to container
COPY plugins.txt /var/jenkins_home/plugins.txt

# Install plugin
RUN /usr/local/bin/plugins.sh /var/jenkins_home/plugins.txt
