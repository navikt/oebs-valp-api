FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:4cd0c2a3a922ccbd8972c8b8956bdaf5372b946c71ac0ff9a073f3a78e2da27c
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]