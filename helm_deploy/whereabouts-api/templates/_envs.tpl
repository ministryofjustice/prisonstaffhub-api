    {{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for web and worker containers
*/}}
{{- define "deployment.envs" }}
env:
  - name: SERVER_PORT
    value: "{{ .Values.image.port }}"

  - name: SPRING_PROFILES_ACTIVE
    value: "postgres,logstash"

  - name: JAVA_OPTS
    value: "{{ .Values.env.JAVA_OPTS }}"

  - name: SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI
    value: "{{ .Values.env.SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI }}"

  - name: ELITE2API_ENDPOINT_URL
    value: "{{ .Values.env.ELITE2API_ENDPOINT_URL }}"

  - name: CASENOTES_ENDPOINT_URL
    value: "{{ .Values.env.CASENOTES_ENDPOINT_URL }}"

  - name: OAUTH_ENDPOINT_URL
    value: "{{ .Values.env.OAUTH_ENDPOINT_URL }}"

  - name: OAUTH_CLIENT_ID
    value: "{{ .Values.env.OAUTH_CLIENT_ID }}"

  - name: APPINSIGHTS_INSTRUMENTATIONKEY
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: APPINSIGHTS_INSTRUMENTATIONKEY

  - name: APPLICATIONINSIGHTS_CONNECTION_STRING
    value: "InstrumentationKey=$(APPINSIGHTS_INSTRUMENTATIONKEY)"

  - name: OAUTH_CLIENT_SECRET
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: OAUTH_CLIENT_SECRET

  - name: DATABASE_USERNAME
    valueFrom:
      secretKeyRef:
        name: dps-rds-instance-output
        key: database_username

  - name: DATABASE_PASSWORD
    valueFrom:
      secretKeyRef:
        name: dps-rds-instance-output
        key: database_password

  - name: DATABASE_NAME
    valueFrom:
      secretKeyRef:
        name: dps-rds-instance-output
        key: database_name

  - name: DATABASE_ENDPOINT
    valueFrom:
      secretKeyRef:
        name: dps-rds-instance-output
        key: rds_instance_endpoint

  - name: SQS_AWS_ACCESS_KEY_ID
    valueFrom:
      secretKeyRef:
        name: whereabouts-api-sqs-instance-output
        key: access_key_id

  - name: SQS_AWS_SECRET_ACCESS_KEY
    valueFrom:
      secretKeyRef:
        name: whereabouts-api-sqs-instance-output
        key: secret_access_key

  - name: SQS_QUEUE_NAME
    valueFrom:
      secretKeyRef:
        name: whereabouts-api-sqs-instance-output
        key: sqs_wb_name

  - name: SQS_AWS_DLQ_ACCESS_KEY_ID
    valueFrom:
      secretKeyRef:
        name: whereabouts-api-sqs-dl-instance-output
        key: access_key_id

  - name: SQS_AWS_DLQ_SECRET_ACCESS_KEY
    valueFrom:
      secretKeyRef:
        name: whereabouts-api-sqs-dl-instance-output
        key: secret_access_key

  - name: SQS_DLQ_NAME
    valueFrom:
      secretKeyRef:
        name: whereabouts-api-sqs-dl-instance-output
        key: sqs_wb_name

{{- end -}}
