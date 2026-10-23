# DYM Cyber · WIM 100 – Aplicación Móvil MVP

**Asignatura:** DSY1105 – Desarrollo de Aplicaciones Móviles  
**Institución:** Duoc UC – Sede San Joaquín  
**Organización / Desafío:** DYM Cyber / Woman and Mining (WIM 100)  

---

## 1. Descripción del Proyecto
Este proyecto corresponde a un Producto Mínimo Viable (MVP) móvil para Android enfocado en digitalizar, ordenar y centralizar el proceso de postulación y evaluación de candidatas para el reconocimiento **WIM 100** (Woman and Mining).

Permite a las evaluadoras registrar y consultar postulantes, revisar sus antecedentes (CV y enlaces a perfiles profesionales simulados), aplicar una matriz homogénea de evaluación con criterios objetivos, justificar las calificaciones con trazabilidad y prevenir evaluaciones duplicadas.

> [!NOTE]
> Todo el sistema opera exclusivamente con **datos ficticios, sintéticos y anonimizados**, cumpliendo con los estándares de privacidad y alcances del caso académico.

---

## 2. Integrantes del Equipo
* **Estudiante 1:** *(Completar con Nombre y Apellido)*
* **Estudiante 2:** *(Completar con Nombre y Apellido)*

---

## 3. Arquitectura del Proyecto (MVVM)
La aplicación sigue estrictamente el patrón arquitectónico **Model-View-ViewModel (MVVM)** distribuido en 4 paquetes principales bajo `com.example.dymcyber`:

```text
com.example.dymcyber/
├── model/        # Entidades Room y modelos de datos (Candidata, Evaluación, Criterios)
├── repository/   # Base de datos Room, DAOs y Repositorios de datos
├── viewmodel/    # Lógica de negocio, gestión de estados y validaciones desacopladas de la UI
└── screens/      # Pantallas y componentes visuales en Jetpack Compose con Material Design 3
```

---

## 4. Tecnologías Utilizadas
* **Lenguaje:** Kotlin
* **UI Framework:** Jetpack Compose con Material Design 3
* **Persistencia Local:** Room (SQLite) con KSP
* **Gestión de Estado y Ciclo de Vida:** ViewModel (`State` / `StateFlow`)
* **Navegación:** Navigation Compose
* **Recursos Nativos:** Intents del sistema y selector de archivos

---

## 5. Instrucciones para Ejecutar el Proyecto
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/OscarInaipil/Moviles_Grupo4.git
   ```
2. Abrir la carpeta `Moviles_Grupo4` en **Android Studio** (versión Ladybug o superior recomendada).
3. Esperar la sincronización de Gradle (`Sync Project with Gradle Files`).
4. Seleccionar un emulador Android (con API 24 o superior) o conectar un dispositivo físico con depuración USB habilitada.
5. Presionar **Run 'app'** (`Shift + F10`).
