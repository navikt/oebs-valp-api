FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:f34d9348e0992d0d112cc8ae840346102c7e722fe4412bd523cb67cde519357c
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]