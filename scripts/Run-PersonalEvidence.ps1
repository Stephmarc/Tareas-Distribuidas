param([ValidateSet('s06','s07','s08','s09','s10','auth','resiliencia')][string]$Sesion='s06')
$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
$portableNode=Join-Path $projectRoot 'platform/runtime/node24.exe'
$nodeForTest=if(Test-Path -LiteralPath $portableNode){$portableNode}else{(Get-Command node).Source}
$evidenceDir=Join-Path $projectRoot 'platform/runtime/evidencias-20261105'
New-Item -ItemType Directory -Force $evidenceDir|Out-Null
Start-Transcript -LiteralPath (Join-Path $evidenceDir "$Sesion-powershell.txt") -Force
try{
 Write-Host "Estudiante: Jannys Graciela Navarro Acrota | Tarea personal | $Sesion"
 Write-Host "Fecha real: $(Get-Date -Format o) | Windows: $([System.Security.Principal.WindowsIdentity]::GetCurrent().Name)"
 Write-Host "Directorio de trabajo: $projectRoot"
 if($Sesion -eq 'resiliencia'){
  Write-Host 'COMANDO EJECUTADO: & .\scripts\Test-Resiliencia.ps1'
  & "$PSScriptRoot/Test-Resiliencia.ps1"
 }elseif($Sesion -eq 'auth'){
  Write-Host 'COMANDO EJECUTADO: node scripts/Verify-Auth.cjs'
  & $nodeForTest (Join-Path $PSScriptRoot 'Verify-Auth.cjs') 2>&1 | ForEach-Object { Write-Host $_ }
  if($LASTEXITCODE -ne 0){throw 'La prueba auth fallo.'}
 }else{
  Write-Host "COMANDO EJECUTADO: node scripts/Verify-Personal.cjs $Sesion"
  & $nodeForTest (Join-Path $PSScriptRoot 'Verify-Personal.cjs') $Sesion 2>&1 | ForEach-Object { Write-Host $_ }
  if($LASTEXITCODE -ne 0){throw "La prueba $Sesion fallo. Se conserva la salida original."}
 }
}finally{Stop-Transcript}
