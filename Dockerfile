FROM openjdk:11-slim
MAINTAINER HMPPS Digital Studio <info@digital.justice.gov.uk>

RUN apt-get update && apt-get install -y curl

RUN addgroup --gid 2000 --system appgroup && \
    adduser --uid 2000 --system appuser --gid 2000

# Install AWS RDS Root cert into Java truststore
RUN mkdir /root/.postgresql \
  && curl https://s3.amazonaws.com/rds-downloads/rds-ca-2015-root.pem \
    > /root/.postgresql/root.crt

WORKDIR /app

COPY build/libs/whereabouts-api*.jar /app/app.jar
COPY run.sh /app

ENV TZ=Europe/London
RUN ln -snf "/usr/share/zoneinfo/$TZ" /etc/localtime && echo "$TZ" > /etc/timezone

RUN chown -R appuser:appgroup /app
USER 2000

ENTRYPOINT ["/bin/sh", "/app/run.sh"]
