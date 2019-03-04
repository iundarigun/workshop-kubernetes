# Jenkins

Run jenkins in docker (create foldcer `~/tmp/jenkins_home` before):
```
$ docker run -v ~/tmp/jenkins_home:/var/jenkins_home -p 8080:8080 -p 50000:50000 --name local-jenkins jenkins/jenkins:lts
```
Config a job with pipeline. We need to create two credentials params for the build, one for github and other for the docker registry. 
```
node {
    def registry = "iundarigun/proxy"
    def registryCredential = "${env.DockerCredentials}"
    def gitCredential = "${env.GitCredentials}"
    def gradlew = "./gradlew"
    stage(name: "Clone"){
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: gitCredential, url: 'https://github.com/iundarigun/workshop-kubernetes']]])
    }
    dir("proxy"){
        stage(name: "Clean"){
            sh "${gradlew} clean "
        }
        stage(name: "Compile"){
            sh "${gradlew} build "
        }
        stage(name: "docker"){
            docker.withRegistry( '', registryCredential ) {
                dockername = docker.build(registry + ":$BUILD_NUMBER", ".")
                dockername.push()
            }
        }
    }
}
```
We take an error envolving java version. To fix this, we go to install java 11 into the docker container.
```
$ docker exec -it --user root local-jenkins bash
# wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/11.0.2+9/f51449fcd52f4d52b93a989c5c56ed3c/jdk-11.0.2_linux-x64_bin.deb
# dpkg -i jdk-11.0.2_linux-x64_bin.deb
# unlink /docker-java-home
# ln -s /usr/lib/jvm/jdk-11.0.2 /docker-java-home
```

We take another error. No docker found, so we go to install docker in the container:
```
$ docker exec -it --user root local-jenkins bash
# apt-get update
# apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg2 \
    software-properties-common
# curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
# add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/debian \
   $(lsb_release -cs) \
   stable"
# apt-get update
# apt-get install docker-ce docker-ce-cli containerd.io  
```
If we run the build again, we have a new error: `Cannot connect to the Docker daemon at unix:///var/run/docker.sock. Is the docker daemon running?`

To fix this, is a litle bit complicate. We need to share the local daemon with the container.
```
$ docker rm local-jenkins
$ run -v ~/tmp/jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock -p 8080:8080 -p 50000:50000 --name local-jenkins jenkins/jenkins:lts
```
Now, we need to install docker and java, again.

Running again, we take a new error: `Got permission denied while trying to connect to the Docker daemon socket at unix:///var/run/docker.sock: Post http://%2Fvar%2Frun%2Fdocker.sock/v1.39/auth: dial unix /var/run/docker.sock: connect: permission denied`

This is simple, only needs add jenkins to docker group:
```
$ docker exec -it --user root local-jenkins bash
# usermod -aG docker jenkins
```

### References
https://github.com/jenkinsci/docker/blob/master/README.md
https://medium.com/@gustavo.guss/jenkins-building-docker-image-and-sending-to-registry-64b84ea45ee9
https://github.com/iundarigun/workshop-kubernetes/blob/master/README.md
