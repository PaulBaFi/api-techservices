#!/bin/sh
set -eu

truststore=/tmp/aiven-ca.p12
if [ -n "${AIVEN_CA_CERT_BASE64:-}" ]; then
  printf '%s' "$AIVEN_CA_CERT_BASE64" | base64 -d > /tmp/aiven-ca.pem
  keytool -importcert -noprompt -trustcacerts -alias aiven-ca \
    -file /tmp/aiven-ca.pem -keystore "$truststore" -storetype PKCS12 \
    -storepass "${AIVEN_TRUSTSTORE_PASSWORD:?AIVEN_TRUSTSTORE_PASSWORD es requerido}"
  export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Djavax.net.ssl.trustStore=$truststore -Djavax.net.ssl.trustStoreType=PKCS12 -Djavax.net.ssl.trustStorePassword=$AIVEN_TRUSTSTORE_PASSWORD"
fi
exec java -jar /app/app.jar
