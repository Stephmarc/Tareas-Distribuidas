$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
$portableNode=Join-Path $projectRoot 'platform/runtime/node24.exe'
$nodeForTest=if(Test-Path -LiteralPath $portableNode){$portableNode}else{(Get-Command node).Source}
$module=Join-Path $projectRoot 'Services/pagatu-catalogo-ms'
$runtimeDir=Join-Path $projectRoot 'platform/runtime'
$jar=(Get-ChildItem -LiteralPath (Join-Path $module 'target') -Filter '*.jar'|Select-Object -First 1).FullName
Start-Transcript -LiteralPath (Join-Path $runtimeDir 'evidencias-20261105/unidad1-powershell.txt') -Force
$second=$null
try{
 Write-Host "Fecha real: $(Get-Date -Format o) | Windows: $([System.Security.Principal.WindowsIdentity]::GetCurrent().Name)"
 Write-Host 'COMANDO: java -Xmx256m -jar catalogo.jar --server.port=8086 --eureka.instance.instance-id=personal-catalogo-8086 --logging.file.name=logs/catalogo-segunda.log'
 $second=Start-Process -FilePath (Get-Command java).Source -ArgumentList @('-Xms48m','-Xmx256m','-jar',('"'+$jar+'"'),'--server.port=8086','--eureka.instance.instance-id=personal-catalogo-8086','--logging.file.name=logs/catalogo-segunda.log') -WorkingDirectory $module -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $runtimeDir 'catalogo-segunda.stdout.log') -RedirectStandardError (Join-Path $runtimeDir 'catalogo-segunda.stderr.log')
 & $nodeForTest (Join-Path $PSScriptRoot 'Verify-Unit1.cjs') 2>&1 | ForEach-Object {Write-Host $_}
 if($LASTEXITCODE -ne 0){throw 'La prueba de dos instancias fallo.'}
}finally{
 if($second){$p=Get-CimInstance Win32_Process -Filter "ProcessId=$($second.Id)";if($p.Name -eq 'java.exe' -and $p.CommandLine -like ('*'+$module+'*') -and $p.CommandLine -like '*--server.port=8086*'){Stop-Process -Id $second.Id;Write-Host 'Segunda instancia de prueba detenida; instancia principal conservada.'}}
 Stop-Transcript
}
