FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:74946cfc55d80d35fbd450c36c669de1f334769d76433108609678191ad83656
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]