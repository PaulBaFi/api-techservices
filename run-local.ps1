$ErrorActionPreference = 'Stop'
$envFile = Join-Path $PSScriptRoot '.env'
if (-not (Test-Path $envFile)) { throw 'No existe .env. Copia .env.example como .env y completa sus valores.' }
Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith('#') -and $line.Contains('=')) {
        $parts = $line -split '=', 2
        $name = $parts[0].Trim()
        $value = $parts[1].Trim()
        if ($value.Length -ge 2 -and (($value.StartsWith('"') -and $value.EndsWith('"')) -or ($value.StartsWith("'") -and $value.EndsWith("'")))) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        # An empty value intentionally removes the process variable. The
        # application.properties default keeps it empty instead of inheriting an old value.
        [Environment]::SetEnvironmentVariable($name, $value, 'Process')
    }
}
# Local API instances are intentionally exposed on 8082 for Postman testing.
# This override prevents a PORT value inherited from another launch from taking 8080.
[Environment]::SetEnvironmentVariable('PORT', '8082', 'Process')
& (Join-Path $PSScriptRoot 'mvnw.cmd') spring-boot:run
