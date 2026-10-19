FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:9eec756379ed5f12ebade505e5b7ecc26e2eb55b46c137c0c47aad8a1757913c
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]