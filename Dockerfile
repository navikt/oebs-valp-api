FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:5f7c2cb4218d3f84f83c18ea86dd293adeb72017b776c17b04447b1123ee8415
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]