## Jenkins Docker Container

Usage:
```
$ docker build -t jenkins .
$ docker run -d -p 8080:8080 jenkins
```

Once Jenkins is up and running go to http://127.0.0.1:8080

### Links

- Job DSL API https://jenkinsci.github.io/job-dsl-plugin/
- Example Jobs https://github.com/jenkinsci/pipeline-examples
