# SaludMVC

Aplicación de escritorio para gestión de órdenes médicas. Desarrollada en Java con JavaFX como proyecto universitario.

## Funcionalidades

- Login de asesores
- Administración de pacientes
- Creación y gestión de órdenes médicas
- Asignación de médicos por especialidad
- Registro de procedimientos con códigos CUPS y diagnósticos CIE-10
- Exportación de órdenes a PDF
- Persistencia local en archivos CSV y JSON

## Tecnologías

- Java 17
- JavaFX (interfaz gráfica con FXML)
- iText (generación de PDFs)
- Maven (gestión de dependencias)
- JUnit 5 (pruebas unitarias)

## Estructura del proyecto

SaludMVC/
├── src/main/java/
│   ├── app/
│   ├── controller/
│   ├── model/
│   └── persistence/
├── src/main/resources/
│   └── data/
│       ├── pacientes.csv
│       ├── asesores.csv
│       ├── medicos.csv
│       ├── historico_ordenes.json
│       ├── TablaReferencia_CIE10.csv
│       └── TablaReferencia_CUPS.csv
└── ordenes_pdf/

## Instalación

1. Clonar el repositorio
2. Abrir con IDE compatible con Maven
3. Ejecutar la clase Launch

## Uso

El asesor inicia sesión con sus credenciales. Desde el menú principal puede:
- Gestionar pacientes
- Crear órdenes médicas
- Asignar médicos según especialidad
- Agregar procedimientos y diagnósticos
- Generar PDF de la orden

## Autores

Proyecto desarrollado para la asignatura de Programación.

## Licencia

Proyecto académico sin fines comerciales.
