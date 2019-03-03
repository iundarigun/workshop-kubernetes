# Configure registry

To use registry without autentication, we can run the command:
```
$ docker run -d -p 5000:5000 --name registry registry:2
```

## Adding security

We can protect our registry with user and password. We have 3 models of security. We will use the **httppasswd** modelity. 

First, we create a file *htpasswd* or other name and put user/pass:
```
$ docker run --rm --entrypoint htpasswd registry:2 -Bbn username password  > ~/workspace/workshop-kubernetes/registry/htpasswd
```
Replace *username* for your username and *password* for your password.

Next, we can start the registry:
```
$ docker run -d -p  5000:5000 -e REGISTRY_AUTH_HTPASSWD_REALM=basic-realm -e REGISTRY_AUTH_HTPASSWD_PATH=/pass/htpasswd -v ~/workspace/workshop-kubernetes/registry:/pass --name registry registry:2
```
### Pushing

We can not use the localhost. So, we need to add to /etc/hosts a new line:
```
local-registry      <your IP address>
```

To create images (proxy for example):
```
$ docker build -t local-registry:5000/proxy:0.0.1 .
```
Push image:
```
$ docker push local-registry:5000/proxy:0.0.1
```
We received an error:
```
The push refers to repository [local-registry:5000/proxy]
Get https://local-registry:5000/v2/: http: server gave HTTP response to HTTPS client
```
We need say to our docker to accept http for our IP Address

Edit the file `/etc/docker/daemon.json` and add the next configuration (if not exists, create with this configuration):
```
{
  "insecure-registries" : ["local-registry:5000"]
}
```



## References
- Interessant article in medium: [Private Docker registry](https://medium.com/@cnadeau_/private-docker-registry-part-1-basic-local-example-c409582e0e3f)
- Official documentation for registry: [Configuring a registry](https://docs.docker.com/registry/configuration/)
- Insecure registries: https://docs.docker.com/registry/insecure/