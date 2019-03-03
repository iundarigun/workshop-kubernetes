# Workshop Kubernetes #

Guide for workshop of kubernetes.

## Prerequesite ##

### Install Docker ###
We can follow the instructions of the [official docker's site](https://docs.docker.com/install/).

if you are using mint, perhaps needs somelse diferent, because mint is ubutu based, but is not an ubuntu:
```
$ sudo apt-get update
$ sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -    
$ sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(. /etc/os-release; echo "$UBUNTU_CODENAME") stable"
$ sudo apt-get update
$ sudo apt-get install docker-ce docker-ce-cli containerd.io
```
The instruction `. /etc/os-release; echo "$UBUNTU_CODENAME"`  translates the mint version to ubuntu version.

For run without sudo:
```
$ sudo usermod -aG docker $USER
```
(reboot the user session)

### References

[Post Install on official documentation](https://docs.docker.com/install/linux/linux-postinstall/)

