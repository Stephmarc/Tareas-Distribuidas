# Registro de verificación

Fecha de trabajo: 29 de octubre de 2026, zona America/Lima.

## Confirmado durante la revisión

- Remoto: Stephmarc/Tareas-Distribuidas. La copia local comenzó en main, que contiene S02 y S03; tarea-1 corresponde a una versión anterior.
- MkDocs existente: S01, S02 y S03, con sus imágenes conservadas.
- Java 21 y Maven disponibles.
- Docker Desktop instalado en la carpeta de usuario y abierto. El entorno del asistente no puede ejecutar su cliente ni conectarse al pipe: acceso denegado / EPERM. Esto no demuestra una falla del motor Docker.
- El frontend requiere Node más reciente que 24.11.0; se preparó una copia portátil de 24.22.2.

## Resultados confirmados

- **MkDocs:** construcción estricta aprobada.
- **YAML:** 27 archivos analizados sin errores de sintaxis.
- **Java:** los ocho módulos y las fuentes de pruebas de órdenes compilan con Java 21 mediante una verificación separada en la copia de trabajo. El build Maven completo aún no se certifica: el entorno impide resolver rutas al cerrar archivos JAR.
- **JUnit:** tres pruebas ejecutadas y aprobadas: precio real y publicación, carrito sin total ante falla y rechazo de un total parcial. Se utilizó JUnit Platform con el classpath completo y el agente de Mockito; son pruebas unitarias, sin PostgreSQL ni Kafka.
- **Angular:** dependencias instaladas; TypeScript y las plantillas de Angular compilan correctamente con ngc y Node 24.22.2. El empaquetado completo con esbuild sigue pendiente porque el entorno deniega el acceso a directorios padre.
- **Docker:** el script de arranque se intentó ejecutar y se detuvo al iniciar docker.exe, con acceso denegado. No se recrearon contenedores desde este entorno.
- **GitHub:** el intento de comprobar el push falló al acceder al almacén wincredman. No hay push ni despliegue confirmado.
- **Capturas:** la herramienta de control del navegador se detuvo al no poder identificar su URL. No se generaron capturas de sesiones nuevas.

Las comprobaciones de Java y JUnit no equivalen a una prueba integral ni a la aprobación de la unidad.

## Pendiente para considerar completa la unidad

1. Build Maven completo y build de Angular en un entorno con acceso normal a sus rutas.
2. PostgreSQL, Config Server, Eureka y Gateway operativos juntos.
3. Feign correcto y fallback; CLOSED, OPEN y HALF_OPEN capturados.
4. S07 temporal: registro, BCrypt, login, JWT y pruebas 401/403; Keycloak final con PKCE.
5. Kafka: eventos, lag y recuperación del consumidor, incluido el evento adicional.
6. Saga: duplicados y compensación comprobados en PostgreSQL.
7. Grafana con datos reales y falla diagnosticada.
8. Cliente Angular con login real y ambos desenlaces de pago.
9. Capturas auténticas que incluyan reloj del sistema y perfil visible, conforme a la guía.
10. Revisar reflexión, feedback personal y número de equipo; exportar los informes finales desde la documentación.

No se han reutilizado capturas de S01–S03 como evidencia de S06–S11.

## Archivos de respaldo de las verificaciones

- [Salida de compilación de fuentes Java](evidencias/compilacion-java.txt).
- [Reporte JUnit de las tres pruebas aprobadas](evidencias/junit.xml).
