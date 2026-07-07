package com.caio.product_management.service;

import com.caio.product_management.domain.Category;
import com.caio.product_management.domain.Product;
import com.caio.product_management.dto.ProductRequestDTO;
import com.caio.product_management.dto.ProductResponseDTO;
import com.caio.product_management.exception.ResourceNotFoundException;
import com.caio.product_management.repository.CategoryRepository;
import com.caio.product_management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(UUID id) {
        return ProductResponseDTO.from(findProductOrThrow(id));
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO request) {
        Category category = findCategoryOrThrow(request.categoryId());
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setDescription(request.description());
        product.setCategory(category);
        return ProductResponseDTO.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponseDTO update(UUID id, ProductRequestDTO request) {
        Product product = findProductOrThrow(id);
        Category category = findCategoryOrThrow(request.categoryId());
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setDescription(request.description());
        product.setCategory(category);
        return ProductResponseDTO.from(productRepository.save(product));
    }

    @Transactional
    public void delete(UUID id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
    }

    private Product findProductOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Category findCategoryOrThrow(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

}