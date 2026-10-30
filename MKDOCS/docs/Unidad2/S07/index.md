# S07 · Seguridad distribuida y control de acceso

**Estudiante:** Jannys Graciela Navarro Acrota  
**Grupo:** Casa o Nada  
**Repositorio:** [Stephmarc/Tareas-Distribuidas](https://github.com/Stephmarc/Tareas-Distribuidas)  
**Aporte:** evolución del servicio de órdenes y su integración con infraestructura y servicios separados.

!!! warning "Estado de verificación"
    Esta página documenta el código preparado. Las pruebas integrales con PostgreSQL, Kafka, Keycloak y Grafana todavía requieren ejecutar el entorno. No se presentan resultados esperados como si fueran pruebas ejecutadas. Las capturas se añadirán después de verificar cada caso.

## 1. Servidor temporal de JWT

`pagatu-auth-ms` conserva el ejercicio de S07: usuarios en PostgreSQL, contraseñas BCrypt, registro con rol CLIENTE, login y JWT firmado con RSA. La clave pública se publica en `/oauth2/jwks`; la clave privada no se incorpora al repositorio. Las claves de esta implementación de práctica se generan al arrancar: reiniciarla invalida sus tokens anteriores.

El servicio temporal escucha en 8085 y necesita una base separada en 15436. No forma parte del arranque final por defecto: S07b sustituye su función por Keycloak. La implementación temporal y su ejecución deben evidenciarse por separado para completar la rúbrica de S07.

## 2. Validación distribuida

El Gateway valida firma, vencimiento y emisor. Órdenes y cliente también validan el JWT, de modo que llamar directamente a sus puertos no evita la autenticación. Los roles se convierten desde `realm_access.roles` a autoridades de Spring Security.

La identidad de una orden procede de `sub`, no de un identificador enviado por el navegador. El listado filtra las órdenes en el servidor y obtener una orden ajena devuelve 403; no se confía en ocultar filas en Angular.

## 3. Matriz de acceso

| Recurso | Sin token | CLIENTE | ADMIN |
| --- | --- | --- | --- |
| Productos | 401 | Consultar | Consultar |
| Crear orden | 401 | Su propia identidad | Su propia identidad |
| Obtener orden | 401 | Solo propia | Cualquiera |
| CRUD administrativo de clientes | 401 | 403 | Permitido |
| Pago de una orden | 401 | Solo propio | Solo propio |
| Health público | Permitido | Permitido | Permitido |

Los códigos de esta tabla describen las reglas implementadas; falta capturarlos en ejecución.

## 4. Verificación pendiente

Capturar login correcto, login erróneo, registro y hash almacenado del servicio temporal. Probar Gateway sin token, token alterado, rol insuficiente y rol permitido. Repetir la prueba de token alterado directamente en cliente y órdenes. Nunca publicar tokens completos ni contraseñas en las capturas.

## Error o hallazgo

El modelo anterior aceptaba `clienteId`, estado y total del request. La creación pública ahora usa `CrearOrdenRequest`, sin esos campos. Las rutas antiguas de escritura de detalles se retiraron del controlador para impedir que el consumidor evada la consulta de precios.

## Reflexión técnica — borrador para defender

La firma asimétrica separa emitir tokens de validarlos. El Gateway necesita la clave pública, no el secreto que permite crear identidades. Al cambiar de proveedor, las reglas de acceso y la lectura de roles se mantienen; cambia el emisor configurado. La firma no sustituye la autorización sobre cada recurso. Por eso el servicio verifica también que la orden pertenezca al sujeto autenticado.

[Continuación: migración a Keycloak](keycloak.md).

## Evidencias y aporte verificable

Los archivos citados corresponden a la implementación del repositorio. Las capturas de ejecución se incorporarán aquí después de la prueba. El [registro de verificación](../verificacion.md) distingue resultados confirmados de pendientes.

## Guía consultada

[Sesión del docente](https://262dist.github.io/pagatu/sesiones/S07_Seguridad_Distribuida_Control_Acceso/). La guía se utiliza como referencia técnica; sus instrucciones de entrega no sustituyen la solicitud de la estudiante.

[Feedback personal pendiente](../feedback.md).
