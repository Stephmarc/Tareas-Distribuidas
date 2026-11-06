$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
$evidenceDir=Join-Path $projectRoot 'platform/runtime/evidencias-20261105'
Start-Transcript -LiteralPath (Join-Path $evidenceDir 'build-powershell.txt') -Force
Push-Location $projectRoot
try{
 Write-Host "Fecha real: $(Get-Date -Format o) | Windows: $([System.Security.Principal.WindowsIdentity]::GetCurrent().Name)"
 $env:JAVA_HOME='C:\jdk-21.0.11+10'
 $maven=if(Get-Command mvn.cmd -ErrorAction SilentlyContinue){(Get-Command mvn.cmd).Source}else{'C:\apache-maven-3.9.15\bin\mvn.cmd'}
 Write-Host 'COMANDO EJECUTADO: mvn --batch-mode --no-transfer-progress verify'
 & $maven --batch-mode --no-transfer-progress verify 2>&1 | ForEach-Object {Write-Host $_}
 if($LASTEXITCODE -ne 0){throw 'Maven verify fallo; se conserva salida.'}
 Push-Location clients/pagatu-frontend
 try{
  $portable=Join-Path $projectRoot 'platform/runtime/node24.exe'
  $nodeForBuild=if(Test-Path -LiteralPath $portable){$portable}else{(Get-Command node).Source}
  Write-Host 'COMANDO EJECUTADO: node node_modules/@angular/cli/bin/ng.js build'
  & $nodeForBuild node_modules/@angular/cli/bin/ng.js build 2>&1 | ForEach-Object {Write-Host $_}
  if($LASTEXITCODE -ne 0){throw 'Angular build fallo; se conserva salida.'}
 }finally{Pop-Location}
}finally{Pop-Location;Stop-Transcript}
