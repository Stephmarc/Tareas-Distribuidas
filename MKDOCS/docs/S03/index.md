# S03 - Registro y descubrimiento con Eureka

## Objetivo

Implementar un servidor de registro y descubrimiento con **Netflix Eureka** e integrar `pagatu-orden-ms` como cliente dentro de la arquitectura distribuida del curso.

## 1. Eureka Server operativo

Se implementó `pagatu-eureka` y se verificó el acceso al dashboard en:

```text
http://localhost:18761
```

El servidor logró iniciar correctamente y mostrar su estado general.

![Dashboard de Eureka](assets/eureka-dashboard.png)

## 2. Configuración centralizada

`pagatu-orden-ms` mantiene la configuración del perfil `dev` en Config Server. Se verificó la respuesta:

```text
http://localhost:18888/pagatu-orden-ms/dev
```

La configuración incluye PostgreSQL y la URL del servidor Eureka:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:18761/eureka
```

## 3. Integración de `pagatu-orden-ms`

Se agregó la dependencia Eureka Client en el microservicio:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

Durante las pruebas se verificó que el microservicio llegó a responder correctamente en su endpoint de salud:

```text
http://localhost:8080/actuator/health
```

con estado `UP`.

## 4. Error o hallazgo

Durante la ejecución concurrente se presentaron conflictos de puertos y disponibilidad entre Config Server, Eureka Server y `pagatu-orden-ms`. En una de las pruebas, Eureka intentó iniciar en un puerto ya ocupado; posteriormente se forzó su ejecución en `18761`. También hubo intentos de iniciar `pagatu-orden-ms` mientras Config Server no estaba disponible, lo que impidió cargar la configuración del datasource.

Aunque Eureka Server quedó operativo y el microservicio contó con la dependencia y configuración de cliente, las instancias no llegaron a visualizarse en el dashboard antes del cierre de la actividad. Este hallazgo permitió identificar la importancia del orden de arranque y del control de puertos en una ejecución distribuida.

## 5. Explicación técnica

Eureka permite que los servicios se localicen mediante un nombre lógico en lugar de depender de una lista manual de direcciones y puertos. Cada instancia se registra en el servidor y los consumidores pueden consultar el registro para descubrir qué instancias se encuentran disponibles. Esto reduce el acoplamiento entre componentes y facilita la ejecución de múltiples instancias.

## Reflexión

En esta sesión comprendí que el registro y descubrimiento de servicios depende no solo de la configuración de Eureka, sino también de que los componentes requeridos estén activos al mismo tiempo. Los conflictos de puertos pueden impedir el arranque incluso cuando la configuración es correcta. También comprobé que Config Server permite mantener centralizada la configuración del microservicio. Como mejora, antes de iniciar una práctica distribuida verificaría los puertos ocupados, el estado de Config Server, la base de datos y Eureka. Esto permite detectar problemas más rápido y facilita la ejecución concurrente de varias instancias.
