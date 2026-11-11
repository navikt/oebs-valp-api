FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:ab7d3f32983b1582ee5eb8006d17a21516735e0ebd977ed512491c011ea23a72
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]