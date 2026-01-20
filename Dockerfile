# Based on https://github.com/Carrieukie/ComposeWebDocker

FROM dhi.io/gradle:9-jdk21-debian13-dev AS build

COPY --chown=gradle:gradle . /app

WORKDIR /app

RUN gradle clean wasmJsBrowserDistribution --no-daemon

FROM dhi.io/busybox:1 AS runtime

COPY --from=build \
	/app/composeApp/build/dist/wasmJs/productionExecutable \
	/app

EXPOSE 80

CMD ["httpd", "-f", "-p", "80", "-h", "/app"]
