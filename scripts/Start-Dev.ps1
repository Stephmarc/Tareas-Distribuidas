
param([switch]$SkipBuild,[switch]$Observabilidad)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$runtimeDir = Join-Path $projectRoot 'platform/runtime'
New-Item -ItemType Directory -Force $runtimeDir | Out-Null
$dockerCandidates = @((Join-Path $env:LOCALAPPDATA 'Programs/DockerDesktop/resources/bin/docker.exe'),'C:/Program Files/Docker/Docker/resources/bin/docker.exe')
$dockerPath = ($dockerCandidates | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1)
if (-not $dockerPath) { $dockerPath = (Get-Command docker -ErrorAction Stop).Source }
& $dockerPath info --format '{{.ServerVersion}}'
if ($LASTEXITCODE -ne 0) { throw 'Abre Docker Desktop y espera a que el motor esté listo.' }
$envFile = Join-Path $projectRoot 'platform/.env'
if (-not (Test-Path -LiteralPath $envFile)) {
    $adminPassword = [guid]::NewGuid().ToString('N')
    $grafanaPassword = [guid]::NewGuid().ToString('N')
    "KEYCLOAK_ADMIN_PASSWORD=$adminPassword`nDB_PASSWORD=pagatu`nGRAFANA_PASSWORD=$grafanaPassword" | Set-Content -LiteralPath $envFile -Encoding utf8
}
Get-Content -LiteralPath $envFile | Where-Object { $_ -match '^([A-Z_]+)=(.*)$' } | ForEach-Object {
    if ($_ -match '^([A-Z_]+)=(.*)$') { [Environment]::SetEnvironmentVariable($Matches[1],$Matches[2],'Process') }
}
Push-Location $projectRoot
try {
    & $dockerPath compose --env-file platform/.env -f platform/compose-dev.yml up -d
    if ($LASTEXITCODE -ne 0) { throw 'Docker Compose no pudo levantar la infraestructura.' }
    if (-not $SkipBuild) {
        & mvn.cmd -B -ntp verify
        if ($LASTEXITCODE -ne 0) { throw 'La compilación o las pruebas Java fallaron. No se iniciarán servicios sin verificar.' }
    }
    function Wait-Http([string]$Url) {
        $ready = $false
        for ($attempt = 0; $attempt -lt 120; $attempt++) {
            try { $null = Invoke-RestMethod -Uri $Url -TimeoutSec 3; $ready = $true; break } catch { Start-Sleep -Seconds 2 }
        }
        if (-not $ready) { throw "No respondió $Url. Revisa platform/runtime y los logs del servicio." }
    }
    function Start-ServiceJar([string]$Module,[string]$Name,[string]$Health) {
        $modulePath = Join-Path $projectRoot $Module
        $pidFile = Join-Path $runtimeDir "$Name.pid"
        if (Test-Path -LiteralPath $pidFile) {
            $serviceProcessId = [int](Get-Content -LiteralPath $pidFile)
            if (Get-Process -Id $serviceProcessId -ErrorAction SilentlyContinue) { Wait-Http $Health; return }
        }
        if (Test-NetConnection -ComputerName localhost -Port ([uri]$Health).Port -InformationLevel Quiet -WarningAction SilentlyContinue) { throw "Puerto ocupado en $Health. No se detendrá un proceso ajeno." }
        $jarPath = (Get-ChildItem -LiteralPath (Join-Path $modulePath 'target') -Filter '*.jar' | Select-Object -First 1).FullName
        if (-not $jarPath) { throw "No existe el JAR de $Name" }
        New-Item -ItemType Directory -Force (Join-Path $modulePath 'logs') | Out-Null
        $serviceProcess = Start-Process -FilePath (Get-Command java).Source -ArgumentList @('-jar',('"'+$jarPath+'"')) -WorkingDirectory $modulePath -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $runtimeDir "$Name.stdout.log") -RedirectStandardError (Join-Path $runtimeDir "$Name.stderr.log")
        $serviceProcess.Id | Set-Content -LiteralPath $pidFile
        Wait-Http $Health
    }
    Wait-Http 'http://localhost:18090/realms/pagatu/.well-known/openid-configuration'
    Start-ServiceJar 'Infra/pagatu-config' 'pagatu-config' 'http://localhost:18888/actuator/health'
    Start-ServiceJar 'Infra/pagatu-eureka' 'pagatu-eureka' 'http://localhost:18761/actuator/health'
    Start-ServiceJar 'Services/pagatu-catalogo-ms' 'pagatu-catalogo-ms' 'http://localhost:8080/actuator/health/liveness'
    Start-ServiceJar 'Services/pagatu-orden-ms' 'pagatu-orden-ms' 'http://localhost:8082/actuator/health/liveness'
    Start-ServiceJar 'Services/pagatu-pago-ms' 'pagatu-pago-ms' 'http://localhost:8084/actuator/health/liveness'
    Start-ServiceJar 'Services/pagatu-cliente-ms' 'pagatu-cliente-ms' 'http://localhost:8083/actuator/health/liveness'
    Start-ServiceJar 'Infra/pagatu-gateway' 'pagatu-gateway' 'http://localhost:18080/actuator/health'
    if ($Observabilidad) {
        & $dockerPath compose --env-file platform/.env -f obs/compose-dev.yml up -d
        if ($LASTEXITCODE -ne 0) { throw 'No arrancó el stack de observabilidad.' }
    }
    $nodePath = (Get-Command node).Source
    $nodeVersion = (& $nodePath -p 'process.versions.node')
    if ([version]$nodeVersion -lt [version]'24.15.0') {
        $nodePath = Join-Path $runtimeDir 'node.exe'
        if (-not (Test-Path -LiteralPath $nodePath)) {
            Invoke-WebRequest 'https://nodejs.org/dist/v24.22.2/win-x64/node.exe' -OutFile $nodePath
            $manifest = (Invoke-WebRequest 'https://nodejs.org/dist/v24.22.2/SHASUMS256.txt').Content
            $expectedHash = (($manifest -split "`n" | Where-Object { $_ -match ' win-x64/node.exe$' }) -split '\s+')[0]
            if ((Get-FileHash -LiteralPath $nodePath -Algorithm SHA256).Hash -ne $expectedHash) { throw 'El hash del runtime Node no coincide.' }
        }
    }
    Push-Location 'clients/pagatu-frontend'
    try {
        & npm.cmd ci --no-audit --no-fund
        if ($LASTEXITCODE -ne 0) { throw 'No se instalaron las dependencias Angular.' }
        & $nodePath node_modules/@angular/cli/bin/ng.js build
        if ($LASTEXITCODE -ne 0) { throw 'Falló el build de Angular.' }
        $frontendProcess = Start-Process -FilePath $nodePath -ArgumentList @('node_modules/@angular/cli/bin/ng.js','serve','--host','localhost','--port','4200') -WorkingDirectory (Get-Location).Path -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $runtimeDir 'frontend.stdout.log') -RedirectStandardError (Join-Path $runtimeDir 'frontend.stderr.log')
        $frontendProcess.Id | Set-Content -LiteralPath (Join-Path $runtimeDir 'frontend.pid')
    } finally { Pop-Location }
    Write-Host 'Infraestructura creada y servicios iniciados.'
    Write-Host 'Frontend: http://localhost:4200 | Eureka: http://localhost:18761 | Kafka UI: http://localhost:18085'
    Write-Host 'Crea tu cuenta desde el login de Keycloak. Las claves de administración están en platform/.env (no se versiona).'
} finally { Pop-Location }

