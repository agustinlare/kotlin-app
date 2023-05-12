FROM adoptopenjdk/openjdk14:jdk-14.0.2_12

ARG ELASTIC_URL
ARG ELASTIC_TOKEN
ARG STAGE

WORKDIR /app
ADD target/app.jar ./app.jar
ADD apm-agent/elastic-apm-agent-1.29.0.jar ./agent.jar


ENTRYPOINT [ "sh", "-c", "java -javaagent:./agent.jar -Delastic.apm.service_name=dummy -Delastic.apm.server_urls=$ELASTIC_URL -Delastic.apm.secret_token=$ELASTIC_TOKEN -Delastic.apm.environment=$STAGE -Delastic.apm.application_packages=com.clave.dummy -jar app.jar" ]
