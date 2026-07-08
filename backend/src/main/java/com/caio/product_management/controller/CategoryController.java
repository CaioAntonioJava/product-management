package com.caio.product_management.controller;

import com.caio.product_management.dto.CategoryRequestDTO;
import com.caio.product_management.dto.CategoryResponseDTO;
import com.caio.product_management.dto.ErrorResponseDTO;
import com.caio.product_management.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(
            summary = "Listar categorias",
            description = "Retorna todas as categorias cadastradas no sistema."
    )
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
    public ResponseEntity<List<CategoryResponseDTO>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar categoria por ID",
            description = "Retorna os dados de uma categoria específica a partir do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<CategoryResponseDTO> findById(
            @Parameter(description = "Identificador UUID da categoria", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar nova categoria",
            description = "Cria uma nova categoria. O nome deve ser único no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (erro de validação)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Já existe uma categoria com este nome",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<CategoryResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da categoria a ser cadastrada",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryRequestDTO.class)))
            @Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar categoria",
            description = "Atualiza o nome de uma categoria existente. O novo nome deve ser único no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (erro de validação)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Já existe uma categoria com este nome",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<CategoryResponseDTO> update(
            @Parameter(description = "Identificador UUID da categoria", required = true)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novos dados da categoria",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CategoryRequestDTO.class)))
            @Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir categoria",
            description = "Remove uma categoria do sistema a partir do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador UUID da categoria", required = true)
            @PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
