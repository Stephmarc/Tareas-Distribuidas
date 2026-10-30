$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
& git -C $repoRoot push origin main
if ($LASTEXITCODE -ne 0) { throw 'GitHub no acepto el push. Conserva el mensaje de autenticacion para revisarlo.' }
Write-Host 'Push completado. GitHub Actions publicara el MkDocs personal.'
