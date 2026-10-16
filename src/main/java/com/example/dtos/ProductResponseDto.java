package com.example.dtos;

import java.math.BigDecimal;

public record ProductResponseDto(
    Long id,
    String name,
    String description,
    Integer stock,
    BigDecimal price,
    String productImage,
    PresentationResponseDto presentation
) {}
