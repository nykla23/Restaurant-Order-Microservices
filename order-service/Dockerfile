# FROM eclipse-temurin:25-jre
#
# WORKDIR /app
#
# # 复制JAR文件
# COPY target/user-service-1.0.0.jar app.jar
#
# # 暴露端口
# EXPOSE 8081
#
# # 运行应用
# ENTRYPOINT ["java", "-jar", "app.jar"]
#
#
#
# 构建阶段
FROM maven:3.8.4-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:11-jre-slim
WORKDIR /app

# 复制构建产物
COPY --from=build /app/target/*.jar app.jar

# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

# 创建日志目录
RUN mkdir -p /app/logs

# 暴露端口
EXPOSE ${SERVER_PORT}

# 运行应用
ENTRYPOINT ["java", "-jar", "/app/app.jar"]