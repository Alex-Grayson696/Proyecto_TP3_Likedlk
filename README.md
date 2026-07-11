# 🎓 LinkedIn Académico — Red de Networking Profesional

> Proyecto #12 — Técnicas de Programación III | Sección 2 | UNEG  
> Alumno: Gabriel Gaudier | C.I.: 31.974.484  
> Profesora: Dubraska Roca | Período: 2026-1

Sistema de escritorio desarrollado en **Java con interfaz gráfica Swing** que modela una red de contactos académico-profesionales usando **Teoría de Grafos**. Permite conectar Estudiantes, Profesores y Empresas, y encontrar rutas de recomendación entre ellos mediante los algoritmos **BFS** y **DFS**.

---

## 📋 Tabla de Contenidos

- [Descripción del Proyecto](#-descripción-del-proyecto)
- [Características](#-características)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Cómo Ejecutar](#-cómo-ejecutar)
- [Guía de Uso](#-guía-de-uso)
- [Patrones de Diseño](#-patrones-de-diseño)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)

---

## 📌 Descripción del Proyecto

**LinkedIn Académico** modela la red de contactos del entorno universitario como un **grafo no dirigido** con lista de adyacencia. Cada nodo representa un actor (Estudiante, Profesor o Empresa) y cada arista representa una relación etiquetada ("Conoce a", "Trabaja en", "Pasante en", etc.).

El sistema permite:
- Gestionar nodos y conexiones de la red (CRUD completo)
- Visualizar el grafo dibujado de forma interactiva
- Ejecutar **BFS** para encontrar la ruta de recomendación más corta
- Ejecutar **DFS** para explorar la red en profundidad
- Persistir todos los datos automáticamente en archivos CSV

---

## ✨ Características

| Función | Descripción |
|---|---|
| 🌐 Vista de Red | Grafo dibujado con nodos arrastrables, coloreados por tipo |
| 👤 Gestión de Nodos | CRUD de Estudiantes, Profesores y Empresas |
| 🔗 Gestión de Conexiones | Crear y eliminar relaciones con etiquetas personalizadas |
| 🔵 BFS | Ruta más corta (menor número de intermediarios) entre dos nodos |
| 🟢 DFS | Exploración en profundidad y detección de conectividad |
| 💾 Persistencia | Guardado automático en `nodos.csv` y `conexiones.csv` |

---

## ✅ Requisitos Previos

Antes de instalar el proyecto, asegúrate de tener lo siguiente:

### 1. Java Development Kit (JDK) 11 o superior

Verifica si tienes Java instalado abriendo una terminal o símbolo del sistema y ejecutando:

```bash
java -version
```

Deberías ver algo similar a:
```
java version "11.0.x" ...
```

**Si no tienes Java instalado:**

- Descárgalo desde: https://www.oracle.com/java/technologies/downloads/
- O instala OpenJDK desde: https://adoptium.net/
- Durante la instalación en Windows, marca la opción **"Set JAVA_HOME variable"**

### 2. Git (para clonar el repositorio)

Verifica si tienes Git instalado:

```bash
git --version
```

**Si no tienes Git instalado:**

- Descárgalo desde: https://git-scm.com/downloads
- Instálalo con las opciones por defecto

### 3. IDE recomendado (opcional)

Puedes usar cualquiera de los siguientes entornos:

| IDE | Descarga |
|---|---|
| IntelliJ IDEA (recomendado) | https://www.jetbrains.com/idea/ |
| Eclipse | https://www.eclipse.org/downloads/ |
| VS Code + Extension Pack for Java | https://code.visualstudio.com/ |

> También puedes compilar y ejecutar el proyecto **sin IDE**, usando solo la terminal.

---

## 🚀 Instalación y Configuración

Sigue estos pasos en orden para instalar y ejecutar el proyecto correctamente.

### Paso 1 — Clonar el Repositorio

Abre una terminal (o Git Bash en Windows) y ejecuta:

```bash
git clone https://github.com/TU_USUARIO/linkedin-academico.git
```

Luego entra a la carpeta del proyecto:

```bash
cd linkedin-academico
```

> Reemplaza `TU_USUARIO` con tu nombre de usuario real de GitHub.

---

### Paso 2 — Verificar la Estructura de Archivos

Asegúrate de que la estructura sea la siguiente antes de continuar:

```
linkedin-academico/
├── Main.java
├── controlador/
│   └── RedControlador.java
├── modelo/
│   ├── Grafo.java
│   ├── Nodo.java
│   ├── Conexion.java
│   └── TipoNodo.java
├── vista/
│   ├── VentanaPrincipal.java
│   └── PanelGrafo.java
├── persistencia/
│   └── GestorDatos.java
└── README.md
```

---

### Paso 3 — Compilar el Proyecto

#### Opción A: Desde la terminal (sin IDE)

Estando dentro de la carpeta raíz del proyecto, ejecuta:

**En Windows (CMD o PowerShell):**
```cmd
mkdir out
javac -d out modelo\*.java persistencia\*.java controlador\*.java vista\*.java Main.java
```

**En Linux / macOS:**
```bash
mkdir -p out
javac -d out modelo/*.java persistencia/*.java controlador/*.java vista/*.java Main.java
```

Si la compilación fue exitosa, no verás ningún mensaje de error y se habrá creado la carpeta `out/` con los archivos `.class`.

---

#### Opción B: Desde IntelliJ IDEA

1. Abre IntelliJ IDEA
2. Haz clic en **"Open"** y selecciona la carpeta `linkedin-academico`
3. IntelliJ detectará automáticamente los archivos Java
4. Haz clic derecho sobre `Main.java` → **"Run 'Main.main()'"**

> IntelliJ compilará y ejecutará el proyecto automáticamente.

---

#### Opción C: Desde Eclipse

1. Abre Eclipse
2. Ve a **File → Import → General → Existing Projects into Workspace**
3. Selecciona la carpeta `linkedin-academico`
4. Haz clic en **Finish**
5. Haz clic derecho sobre `Main.java` → **Run As → Java Application**

---

#### Opción D: Desde VS Code

1. Abre VS Code
2. Ve a **File → Open Folder** y selecciona `linkedin-academico`
3. Instala la extensión **"Extension Pack for Java"** si no la tienes
4. Abre `Main.java`
5. Haz clic en el botón **▶ Run** que aparece encima del método `main`

---

### Paso 4 — Ejecutar el Proyecto

#### Desde la terminal (después de compilar con Opción A):

**En Windows:**
```cmd
java -cp out Main
```

**En Linux / macOS:**
```bash
java -cp out Main
```

Si todo está correcto, se abrirá la ventana principal de **LinkedIn Académico** con los datos de demostración cargados.

---

### Paso 5 — Datos de Persistencia (opcional)

La primera vez que ejecutes el programa, se cargará automáticamente una red de demostración con:
- 4 estudiantes, 3 profesores y 3 empresas
- 13 conexiones predefinidas

A partir de la segunda ejecución, el programa cargará los datos guardados en:

```
linkedin-academico/
├── nodos.csv        ← se crea automáticamente
└── conexiones.csv   ← se crea automáticamente
```

> Estos archivos se generan en la misma carpeta desde donde ejecutas el programa.  
> **No los elimines** si deseas conservar los datos entre sesiones.

Para **reiniciar la red desde cero**, simplemente elimina ambos archivos CSV y vuelve a ejecutar el programa.

---

## 📁 Estructura del Proyecto

```
linkedin-academico/
│
├── Main.java                          # Punto de entrada — inyecta dependencias
│
├── modelo/                            # Capa de Modelo (MVC)
│   ├── TipoNodo.java                  # Enum: ESTUDIANTE, PROFESOR, EMPRESA
│   ├── Nodo.java                      # Entidad del grafo
│   ├── Conexion.java                  # Arista con etiqueta de relación
│   └── Grafo.java                     # Lista de adyacencia + BFS + DFS
│
├── controlador/                       # Capa de Controlador (MVC)
│   └── RedControlador.java            # Lógica de negocio y validaciones
│
├── vista/                             # Capa de Vista (MVC)
│   ├── VentanaPrincipal.java          # Ventana Swing con 4 pestañas
│   └── PanelGrafo.java                # Canvas Java2D — visualización del grafo
│
├── persistencia/                      # Capa de Persistencia
│   └── GestorDatos.java               # Lectura y escritura de archivos CSV
│
├── nodos.csv                          # Generado automáticamente al ejecutar
├── conexiones.csv                     # Generado automáticamente al ejecutar
└── README.md
```

---

## ▶️ Cómo Ejecutar

### Resumen rápido (terminal)

```bash
# 1. Clonar
git clone https://github.com/TU_USUARIO/linkedin-academico.git
cd linkedin-academico

# 2. Compilar (Linux/macOS)
mkdir -p out
javac -d out modelo/*.java persistencia/*.java controlador/*.java vista/*.java Main.java

# 3. Ejecutar
java -cp out Main
```

---

## 📖 Guía de Uso

### Pestaña 1 — Vista de Red
- Muestra el grafo completo dibujado en pantalla
- **Azul** = Estudiante · **Verde** = Profesor · **Naranja** = Empresa
- Puedes **arrastrar los nodos** con el mouse para reorganizar el grafo
- Pasa el cursor sobre un nodo para ver su información completa en un tooltip
- Haz clic en **↺ Refrescar** para actualizar el canvas tras cambios

### Pestaña 2 — Nodos
- **Agregar:** Ingresa nombre, tipo y descripción → pulsa `+ Agregar`
- **Editar:** Selecciona una fila de la tabla → modifica los campos → pulsa `✎ Editar`
- **Eliminar:** Selecciona una fila → pulsa `✕ Eliminar` → confirma

### Pestaña 3 — Conexiones
- Selecciona **Nodo A** y **Nodo B** desde los desplegables
- Elige el tipo de relación ("Conoce a", "Trabaja en", "Pasante en", etc.)
- Pulsa `+ Conectar` para crear la arista
- Para eliminar: selecciona la conexión en la tabla → pulsa `✕ Eliminar`

### Pestaña 4 — Algoritmos
| Botón | Qué hace |
|---|---|
| 🔵 BFS — Ruta más corta | Camino con menos intermediarios entre origen y destino |
| 🔵 BFS — Exploración completa | Todos los nodos alcanzables con su grado de separación |
| 🟢 DFS — Primer camino | Primer camino encontrado en profundidad |
| 🟢 DFS — Exploración completa | Árbol de exploración con indentación por nivel |
| 👥 Ver contactos directos | Lista de vecinos inmediatos del nodo seleccionado |

---

## 🏗️ Patrones de Diseño

| Patrón | Categoría | Aplicación |
|---|---|---|
| MVC | Arquitectónico | Vista nunca accede al Grafo directamente |
| Factory Method | Creacional | `generarId(TipoNodo)` centraliza la creación de IDs |
| Observer | Comportamiento | `actualizarTodo()` refresca toda la GUI en una llamada |
| Inyección de Dependencias | Estructural | `Main` construye e inyecta el Controlador en la Vista |

---

## 🛠️ Tecnologías Utilizadas

- **Java 11+** — Lenguaje de programación
- **Java Swing** — Interfaz gráfica de usuario
- **Java2D (Graphics2D)** — Visualización del grafo en canvas
- **CSV (API estándar de Java)** — Persistencia sin dependencias externas
- **Git / GitHub** — Control de versiones

---

## 📝 Notas Adicionales

- El proyecto **no requiere dependencias externas** ni librerías de terceros.
- Funciona en **Windows, Linux y macOS** con Java 11 o superior.
- Los archivos CSV se crean en la carpeta donde se ejecuta el programa.
- Si ves caracteres extraños en Windows (tildes o ñ), asegúrate de que tu consola use UTF-8:
  ```cmd
  chcp 65001
  ```

---

*Universidad Nacional Experimental de Guayana — Ingeniería en Informática*  
*Técnicas de Programación III | 2026-1*
