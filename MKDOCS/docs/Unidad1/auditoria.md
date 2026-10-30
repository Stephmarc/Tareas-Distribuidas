# Unidad 1 · Revisión del trabajo previo

**Estudiante:** Jannys Graciela Navarro Acrota  
**Grupo:** Casa o Nada  
**Repositorio:** [Stephmarc/Tareas-Distribuidas](https://github.com/Stephmarc/Tareas-Distribuidas)  
**Aporte:** evolución del servicio de órdenes y su integración con infraestructura y servicios separados.

## Estado encontrado

| Sesión | Archivos encontrados | Hallazgo |
| --- | --- | --- |
| S01 | CRUD de órdenes, Flyway, DTO, imágenes | Se conserva el trabajo. No se ha repetido toda la prueba de dos instancias. |
| S02 | Config Server, perfiles de órdenes y documento | Se conserva. Falta nueva verificación conjunta. |
| S03 | Eureka y documento con dashboard | El informe reconoce que las instancias no llegaron a verse. Eureka Client estaba solo en dependencyManagement; corregido. |
| S04 | No había Gateway | Se añadió el módulo y sus rutas por nombre lógico. Falta evidencia de distribución entre dos instancias. |
| S05 | No había informe de consolidación | La evaluación requiere demostración y defensa; no se inventa un resultado. |

## Ajustes realizados

El Gateway ahora permite continuar la unidad 2 sin llamar desde Angular directamente a cada microservicio. Las tablas originales de órdenes se conservan. Las modificaciones de esquema usan V2 y V3, para evitar editar migraciones aplicadas o reiniciar datos existentes.

La revisión no convierte pendientes anteriores en actividades aprobadas: la evidencia debe completarse con los servicios funcionando.
