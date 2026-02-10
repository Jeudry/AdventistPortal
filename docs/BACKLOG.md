# 📋 Scrum Product Backlog: RosaFiesta App

Este documento detalla las Historias de Usuario (User Stories) necesarias para desarrollar la aplicación completa de **RosaFiesta**.
**Stack Tecnológico:** Spring Boot 4 (Kotlin), Compose Multiplatform (Android/iOS/Desktop), PostgreSQL, Google AI (Gemini).

---

## 🏗️ Épica 1: Arquitectura y Configuración (Cimientos)
*Configuración del entorno de desarrollo y la infraestructura base.*

### 1.1. Inicialización del Proyecto Multiplataforma
> **Como** desarrollador,  
> **Quiero** configurar un proyecto Gradle con módulos compartidos (KMP) para Android, iOS y Desktop,  
> **Para** tener una base de código unificada y evitar duplicar lógica de negocio.
> * **Criterios de Aceptación:**
    >   * El proyecto compila en Android Studio y Xcode.
>   * Estructura de carpetas definida: `commonMain`, `androidMain`, `iosMain`, `desktopMain`.
>   * Dependencias de Compose Multiplatform inyectadas.

### 1.2. Configuración de Base de Datos PostgreSQL
> **Como** arquitecto de software,  
> **Quiero** configurar la conexión a PostgreSQL usando Spring Data JPA y Testcontainers,  
> **Para** persistir la información de clientes, eventos e inventario de forma segura.
> * **Criterios de Aceptación:**
    >   * Contenedor Docker con PostgreSQL levantado correctamente.
>   * Archivo `application.properties` configurado con credenciales de entorno.
>   * Test de conexión exitoso al iniciar la aplicación Spring Boot.

### 1.3. Configuración de Seguridad Base
> **Como** administrador del sistema,  
> **Quiero** un sistema de autenticación básico (JWT),  
> **Para** que solo el personal autorizado de RosaFiesta pueda acceder al panel de administración.
> * **Criterios de Aceptación:**
    >   * Endpoint `/login` devuelve un token JWT válido.
>   * Endpoints protegidos rechazan peticiones sin token.

---

## 🎨 Épica 2: Diseño y Experiencia de Usuario (UI System)
*Implementación de la identidad visual de RosaFiesta.*

### 2.1. Sistema de Diseño "Armonía RosaFiesta"
> **Como** diseñador,  
> **Quiero** implementar un tema en Compose que use la paleta de colores definida,  
> **Para** mantener la consistencia de marca en todas las pantallas.
> * **Criterios de Aceptación:**
    >   * **Primary:** Rosa (Fondos principales, Headers).
>   * **Secondary:** Púrpura (Texto destacado, Títulos).
>   * **Tertiary:** Verde (Botones de acción positiva, confirmaciones).
>   * **Error/Warning:** Amarillo (Alertas, llamadas de atención).
>   * Tipografía legible y moderna configurada en `MaterialTheme`.

### 2.2. Componentes UI Reutilizables
> **Como** desarrollador frontend,  
> **Quiero** crear una librería de componentes propios (Botones, Tarjetas de Evento, Inputs),  
> **Para** acelerar el desarrollo de las pantallas siguientes.
> * **Criterios de Aceptación:**
    >   * `RosaCard`: Tarjeta con bordes redondeados y sombra suave.
>   * `RosaButton`: Botón con gradiente o color sólido según estado.

---

## 📦 Épica 3: Gestión de Inventario (Core Domain)
*Digitalización de los activos de la empresa.*

### 3.1. CRUD de Artículos de Decoración
> **Como** encargado de almacén,  
> **Quiero** poder crear, leer, actualizar y eliminar artículos (mesas, sillas, manteles),  
> **Para** mantener el catálogo al día.
> * **Criterios de Aceptación:**
    >   * Campos: Nombre, Categoría, Cantidad Total, Precio Alquiler, Foto URL.
>   * Las fotos se pueden subir y vincular al registro en PostgreSQL.

### 3.2. Control de Disponibilidad (Lógica de Negocio)
> **Como** planificador,  
> **Quiero** saber cuántas unidades de un artículo están disponibles para una fecha específica,  
> **Para** evitar alquilar el mismo material a dos eventos simultáneos (Overbooking).
> * **Criterios de Aceptación:**
    >   * Consulta que recibe `ItemID` y `FechaEvento` y devuelve `CantidadDisponible`.
>   * Bloqueo de stock cuando un evento pasa a estado "Confirmado".

---

## 📅 Épica 4: Planificación de Eventos
*El corazón de la aplicación para la gestión del servicio.*

### 4.1. Creación de Expediente de Evento
> **Como** coordinador,  
> **Quiero** registrar un nuevo evento con los datos del cliente y la logística básica,  
> **Para** iniciar el proceso de planificación.
> * **Criterios de Aceptación:**
    >   * Formulario con: Cliente, Tipo de Evento (Boda, XV Años, Corporativo), Fecha, Lugar, Presupuesto Estimado.
>   * Estado inicial: "Borrador".

### 4.2. Moodboard y Selección de Items
> **Como** decorador,  
> **Quiero** agregar productos del inventario a un evento específico,  
> **Para** armar la propuesta visual y económica.
> * **Criterios de Aceptación:**
    >   * Carrito de compras interno por evento.
>   * Visualización del costo acumulado en tiempo real.

---

## 🤖 Épica 5: Inteligencia Artificial (Google Gemini)
*Funcionalidades avanzadas para potenciar la creatividad y eficiencia.*

### 5.1. Generador de Conceptos Visuales (Gemini Vision)
> **Como** cliente indeciso,  
> **Quiero** subir una foto de referencia (ej. Pinterest) y que la App me sugiera productos de RosaFiesta similares,  
> **Para** lograr el estilo que deseo con lo que hay disponible.
> * **Criterios de Aceptación:**
    >   * El sistema analiza la imagen y extrae etiquetas (ej. "Rústico", "Madera", "Flores blancas").
>   * El sistema busca en la DB items que coincidan con esas etiquetas.

### 5.2. Asistente de Redacción (Copywriting)
> **Como** organizador,  
> **Quiero** generar textos para invitaciones o agradecimientos basados en el tono del evento,  
> **Para** ahorrar tiempo en la comunicación.
> * **Criterios de Aceptación:**
    >   * Input: "Boda formal, noche, pareja Ana y Juan".
>   * Output: 3 opciones de texto generadas por Gemini.

### 5.3. Asistente de Planificación (Chatbot)
> **Como** usuario,  
> **Quiero** preguntar "¿Qué necesito para una fiesta de 50 personas?" y recibir una lista sugerida,  
> **Para** no olvidar detalles importantes.
> * **Criterios de Aceptación:**
    >   * Chat integrado en la app.
>   * Respuestas contextuales basadas en el inventario de RosaFiesta.

---

## 💰 Épica 6: Finanzas y Administración
*Control económico del negocio.*

### 6.1. Generación de Presupuestos
> **Como** administrador,  
> **Quiero** generar un presupuesto detallado en PDF,  
> **Para** enviarlo al cliente para su aprobación.
> * **Criterios de Aceptación:**
    >   * PDF con desglose de items, mano de obra y transporte.
>   * Diseño con logo y colores de RosaFiesta.

### 6.2. Gestión de Pagos
> **Como** tesorero,  
> **Quiero** registrar los pagos parciales y ver el saldo restante de un evento,  
> **Para** asegurar el cobro completo antes del día del evento.
> * **Criterios de Aceptación:**
    >   * Indicador visual de estado de pago (Rojo: Pendiente, Amarillo: Parcial, Verde: Pagado).