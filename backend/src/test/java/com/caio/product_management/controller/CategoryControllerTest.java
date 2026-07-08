package com.caio.product_management.controller;

import com.caio.product_management.dto.CategoryRequestDTO;
import com.caio.product_management.dto.CategoryResponseDTO;
import com.caio.product_management.exception.ResourceAlreadyExistsException;
import com.caio.product_management.exception.ResourceNotFoundException;
import com.caio.product_management.service.CategoryService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID categoryId;
    private CategoryResponseDTO categoryResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();

        categoryId = UUID.randomUUID();
        categoryResponse = new CategoryResponseDTO(
                categoryId,
                "Electronics",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("GET /api/categories should return 200 with the list of categories")
    void findAllShouldReturnList() throws Exception {
        // Given
        when(categoryService.findAll()).thenReturn(List.of(categoryResponse));

        // When / Then
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(categoryId.toString()))
                .andExpect(jsonPath("$[0].name").value("Electronics"));
    }

    @Test
    @DisplayName("GET /api/categories/{id} should return 200 when category exists")
    void findByIdShouldReturnCategory() throws Exception {
        // Given
        when(categoryService.findById(categoryId)).thenReturn(categoryResponse);

        // When / Then
        mockMvc.perform(get("/api/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    @DisplayName("GET /api/categories/{id} should propagate the exception")
    void findByIdShouldPropagateException() throws Exception {
        // Given
        when(categoryService.findById(categoryId))
                .thenThrow(new ResourceNotFoundException("Category not found with id: " + categoryId));

        // When / Then
        try {
            mockMvc.perform(get("/api/categories/{id}", categoryId))
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
    @DisplayName("POST /api/categories should return 201 and the created category")
    void createShouldReturnCreated() throws Exception {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO("Books");
        when(categoryService.create(any(CategoryRequestDTO.class))).thenReturn(categoryResponse);

        // When / Then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    @DisplayName("POST /api/categories with blank name should fail validation")
    void createShouldReturnBadRequestWhenNameBlank() throws Exception {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO(" ");

        // When / Then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/categories with empty name should fail validation")
    void createShouldReturnBadRequestWhenNameEmpty() throws Exception {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO("");

        // When / Then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/categories with too long name should fail validation")
    void createShouldReturnBadRequestWhenNameTooLong() throws Exception {
        // Given
        String longName = "a".repeat(81);
        CategoryRequestDTO request = new CategoryRequestDTO(longName);

        // When / Then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/categories should propagate service exceptions")
    void createShouldPropagateServiceException() throws Exception {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO("Books");
        when(categoryService.create(any(CategoryRequestDTO.class)))
                .thenThrow(new ResourceAlreadyExistsException("Category with name 'Books' already exists"));

        // When / Then
        try {
            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());
        } catch (Exception ex) {
            // standalone MockMvc does not handle exceptions via @RestControllerAdvice,
            // so they propagate as ServletException; verify the cause chain.
            Throwable root = ex;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }
            assertThat(root).isInstanceOf(ResourceAlreadyExistsException.class);
            assertThat(root.getMessage()).contains("already exists");
        }
    }

    @Test
    @DisplayName("PUT /api/categories/{id} should return 200 and updated category")
    void updateShouldReturnUpdated() throws Exception {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO("Updated");
        when(categoryService.update(eq(categoryId), any(CategoryRequestDTO.class)))
                .thenReturn(new CategoryResponseDTO(categoryId, "Updated",
                        LocalDateTime.now(), LocalDateTime.now()));

        // When / Then
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @DisplayName("PUT /api/categories/{id} with invalid payload should return 400")
    void updateShouldReturnBadRequestWhenInvalid() throws Exception {
        // Given
        CategoryRequestDTO request = new CategoryRequestDTO("");

        // When / Then
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} should return 204 when category exists")
    void deleteShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(categoryService).delete(categoryId);

        // When / Then
        mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNoContent())
                .andExpect(header().doesNotExist("Content-Type"));
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} should propagate not found")
    void deleteShouldPropagateNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Category not found"))
                .when(categoryService).delete(categoryId);

        // When / Then
        try {
            mockMvc.perform(delete("/api/categories/{id}", categoryId))
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