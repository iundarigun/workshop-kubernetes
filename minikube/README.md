# Kubernetes

To emulate kubernetes, we go to install minikube. It is a tool that makes it easy to run Kubernetes locally.

## Install

To install minikube, before we must to install a tool to virtualize machines.

### Installing VirtualBox

To install virtualBox, we need to follow the instructions of the [virtualBox site](https://www.virtualbox.org/wiki/Linux_Downloads)

### Install minikube

To install Minikube, follow the next steps:
```
$ curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
$ chmod +x minikube
$ sudo cp minikube /usr/local/bin 
$ rm minikube
```

### Installing kubectl

```
$ sudo apt-get update && sudo apt-get install -y apt-transport-https
$ curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a /etc/apt/sources.list.d/kubernetes.list
$ sudo apt-get update
$ sudo apt-get install -y kubectl
```

## Run minikube

To run minikube, only need to run `minikube start` to start a default minikube. For now, we go to start a minikube using dockerhub as a registry.

```
$ minikube start -p mini-dockerhub
```

## References
- Official doc: https://kubernetes.io/docs/tasks/tools/install-minikube/
- VirtualBox site: https://www.virtualbox.org/wiki/Linux_Downloads
