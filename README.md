# API TechServices Perú

API REST segura para consultar clientes con API Key y token Bearer.

## Puesta en marcha
1. En phpMyAdmin, ejecuta `sql/01_init.sql`.
2. Copia `.env.example` a `.env` y completa `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`.
3. Ejecuta `./run-local.ps1` en PowerShell, o `./mvnw.cmd clean verify`.

La segunda credencial es `API_KEY_SEGURA_2_2026` y `TOKEN_SEGUNDO_654321` (saldo inicial 2).

## Prueba
```bash
curl -H "x-api-key: MI_API_KEY_2026" -H "Authorization: Bearer TOKEN_SEGURO_123456" http://localhost:8080/api/clientes/1
```

En Render, configura `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y opcionalmente `PORT`; el Dockerfile usa Java 21.

## Aiven MySQL

No guardes la contraseña ni la CA en Git. Copia `.env.aiven.example` a `.env` y completa `DB_PASSWORD` localmente.
La URL JDBC debe conservar `sslMode=VERIFY_IDENTITY`. Genera el truststore local con:

```powershell
.\scripts\create-aiven-truststore.ps1
```

El cliente MariaDB de XAMPP importa el SQL solicitando la contraseña de forma interactiva:

```powershell
& 'C:\xampp\mysql\bin\mysql.exe' --host=techservices-db-paulalexisbf0104-e5dc.b.aivencloud.com --port=26416 --user=avnadmin --ssl-ca='C:\Users\LENOVO\Downloads\ca.pem' --ssl-verify-server-cert -p < .\sql\01_init.sql
```

La contraseña se escribe cuando `mysql` muestre `Enter password:`; nunca se pone en el comando. Después verifica con `SELECT COUNT(*) FROM bd_techservices.clientes;` y `SELECT COUNT(*) FROM bd_techservices.usuarios_api;` (deben ser 5 y 2).

En Render, configura `AIVEN_CA_CERT_BASE64` con el contenido Base64 del CA, `AIVEN_TRUSTSTORE_PASSWORD` como secreto, y la misma `DB_URL` con `sslMode=VERIFY_IDENTITY`. El entrypoint crea el truststore en `/tmp` al arrancar.

## Importador JDBC temporal

El importador aislado `tools/AivenSqlImporter.java` no forma parte del arranque de Spring Boot. Usa Connector/J, TLS `VERIFY_IDENTITY`, comprueba que las tablas remotas no tengan datos y solo entonces ejecuta `sql/01_init.sql`.

```powershell
$jar = (Get-ChildItem "$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j" -Recurse -Filter '*.jar' | Select-Object -First 1 -ExpandProperty FullName)
javac -cp $jar -d target\aiven-importer tools\AivenSqlImporter.java
java -cp "target\aiven-importer;$jar" AivenSqlImporter
```
