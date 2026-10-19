package com.example.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.dtos.ProductRequestDto;
import com.example.dtos.ProductResponseDto;

public interface ProductService {

    Page<ProductResponseDto> findAll(Pageable pageable);

    ProductResponseDto findById(Long id);

    ProductResponseDto create(ProductRequestDto request);

    void deleteById(Long id);
}