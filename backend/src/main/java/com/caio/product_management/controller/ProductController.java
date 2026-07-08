package com.caio.product_management.controller;

import com.caio.product_management.dto.ErrorResponseDTO;
import com.caio.product_management.dto.ProductPatchDTO;
import com.caio.product_management.dto.ProductRequestDTO;
import com.caio.product_management.dto.ProductResponseDTO;
import com.caio.product_management.service.ProductService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "Listar produtos",
            description = "Retorna todos os produtos cadastrados, ordenados por nome. " +
                    "Permite filtro opcional por nome (busca parcial, sem diferenciar maiúsculas/minúsculas)."
    )
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    public ResponseEntity<List<ProductResponseDTO>> findAll(
            @Parameter(description = "Filtro opcional por nome do produto (busca parcial)", example = "notebook")
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(productService.findAll(name));
    }

    @GetMapping("/by-category/{categoryId}")
    @Operation(
            summary = "Listar produtos por categoria",
            description = "Retorna todos os produtos pertencentes à categoria informada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos da categoria retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ProductResponseDTO>> findByCategory(
            @Parameter(description = "Identificador UUID da categoria", required = true)
            @PathVariable UUID categoryId) {
        return ResponseEntity.ok(productService.findByCategory(categoryId));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna os dados de um produto específico a partir do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ProductResponseDTO> findById(
            @Parameter(description = "Identificador UUID do produto", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar novo produto",
            description = "Cria um novo produto vinculado a uma categoria existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (erro de validação)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ProductResponseDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do produto a ser cadastrado",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductRequestDTO.class)))
            @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar produto (completo)",
            description = "Atualiza todos os campos de um produto existente. " +
                    "Todos os campos do corpo da requisição são obrigatórios."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (erro de validação)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produto ou categoria não encontrado(a)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ProductResponseDTO> update(
            @Parameter(description = "Identificador UUID do produto", required = true)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novos dados do produto (todos os campos obrigatórios)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductRequestDTO.class)))
            @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Atualizar produto (parcial)",
            description = "Atualiza apenas os campos enviados no corpo da requisição. " +
                    "Campos não enviados (null) permanecem inalterados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (erro de validação)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produto ou categoria não encontrado(a)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ProductResponseDTO> patch(
            @Parameter(description = "Identificador UUID do produto", required = true)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Campos opcionais a serem atualizados",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductPatchDTO.class)))
            @Valid @RequestBody ProductPatchDTO request) {
        return ResponseEntity.ok(productService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir produto",
            description = "Remove um produto do sistema a partir do seu identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador UUID do produto", required = true)
            @PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
