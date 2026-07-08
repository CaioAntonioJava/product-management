package com.caio.product_management.controller;

import com.caio.product_management.dto.ProductPatchDTO;
import com.caio.product_management.dto.ProductRequestDTO;
import com.caio.product_management.dto.ProductResponseDTO;
import com.caio.product_management.exception.ResourceNotFoundException;
import com.caio.product_management.service.ProductService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID productId;
    private UUID categoryId;
    private ProductResponseDTO productResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();

        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        productResponse = new ProductResponseDTO(
                productId,
                "Laptop",
                new BigDecimal("1999.99"),
                10,
                "Gaming laptop",
                categoryId,
                "Electronics",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("GET /api/products should return 200 with products list")
    void findAllShouldReturnList() throws Exception {
        // Given
        when(productService.findAll(null)).thenReturn(List.of(productResponse));

        // When / Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(productId.toString()))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    @DisplayName("GET /api/products?name=X should forward the filter to the service")
    void findAllShouldForwardNameParam() throws Exception {
        // Given
        when(productService.findAll("lap")).thenReturn(List.of(productResponse));

        // When / Then
        mockMvc.perform(get("/api/products").param("name", "lap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    @DisplayName("GET /api/products/by-category/{categoryId} should return 200")
    void findByCategoryShouldReturnList() throws Exception {
        // Given
        when(productService.findByCategory(categoryId)).thenReturn(List.of(productResponse));

        // When / Then
        mockMvc.perform(get("/api/products/by-category/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(categoryId.toString()));
    }

    @Test
    @DisplayName("GET /api/products/by-category/{categoryId} should propagate not found")
    void findByCategoryShouldPropagateNotFound() throws Exception {
        // Given
        when(productService.findByCategory(categoryId))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        // When / Then
        try {
            mockMvc.perform(get("/api/products/by-category/{categoryId}", categoryId))
                    .andExpect(status().isInternalServerError());
        } catch (Exception ex) {
            Throwable root = ex;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }
            assertThat(root).isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("GET /api/products/{id} should return 200 with the product")
    void findByIdShouldReturnProduct() throws Exception {
        // Given
        when(productService.findById(productId)).thenReturn(productResponse);

        // When / Then
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    @DisplayName("POST /api/products should return 201 with the created product")
    void createShouldReturnCreated() throws Exception {
        // Given
        ProductRequestDTO request = new ProductRequestDTO(
                "Laptop",
                new BigDecimal("1999.99"),
                10,
                "Gaming laptop",
                categoryId
        );
        when(productService.create(any(ProductRequestDTO.class))).thenReturn(productResponse);

        // When / Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    @DisplayName("POST /api/products should return 400 when name is missing")
    void createShouldReturnBadRequestWhenNameBlank() throws Exception {
        // Given
        ProductRequestDTO request = new ProductRequestDTO(
                "",
                new BigDecimal("1999.99"),
                10,
                "Gaming laptop",
                categoryId
        );

        // When / Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products should return 400 when price is negative")
    void createShouldReturnBadRequestWhenPriceNegative() throws Exception {
        // Given
        ProductRequestDTO request = new ProductRequestDTO(
                "Laptop",
                new BigDecimal("-1"),
                10,
                "Gaming laptop",
                categoryId
        );

        // When / Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products should return 400 when stockQuantity is negative")
    void createShouldReturnBadRequestWhenStockNegative() throws Exception {
        // Given
        ProductRequestDTO request = new ProductRequestDTO(
                "Laptop",
                new BigDecimal("1999.99"),
                -1,
                "Gaming laptop",
                categoryId
        );

        // When / Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products should return 400 when categoryId is missing")
    void createShouldReturnBadRequestWhenCategoryMissing() throws Exception {
        // Given
        ProductRequestDTO request = new ProductRequestDTO(
                "Laptop",
                new BigDecimal("1999.99"),
                10,
                "Gaming laptop",
                null
        );

        // When / Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products should return 400 when description exceeds max length")
    void createShouldReturnBadRequestWhenDescriptionTooLong() throws Exception {
        // Given
        String longDescription = "a".repeat(1001);
        ProductRequestDTO request = new ProductRequestDTO(
                "Laptop",
                new BigDecimal("1999.99"),
                10,
                longDescription,
                categoryId
        );

        // When / Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products should return 400 when name exceeds max length")
    void createShouldReturnBadRequestWhenNameTooLong() throws Exception {
        // Given
        String longName = "a".repeat(121);
        ProductRequestDTO request = new ProductRequestDTO(
                longName,
                new BigDecimal("1999.99"),
                10,
                "Gaming laptop",
                categoryId
        );

        // When / Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/products/{id} should return 200 with the updated product")
    void updateShouldReturnUpdated() throws Exception {
        // Given
        ProductRequestDTO request = new ProductRequestDTO(
                "Laptop Pro",
                new BigDecimal("2499.99"),
                5,
                "Updated",
                categoryId
        );
        when(productService.update(eq(productId), any(ProductRequestDTO.class))).thenReturn(productResponse);

        // When / Then
        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    @DisplayName("PUT /api/products/{id} with invalid payload should return 400")
    void updateShouldReturnBadRequestWhenInvalid() throws Exception {
        // Given
        ProductRequestDTO request = new ProductRequestDTO(
                "",
                new BigDecimal("-1"),
                -1,
                null,
                null
        );

        // When / Then
        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/products/{id} should return 200 with the patched product")
    void patchShouldReturnPatched() throws Exception {
        // Given
        ProductPatchDTO request = new ProductPatchDTO(
                "New Name",
                null,
                null,
                null,
                null
        );
        when(productService.patch(eq(productId), any(ProductPatchDTO.class))).thenReturn(productResponse);

        // When / Then
        mockMvc.perform(patch("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    @DisplayName("PATCH /api/products/{id} with negative price should return 400")
    void patchShouldReturnBadRequestWhenPriceNegative() throws Exception {
        // Given
        ProductPatchDTO request = new ProductPatchDTO(
                null,
                new BigDecimal("-1"),
                null,
                null,
                null
        );

        // When / Then
        mockMvc.perform(patch("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/products/{id} with negative stock should return 400")
    void patchShouldReturnBadRequestWhenStockNegative() throws Exception {
        // Given
        ProductPatchDTO request = new ProductPatchDTO(
                null,
                null,
                -1,
                null,
                null
        );

        // When / Then
        mockMvc.perform(patch("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/products/{id} with too long name should return 400")
    void patchShouldReturnBadRequestWhenNameTooLong() throws Exception {
        // Given
        String longName = "a".repeat(121);
        ProductPatchDTO request = new ProductPatchDTO(
                longName,
                null,
                null,
                null,
                null
        );

        // When / Then
        mockMvc.perform(patch("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/products/{id} should propagate not found")
    void patchShouldPropagateNotFound() throws Exception {
        // Given
        ProductPatchDTO request = new ProductPatchDTO("Anything", null, null, null, null);
        when(productService.patch(eq(productId), any(ProductPatchDTO.class)))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        // When / Then
        try {
            mockMvc.perform(patch("/api/products/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        } catch (Exception ex) {
            Throwable root = ex;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }
            assertThat(root).isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("DELETE /api/products/{id} should return 204 when product exists")
    void deleteShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(productService).delete(productId);

        // When / Then
        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} should propagate not found")
    void deleteShouldPropagateNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found"))
                .when(productService).delete(productId);

        // When / Then
        try {
            mockMvc.perform(delete("/api/products/{id}", productId))
                    .andExpect(status().isInternalServerError());
        } catch (Exception ex) {
            Throwable root = ex;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }
            assertThat(root).isInstanceOf(ResourceNotFoundException.class);
        }
    }
}