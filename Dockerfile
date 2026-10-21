FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:3a5436538e2d4dfccb6226934f908e4b26ba79e2c76816fb97ffc67798b83c41
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]