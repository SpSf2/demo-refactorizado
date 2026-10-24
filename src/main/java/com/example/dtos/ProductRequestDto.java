package com.example.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequestDto(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no debe superar los 150 caracteres")
    String name,

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
    String description,

    @NotNull(message = "El stock es obligatorio")
    @Positive(message = "El stock debe ser un número positivo")
    Integer stock,

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un número positivo")
    BigDecimal price,

    @NotNull(message = "El id de la presentación es obligatorio")
    Long presentationId
) {}


