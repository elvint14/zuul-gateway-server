FROM openjdk:11.0.13
ADD build/libs/zuul-gateway-server-1.jar zuul-gateway-server-1.jar
ENTRYPOINT ["java", "-jar","zuul-gateway-server-1.jar"]
EXPOSE 8001