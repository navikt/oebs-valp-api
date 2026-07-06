FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:5f0fa689f3f97213b6518090da231adbcdca89643a8836c96725cbfebbf06a3f
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]