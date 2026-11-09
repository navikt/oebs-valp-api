FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:8ed2bf31fa1188ad0c22a5bde4fc212669fe68fe997e0dbe4e6dbeea95d80226
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]