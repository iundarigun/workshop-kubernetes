FROM openjdk:11.0.2-jre-stretch

ENTRYPOINT ["/usr/bin/java", "-jar", "/home/proxy.jar"]

ADD build/libs/*.jar /home/proxy.jar
