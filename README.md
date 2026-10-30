# Sistemas distribuidos · Casa o Nada

Jannys Graciela Navarro Acrota. Continuación del servicio de órdenes existente.

## Documentación

[Unidad 2 en MkDocs](MKDOCS/docs/Unidad2/index.md) y [estado de verificación](MKDOCS/docs/Unidad2/verificacion.md). La implementación está preparada; no se considera finalizada la evidencia integral hasta ejecutar los casos documentados.

## Requisitos

Java 21, Maven, Docker Desktop con motor Linux y Node compatible con Angular 22 (24.15 o posterior de la rama 24).

## Ejecutar

En PowerShell desde esta carpeta:

```powershell
.\scripts\Start-Dev.ps1 -Observabilidad
```

El script crea infraestructura con Compose, prueba Java, arranca los JAR y verifica Angular. Las claves locales se guardan en platform/.env, excluido de Git. No elimina volúmenes ni detiene procesos ajenos. Verificar Docker antes de ejecutarlo.

## Sitio

```text
python -m pip install -r requirements-docs.txt
python -m mkdocs build --strict -f MKDOCS/mkdocs.yml
python -m mkdocs serve -f MKDOCS/mkdocs.yml
```

El workflow existente publica MkDocs al hacer push a main. Su ejecución exitosa debe comprobarse en GitHub Actions; un push por sí solo no confirma el despliegue.
