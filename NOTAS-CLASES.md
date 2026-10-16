# NOTAS DE CLASES — demo-refactorizado

Archivo de traspaso para las sesiones de explicación de código.
Sirve para mantener la secuencia entre días aunque se cambie de sesión o de equipo.

---

## Cómo retomar esta secuencia

1. Abrir la terminal en esta carpeta:

   ```bash
   cd ~/Documentos/JavaProjects/ApiRest/demo-refactorizado
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

### Raíz
- [ ] `src/main/java/com/example/DemoRefactorizadoApplication.java` — clase principal de arranque de Spring Boot.

### Entidades (`entities/`)
- [ ] `Product.java` — entidad JPA `products`; relación `@ManyToOne` con `Presentation`.
- [ ] `Presentation.java` — entidad JPA `presentations`.

### DTOs (`dtos/`)
- [ ] `ProductRequestDto.java` — `record` de entrada con validaciones (`@NotBlank`, `@Size`, `@NotNull`, `@Positive`).
- [ ] `ProductResponseDto.java` — `record` de salida; anida `PresentationResponseDto`.
- [ ] `PresentationResponseDto.java` — `record` de salida para presentación.

### Repositorios (`repositories/`)
- [ ] `ProductRepository.java` — `JpaRepository` con consultas `@Query` (fetch de presentation, paginación).
- [ ] `PresentationRepository.java` — `JpaRepository` básico.

### Servicios (`services/`)
- [ ] `PresentationService.java` — interfaz de servicio (findAll, findById, create, deleteById).

### Pendientes / por revisar
- [ ] No existe `PresentationServiceImpl` (aún).
- [ ] No existe `ProductService`/`ProductServiceImpl` (aún).
- [ ] No existe capa de controladores (`controllers/`) todavía.

> Actualizar esta sección según avance el refactor.

---

## Registro de avance

| Fecha | Clase explicada | Punto donde quedamos / pendientes |
|-------|-----------------|-----------------------------------|
| 2026-10-09 | (sin empezar) | Sesión creada y movida al proyecto; formato acordado. |

---

## Próxima clase a explicar

> _(Anotar aquí la clase con la que se continuará la próxima vez.)_

- [ ] Clase: `...`
- Notas previas:
