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

## Inventario de clases

Estado: `[ ]` pendiente de explicar · `[x]` explicada.
(Entre paréntesis, si la clase ya existe en el repo.)

### Raíz
- [ ] `src/main/java/com/example/DemoRefactorizadoApplication.java` — clase principal de arranque de Spring Boot.

### Entidades (`entities/`)
- [ ] `Product.java` — entidad JPA `products`; relación `@ManyToOne` (LAZY) con `Presentation`.
- [ ] `Presentation.java` — entidad JPA `presentations`.

### DTOs (`dtos/`)
- [ ] `ProductRequestDto.java` — `record` de entrada con validaciones (`@NotBlank`, `@Size`, `@NotNull`, `@Positive`).
- [ ] `ProductResponseDto.java` — `record` de salida; anida `PresentationResponseDto`.
- [ ] `PresentationResponseDto.java` — `record` de salida para presentación.

### Repositorios (`repositories/`)
- [ ] `ProductRepository.java` — `JpaRepository` con `@Query` (`JOIN FETCH` de presentation) y paginación `Page`/`Pageable`.
- [ ] `PresentationRepository.java` — `JpaRepository` básico.

### Servicios (`services/`)
- [ ] `PresentationService.java` — interfaz: findAll, findById, create, deleteById.
- [ ] `PresentationServiceImpl.java` — (ya creada) implementación `@Service`; `PresentationRepository` por constructor; `@Transactional` (readOnly en lecturas); lanza `EntityNotFoundException`.
- [ ] `ProductService.java` — (ya creada) interfaz: `Page<ProductResponseDto> findAll(Pageable)` + findById/create/deleteById.
- [ ] `ProductServiceImpl.java` — (ya creada) `@Service`; `ProductRepository` + `PresentationRepository`; mapeo anidado y `page.map(...)`; `@Transactional`.

### Controladores (`controllers/`)
- [ ] `ProductController.java` — (ya creada) `@RestController` en `/api/products`; paginación con `@PageableDefault(size = 20, sort = "name", ASC)`; GET lista, GET por id, POST (201), DELETE (204).
- [ ] `PresentationController.java` — (ya creada) `@RestController` en `/api/presentations`; sin paginación; GET lista, GET por id, POST (201), DELETE (204).

### Excepciones (`exceptions/`)
- [ ] `GlobalExceptionHandler.java` — (ya creada) `@RestControllerAdvice`; convierte `EntityNotFoundException` → **404** con `{"error": "..."}`.

### Pendientes / por revisar
- [ ] Pruebas de integración (**opción C**): configurar MySQL en `application.properties` + probar los endpoints con `curl`/Postman.
- [ ] (Opcional) Pruebas `@WebMvcTest` de los controladores (no necesitan base de datos).
- [ ] (Opcional) `PresentationRequestDto` con `@NotBlank` para validar la creación de presentaciones.

> Actualizar esta sección según avance el refactor.

---

## Decisiones de diseño

- **Paginación solo en productos** (Opción A): `ProductService.findAll(Pageable)`. El orden viaja dentro del `Pageable` (`Sort`), no fijado en la query.
- Se eliminó del repositorio `findAllWithPresentation()` (el `List` con `ORDER BY p.name ASC` fijo), que quedaba sin uso.
- `PresentationService.findAll()` se mantiene sin paginar (devuelve `List`).
- Excepciones de "no encontrado": `EntityNotFoundException` (`jakarta.persistence`) tanto en `ProductServiceImpl` como en `PresentationServiceImpl`.
- El `@RestControllerAdvice` vive en el paquete **`exceptions/`** (no en `controllers/`), para separar el manejo de errores.
- Rutas base: `/api/products` y `/api/presentations`.
- `ProductController.create` lleva `@Valid` (el `ProductRequestDto` tiene validaciones); `PresentationController.create` no, porque `PresentationResponseDto` no tiene restricciones.

---

## Registro de avance

| Fecha | Clase explicada | Punto donde quedamos / pendientes |
|-------|-----------------|-----------------------------------|
| 2026-10-09 | (sin empezar) | Sesión creada y movida al proyecto; formato acordado. |
| 2026-10-18 | `PresentationServiceImpl` | Creada y compilando (`mvn compile` → BUILD SUCCESS); `@Transactional` añadido. Pendiente de explicar. |
| 2026-10-18 | `ProductService` + `ProductServiceImpl` | Creadas y compilando. Interfaz con paginación (Opción A); repositorio limpiado. Pendiente de explicar. |
| 2026-10-19 | (notas) | Corregida la ruta del proyecto a `/home/dtgonzalez/git/demo-refactorizado` y re-formateado el archivo. |
| 2026-10-19 | `ProductController` + `PresentationController` + `GlobalExceptionHandler` | Creadas y compilando. `PresentationServiceImpl` alineado a `EntityNotFoundException`. Commits `01fed75` y `5bc927b`. Pendiente de explicar. |

---

## Próxima clase a explicar

> _(Anotar aquí la clase con la que se continuará la próxima vez.)_

- [ ] Clase: `ProductController` (o la capa de controladores en general)
- Notas previas: siguiente hito = **pruebas de integración (opción C)**: configurar MySQL + probar con `curl`.

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

3. Verificar que quedó bien (opcional):

   ```bash
   git -C /home/dtgonzalez/git/demo-refactorizado diff -- NOTAS-CLASES.md
   ```
