package com.example.services;

import com.example.dtos.PresentationRequestDto;
import com.example.dtos.PresentationResponseDto;
import com.example.entities.Presentation;
import com.example.repositories.PresentationRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PresentationServiceImpl implements PresentationService {

    private final PresentationRepository presentationRepository;

    public PresentationServiceImpl(PresentationRepository presentationRepository) {
        this.presentationRepository = presentationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresentationResponseDto> findAll() {
        return presentationRepository.findAll()
                .stream()
                .map(presentation -> new PresentationResponseDto(
                        presentation.getId(),
                        presentation.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PresentationResponseDto findById(Long id) {
        return presentationRepository.findById(id)
                .map(presentation -> new PresentationResponseDto(
                        presentation.getId(),
                        presentation.getName()))
                .orElseThrow(() -> new EntityNotFoundException("Presentation not found with id: " + id));
    }

    @Override
    @Transactional
    public PresentationResponseDto create(PresentationRequestDto request) {
        Presentation presentation = new Presentation();
        presentation.setName(request.name());
        Presentation savedPresentation = presentationRepository.save(presentation);
        return new PresentationResponseDto(
                savedPresentation.getId(),
                savedPresentation.getName());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!presentationRepository.existsById(id)) {
            throw new EntityNotFoundException("Presentation not found with id: " + id);
        }
        presentationRepository.deleteById(id);
    }
}
