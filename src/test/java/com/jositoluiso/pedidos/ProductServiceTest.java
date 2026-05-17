package com.jositoluiso.pedidos;

import com.jositoluiso.pedidos.entity.Product;
import com.jositoluiso.pedidos.repository.ProductRepository;
import com.jositoluiso.pedidos.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateProductSuccessfully() {
        // DATOS DE ENTRADA
        Product product = new Product();
        product.setName("Laptop");
        product.setDescription("High-performance laptop");
        product.setPrice(BigDecimal.valueOf(1500.00));
        product.setStock(10);

        // SIMULAMOS LO QUE DEVUELVE EL REPOSITORY
        Product savedProduct = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High-performance laptop")
                .price(BigDecimal.valueOf(1500.00))
                .stock(10)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        // EJECUTAMOS EL MÉTODO REAL
        Product response = productService.create(product);

        // VALIDAMOS RESULTADO
        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        assertEquals(BigDecimal.valueOf(1500.00), response.getPrice());
        assertEquals(10, response.getStock());
        assertTrue(response.getActive());

        // VERIFICAMOS INTERACCIONES
        verify(productRepository, times(1))
                .save(any(Product.class));
    }

    @Test
    void shouldFindAllProducts() {
        // PREPARAMOS DATOS
        Product product1 = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High-performance")
                .price(BigDecimal.valueOf(1500.00))
                .stock(10)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Mouse")
                .description("Wireless mouse")
                .price(BigDecimal.valueOf(25.00))
                .stock(50)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        // EJECUTAMOS EL MÉTODO REAL
        List<Product> response = productService.findAll();

        // VALIDAMOS RESULTADO
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Laptop", response.get(0).getName());
        assertEquals("Mouse", response.get(1).getName());

        // VERIFICAMOS INTERACCIONES
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldFindProductByIdSuccessfully() {
        // PREPARAMOS DATOS
        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High-performance")
                .price(BigDecimal.valueOf(1500.00))
                .stock(10)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        // EJECUTAMOS EL MÉTODO REAL
        Product response = productService.findById(1L);

        // VALIDAMOS RESULTADO
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Laptop", response.getName());

        // VERIFICAMOS INTERACCIONES
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // PREPARAMOS MOCK - NO EXISTE EL PRODUCTO
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        // VERIFICAMOS QUE SE LANZA UNA EXCEPCIÓN
        assertThrows(RuntimeException.class, () -> {
            productService.findById(999L);
        });

        // VERIFICAMOS INTERACCIONES
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        // PRODUCTO EXISTENTE
        Product existingProduct = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Old description")
                .price(BigDecimal.valueOf(1500.00))
                .stock(10)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // DATOS PARA ACTUALIZACIÓN
        Product updateData = Product.builder()
                .name("Laptop Pro")
                .description("Updated laptop")
                .price(BigDecimal.valueOf(2000.00))
                .stock(5)
                .build();

        // PRODUCTO ACTUALIZADO
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Laptop Pro")
                .description("Updated laptop")
                .price(BigDecimal.valueOf(2000.00))
                .stock(5)
                .active(true)
                .createdAt(existingProduct.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class)))
                .thenReturn(updatedProduct);

        // EJECUTAMOS EL MÉTODO REAL
        Product response = productService.update(1L, updateData);

        // VALIDAMOS RESULTADO
        assertNotNull(response);
        assertEquals("Laptop Pro", response.getName());
        assertEquals("Updated laptop", response.getDescription());
        assertEquals(BigDecimal.valueOf(2000.00), response.getPrice());
        assertEquals(5, response.getStock());

        // VERIFICAMOS INTERACCIONES
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        // PRODUCTO EXISTENTE
        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High-performance")
                .price(BigDecimal.valueOf(1500.00))
                .stock(10)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        // EJECUTAMOS EL MÉTODO REAL
        productService.delete(1L);

        // VERIFICAMOS INTERACCIONES
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }
}
