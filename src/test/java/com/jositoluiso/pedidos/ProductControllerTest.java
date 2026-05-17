package com.jositoluiso.pedidos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jositoluiso.pedidos.controller.ProductController;
import com.jositoluiso.pedidos.dto.ProductRequestDTO;
import com.jositoluiso.pedidos.entity.Product;
import com.jositoluiso.pedidos.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    void shouldCreateProductWithValidInput() throws Exception {
        // ENTRADA VÁLIDA
        ProductRequestDTO productRequest = ProductRequestDTO.builder()
                .name("Laptop")
                .description("High-performance laptop")
                .price(BigDecimal.valueOf(1500.00))
                .stock(10)
                .build();

        // SALIDA DEL SERVICIO
        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High-performance laptop")
                .price(BigDecimal.valueOf(1500.00))
                .stock(10)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productService.create(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated());

        verify(productService, times(1)).create(any(Product.class));
    }

    @Test
    void shouldReturn400WhenProductNameIsBlank() throws Exception {
        String invalidJson = """
        {
            "name": "",
            "description": "High-performance laptop",
            "price": 1500.00,
            "stock": 10
        }
        """;

        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).create(any());
    }

    @Test
    void shouldReturn400WhenPriceIsNegative() throws Exception {
        String invalidJson = """
        {
            "name": "Laptop",
            "description": "High-performance laptop",
            "price": -100.00,
            "stock": 10
        }
        """;

        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).create(any());
    }

    @Test
    void shouldReturn400WhenStockIsNegative() throws Exception {
        String invalidJson = """
        {
            "name": "Laptop",
            "description": "High-performance laptop",
            "price": 1500.00,
            "stock": -5
        }
        """;

        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).create(any());
    }

    @Test
    void shouldGetAllProducts() throws Exception {
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

        when(productService.findAll()).thenReturn(java.util.List.of(product1));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());

        verify(productService, times(1)).findAll();
    }

    @Test
    void shouldGetProductById() throws Exception {
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

        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk());

        verify(productService, times(1)).findById(1L);
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        ProductRequestDTO productRequest = ProductRequestDTO.builder()
                .name("Laptop Pro")
                .description("Updated laptop")
                .price(BigDecimal.valueOf(2000.00))
                .stock(5)
                .build();

        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Laptop Pro")
                .description("Updated laptop")
                .price(BigDecimal.valueOf(2000.00))
                .stock(5)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productService.update(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/products/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk());

        verify(productService, times(1)).update(eq(1L), any(Product.class));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).delete(1L);
    }
}
