$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$dockerExecutable = Join-Path $env:LOCALAPPDATA 'Programs\DockerDesktop\resources\bin\docker.exe'
if (-not (Test-Path -LiteralPath $dockerExecutable)) { $dockerExecutable = (Get-Command docker -ErrorAction Stop).Source }
& $dockerExecutable compose --env-file (Join-Path $repoRoot 'platform\.env') -f (Join-Path $repoRoot 'platform\compose-dev.yml') up -d kafka
if ($LASTEXITCODE -ne 0) { throw 'Kafka no pudo reiniciarse. Conserva la salida para revisar el error.' }
Write-Host 'Se corrigieron los permisos del volumen y se inicio Kafka. No se borraron datos ni se recompilo Java.'
