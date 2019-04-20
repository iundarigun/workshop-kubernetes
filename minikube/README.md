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

## Kubectl
Kubectl is the instruction to interact with kubernates cluster, like minikube.

```
$ kubectl cluster-info
$ kubectl get nodes
``` 

Dashboard:
```
$ minikube -p mini-dockerhub dashboard
```

To access the bash in minikube:
```
$ minikube -p mini-dockerhub ssh
# systemctl status docker
```

We can deploy an application by yaml files. The next file is a deploy pod descriptor:
```
apiVersion: v1
kind: Pod
metadata:
  name: helloworld
spec:
  containers:
    - name: helloworld
      image: iundarigun/helloworld
      ports:
        - containerPort: 2012
```

To deploy this pod, we run the next instruction:
```
$ kubectl apply -f k8sfiles/helloworld.yaml
```

To see logs:
```
$ kubectl logs -f --tail=20 <pod_name>
```

The pod don't recovery alone, it need a wrapper to care about. This is the Deployment.
```
$ kubectl delete pod <pod_name>
```

If we want autorecovery when pod dead, we can declare pod as deployment:
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: helloworld
spec:
  replicas: 1
  selector:
    matchLabels:
      app: helloworld
  template:
    metadata:
      labels:
        app: helloworld
    spec:
      containers:
        - name: helloworld
          image: iundarigun/helloworld
          ports:
            - containerPort: 2012
```
Exists an other mode like *Deployment*, **statefulSet**. We declare it like a deployment, but when deplyoment is lightweight and used for stateless app, statefulSet is used when state has to be persisted.

For now, we can not access to this container, because we don't "publish" it. We need a service:
```
apiVersion: v1
kind: Service
metadata:
  name: helloworld
  labels:
    app: helloworld
spec:
  type: LoadBalancer
  ports:
    - port: 2012
      name: helloworld
  selector:
    app: helloworld
```
To get the url:
```
minikube -p mini-dokcerhub service servicename --url
```

## Minikube with local registry

Our goal is deploy `proxy` and `preferences` aplications. So, we try to deploy this yaml:
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: proxy
spec:
  replicas: 1
  selector:
    matchLabels:
      app: proxy
  template:
    metadata:
      labels:
        app: proxy
    spec:
      containers:
        - name: proxy
          image: local-registry:5000/proxy:0.0.1
          ports:
            - containerPort: 9000
```

The pod can not start:
```
$ kubectl get pods
NAME                          READY   STATUS         RESTARTS   AGE
helloworld-795ddd8bb8-fcksz   1/1     Running        1          32h
proxy-bf5494fcf-xl4ds         0/1     ErrImagePull   0          43s
```
Kubernetes can not pull the image. To more information, we can access ssh minkube and try to pull the container.

```
$ minikube -p mini-dockerhub ssh
                         _             _            
            _         _ ( )           ( )           
  ___ ___  (_)  ___  (_)| |/')  _   _ | |_      __  
/' _ ` _ `\| |/' _ `\| || , <  ( ) ( )| '_`\  /'__`\
| ( ) ( ) || || ( ) || || |\`\ | (_) || |_) )(  ___/
(_) (_) (_)(_)(_) (_)(_)(_) (_)`\___/'(_,__/'`\____)

> docker pull local-reigstry:5000/proxy:0.0.1 
Error response from daemon: Get https://local-reigstry:5000/v2/: dial tcp: lookup local-reigstry on 10.0.2.3:53: no such host
```
It is trying to pull from https. We need insecure-registry for ouu cluster.

To start minikube using local (insecure) registry, we need to explicit with the right params: 
```
$ minikube start -p mini-localregistry --insecure-registry local-registry:5000 --memory 4096
```
Now, if we try to deploy, the image can not be pulled again. Go again to ssh:
```
$ minikube -p mini-localregistry ssh
                         _             _            
            _         _ ( )           ( )           
  ___ ___  (_)  ___  (_)| |/')  _   _ | |_      __  
/' _ ` _ `\| |/' _ `\| || , <  ( ) ( )| '_`\  /'__`\
| ( ) ( ) || || ( ) || || |\`\ | (_) || |_) )(  ___/
(_) (_) (_)(_)(_) (_)(_)(_) (_)`\___/'(_,__/'`\____)

> docker pull local-registry:5000/proxy:0.0.1
Error response from daemon: Get http://local-registry:5000/v2/proxy/manifests/0.0.1: no basic auth credentials
```
Ok, now the problem changes. We need to say to kubernetes the user/pass to pull image from this registry. We will create a `secret` to login in the local registry:
```
$ kubectl create secret docker-registry local-registry --docker-server=local-registry:5000 --docker-username=username --docker-password=password
```

We put on the yaml file, on `spec` section, the key `imagePullSecrets` and especify the name. The yaml file looks like this:
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: proxy
spec:
  replicas: 1
  selector:
    matchLabels:
      app: proxy
  template:
    metadata:
      labels:
        app: proxy
    spec:
      imagePullSecrets:
      - name: local-registry
      containers:
        - name: proxy
          image: local-registry:5000/proxy:0.0.1
          ports:
            - containerPort: 9000
``` 

## Comunication problems

Nice. Next, we need to deploy the `preferences` app like a service. We want to indicate the port, só we can change a little the service description to specify the port:
```
apiVersion: v1
kind: Service
metadata:
  name: preferences
  labels:
    app: preferences
spec:
  type: NodePort
  ports:
    - port: 9009
      name: preferences
      nodePort: 30400
      protocol: TCP
      targetPort: 9009
  selector:
    app: preferences
```

We go to deploy service and deployment. If we get the pods' status, we found a problem with preferences' pod.
```
$ kubectl get pods
NAME                           READY   STATUS    RESTARTS   AGE
preferences-5bb95d696b-q5szl   0/1     Error     1          33s
proxy-77f99474c6-828bj         1/1     Running   0          9m20s
```

The problem is that the app can not connect to database. Make sense, because the database is not on localhost and we don't change this configuration.

So, we can declare environment variables on yaml file:
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: preferences
spec:
  replicas: 1
  selector:
    matchLabels:
      app: preferences
  template:
    metadata:
      labels:
        app: preferences
    spec:
      imagePullSecrets:
      - name: local-registry
      containers:
        - name: preferences
          image: local-registry:5000/preferences:0.0.1
          ports:
            - containerPort: 9009
          env:
          - name: SPRING_DATASOURCE_URL
            value: jdbc:mysql://192.168.25.2:3306/preferences?useSSL=false&allowPublicKeyRetrieval=true
```
Now, the pod is running. Só, go to test it. Get the url and test some endpoints.

```
minikube -p mini-localregistry service preferences --url
```
It is not working like expected.
```
"message": "Connection refused (Connection refused) executing GET http://localhost:9000/books/2"
```

We need to config the proxy endpoint url. The first aproach can be getting the url pod. 

```
kubectl get pod proxy-77f99474c6-828bj -o yaml | grep podIP
```
Then, we can used in `env` section in the yaml file:
```
    - name: URL_PROXY
      value: http://172.17.0.8:9000
```

Ok, but, is not the better solution (for example, scaling services). We can create a service to access internally. 
```
apiVersion: v1
kind: Service
metadata:
  name: proxy
  labels:
    app: proxy
spec:
  type: ClusterIP
  ports:
    - port: 80
      name: proxy
      targetPort: 9000
  selector:
    app: proxy
```
ClusterIP type exposes the service on a cluster-internal IP. Choosing this value makes the service only reachable from within the cluster.
```
    - name: URL_PROXY
      value: http://proxy
```

## How to deploy a new version

If we want to delivery a new version, we can creating a problem if the time to up the new instance is too big:
```
M=getTestValue, response={"status":"UP"}
M=getTestValue, response={"status":"UP"}
M=getTestValue, response={"status":"UP"}
M=getTestValue, response={"status":"UP"}
M=getTestValue, response={"status":"UP"}
M=getTestValue, erro=I/O error on GET request for "http://192.168.99.109:30400/actuator/health": Connection refused (Connection refused); nested exception is java.net.ConnectException: Connection refused (Connection refused)
M=getTestValue, erro=I/O error on GET request for "http://192.168.99.109:30400/actuator/health": Connection refused (Connection refused); nested exception is java.net.ConnectException: Connection refused (Connection refused)
M=getTestValue, erro=I/O error on GET request for "http://192.168.99.109:30400/actuator/health": Connection refused (Connection refused); nested exception is java.net.ConnectException: Connection refused (Connection refused)
M=getTestValue, erro=I/O error on GET request for "http://192.168.99.109:30400/actuator/health": Connection refused (Connection refused); nested exception is java.net.ConnectException: Connection refused (Connection refused)
M=getTestValue, erro=I/O error on GET request for "http://192.168.99.109:30400/actuator/health": Connection refused (Connection refused); nested exception is java.net.ConnectException: Connection refused (Connection refused)
```
This problem is about definition of ready. By default, when the container is running, it is ok for the Kubernetes. We need to say that don't use the container until the new is ready to receive new requisitions.
```
  readinessProbe:
    httpGet:
      path: /actuator/health
      port: 9009
    initialDelaySeconds: 10
    timeoutSeconds: 2
    periodSeconds: 3
    failureThreshold: 1
```
Now, when we delivery a new version, only kill the old version when the `/actuator/health` endpoint is returning 200.

```
$ kubectl get pods
NAME                           READY   STATUS    RESTARTS   AGE
preferences-6dcfff7c9f-jcg5s   0/1     Running   0          15s
preferences-8f7448fb5-26ctv    1/1     Running   0          2m41s
proxy-77f99474c6-vsbh7         1/1     Running   0          43m
```

## Open ports
If we want access to minikube from other host, we need to open ports of the virtual machine:
```
$ vboxmanage controlvm "mini-localregistry" natpf1 "preferences,tcp,,30400,,30400"
```

## References
- Official doc: https://kubernetes.io/docs/tasks/tools/install-minikube/
- VirtualBox site: https://www.virtualbox.org/wiki/Linux_Downloads
