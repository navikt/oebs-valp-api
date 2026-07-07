FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:83f45ad778126616cca502a30b3262cf6c878fabf9b6064c188428cd869b2e5f
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]