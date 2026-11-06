const fs=require('fs'),path=require('path'),cp=require('child_process'),crypto=require('crypto');
const root=path.resolve(__dirname,'..'),runtime=path.join(root,'platform/runtime'),out=path.join(runtime,'evidencias-20261105');
const docker=path.join(process.env.LOCALAPPDATA,'Programs/DockerDesktop/resources/bin/docker.exe');
const report={sesion:'S07-auth-propio',fecha:new Date().toISOString(),pasos:[]};
function ok(test,label,data){if(!test)throw Error(label);report.pasos.push({prueba:label,...data});console.log(label+' '+JSON.stringify(data));}
async function req(route,body){const r=await fetch('http://localhost:8085'+route,{method:body?'POST':'GET',headers:{'Content-Type':'application/json'},body:body?JSON.stringify(body):undefined,signal:AbortSignal.timeout(30000)});return{status:r.status,data:await r.json()};}
function sql(q){return cp.execFileSync(docker,['exec','pagatu-u2-postgres-auth-1','psql','-U','pagatu','-d','pagatu_auth_db','-At','-c',q],{encoding:'utf8'}).trim();}
(async()=>{
console.log('S07 auth propio · '+new Date().toLocaleString('es-PE',{timeZone:'America/Lima',hour12:false})+' · Windows: '+require('os').userInfo().username);
const credentials={username:'personal-'+Date.now(),password:crypto.randomBytes(16).toString('hex')};
const r=await req('/api/v1/auth/register',credentials);ok(r.status===201,'Registro HTTP 201',r);
const bad=await req('/api/v1/auth/login',{...credentials,password:'incorrecta-demo'});ok(bad.status===401,'Login incorrecto HTTP 401',bad);
const login=await req('/api/v1/auth/login',credentials);ok(login.status===200,'Login correcto HTTP 200',{status:login.status,token_type:login.data.token_type,expires_in:login.data.expires_in,nota:'JWT completo omitido para no publicar credenciales'});
const parts=login.data.access_token.split('.'),header=JSON.parse(Buffer.from(parts[0],'base64url')),claims=JSON.parse(Buffer.from(parts[1],'base64url'));
const jwks=await req('/oauth2/jwks');const jwk=jwks.data.keys.find(x=>x.kid===header.kid);ok(!jwk.d&&crypto.verify('RSA-SHA256',Buffer.from(parts[0]+'.'+parts[1]),crypto.createPublicKey({key:jwk,format:'jwk'}),Buffer.from(parts[2],'base64url')),'Firma RS256 verificada con JWKS publico',{header,claims,jwksPublico:jwks.data});
const users=sql(`SELECT id,username,password FROM usuarios WHERE id=${r.data.id}`),roles=sql('TABLE roles'),mapping=sql(`SELECT * FROM usuario_roles WHERE usuario_id=${r.data.id}`);
ok(/\$2[aby]\$/.test(users)&&mapping.includes('CLIENTE'),'Tablas y hash BCrypt real de usuario exclusivo',{usuarios:users,roles,usuario_roles:mapping});
report.resultado='APROBADO';fs.writeFileSync(path.join(out,'s07-auth.json'),JSON.stringify(report,null,2));
})().catch(e=>{console.error(e.message);process.exitCode=1});
