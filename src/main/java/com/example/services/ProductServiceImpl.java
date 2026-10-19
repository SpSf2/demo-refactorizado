package com.example.services;

import com.example.dtos.PresentationResponseDto;
import com.example.dtos.ProductRequestDto;
import com.example.dtos.ProductResponseDto;
import com.example.entities.Presentation;
import com.example.entities.Product;
import com.example.repositories.PresentationRepository;
import com.example.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final PresentationRepository presentationRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              PresentationRepository presentationRepository) {
        this.productRepository = productRepository;
        this.presentationRepository = presentationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAll(Pageable pageable) {
        return productRepository.findAllWithPresentationPaged(pageable)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto findById(Long id) {
        return productRepository.findByIdWithPresentation(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found with id: " + id));
    }

    @Override
    @Transactional
    public ProductResponseDto create(ProductRequestDto request) {
        Presentation presentation = presentationRepository.findById(request.presentationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Presentation not found with id: " + request.presentationId()));

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .stock(request.stock())
                .price(request.price())
                .productImage(request.productImage())
                .presentation(presentation)
                .build();

        Product saved = productRepository.save(product);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponseDto toDto(Product product) {
        Presentation presentation = product.getPresentation();
        PresentationResponseDto presentationDto = presentation == null
                ? null
                : new PresentationResponseDto(presentation.getId(), presentation.getName());

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getStock(),
                product.getPrice(),
                product.getProductImage(),
                presentationDto);
    }
}