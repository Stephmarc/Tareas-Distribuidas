# S07 · Seguridad distribuida

**Jannys Graciela Navarro Acrota** · Tarea personal de Sistemas Distribuidos.

Grupo de referencia: Casa o Nada. Esta documentación corresponde al avance individual, no al informe grupal oficial.

## Trabajo realizado

Se protegieron Gateway, órdenes y cliente con JWT. La identidad del cliente procede del token y las rutas administrativas requieren el rol ADMIN.

## Pruebas realizadas

| Caso | Resultado |
| --- | --- |
| Sin token | 401 |
| Firma alterada | 401 |
| CLIENTE accede a clientes administrativos | 403 |
| Registro en el servicio JWT temporal | 201 |
| Contraseña incorrecta | 401 |
| Login correcto | 200 |

Se verificaron firma **RS256**, clave pública sin parte privada y contraseña almacenada con **BCrypt**. El servicio temporal se detuvo después de probarlo; el proveedor final es **Keycloak**, con PKCE S256 para Angular.

[Pruebas de acceso](../evidencias/pruebas-api.json) · [Prueba del JWT temporal](../evidencias/autenticacion-temporal.json).

## Explicación breve

Autenticar identifica a la persona; autorizar decide qué puede hacer. Validar también en los microservicios impide saltarse la seguridad llamándolos directamente.
