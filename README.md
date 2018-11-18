# Jenkins Docker Container

## Plugin
 - [Jenkins Plugins List](files/plugins.txt)

### Build and deploy locally
```bash
$ docker build -t jenkins .
$ docker run -d -p 8080:8080 jenkins
```
Once Jenkins is up and running go to http://127.0.0.1:8080

### Deploy from remote repo

#### Locally
```bash
$ docker pull saidsef/alpine-jenkins-dockerfile:dev
$ docker run -d -p 8080:8080 saidsef/alpine-jenkins-dockerfile:dev
```
Once Jenkins is up and running go to http://host-name:8080

#### Kubernetes

Create `namespace` called `cicd` via `kubectl create namespace cicd`, and then deploy template:

```bash
kubectl apply -f k8s-jenkins-cicd.yml
```

To login:
```bash
kubectl logs pod/<pod-name> -n cicd -f | grep 'created local'

** OR **

docker logs saidsef/alpine-jenkins-dockerfile:dev | grep 'created local'
```
Default admin password will be print in the log output - be patient, depending on the available resources startup might take a while!

Once the service has been successfully deployed, use the following command to access login screen:

```bash
kubectl port-forward <pod-name> 8080:8080
```

### Links

- Job DSL API https://jenkinsci.github.io/job-dsl-plugin/
- Example Jobs https://github.com/jenkinsci/pipeline-examples
