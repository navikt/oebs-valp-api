FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:689d87058a37ee3fccbd79e526c9535789926cdf43746cd988d087989baaa22c
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]