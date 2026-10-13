FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:02c76531a4ac3c8d57885ecfea45d34babdd96b3537913e4860acef7fd155599
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]