package com.jositoluiso.pedidos.controller;

import com.jositoluiso.pedidos.dto.ProductRequestDTO;
import com.jositoluiso.pedidos.dto.ProductResponseDTO;
import com.jositoluiso.pedidos.entity.Product;
import com.jositoluiso.pedidos.mapper.ProductMapper;
import com.jositoluiso.pedidos.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Products management API")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create a new product")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO productRequest) {
        Product product = ProductMapper.toEntity(productRequest);
        Product createdProduct = productService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductMapper.toResponseDTO(createdProduct));
    }

    @Operation(summary = "Get all products")
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(ProductMapper.toResponseList(products));
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(ProductMapper.toResponseDTO(product));
    }

    @Operation(summary = "Update an existing product")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO productRequest
    ) {
        Product product = ProductMapper.toEntity(productRequest);
        Product updatedProduct = productService.update(id, product);
        return ResponseEntity.ok(ProductMapper.toResponseDTO(updatedProduct));
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}