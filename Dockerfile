FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:62997015e83c649d724b1cc7b97e2644311b9222527b9b0eab5df1d8d26deb0e
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]