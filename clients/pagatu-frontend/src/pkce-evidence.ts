// Diagnostic mode observes the real exchange; it never stores tokens or code_verifier.
export type PkceProof={stateCoincide:boolean,verifierS256Coincide:boolean,http:number,fecha:string};
const key='pagatu-evidence-oauth';
export function rememberAuthorization(url:string){
 const u=new URL(url);
 sessionStorage.setItem(key,JSON.stringify({state:u.searchParams.get('state'),challenge:u.searchParams.get('code_challenge')}));
}
export function observePkce(done:(proof:PkceProof)=>void){
 const saved=sessionStorage.getItem(key);if(!saved)return;
 const expected=JSON.parse(saved) as {state:string,challenge:string};
 const returnedState=new URLSearchParams(location.hash.slice(1)).get('state');
 const original=window.fetch.bind(window);
 window.fetch=async(input:RequestInfo|URL,init?:RequestInit)=>{
  const url=typeof input==='string'?input:input instanceof URL?input.href:input.url;
  const body=init?.body;
  if(url==='http://localhost:18090/realms/pagatu/protocol/openid-connect/token'&&body instanceof URLSearchParams&&body.get('grant_type')==='authorization_code'){
   window.fetch=original;
   const verifier=body.get('code_verifier')||'';
   const digest=new Uint8Array(await crypto.subtle.digest('SHA-256',new TextEncoder().encode(verifier)));
   const challenge=btoa(String.fromCharCode(...digest)).replace(/\+/g,'-').replace(/\//g,'_').replace(/=+$/,'');
   const response=await original(input,init);
   const proof={stateCoincide:returnedState===expected.state,verifierS256Coincide:!!verifier&&challenge===expected.challenge,http:response.status,fecha:new Date().toISOString()};
   sessionStorage.removeItem(key);done(proof);console.info('PKCE_EVIDENCE '+JSON.stringify(proof));
   return response;
  }
  return original(input,init);
 };
}
