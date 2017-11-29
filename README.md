# Jenkins Docker Container

## Plugin
 - [Jenkins Plugins List](plugins.txt)

### Build and deploy locally
```bash
$ docker build -t jenkins .
$ docker run -d -p 8080:8080 jenkins
```
Once Jenkins is up and running go to http://127.0.0.1:8080

### Deploy from remote repo
```bash
$ docker pull saidsef/alpine-jenkins-dockerfile-demo:dev
$ docker run -d -p 8080:8080 saidsef/alpine-jenkins-dockerfile-demo:dev
```
Once Jenkins is up and running go to http://host-name:8080

### Links

- Job DSL API https://jenkinsci.github.io/job-dsl-plugin/
- Example Jobs https://github.com/jenkinsci/pipeline-examples
