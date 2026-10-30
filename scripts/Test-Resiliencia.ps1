$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$runtimeDir = Join-Path $repoRoot 'platform\runtime'
$pidPath = Join-Path $runtimeDir 'pagatu-catalogo-ms.pid'
$catalogServiceId = [int](Get-Content -LiteralPath $pidPath)
$catalogService = Get-CimInstance Win32_Process -Filter "ProcessId=$catalogServiceId"
if (-not $catalogService -or $catalogService.CommandLine -notmatch 'pagatu-catalogo-ms') {
    throw 'El PID no corresponde al catálogo del proyecto. No se detendrá ningún proceso.'
}
$wasStopped = $false
$testFailed = $false
try {
    Write-Host 'Deteniendo únicamente catálogo para comprobar la caída real...'
    Stop-Process -Id $catalogServiceId -ErrorAction Stop
    $wasStopped = $true
    & node (Join-Path $PSScriptRoot 'Verify-Resilience.cjs') fallo
    if ($LASTEXITCODE -ne 0) { $testFailed = $true }
} finally {
    if ($wasStopped) {
        Write-Host 'Recuperando catálogo. No se eliminaron datos.'
        $modulePath = Join-Path $repoRoot 'Services\pagatu-catalogo-ms'
        $jarPath = (Get-ChildItem -LiteralPath (Join-Path $modulePath 'target') -Filter '*.jar' | Select-Object -First 1).FullName
        $catalogRestart = Start-Process -FilePath (Get-Command java).Source -ArgumentList @('-Xmx384m','-jar',('"'+$jarPath+'"')) -WorkingDirectory $modulePath -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $runtimeDir 'pagatu-catalogo-ms.stdout.log') -RedirectStandardError (Join-Path $runtimeDir 'pagatu-catalogo-ms.stderr.log')
        $catalogRestart.Id | Set-Content -LiteralPath $pidPath
    }
}
$ready = $false
for ($attempt=0; $attempt -lt 120; $attempt++) {
    try { $null=Invoke-RestMethod 'http://localhost:8080/actuator/health/liveness' -TimeoutSec 3; $ready=$true; break } catch { Start-Sleep -Seconds 2 }
}
if (-not $ready) { throw 'Catálogo no volvió a responder. Revisa su log; el resto de servicios sigue en ejecución.' }
if ($testFailed) { throw 'Catálogo se recuperó, pero falló alguna comprobación. Conserva la salida.' }
& node (Join-Path $PSScriptRoot 'Verify-Resilience.cjs') recuperacion
if ($LASTEXITCODE -ne 0) { throw 'El servicio se recuperó, pero falta revisar el estado final del circuito.' }
Write-Host 'PRUEBA COMPLETA: catálogo caído, carritos guardados, OPEN, HALF_OPEN y recuperación CLOSED.'
