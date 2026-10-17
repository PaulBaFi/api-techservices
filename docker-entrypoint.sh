#!/bin/sh
set -eu

truststore=/tmp/aiven-ca.p12
case "${DB_URL:-}" in
  *file:*|*C:\\*|*C:/*|*trustCertificateKeyStoreUrl*|*trustCertificateKeyStoreType*|*trustCertificateKeyStorePassword*)
    echo 'DB_URL contiene una ruta local o propiedades duplicadas del truststore.' >&2; exit 1 ;;
esac
case "${DB_URL:-}" in
  *sslMode=VERIFY_IDENTITY*) : ;;
  *) echo 'DB_URL debe incluir sslMode=VERIFY_IDENTITY.' >&2; exit 1 ;;
esac
: "${AIVEN_CA_CERT_BASE64:?AIVEN_CA_CERT_BASE64 es requerido}"
: "${AIVEN_TRUSTSTORE_PASSWORD:?AIVEN_TRUSTSTORE_PASSWORD es requerido}"
printf '%s' "$AIVEN_CA_CERT_BASE64" | base64 -d > /tmp/aiven-ca.pem
java -cp /app/truststore-init TruststoreInitializer
# Connector/J receives these through Spring/Hikari properties, not JVM arguments.
export AIVEN_TRUSTSTORE_URL="file:$truststore"
export AIVEN_TRUSTSTORE_TYPE=PKCS12
exec java -jar /app/app.jar
