param([Parameter(Mandatory)][ValidateSet('catalogo','orden','cliente','pago','auth','gateway')][string]$Name,[ValidateSet('Start','Stop','Restart')][string]$Action='Start',[switch]$Legacy)
$ErrorActionPreference='Stop'
$projectRoot=Split-Path -Parent $PSScriptRoot
$moduleName=if($Name -eq 'gateway'){'pagatu-gateway'}else{"pagatu-$Name-ms"}
$module=Join-Path $projectRoot ($(if($Name -eq 'gateway'){'Infra/'}else{'Services/'})+$moduleName)
$pidPath=Join-Path $projectRoot "platform/runtime/$moduleName.pid"
$port=@{catalogo=8080;orden=8082;cliente=8083;pago=8084;auth=8085;gateway=18080}[$Name]
function Stop-OwnService {
 if(-not(Test-Path -LiteralPath $pidPath)){return}
 $serviceId=[int](Get-Content -LiteralPath $pidPath)
 $process=Get-CimInstance Win32_Process -Filter "ProcessId=$serviceId" -ErrorAction SilentlyContinue
 if(-not $process){return}
 if($process.Name -ne 'java.exe' -or $process.CommandLine -notlike ('*'+$module+'*')){throw 'El PID pertenece a otro proceso. No se detendra.'}
 Stop-Process -Id $serviceId -ErrorAction Stop
 Write-Host "$moduleName detenido (PID $serviceId)."
}
if($Action -in @('Stop','Restart')){Stop-OwnService}
if($Action -eq 'Stop'){return}
if($Action -eq 'Start' -and (Test-Path -LiteralPath $pidPath)){
 $serviceId=[int](Get-Content -LiteralPath $pidPath)
 $p=Get-CimInstance Win32_Process -Filter "ProcessId=$serviceId" -ErrorAction SilentlyContinue
 if($p -and $p.Name -eq 'java.exe' -and $p.CommandLine -like ('*'+$module+'*')){Write-Host "$moduleName ya esta iniciado.";return}
}
$env:KEYCLOAK_ISSUER=if($Legacy){'http://localhost:8085'}else{'http://localhost:18090/realms/pagatu'}
$jar=(Get-ChildItem -LiteralPath (Join-Path $module 'target') -Filter '*.jar'|Select-Object -First 1).FullName
if(-not $jar){throw 'Falta el JAR. Ejecuta Maven verify.'}
$runtimeDir=Join-Path $projectRoot 'platform/runtime'
$started=Start-Process -FilePath (Get-Command java).Source -ArgumentList @('-Xms48m','-Xmx256m','-jar',('"'+$jar+'"')) -WorkingDirectory $module -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $runtimeDir "$moduleName.stdout.log") -RedirectStandardError (Join-Path $runtimeDir "$moduleName.stderr.log")
$started.Id|Set-Content -LiteralPath $pidPath
$health=if($Name -eq 'gateway'){"http://localhost:$port/actuator/health"}else{"http://localhost:$port/actuator/health/liveness"}
for($attempt=0;$attempt -lt 150;$attempt++){
 try{$null=Invoke-RestMethod $health -TimeoutSec 3;Write-Host "$moduleName listo: $health | PID $($started.Id)";return}catch{Start-Sleep -Seconds 2}
}
throw "$moduleName no respondio. Consulta platform/runtime/$moduleName.stdout.log"
