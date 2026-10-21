# NOTAS DE CLASES — demo-refactorizado

Archivo de traspaso para las sesiones de explicación de código.
Sirve para mantener la secuencia entre días aunque se cambie de sesión o de equipo.

---

## Cómo retomar esta secuencia

1. Abrir la terminal en esta carpeta:

   ```bash
   cd /home/dtgonzalez/git/demo-refactorizado
   opencode
   ```

2. Recuperar la sesión de explicaciones:
   - `Ctrl+X` y luego `L`, o `/sessions`
   - `Ctrl+O` para ver sesiones y proyectos recientes juntos.
3. Si se empieza una sesión nueva, adjuntar este archivo con:

   ```
   Continúa desde @NOTAS-CLASES.md
   ```

---

## Formato de explicación acordado

Cada bloque de código se explica con dos partes:

1. **Lectura Breve** — una frase en lenguaje natural que "lee" el código de
   izquierda a derecha (modificador, tipo de retorno, nombre y parámetros,
   indicando de dónde viene cada uno).
2. **Despiece** — análisis elemento por elemento (anotación, tipo, nombre),
   explicando qué es, qué hace en el framework/lenguaje y por qué se usa ahí.

Estilo: didáctico, en español, sin dar nada por supuesto, diferenciando
anotaciones, tipos y nombres de variable.

---

## Entorno de pruebas (opción C)

| Dato | Valor |
|------|-------|
| MySQL | Docker, contenedor `mysql-server`, imagen `mysql:8.4` |
| Puerto host | **3307** → 3306 del contenedor |
| BD | `refactor_prod_presenta` (utf8mb4) |
| Usuario / contraseña | `root` / `Temp2026` |
| Datasource | Configurado en `application.properties` (`ddl-auto=update`) |

- Las tablas `presentations` y `products` las crea Hibernate con `ddl-auto=update` (válido para aprender; **en producción no se usa**).
- Cliente de consola disponible en `/usr/bin/mysql`.
- **Aviso:** la contraseña está en claro en `application.properties`, y ese archivo sí se commitea. Pendiente decidir si se mueve a variable de entorno o a un perfil local ignorado en `.gitignore`.

---

## Inventario de clases

Estado: `[ ]` pendiente de explicar · `[x]` explicada.
(Entre paréntesis, si la clase ya existe en el repo.)

### Raíz
- [ ] `src/main/java/com/example/DemoRefactorizadoApplication.java` — clase principal de arranque de Spring Boot.

### Entidades (`entities/`)
- [ ] `Product.java` — entidad JPA `products`; relación `@ManyToOne` (LAZY) con `Presentation` (**lado dueño** de la relación).
- [ ] `Presentation.java` — entidad JPA `presentations`. **No tiene `@OneToMany`**: la relación es unidireccional `Product → Presentation`, y es correcto así. Añadirlo cargaría colecciones de hijos, con riesgo de `LazyInitializationException` y de bucles en el JSON, sin necesidad actual.

### DTOs (`dtos/`)
- [ ] `ProductRequestDto.java` — `record` de entrada con validaciones (`@NotBlank`, `@Size`, `@NotNull`, `@Positive`).
- [ ] `ProductResponseDto.java` — `record` de salida; anida `PresentationResponseDto`.
- [ ] `PresentationRequestDto.java` — (ya creada) `record` de entrada con `@NotBlank` y `@Size(max = 100)`. **Sin `id`**: el cliente no puede imponerlo.
- [ ] `PresentationResponseDto.java` — `record` de salida para presentación.

### Repositorios (`repositories/`)
- [ ] `ProductRepository.java` — `JpaRepository` con `@Query` (`JOIN FETCH` de presentation) y paginación `Page`/`Pageable`.
- [ ] `PresentationRepository.java` — `JpaRepository` básico.

### Servicios (`services/`)
- [ ] `PresentationService.java` — interfaz: `findAll`, `findById`, `create(PresentationRequestDto)`, `deleteById`.
- [ ] `PresentationServiceImpl.java` — (ya creada) implementación `@Service`; `PresentationRepository` por constructor; `@Transactional` (readOnly en lecturas); lanza `EntityNotFoundException`.
- [ ] `ProductService.java` — (ya creada) interfaz: `Page<ProductResponseDto> findAll(Pageable)` + findById/create/deleteById.
- [ ] `ProductServiceImpl.java` — (ya creada) `@Service`; `ProductRepository` + `PresentationRepository`; mapeo anidado y `page.map(...)`; `@Transactional`.

### Controladores (`controllers/`)
- [ ] `ProductController.java` — (ya creada) `@RestController` en `/api/products`; paginación con `@PageableDefault(size = 20, sort = "name", ASC)`; GET lista, GET por id, POST (201, `@Valid`), DELETE (204).
- [ ] `PresentationController.java` — (ya creada) `@RestController` en `/api/presentations`; sin paginación; GET lista, GET por id, POST (201, **`@Valid @RequestBody PresentationRequestDto`**), DELETE (204).

### Excepciones (`exceptions/`)
- [ ] `GlobalExceptionHandler.java` — (ya creada) `@RestControllerAdvice` con **tres** casos:

  | Excepción capturada | Código | Respuesta |
  |---------------------|--------|-----------|
  | `EntityNotFoundException` | **404** | `{"error": "..."}` |
  | `MethodArgumentNotValidException` | **400** | `{campo: mensaje, ...}` |
  | `DataIntegrityViolationException` | **409** | `{"error": "No se puede completar la operación: ..."}` |

### Pendientes / por revisar
- [ ] **(Feature nueva)** Subida de **imagen real**: endpoint `multipart` con `MultipartFile`, servicio de almacenamiento, carpeta `uploads/` y servido de archivos. Hoy `productImage` es **solo un `String`** (ruta o URL), no un binario. Aunque se implemente la subida, en BD **seguirá guardándose texto**: los binarios van al disco o a un servicio de objetos, no a la BD.
- [ ] `mvn test` fallará: `DemoRefactorizadoApplicationTests` es `@SpringBootTest` y depende del MySQL en marcha.
- [ ] (Opcional) Pruebas `@WebMvcTest` de los controladores (no necesitan BD). Nota: en Spring Boot 4, `@WebMvcTest` está en `org.springframework.boot.webmvc.test.autoconfigure` y se usa `@MockitoBean`, no `@MockBean`.
- [ ] Commitear `application.properties` (decidir antes lo de las credenciales).

> Actualizar esta sección según avance el refactor.

---

## Decisiones de diseño

- **Paginación solo en productos** (Opción A): `ProductService.findAll(Pageable)`. El orden viaja dentro del `Pageable` (`Sort`), no fijado en la query.
- Se eliminó del repositorio `findAllWithPresentation()` (el `List` con `ORDER BY p.name ASC` fijo), que quedaba sin uso.
- `PresentationService.findAll()` se mantiene sin paginar (devuelve `List`).
- Excepciones de "no encontrado": `EntityNotFoundException` (`jakarta.persistence`) tanto en `ProductServiceImpl` como en `PresentationServiceImpl`.
- El `@RestControllerAdvice` vive en el paquete **`exceptions/`** (no en `controllers/`), para separar el manejo de errores.
- Rutas base: `/api/products` y `/api/presentations`.
- **Request vs Response:** hacia dentro siempre `XxxRequestDto` (con `@Valid`), hacia fuera siempre `XxxResponseDto`. Antes `PresentationController.create` recibía un **Response** como entrada, lo que impedía validar y dejaba entrar un `id` impuesto por el cliente. Corregido.
- **409 vs 500:** una violación de integridad referencial es un **conflicto con el estado de los datos**, no un fallo interno. Devuelve 409.
- El mensaje del 409 es **genérico a propósito**: inspeccionar el error nativo de MySQL (`foreign key constraint fails`) acoplaría el código a un dialecto concreto.
- `Presentation.java` **no lleva `@OneToMany`**: relación unidireccional.

---

## Pruebas de integración (Postman / curl) — 2026-10-21

Base: `http://localhost:8080` — todos los POST con **Body → raw → JSON**.

| # | Petición | Esperado | Resultado |
|---|----------|----------|-----------|
| 1 | POST `/api/presentations` `{"name":"Botella 1L"}` | 201 | OK |
| 2 | POST `/api/presentations` `{"name":"Caja 6u"}` | 201 | OK |
| 3 | GET `/api/presentations` | 200 (2 elementos) | OK |
| 4 | GET `/api/presentations/1` | 200 | OK |
| 5 | GET `/api/presentations/999` | 404 `{"error":...}` | OK |
| 6 | POST `/api/products` con `presentationId: 1` | 201 con `"presentation"` **anidada** | OK |
| 7 | POST `/api/products` inválido | 400 | Funcionó, pero **formato feo** → corregido |
| 8 | POST `/api/products` con `presentationId: 999` | 404 | OK |
| 9 | GET `/api/products?page=0&size=20&sort=name,asc` | 200 con metadata de página | OK |
| 10 | GET `/api/products?page=0&size=5&sort=price,desc` | 200 | OK |
| 11 | GET `/api/products?page=0&size=1&sort=name,asc` | totalElements 2, totalPages 2 | OK |
| 12 | GET `/api/products/999` | 404 | OK |
| 13 | DELETE `/api/presentations/1` (con productos hijos) | 500 feo → **debería ser 409** | Corregido |
| 14 | DELETE en orden hijo → padre | 204 ×3 | OK |

**Hallazgos (los dos huecos del handler), ya corregidos:**

1. `@Valid` fallaba correctamente, pero el `400` salía con el volcado interno de Spring (`trace`, `codes`, `arguments`...) porque **no había handler** para `MethodArgumentNotValidException`. Ahora devuelve `{campo: mensaje}`.
2. Borrar una presentación con productos daba **500** porque **no había handler** para `DataIntegrityViolationException`. Ahora da **409**.

**Nota menor (cosmética):** el orden de las claves en el JSON de error del 400 **no es estable**. El `LinkedHashMap` conserva el orden de inserción, pero la lista `getBindingResult().getFieldErrors()` viene en el orden interno de Hibernate Validator, que no está garantizado. Se comprobó que dos ejecuciones del mismo endpoint devolvieron órdenes distintos. No se persigue: el JSON no garantiza orden. Si algún día se quiere alfabético y estable, basta con `new TreeMap<>()`.

**Detalle de MySQL:** tras borrar todo, `AUTO_INCREMENT` **no vuelve a 1** (quedó en 3). Es correcto y deseable: reutilizar ids borrados es peligroso en producción.

---

## Registro de avance

| Fecha | Clase explicada | Punto donde quedamos / pendientes |
|-------|-----------------|-----------------------------------|
| 2026-10-09 | (sin empezar) | Sesión creada y movida al proyecto; formato acordado. |
| 2026-10-18 | `PresentationServiceImpl` | Creada y compilando. `@Transactional` añadido. |
| 2026-10-18 | `ProductService` + `ProductServiceImpl` | Creadas y compilando. Interfaz con paginación (Opción A). |
| 2026-10-19 | (notas) | Corregida la ruta del proyecto y re-formateado el archivo. |
| 2026-10-19 | `ProductController` + `PresentationController` + `GlobalExceptionHandler` | Creadas y compilando. `PresentationServiceImpl` alineado a `EntityNotFoundException`. |
| 2026-10-20 | (entorno) | MySQL + `application.properties`; `ddl-auto=update` creó las tablas. Susto con `devtools`: recompilar con la app corriendo provoca un reinicio y puede fallar con "Failed to configure a DataSource". |
| 2026-10-21 | (pruebas) | Plan de pruebas completo en Postman: 12/12. Dos hallazgos en el handler. |
| 2026-10-21 | `GlobalExceptionHandler` (arreglo) | Añadidos 400 (`MethodArgumentNotValidException`) y 409 (`DataIntegrityViolationException`). Probado OK. |
| 2026-10-21 | `PresentationRequestDto` (nuevo) + `PresentationService`, `PresentationServiceImpl`, `PresentationController` | Eliminado el anti-patrón de usar el Response como entrada. `@Valid` real en presentaciones. Compila y probado OK. |

> Está todo **sin commitear** salvo los commits `01fed75`, `5bc927b` y `2b98f09`.
> Pendiente de commit: `GlobalExceptionHandler`, `PresentationRequestDto` (nuevo), `PresentationService`, `PresentationServiceImpl`, `PresentationController`, `application.properties`.

---

## Próxima clase a explicar

> _(Anotar aquí la clase con la que se continuará la próxima vez.)_

- [ ] Clase: `GlobalExceptionHandler` (los tres `@ExceptionHandler`) — es el archivo con más jugo didáctico de la sesión.
- Notas previas: siguiente hito = **subida de imagen real** (`MultipartFile` + carpeta `uploads/`), como feature nueva.
- Pendientes menores: commitear lo actual; decidir lo de la contraseña en `application.properties`.

---

## Cómo actualizar este archivo (procedimiento)

1. El agente escribe la versión nueva en:

   ```
   /home/dtgonzalez/.opencode/plan/NOTAS-CLASES.md
   ```

2. Copiarla encima de la del proyecto:

   ```bash
   cp /home/dtgonzalez/.opencode/plan/NOTAS-CLASES.md \
      /home/dtgonzalez/git/demo-refactorizado/NOTAS-CLASES.md
   ```

3. Verificar que quedó bien:

   ```bash
   diff /home/dtgonzalez/.opencode/plan/NOTAS-CLASES.md \
        /home/dtgonzalez/git/demo-refactorizado/NOTAS-CLASES.md && echo OK
   ```
