const fs=require('fs'),path=require('path');
const root=path.resolve(__dirname,'..');
const runtime=path.join(root,'platform/runtime');
(async()=>{
 const config=JSON.parse(fs.readFileSync(path.join(runtime,'verificacion-local.json'),'utf8'));
 const r=await fetch('http://localhost:18090/realms/pagatu/protocol/openid-connect/token',{method:'POST',body:new URLSearchParams({grant_type:'client_credentials',client_id:config.clientId,client_secret:config.secret})});
 if(!r.ok)throw Error('No se pudo autenticar la cuenta técnica');
 const token=await r.json();const headers={Authorization:'Bearer '+token.access_token,'Content-Type':'application/json','X-Trace-ID':'s06-caida-real-'+Date.now()};
 const reportPath=path.join(runtime,'resiliencia-real.json');
 const report=process.argv[2]==='fallo'?{fecha:new Date().toISOString(),tipoFallo:'Proceso de catálogo detenido y recuperado desde PowerShell',carritos:[],estados:[]}:JSON.parse(fs.readFileSync(reportPath,'utf8'));
 async function state(expected){const response=await fetch('http://localhost:8082/actuator/circuitbreakers',{headers});if(!response.ok)throw Error('Actuator HTTP '+response.status);const data=await response.json();const actual=data.circuitBreakers.catalogo.state;report.estados.push({fecha:new Date().toISOString(),state:actual,data});fs.writeFileSync(reportPath,JSON.stringify(report,null,2));console.log('Circuit Breaker: '+actual);if(expected&&actual!==expected)throw Error('Esperado '+expected+'; recibido '+actual);}
 async function create(){const response=await fetch('http://localhost:18080/api/v1/ordenes',{method:'POST',headers,body:JSON.stringify({metodoPago:'YAPE_PLIN',detalles:[{idProducto:1,cantidad:1}]}),signal:AbortSignal.timeout(20000)});if(response.status!==201)throw Error('Crear orden HTTP '+response.status);return response.json();}
 if(process.argv[2]==='fallo'){
  for(let i=0;i<5;i++){const o=await create();report.carritos.push({id:o.id,estado:o.estado,total:o.total});if(o.estado!=='CARRITO'||o.total!==null)throw Error('Fallback incorrecto: '+JSON.stringify(report.carritos.at(-1)));}
  await state('OPEN');await new Promise(resolve=>setTimeout(resolve,11000));await state('HALF_OPEN');
 }else if(process.argv[2]==='recuperacion'){
  report.recuperacion=[];for(let i=0;i<2;i++){const o=await create();report.recuperacion.push({id:o.id,estado:o.estado,total:o.total});if(o.total===null)throw Error('El catálogo todavía no confirmó el precio.');}
  await state('CLOSED');report.resultado='APROBADO';
 }else throw Error('Modo inválido');
 fs.writeFileSync(reportPath,JSON.stringify(report,null,2));console.log('Reporte guardado en platform/runtime/resiliencia-real.json');
})().catch(e=>{console.error(e.message);process.exitCode=1;});
