# =========================
# BƯỚC 1: BUILD WAR
# =========================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests


# =========================
# BƯỚC 2: CHẠY TOMCAT
# =========================
FROM tomcat:9.0-jdk17-temurin

# Xóa ứng dụng mặc định của Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR vào Tomcat
COPY --from=build /app/target/sendEmailC14-1.0-SNAPSHOT.war \
    /usr/local/tomcat/webapps/ROOT.war

# Render sử dụng PORT
EXPOSE 10000

CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT:-10000}\\\"/\" /usr/local/tomcat/conf/server.xml && catalina.sh run"]