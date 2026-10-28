# Jenkins Docker Container [![CI](https://github.com/saidsef/alpine-jenkins-dockerfile/actions/workflows/docker.yml/badge.svg)](#deploy-from-remote-repo) [![Tagging](https://github.com/saidsef/alpine-jenkins-dockerfile/actions/workflows/tagging.yml/badge.svg)](#deploy-from-remote-repo) [![Release](https://github.com/saidsef/alpine-jenkins-dockerfile/actions/workflows/release.yml/badge.svg)](#deploy-from-remote-repo)

Jenkins is used to automate development workflows, so you can focus on work that matters most. Jenkins is commonly used for:

- Building projects
- Running tests to detect bugs and other issues as soon as they are introduced
- Static code analysis
- Deployment

Execute repetitive tasks, save time, and optimize your development process with Jenkins.

## Plugin

- [Jenkins Plugins List](files/plugins.txt)

### Build and deploy locally

```bash
docker build -t docker.io/saidsef/alpine-jenkins-dockerfile:latest .
./scripts/run.sh
```

Once Jenkins is up and running go to [http://127.0.0.1:8080](http://127.0.0.1:8080)

### Deploy from remote repo

#### Locally

```bash
docker pull docker.io/saidsef/alpine-jenkins-dockerfile:latest
./scripts/run.sh
```

> Once Jenkins is up and running go to `http://localhost:8080`

#### Kubernetes

```bash
kubectl apply -k ./deployment
```

> Optional: Deploy to specific `namespace` via `--namespace`.

To login:

```bash
kubectl logs pod/<pod-name> -f | grep 'created local'
```

Default admin password will be print in the log output via the groovy script - be **patient**, depending on the available resources startup might take a while!

Once the service has been successfully deployed, use the following command to access login screen:

```bash
kubectl port-forward <pod-name> 8080:8080
```

### MCP server

The [MCP Server](https://plugins.jenkins.io/mcp-server/) plugin serves the Model Context Protocol over Streamable HTTP at `/mcp-server/mcp`. A client authenticates with HTTP Basic, using a Jenkins user name and an API token. A user creates the token from the user menu under **Security** and **Add new token**.

```bash
echo -n "admin:<api-token>" | base64
```

```json
{
  "servers": {
    "jenkins": {
      "type": "http",
      "url": "http://localhost:8080/mcp-server/mcp",
      "headers": {
        "Authorization": "Basic <base64-credentials>"
      }
    }
  }
}
```

### OpenTelemetry

The [OpenTelemetry](https://plugins.jenkins.io/opentelemetry/) plugin sends build traces, Jenkins metrics and the Jenkins system log to an OTLP endpoint. Pipeline build logs stay in Jenkins. The startup script copies `OTEL_EXPORTER_OTLP_ENDPOINT` into the plugin configuration on every start, and the OpenTelemetry SDK reads the other `OTEL_*` variables, such as `OTEL_METRIC_EXPORT_INTERVAL`, from the container environment. Without the endpoint variable, the plugin does not export anything until an administrator sets an endpoint under **Manage Jenkins** and **System**.

```bash
docker run -p 8080:8080 -e OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317 docker.io/saidsef/alpine-jenkins-dockerfile:latest
```

### Content Security Policy

The startup script turns on [Content Security Policy](https://www.jenkins.io/doc/book/security/csp/) enforcement on every start. Jenkins sends a `Content-Security-Policy` header, and browsers block any script or style that the policy does not allow. A plugin page that stops working under the policy works again once Jenkins sends the header as report-only:

```bash
docker run -p 8080:8080 -e JENKINS_JAVA_OPTS=-Djenkins.security.csp.CspHeader.headerName=Content-Security-Policy-Report-Only docker.io/saidsef/alpine-jenkins-dockerfile:latest
```

### Reverse proxy check

The startup script disables the reverse proxy check under **Manage Jenkins**. Jenkins serves the System page at both `/manage/configure` and `/configure`, and the check only passes from a page under `/manage`, so opening `/configure` shows a broken proxy warning even when the proxy forwards every request correctly. The [reverse proxy troubleshooting guide](https://www.jenkins.io/doc/book/system-administration/reverse-proxy-configuration-troubleshooting/) describes how to test a proxy by hand.

### Links

- [Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/)
- [Example Jobs](https://github.com/jenkinsci/pipeline-examples)

## Source

Our latest and greatest source of Jenkins can be found on [GitHub]. Fork us!

## Contributing

We would :heart: you to contribute by making a [pull request](https://github.com/saidsef/alpine-jenkins-dockerfile/pulls).

Please read the official [Contribution Guide](./CONTRIBUTING.md) for more information on how you can contribute.

## Troubleshooting

If you encounter issues while starting or accessing Jenkins, here are some common troubleshooting steps:

### Unable to Access Jenkins at `http://127.0.0.1:8080` or `http://localhost:8080`

- Ensure the Docker container is running. You can verify this by running:
  ```bash
  docker ps
  ```
  Look for a container with the name or image `alpine-jenkins-dockerfile`.
- Check the container logs for any errors:
  ```bash
  docker logs <container-id>
  ```
  Replace `<container-id>` with the actual container ID from the `docker ps` output.

### Slow Startup

- Jenkins may take some time to initialize, especially on the first run. Be patient and monitor the logs for progress.

By following these steps, you should be able to resolve most common issues.
