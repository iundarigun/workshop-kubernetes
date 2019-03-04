# MySQL

Create a new docker instance of mysql with name local-mysql, listen in 3306 port (once)

```
$ docker run -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root --name local-mysql -d mysql
```

Connect and create the database

```
$ docker exec -it local-mysql mysql -uroot -proot
> create database preferences;
```

Start container next time:
```
$ docker start local-mysql
```

# Docker

How to generate image. Go to the preferences base path:
```
> docker build -t <user/registry>/preferences:<version> .
```

### Changing a url to run in docker
```
> docker run -p 9009:9009 -e "SPRING_DATASOURCE_URL=jdbc:mysql://<URL or IP>:3306/preferences?useSSL=false&allowPublicKeyRetrieval=true" <user/registry>/preferences:<version>
```
