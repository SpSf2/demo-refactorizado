package com.example.services;

import com.example.dtos.PresentationResponseDto;
import java.util.List;

public interface PresentationService {

    List<PresentationResponseDto> findAll();

    PresentationResponseDto findById(Long id);

    PresentationResponseDto create(PresentationResponseDto request);

    void deleteById(Long id);
}