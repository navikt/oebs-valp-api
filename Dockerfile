FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:ba8a4ec8fc24476762f6f3c8cdf861f18681845a5bc714b610cf1316859f7e2e
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]