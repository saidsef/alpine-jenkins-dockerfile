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

### Links

- [Job DSL API](https://jenkinsci.github.io/job-dsl-plugin/)
- [Example Jobs](https://github.com/jenkinsci/pipeline-examples)

## Source

Our latest and greatest source of Jenkins can be found on [GitHub]. Fork us!

## Contributing

We would :heart: you to contribute by making a [pull request](https://github.com/saidsef/alpine-jenkins-dockerfile/pulls).

Please read the official [Contribution Guide](./CONTRIBUTING.md) for more information on how you can contribute.
