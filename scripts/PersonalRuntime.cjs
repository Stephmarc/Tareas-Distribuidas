const fs=require('fs'),path=require('path'),cp=require('child_process');
const root=path.resolve(__dirname,'..');
async function control(name,action,legacy=false){
 if(!['catalogo','orden','cliente','pago','auth','gateway'].includes(name)||!['Start','Stop','Restart'].includes(action))throw Error('Servicio o accion fuera del proyecto');
 const moduleName=name==='gateway'?'pagatu-gateway':`pagatu-${name}-ms`,module=path.join(root,name==='gateway'?'Infra':'Services',moduleName),runtime=path.join(root,'platform/runtime'),pidPath=path.join(runtime,moduleName+'.pid');
 let ownedPid=null;
 if(fs.existsSync(pidPath)){
  const pid=Number(fs.readFileSync(pidPath,'utf8').replace(/^\uFEFF/,'').trim());if(!Number.isInteger(pid)||pid<1)throw Error('PID invalido');
  const raw=cp.execFileSync('powershell.exe',['-NoProfile','-Command',`[Console]::OutputEncoding=[System.Text.UTF8Encoding]::new(); Get-CimInstance Win32_Process -Filter 'ProcessId=${pid}' | Select-Object Name,CommandLine | ConvertTo-Json -Compress`],{encoding:'utf8',stdio:['ignore','pipe','pipe'],windowsHide:true}).trim();
  if(raw){const p=JSON.parse(raw);if(p.Name!=='java.exe'||!p.CommandLine.toLowerCase().includes(module.toLowerCase()))throw Error('El PID pertenece a otro proceso; no se toca');ownedPid=pid;}
 }
 console.log('CONTROL REAL: '+moduleName+' '+action+(legacy?' issuer propio':' issuer Keycloak'));
 if(ownedPid&&action==='Start'){console.log('Servicio propio ya iniciado PID '+ownedPid);return;}
 if(ownedPid){process.kill(ownedPid);console.log('Proceso propio detenido PID '+ownedPid);}
 if(action==='Stop')return;
 const jar=fs.readdirSync(path.join(module,'target')).find(x=>x.endsWith('.jar'));if(!jar)throw Error('Falta JAR');
 const java=cp.execFileSync('where.exe',['java'],{encoding:'utf8',windowsHide:true}).trim().split(/\r?\n/)[0];
 const stdout=fs.openSync(path.join(runtime,moduleName+'.stdout.log'),'w'),stderr=fs.openSync(path.join(runtime,moduleName+'.stderr.log'),'w');
 const child=cp.spawn(java,['-Xms48m','-Xmx256m','-jar',path.join(module,'target',jar)],{cwd:module,detached:true,windowsHide:true,stdio:['ignore',stdout,stderr],env:{...process.env,KEYCLOAK_ISSUER:legacy?'http://localhost:8085':'http://localhost:18090/realms/pagatu'}});child.unref();fs.closeSync(stdout);fs.closeSync(stderr);fs.writeFileSync(pidPath,String(child.pid));
 const port={catalogo:8080,orden:8082,cliente:8083,pago:8084,auth:8085,gateway:18080}[name],url=`http://localhost:${port}/actuator/health${name==='gateway'?'':'/liveness'}`;
 for(let i=0;i<150;i++){try{const r=await fetch(url,{signal:AbortSignal.timeout(3000)});if(r.ok){console.log('Servicio UP '+url+' PID '+child.pid);return;}}catch{}await new Promise(r=>setTimeout(r,2000));}
 throw Error('Servicio no se recupero: '+moduleName);
}
module.exports={control};
