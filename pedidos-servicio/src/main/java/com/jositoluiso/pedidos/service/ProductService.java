package com.jositoluiso.pedidos.service;

import com.jositoluiso.pedidos.entity.Product;
//import com.jositoluiso.pedidos.exception.ResourceNotFoundException;
import com.jositoluiso.pedidos.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @CacheEvict(value = "products", allEntries = true)
    public Product create(Product product) {
        product.setActive(true);
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Cacheable("products")
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "product", key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id)
               .orElseThrow();
                //() -> ResourceNotFoundException.notFound("Product", id));
    }

    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "product", key = "#id")
    })
    public Product update(Long id, Product productUpdates) {
        Product existingProduct = findById(id);

        existingProduct.setName(productUpdates.getName());
        existingProduct.setDescription(productUpdates.getDescription());
        existingProduct.setPrice(productUpdates.getPrice());
        existingProduct.setStock(productUpdates.getStock());

        return productRepository.save(existingProduct);
    }

    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "product", key = "#id")
    })
    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
}