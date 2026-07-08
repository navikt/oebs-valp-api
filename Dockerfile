FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:e44d199e875b531232ab20aca4fc2f62d214cc1c097cb0f6ef8d7f3fae8b9693
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]