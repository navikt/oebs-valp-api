FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:532ae517480880f98d9effb55f4924dc578dad0b53d486670e8693c90f13a157
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]