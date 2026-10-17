param(
    [string]$CaPath = $env:AIVEN_CA_CERT_PATH,
    [string]$OutputPath = $(if ($env:AIVEN_TRUSTSTORE_PATH) { $env:AIVEN_TRUSTSTORE_PATH } else { 'certs\aiven-ca.p12' })
)
$ErrorActionPreference = 'Stop'
if (-not $CaPath -or -not (Test-Path $CaPath)) { throw 'Indica AIVEN_CA_CERT_PATH con la ruta del CA PEM de Aiven.' }
$password = Read-Host 'Contraseña del truststore PKCS12 (no se guarda en archivos)'
$output = Join-Path $PSScriptRoot "..\$OutputPath"
New-Item -ItemType Directory -Force -Path (Split-Path $output) | Out-Null
& keytool -importcert -noprompt -trustcacerts -alias aiven-ca -file $CaPath -keystore $output -storetype PKCS12 -storepass $password
if ($LASTEXITCODE -ne 0) { throw "keytool terminó con código $LASTEXITCODE" }
Write-Output "Truststore creado: $output"
