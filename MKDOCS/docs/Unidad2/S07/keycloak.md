# S07b · Migración a Keycloak

**Estudiante:** Jannys Graciela Navarro Acrota  
**Grupo:** Casa o Nada  
**Repositorio:** [Stephmarc/Tareas-Distribuidas](https://github.com/Stephmarc/Tareas-Distribuidas)  
**Aporte:** evolución del servicio de órdenes y su integración con infraestructura y servicios separados.

!!! warning "Estado de verificación"
    Esta página documenta el código preparado. Las pruebas integrales con PostgreSQL, Kafka, Keycloak y Grafana todavía requieren ejecutar el entorno. No se presentan resultados esperados como si fueran pruebas ejecutadas. Las capturas se añadirán después de verificar cada caso.

La configuración final utiliza el realm pagatu, roles ADMIN y CLIENTE, un cliente público pagatu-frontend y PKCE S256 obligatorio. El archivo de importación no incluye contraseñas de personas ni tokens. El script genera una clave local de administración en platform/.env, que Git ignora.

El servicio temporal de S07 se conserva para documentar su construcción, pero el arranque final no lo utiliza. Gateway y servicios leen KEYCLOAK_ISSUER y validan al proveedor externo. La columna cliente_id se convierte a texto con una migración aditiva para admitir el sub de Keycloak, sin editar V1 ni eliminar datos.

Para completar la evidencia hay que capturar realm, roles, configuración PKCE y un token real validado, mostrando solo los claims necesarios. También comprobar login correcto, expiración y rechazo de firma alterada.

[Guía del docente](https://262dist.github.io/pagatu/sesiones/S07b_Seguridad_Distribuida_Keycloak/).
