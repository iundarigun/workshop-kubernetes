# Jenkins

Run jenkins in docker (create folder `~/tmp/jenkins_home` before):
```
$ docker run -v ~/tmp/jenkins_home:/var/jenkins_home -p 8080:8080 -p 50000:50000 --name local-jenkins jenkins/jenkins:lts
```
Config a job with pipeline. We need to create two credentials params for the build, one for github and other for the docker registry. 
```
node {
    def registry = "iundarigun/preferences"
    def registryCredential = "${env.DockerCredentials}"
    def gitCredential = "${env.GitCredentials}"
    def gradlew = "./gradlew"
    stage(name: "Clone"){
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: gitCredential, url: 'https://github.com/iundarigun/workshop-kubernetes']]])
    }
    dir("preferences"){
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
## Java 11
We take an error envolving java version. To fix this, we go to install java 11 into the docker container.
```
$ docker exec -it --user root local-jenkins bash
# wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/11.0.2+9/f51449fcd52f4d52b93a989c5c56ed3c/jdk-11.0.2_linux-x64_bin.deb
# dpkg -i jdk-11.0.2_linux-x64_bin.deb
# unlink /docker-java-home
# ln -s /usr/lib/jvm/jdk-11.0.2 /docker-java-home
```

## Docker

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
$ docker run -v ~/tmp/jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock -p 8080:8080 -p 50000:50000 --name local-jenkins jenkins/jenkins:lts
```
Now, we need to install docker and java, again. 
- if you don't want to do all again, you can just jump to **Custom image** topic 

Running again, we take a new error: `Got permission denied while trying to connect to the Docker daemon socket at unix:///var/run/docker.sock: Post http://%2Fvar%2Frun%2Fdocker.sock/v1.39/auth: dial unix /var/run/docker.sock: connect: permission denied`

This is simple, only needs add jenkins to docker group:
```
$ docker exec -it --user root local-jenkins bash
# usermod -aG docker jenkins
```
After this, restart the jenkins' container and rerun the pipeline! 

## Uploading in local-registry

We need to change a few parts of our script.

_Changing tag - line 2 of script_
```
def registry = "local-registry:5000/preferences"
```

_Changing where docker login - line 17 of script_
```
docker.withRegistry( 'http://local-registry:5000', registryCredential ) {

```

The complete script:

```
node {
    def registry = "local-registry:5000/preferences"
    def registryCredential = "${env.DockerCredentials}"
    def gitCredential = "${env.GitCredentials}"
    def gradlew = "./gradlew"
    stage(name: "Clone"){
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: gitCredential, url: 'https://github.com/iundarigun/workshop-kubernetes']]])
    }
    dir("preferences"){
        stage(name: "Clean"){
            sh "${gradlew} clean "
        }
        stage(name: "Compile"){
            sh "${gradlew} build "
        }
        stage(name: "docker"){
            docker.withRegistry( 'http://local-registry:5000', registryCredential ) {
                dockername = docker.build(registry + ":$BUILD_NUMBER", ".")
                dockername.push()
            }
        }
    }
}
```
## Get version for app

We can get version directly form the code. We need to def a new var on the script, create a new stage to get the version and define a function to read this version:

```
node {
    def registry = "local-registry:5000/preferences"
    def registryCredential = "${env.DockerCredentials}"
    def gitCredential = "${env.GitCredentials}"
    def gradlew = "./gradlew"
    def version= ''
    stage(name: "Clone"){
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: gitCredential, url: 'https://github.com/iundarigun/workshop-kubernetes']]])
    }
    dir("preferences"){
        stage(name: "Clean"){
            sh "${gradlew} clean "
        }
        stage(name: "Version"){
            version=getVersion()
        }
        stage(name: "Compile"){
            sh "${gradlew} build "
        }
        stage(name: "docker"){
            docker.withRegistry( 'http://local-registry:5000', registryCredential ) {
                dockername = docker.build(registry + ":${version}", ".")
                dockername.push()
            }
        }
    }
}
def getVersion() {
  def matcher = readFile("gradle.properties") =~ 'version=(.+)'
    matcher ? matcher[0][1] : null
}
```

## Increase version and commit

The version of the container is a combination of the numbers, major (the first number), minor (the second number) and path (the last number). 

Our app is ready to change version from gradle task. So, we will be create a new parameter to run the job and we will use to determinate the increase number. This parameter can only take three values:
- patch
- minor
- major

Now, we add this param as a var at the begining of the script and use to generate the version.

The script seems like this:

```
node {
    def registry = "local-registry:5000/preferences"
    def registryCredential = "${env.DockerCredentials}"
    def gitCredential = "${env.GitCredentials}"
    def gradlew = "./gradlew"
    def typeVersion = "${env.Version}"
    def version= ''
    stage(name: "Clone"){
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: gitCredential, url: 'https://github.com/iundarigun/workshop-kubernetes']]])
    }
    dir("preferences"){
        stage(name: "Clean"){
            sh "${gradlew} clean "
        }
        stage(name: "Version"){
            sh "${gradlew} -PtypeVersion=${typeVersion} patchVersion"
            version=getVersion()
        }
        stage(name: "Compile"){
            sh "${gradlew} build "
        }
        stage(name: "docker"){
            docker.withRegistry( 'http://local-registry:5000', registryCredential ) {
                dockername = docker.build(registry + ":${version}", ".")
                dockername.push()
            }
        }
    }
}
def getVersion() {
  def matcher = readFile("gradle.properties") =~ 'version=(.+)'
    matcher ? matcher[0][1] : null
}
```

This aproach is working but we can detect two points to improve:
- This pipeline only runs from master branch. This is a litle restrictive
- If we want to change de version definitively, we need to do in the code and commit the changes.

First, we create a new _string parameter_ for the pipeline named **branch**. Add this param as a var and use in stage clone:
```
    // Code before is hide
    // ...
    def branch = "${env.Branch}"
    stage(name: "Clone"){
        checkout([$class: 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: gitCredential, url: 'https://github.com/iundarigun/workshop-kubernetes']]])
    }
    // Code after is hide
    // ...
```

To commit needs some others changes.

- WithCredentials
- Store auth

### WithCredentials
We need change _Clone_ stage:
```
    // Code before is hide
    // ...
    stage(name: "Clone"){
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: gitCredential,
                          usernameVariable: 'USERGIT', passwordVariable: 'PASSGIT']]) {
            git url: "https://${env.USERGIT}:${env.PASSGIT}@github.com/iundarigun/workshop-kubernetes", credentialsId: gitCredential, branch: branch
        }    
    }
    // Code after is hide
    // ...
    
```
After, we add new step at the end of the pipeline:

```
    // Code before is hide
    // ...
    stage(name: "Commit changes"){
        sh "git commit -am \"version ${version}\""
        sh "git push origin ${branch}"
    }
    // Code after is hide
    // ...
```

Oh, we take this error:
```
*** Please tell me who you are.

Run

  git config --global user.email "you@example.com"
  git config --global user.name "Your Name"
```

So, we need to do this. I like to put before all steps, like this:

```
node {
    def registry = "local-registry:5000/preferences"
    def registryCredential = "${env.DockerCredentials}"
    def gitCredential = "${env.GitCredentials}"
    def gradlew = "./gradlew"
    def typeVersion = "${env.Version}"
    def version= ''
    def branch = "${env.Branch}"
    
    sh "git config user.email \"jenkins@devcave.com.br\""
    sh "git config user.name \"Jenkins\""

    stage(name: "Clone"){
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: gitCredential,
                          usernameVariable: 'USERGIT', passwordVariable: 'PASSGIT']]) {
            git url: "https://${env.USERGIT}:${env.PASSGIT}@github.com/iundarigun/workshop-kubernetes", credentialsId: gitCredential, branch: branch
        }    
    }
    dir("preferences"){
        stage(name: "Clean"){
            sh "${gradlew} clean "
        }
        stage(name: "Version"){
            sh "${gradlew} -PtypeVersion=${typeVersion} patchVersion"
            version=getVersion()
        }
        stage(name: "Compile"){
            sh "${gradlew} build "
        }
        stage(name: "docker"){
            docker.withRegistry( 'http://local-registry:5000', registryCredential ) {
                dockername = docker.build(registry + ":${version}", ".")
                dockername.push()
            }
        }
        stage(name: "Commit changes"){
            sh "git commit -am \"version ${version}\""
            sh "git push origin ${branch}"            
        }
    }
}
def getVersion() {
  def matcher = readFile("gradle.properties") =~ 'version=(.+)'
    matcher ? matcher[0][1] : null
}
```

### WithCredentials
This aproach is more elegant. We can use _store_ option on git to maintain credentials cache:

```
    // Code before is hide
    // ...
    stage(name: "Prepare"){
        sh "git config credential.helper cache"
        sh "git config user.email \"jenkins@devcave.com.br\""
        sh "git config user.name \"Jenkins\""
    }
    stage(name: "Clone"){
        git url: "https://iundarigun@github.com/iundarigun/workshop-kubernetes", credentialsId: gitCredential, branch: branch
            
    }
    // Some code
    // ...
    stage(name: "Commit changes"){
        sh "git commit -am \"version ${version}\""
        sh "git push origin ${branch}"            
    }
    // Code after is hide
    // ...
 
```
Unfortunately, this aproach is not valid for public repositories. An alternative could be create a git-credential file on Jenkins. More information on https://git-scm.com/book/en/v2/Git-Tools-Credential-Storage

## Versioning Pipeline

To finalize this chapter, we can create a Jenkinsfile on our code base to versioning all changing for the pipeline.

# Deploying in kubernetes

Disclaimer: This example is only to deploy in minikube

### Run the pipeline

**Install kubectl**

```
$ docker exec -it --user root local-jenkins bash
# sudo apt-get update && sudo apt-get install -y apt-transport-https
# curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
# echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a /etc/apt/sources.list.d/kubernetes.list
# sudo apt-get update
# sudo apt-get install -y kubectl
```

**Open kube port**

We need to allow connections on kube port (8443) to deploy by kubectl command. We need to open this por with the next instruction on minikube host (we choose a random port 14304):

```
$ vboxmanage controlvm "mini-localhub" natpf1 "kubectl,tcp,,14304,,8443"
```

**Create config kubectl**

Create file inside Jenkins (or in mapped folder) /var/jenkins_home/.kube/config
```
apiVersion: v1
clusters:
- cluster:
    insecure-skip-tls-verify: true
    server: https://<ip host minikub>:<port>
  name: mini-localregistry
contexts:
- context:
    cluster: mini-localregistry
    user: mini-localregistry
  name: mini-localregistry
current-context: mini-localregistry
kind: Config
preferences: {}
users:
- name: mini-localregistry
  user:
    client-certificate: /var/jenkins_home/.minikube/client.crt
    client-key: /var/jenkins_home/.minikube/client.k
```

We add to elements in Jenkinsfile. One on `version` step:
```
    // Code before is hide
    // ...
    stage(name: "Version"){
        sh "${gradlew} -PtypeVersion=${typeVersion} patchVersion"
        version=getVersion()
        sh "${gradlew} replaceVersion -PprojectVersion=${version}"
    }
    // Code after is hide
    // ...
```
The other element is a new step after commit:
```
    // Code before is hide
    // ...
    stage(name: "Deploy"){
        sh "kubectl apply -f k8s/preferences-jenkins.yaml"
    }
    // Code after is hide
    // ...
```
We can put an approval stage, before the deploy:
```
    // Code before is hide
    // ...
    try{
        stage(name: "Approval", concurrency: 1)
        timeout(time: 2, unit: "HOURS") {
            input(message: "Approve Deployment Preferences branch  ${branch} ?")
        }
    }
    catch (all){
        throw all
    }
    stage(name: "Deploy"){
    // Code after is hide
    // ...
```

## Custom image

To run a custom image with docker and java 11 we can run the next instruction:
```
$ docker run -v ~/tmp/jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock -p 8080:8080 -p 50000:50000 --name local-jenkins iundarigun/jenkins-java11-docker
```

The Dockerfile to build the image is in this folder. 
- https://github.com/iundarigun/workshop-kubernetes/blob/master/jenkins/Dockerfile 


## References
- https://github.com/jenkinsci/docker/blob/master/README.md
- https://medium.com/@gustavo.guss/jenkins-building-docker-image-and-sending-to-registry-64b84ea45ee9
- https://github.com/iundarigun/workshop-kubernetes/blob/master/README.md
- https://jenkins.io/doc/book/pipeline/
