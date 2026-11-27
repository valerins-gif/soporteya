# SoporteYa · Mesa de ayuda de TI

Proyecto guía del programa ADSO (SENA, ficha 3230489). Los funcionarios de una empresa reportan fallas de tecnología y los técnicos las atienden con tiempos de respuesta medidos.

## Cómo ejecutarlo

```bash
sudo -u postgres psql -c "CREATE DATABASE soporteya OWNER adso;"
DB_PASSWORD=su_clave ./mvnw spring-boot:run
```

Abra http://localhost:8080

## Equipo

| Integrante | Rol en Scrum | Módulo |
|---|---|---|
| (nombre) | Dueño del producto | Tickets |
| (nombre) | Scrum Master | Tablero técnico |
| (nombre) | Desarrollo | Administración |