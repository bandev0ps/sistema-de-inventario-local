# Sistema de Gestión e Inventario de Equipos de Laboratorio con GUI (Swing)

Aplicación de escritorio desarrollada en **Java** con interfaz gráfica de usuario (**Swing**) para la administración integral del inventario, gestión de préstamos, calibración y mantenimiento de equipos en un laboratorio de ingeniería.

---

## Funcionalidades Principales

### 1. Gestión de Inventario y Registro de Equipos
* **Registro de Equipos Especializados:** Formularios dinámicos para dar de alta tres tipos de equipos con atributos específicos:
  * **Portátiles:** Marca, número de serie y capacidad de almacenamiento en GB.
  * **Proyectores:** Resolución, lúmenes y contador de horas de lámpara.
  * **Multímetros:** Rango de voltaje, precisión, tipo (digital/analógico) e indicador de calibración (`calibrado`).
* **Visualización Dinámica:** Contiene una tabla general con actualización en tiempo real que muestra el tipo, código de inventario, ubicación, estado operativo y el resumen técnico detallado del equipo obtenido mediante comportamiento polimórfico.
* **Búsqueda y Filtros:** Búsqueda rápida por código de inventario y filtrado de la tabla por estado operativo y tipo de equipo.

---

### 2. Módulo de Préstamos y Devoluciones
* **Asociación de Préstamos Activos:** Registro de préstamos en la capa de modelo/servicios asignando el número de cuenta y nombre del estudiante al equipo seleccionado.
* **Evaluación de Reglas de Negocio en Tiempo Real:**
  * **Portátil:** Se permite el préstamo únicamente si su estado es `DISPONIBLE`.
  * **Proyector:** Valida que esté `DISPONIBLE` y que el contador de horas de lámpara sea **menor a 2000 horas**.
  * **Multímetro:** Garantiza que esté `DISPONIBLE`, que sea un equipo `Calibrable` y que se encuentre **debidamente calibrado** (`calibrado == true`).
* **Devoluciones:** Procesa el retorno del equipo, marcando el registro de préstamo como inactivo y restaurando el estado del equipo a `DISPONIBLE`.
* **Vista de Préstamos Activos:** Listado exclusivo para consultar todos los equipos prestados actualmente junto con los datos del estudiante asignado.

---

### 3. Gestión de Mantenimiento y Calibración
* **Envío a Taller/Mantenimiento (Interfaz `Mantenible`):** Permite cambiar el estado operativo de un equipo a `EN_MANTENIMIENTO` especificando el motivo del envío, o reincorporarlo marcándolo como `DISPONIBLE` una vez reparado.
* **Registro de Calibración (Interfaz `Calibrable`):** Permite evaluar si un multímetro u otro equipo calibrable requiere mantenimiento técnico y marcar su estado de calibración como actualizado (`calibrado = true`).

---

### 4. Panel de Resumen y Estadísticas del Laboratorio
* **Conteo Por Tipo:** Muestra el total de Portátiles, Proyectores y Multímetros registrados.
* **Conteo Por Estado Operativo:** Desglose según estados (`DISPONIBLE`, `PRESTADO`, `EN_MANTENIMIENTO`, `FUERA_DE_SERVICIO`).
* **Métricas Globales:** Indicadores globales del total de equipos en inventario y cantidad de préstamos activos.

---

### 5. Control Excepciones y Manejo de Errores
* **Manejo de Excepciones Personalizadas:**
  * `EquipoNotFoundException`: Captura búsquedas de equipos con ID o código de inventario inexistentes.
  * `EstadoOperativoInvalidoException`: Previene acciones no permitidas según el estado del equipo (ej. intentar prestar un equipo en mantenimiento).
  * `ReglaPrestamoException`: Notifica el incumplimiento de reglas específicas (lámpara agotada, multímetro sin calibrar).
  * `PrestamoActivoException`: Evita préstamos duplicados para equipos ya prestados.
* **Integridad de la GUI:** Uso de diálogos interactivos (`JOptionPane` y `JDialog`) para mostrar alertas y errores sin interrumplir ni cerrar la aplicación. Validaciones de entradas numéricas, campos vacíos e insumos inválidos.

---

## Arquitectura y Estructura del Proyecto

El proyecto cumple con el principio de responsabilidad única, separando la interfaz gráfica de la lógica de negocio en 5 paquetes principales:

```text
lab/
 ├── lab.modelo/        # Clase base abstracta (Equipo), enum EstadoOperativo y subclases (Portatil, Proyector, Multimetro)
 ├── lab.prestamos/     # Clase Prestamo y registro/historial de préstamos activos
 ├── lab.servicios/     # GestorLaboratorio (Lógica de inventario, reglas de negocio, consultas y reportes)
 ├── lab.excepciones/   # Excepciones personalizadas del sistema
 └── lab.app/           # Componentes GUI (Ventana principal, diálogos JDialog, eventos y controladores)
