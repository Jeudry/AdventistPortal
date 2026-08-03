# 🌸 Proyecto: AdventistPortal App - Planificación Integral

## 1. Visión General
**AdventistPortal** es una plataforma multiplataforma (Android, iOS, Desktop, Web) diseñada para la gestión integral de eventos, decoración y planificación. La app busca automatizar la logística interna y ofrecer una experiencia de consultoría creativa impulsada por IA.

### Paleta de Colores (Armonía Visual)
* **Primario:** Rosa (#FFC0CB) - Creatividad y Calidez.
* **Secundario:** Púrpura (#800080) - Elegancia y Exclusividad.
* **Acentuado:** Verde (#4CAF50) - Crecimiento, Naturaleza y Éxito.
* **Acción:** Amarillo (#FFD700) - Energía y Atención (CTAs).

---

## 2. Stack Tecnológico
* **Backend:** Spring Boot 4 + Kotlin.
* **Base de Datos:** PostgreSQL (Relacional, para alta integridad de datos).
* **Frontend:** Compose Multiplatform (Código compartido al 95%).
* **Comunicación:** GraphQL (Eficiencia en carga de datos complejos de eventos).
* **IA:** Google Gemini API (Modelado de eventos, generación de ideas y análisis de stock).

---

## 3. Arquitectura del Sistema

### A. Módulos del Backend (Spring Boot 4)
* **Event Engine:** Gestión de fechas, locaciones y estados (Planificando, En Montaje, Finalizado).
* **Inventory API:** Control de stock de mobiliario, mantelería y flores en tiempo real.
* **Financial Suite:** Generación de presupuestos dinámicos, facturación y pagos.
* **AI Integration Service:** Gateway para conectar con Gemini para sugerencias de diseño.

### B. Módulos del Frontend (Compose Multiplatform)
* **Shared UI Core:** Componentes comunes (Botones AdventistPortal, inputs, tarjetas).
* **Client Portal:** Visualización de moodboards y seguimiento del evento.
* **Admin Dashboard:** Gestión de inventario con escaneo de QR y reportes financieros.

---

## 4. Funcionalidades de Inteligencia Artificial
1.  **AI Moodboard Assistant:** El cliente describe un concepto y la IA genera una lista de materiales e ideas de decoración basadas en el stock real de la empresa.
2.  **Smart Budgeter:** Calcula presupuestos basados en la complejidad del evento y el historial de precios en PostgreSQL.
3.  **Chatbot Consultor:** Respuesta automática a preguntas frecuentes de clientes sobre disponibilidad y servicios.
4.  **Generador de Contenidos:** Creación de textos para invitaciones y post-eventos.

---

## 5. Esquema de Datos Inicial (PostgreSQL)
* **Users:** (id, nombre, email, rol [ADMIN, CLIENT]).
* **Events:** (id, client_id, fecha, locación, presupuesto_est, estado).
* **Inventory:** (id, item_nombre, categoría, cantidad_total, cantidad_disponible, precio_alquiler).
* **Budgets:** (id, event_id, total, desglose_json, pagado).

---

## 6. Hoja de Ruta de Desarrollo

### Fase 1: Cimientos (Backend & DB)
* Configuración de Spring Boot 4 y conexión a PostgreSQL.
* Creación de Entidades JPA y Security Resolvers para GraphQL.

### Fase 2: Lógica Multiplataforma
* Setup de proyecto Compose Multiplatform.
* Implementación de Temas (Material 3) con la paleta de AdventistPortal.

### Fase 3: Integración de IA
* Conexión con el SDK de Google AI.
* Desarrollo del asistente de sugerencias decorativas.

### Fase 4: Despliegue y Pruebas
* Contenerización con Docker.
* Generación de instaladores para Android (.apk), iOS, y Desktop (.exe/.dmg).