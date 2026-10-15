FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:68da7450940c9ef4a2e8fef77fcee89a6b82c273908b08d40802f57e0298c0a2
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]