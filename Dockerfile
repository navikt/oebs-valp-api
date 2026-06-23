FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:1871487456d9e15d75df1fb24a2eb603af6ba74533f7c7ce9dfd4cd118dd4392
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]