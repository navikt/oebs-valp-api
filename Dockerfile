FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:321d49f112eb4bb87c98772e5a36d85d7c4b267eb42969040b89cbbf1d4d9bd8
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]