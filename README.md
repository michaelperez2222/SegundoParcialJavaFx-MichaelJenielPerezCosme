
# Garaje de Vice City - Segundo Parcial Java Fx

## Datos del Estudiante
- **Nombre:** [Michael Jeniel Perez Cosme]
- **Matrícula:** [2023-0802]

## Descripción del Proyecto
Aplicación de escritorio desarrollada en JavaFX para la gestión del garaje de la crew en Vice City (Leonida). Permite registrar, visualizar, eliminar vehículos, exportar/importar en formato CSV (con soporte opcional para `FileChooser`), persistir datos mediante serialización binaria (`garaje.dat`) y llevar un registro histórico de operaciones en un archivo de texto (`bitacora.txt`).

## Funcionalidades de la APP
- [x] Modelo `Vehiculo` con serialización.
- [x] Interfaz gráfica con formulario completo, validaciones estrictas (`NumberFormatException`, rangos válidos, placas únicas) y `TableView`.
- [x] Exportación e importación de archivos CSV con manejo de descartes y resúmenes.
- [x] Serialización binaria para guardar y cargar partida automáticamente al iniciar.
- [x] Bitácora de texto en modo `append` usando `try-with-resources`.
- [x] Bono de `FileChooser` implementado.
- [x] Control de commits y estructura dentro de la carpeta GarajeGTAVI
