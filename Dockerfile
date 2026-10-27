FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:05ef05db3439b79450c2f6dc1580a4cf1179a86f6d9342a52810a7a1fc2bae58
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]