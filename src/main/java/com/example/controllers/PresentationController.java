package com.example.controllers;

import com.example.dtos.PresentationRequestDto;
import com.example.dtos.PresentationResponseDto;
import com.example.services.PresentationService;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/presentations")
public class PresentationController {

    private final PresentationService presentationService;

    public PresentationController(PresentationService presentationService) {
        this.presentationService = presentationService;
    }

    @GetMapping
    public List<PresentationResponseDto> findAll() {
        return presentationService.findAll();
    }

    @GetMapping("/{id}")
    public PresentationResponseDto findById(@PathVariable Long id) {
        return presentationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PresentationResponseDto create(@Valid @RequestBody PresentationRequestDto request) {
        return presentationService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        presentationService.deleteById(id);
    }
}
