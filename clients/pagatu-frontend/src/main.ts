import {Component,signal,OnDestroy} from '@angular/core';
import {bootstrapApplication} from '@angular/platform-browser';
import Keycloak from 'keycloak-js';
type Producto={id:number,nombre:string,precio:number};
type Orden={id:number,estado:string,total:number|null,detalles:unknown[]};
@Component({selector:'app-root',standalone:true,template:`
<header><strong>pagatu</strong><span>Casa o Nada · Sistemas distribuidos</span><button (click)="login()">{{authenticated()?'Mi cuenta':'Ingresar'}}</button>@if(authenticated()){<button (click)="logout()">Salir</button>}</header>
<main><p class="eyebrow">UNIDAD 2 · CLIENTE DE ÓRDENES</p><h1>Productos y mis pedidos</h1><p>Consulta el catálogo, registra una orden y sigue el resultado del pago.</p>
@if(message()){<p class="notice" role="status">{{message()}}</p>}
@if(!authenticated()){<section><h2>Inicia sesión para continuar</h2><p>Tu cuenta permite consultar tus órdenes y su estado de pago.</p><button (click)="login()">Ingresar con Keycloak</button></section>}
@if(authenticated()){
<section><h2>Catálogo</h2><div class="products">@for(p of products();track p.id){<article><h3>{{p.nombre}}</h3><p>S/ {{p.precio.toFixed(2)}}</p><label>Cantidad <input #cantidad type="number" min="1" max="100" value="1"></label><label>Método <select #metodo><option value="YAPE_PLIN">Yape / Plin</option><option value="RECHAZADO">Prueba de rechazo</option></select></label><button [disabled]="busy()" (click)="create(p.id,+cantidad.value,metodo.value)">Crear orden</button></article>}</div></section>
<section><div class="row"><h2>Mis pedidos</h2><button (click)="loadOrders()">Actualizar</button></div><p>Un pedido pendiente aún no tiene un resultado de pago confirmado.</p><table><thead><tr><th>Orden</th><th>Estado</th><th>Total</th></tr></thead><tbody>@for(o of orders();track o.id){<tr><td>#{{o.id}}</td><td><span class="badge">{{o.estado}}</span></td><td>{{o.total===null?'Por confirmar':'S/ '+o.total.toFixed(2)}}</td></tr>}@empty{<tr><td colspan="3">Todavía no hay pedidos.</td></tr>}</tbody></table></section>}
</main><footer>Jannys Graciela Navarro Acrota · Casa o Nada</footer>`})
class App implements OnDestroy{
 authenticated=signal(false);products=signal<Producto[]>([]);orders=signal<Orden[]>([]);message=signal('');busy=signal(false);
 private keycloak=new Keycloak({url:'http://localhost:18090',realm:'pagatu',clientId:'pagatu-frontend'});
 private timer:ReturnType<typeof setInterval>|undefined;private attempts=0;private polling=false;
 constructor(){this.keycloak.init({onLoad:'check-sso',pkceMethod:'S256',checkLoginIframe:false}).then(async ok=>{this.authenticated.set(ok);if(ok){await this.loadProducts();await this.loadOrders();}}).catch(()=>this.message.set('No se pudo conectar con el servicio de acceso. Comprueba que Keycloak esté iniciado.'));}
 login(){return this.keycloak.login({redirectUri:location.origin,scope:'openid'});}
 logout(){return this.keycloak.logout({redirectUri:location.origin});}
 private async api<T>(url:string,options:RequestInit={}):Promise<T>{await this.keycloak.updateToken(30);const response=await fetch('http://localhost:18080/api/v1/'+url,{...options,headers:{'Content-Type':'application/json',Authorization:'Bearer '+this.keycloak.token}});if(!response.ok)throw new Error('La operación respondió HTTP '+response.status);return response.json() as Promise<T>;}
 async loadProducts(){try{this.products.set(await this.api<Producto[]>('productos'));}catch(e){this.message.set(String(e));}}
 async create(id:number,cantidad:number,metodoPago:string){if(this.busy()||!Number.isInteger(cantidad)||cantidad<1){return;}this.busy.set(true);try{const o=await this.api<Orden>('ordenes',{method:'POST',body:JSON.stringify({metodoPago,detalles:[{idProducto:id,cantidad}]})});this.message.set(o.estado==='CARRITO'?'Orden guardada. El catálogo no confirmó los precios.':'Orden registrada. El pago sigue pendiente.');await this.loadOrders();}catch(e){this.message.set(String(e));}finally{this.busy.set(false);}}
 async loadOrders(){try{this.orders.set(await this.api<Orden[]>('ordenes'));if(this.orders().some(o=>o.estado==='PENDIENTE_PAGO')){if(!this.timer){this.attempts=0;this.timer=setInterval(()=>{if(this.polling)return;if(++this.attempts>120){this.stop();this.message.set('El pago sigue pendiente. Puedes volver a consultar más tarde.');return;}this.polling=true;this.loadOrders().finally(()=>this.polling=false);},2000);}}else{this.stop();}}catch(e){this.message.set(String(e));this.stop();}}
 private stop(){if(this.timer)clearInterval(this.timer);this.timer=undefined;}
 ngOnDestroy(){this.stop();}
}
bootstrapApplication(App).catch(console.error);
