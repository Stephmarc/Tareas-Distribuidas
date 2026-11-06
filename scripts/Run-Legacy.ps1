$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
$portableNode=Join-Path $projectRoot 'platform/runtime/node24.exe'
$nodeForTest=if(Test-Path -LiteralPath $portableNode){$portableNode}else{(Get-Command node).Source}
Start-Transcript -LiteralPath (Join-Path $projectRoot 'platform/runtime/evidencias-20261105/s07-legacy-powershell.txt') -Force
try{
 Write-Host "Fecha real: $(Get-Date -Format o) | Windows: $([System.Security.Principal.WindowsIdentity]::GetCurrent().Name)"
 Write-Host 'COMANDO EJECUTADO: node scripts/Verify-Legacy.cjs'
 & $nodeForTest (Join-Path $PSScriptRoot 'Verify-Legacy.cjs') 2>&1 | ForEach-Object {Write-Host $_}
 if($LASTEXITCODE -ne 0){throw 'S07 propio fallo; revisar reporte.'}
}finally{Stop-Transcript}
