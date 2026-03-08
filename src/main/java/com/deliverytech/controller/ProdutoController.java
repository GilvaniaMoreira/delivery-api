package com.deliverytech.controller;

import com.deliverytech.dto.request.ProdutoRequest;
import com.deliverytech.dto.response.ErrorResponse;
import com.deliverytech.dto.response.ProdutoResponse;
import com.deliverytech.model.Produto;
import com.deliverytech.model.Restaurante;
import com.deliverytech.service.ProdutoService;
import com.deliverytech.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Gerenciamento de produtos dos restaurantes")
public class ProdutoController {

        private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

        private final ProdutoService produtoService;
        private final RestauranteService restauranteService;

        @Operation(summary = "Cadastrar produto", description = "Cria um novo produto vinculado a um restaurante.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Produto cadastrado com sucesso", content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Restaurante não encontrado")
        })
        @PostMapping
        public ResponseEntity<ProdutoResponse> cadastrar(@Valid @RequestBody ProdutoRequest request) {
                logger.info("Cadastrando produto: {} para restaurante ID: {}", request.getNome(),
                                request.getRestauranteId());
                Restaurante restaurante = restauranteService.buscarPorId(request.getRestauranteId())
                                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

                Produto produto = Produto.builder()
                                .nome(request.getNome())
                                .categoria(request.getCategoria())
                                .descricao(request.getDescricao())
                                .preco(request.getPreco())
                                .disponivel(true)
                                .restaurante(restaurante)
                                .build();

                Produto salvo = produtoService.cadastrar(produto);
                return ResponseEntity.ok(new ProdutoResponse(
                                salvo.getId(), salvo.getNome(), salvo.getCategoria(), salvo.getDescricao(),
                                salvo.getPreco(),
                                salvo.getDisponivel()));
        }

        @Operation(summary = "Listar produtos por restaurante", description = "Retorna todos os produtos de um restaurante específico.")
        @ApiResponse(responseCode = "200", description = "Lista de produtos do restaurante")
        @GetMapping("/restaurante/{restauranteId}")
        public List<ProdutoResponse> listarPorRestaurante(@PathVariable Long restauranteId) {
                logger.info("Listando produtos do restaurante ID: {}", restauranteId);
                return produtoService.buscarPorRestaurante(restauranteId).stream()
                                .map(p -> new ProdutoResponse(p.getId(), p.getNome(), p.getCategoria(),
                                                p.getDescricao(), p.getPreco(),
                                                p.getDisponivel()))
                                .collect(Collectors.toList());
        }

        @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso", content = @Content(schema = @Schema(implementation = ProdutoResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
        })
        @PutMapping("/{id}")
        public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id,
                        @Valid @RequestBody ProdutoRequest request) {
                logger.info("Atualizando produto ID: {}", id);
                Produto atualizado = Produto.builder()
                                .nome(request.getNome())
                                .categoria(request.getCategoria())
                                .descricao(request.getDescricao())
                                .preco(request.getPreco())
                                .build();
                Produto salvo = produtoService.atualizar(id, atualizado);
                return ResponseEntity.ok(new ProdutoResponse(salvo.getId(), salvo.getNome(), salvo.getCategoria(),
                                salvo.getDescricao(), salvo.getPreco(), salvo.getDisponivel()));
        }

        @Operation(summary = "Alterar disponibilidade", description = "Ativa ou desativa a disponibilidade de um produto.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Disponibilidade alterada com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
        })
        @PatchMapping("/{id}/disponibilidade")
        public ResponseEntity<Void> alterarDisponibilidade(@PathVariable Long id, @RequestParam boolean disponivel) {
                logger.info("Alterando disponibilidade do produto ID: {} para: {}", id, disponivel);
                produtoService.alterarDisponibilidade(id, disponivel);
                return ResponseEntity.noContent().build();
        }
}
