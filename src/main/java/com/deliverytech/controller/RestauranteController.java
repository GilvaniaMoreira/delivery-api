package com.deliverytech.controller;

import com.deliverytech.dto.request.RestauranteRequest;
import com.deliverytech.dto.response.ErrorResponse;
import com.deliverytech.dto.response.RestauranteResponse;
import com.deliverytech.model.Restaurante;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurantes")
@RequiredArgsConstructor
@Tag(name = "Restaurantes", description = "Gerenciamento de restaurantes parceiros")
public class RestauranteController {

        private static final Logger logger = LoggerFactory.getLogger(RestauranteController.class);
        private final RestauranteService restauranteService;

        @Operation(summary = "Cadastrar restaurante", description = "Registra um novo restaurante parceiro no sistema.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Restaurante cadastrado com sucesso", content = @Content(schema = @Schema(implementation = RestauranteResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PostMapping
        public ResponseEntity<RestauranteResponse> cadastrar(@Valid @RequestBody RestauranteRequest request) {
                logger.info("Cadastrando restaurante: {}", request.getNome());
                Restaurante restaurante = Restaurante.builder()
                                .nome(request.getNome())
                                .telefone(request.getTelefone())
                                .categoria(request.getCategoria())
                                .taxaEntrega(request.getTaxaEntrega())
                                .tempoEntregaMinutos(request.getTempoEntregaMinutos())
                                .ativo(true)
                                .build();
                Restaurante salvo = restauranteService.cadastrar(restaurante);
                return ResponseEntity.ok(new RestauranteResponse(
                                salvo.getId(), salvo.getNome(), salvo.getCategoria(), salvo.getTelefone(),
                                salvo.getTaxaEntrega(), salvo.getTempoEntregaMinutos(), salvo.getAtivo()));
        }

        @Operation(summary = "Listar todos os restaurantes", description = "Retorna uma lista paginada de todos os restaurantes.")
        @ApiResponse(responseCode = "200", description = "Lista de restaurantes retornada com sucesso")
        @GetMapping
        public Page<RestauranteResponse> listarTodos(Pageable pageable) {
                logger.info("Listando restaurantes - página: {}", pageable.getPageNumber());
                return restauranteService.listarTodos(pageable).map(r -> new RestauranteResponse(r.getId(), r.getNome(),
                                r.getCategoria(), r.getTelefone(), r.getTaxaEntrega(), r.getTempoEntregaMinutos(),
                                r.getAtivo()));
        }

        @Operation(summary = "Buscar restaurante por ID", description = "Retorna os dados de um restaurante específico.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Restaurante encontrado", content = @Content(schema = @Schema(implementation = RestauranteResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
        })
        @GetMapping("/{id}")
        public ResponseEntity<RestauranteResponse> buscarPorId(@PathVariable Long id) {
                logger.info("Buscando restaurante ID: {}", id);
                return restauranteService.buscarPorId(id)
                                .map(r -> new RestauranteResponse(r.getId(), r.getNome(), r.getCategoria(),
                                                r.getTelefone(),
                                                r.getTaxaEntrega(), r.getTempoEntregaMinutos(), r.getAtivo()))
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Buscar restaurantes por categoria", description = "Retorna todos os restaurantes de uma categoria específica.")
        @ApiResponse(responseCode = "200", description = "Lista de restaurantes da categoria")
        @GetMapping("/categoria/{categoria}")
        public List<RestauranteResponse> buscarPorCategoria(@PathVariable String categoria) {
                return restauranteService.buscarPorCategoria(categoria).stream()
                                .map(r -> new RestauranteResponse(r.getId(), r.getNome(), r.getCategoria(),
                                                r.getTelefone(),
                                                r.getTaxaEntrega(), r.getTempoEntregaMinutos(), r.getAtivo()))
                                .collect(Collectors.toList());
        }

        @Operation(summary = "Atualizar restaurante", description = "Atualiza os dados de um restaurante existente.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso", content = @Content(schema = @Schema(implementation = RestauranteResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
        })
        @PutMapping("/{id}")
        public ResponseEntity<RestauranteResponse> atualizar(@PathVariable Long id,
                        @Valid @RequestBody RestauranteRequest request) {
                logger.info("Atualizando restaurante ID: {}", id);
                Restaurante atualizado = Restaurante.builder()
                                .nome(request.getNome())
                                .telefone(request.getTelefone())
                                .categoria(request.getCategoria())
                                .taxaEntrega(request.getTaxaEntrega())
                                .tempoEntregaMinutos(request.getTempoEntregaMinutos())
                                .build();
                Restaurante salvo = restauranteService.atualizar(id, atualizado);
                return ResponseEntity.ok(new RestauranteResponse(salvo.getId(), salvo.getNome(), salvo.getCategoria(),
                                salvo.getTelefone(), salvo.getTaxaEntrega(), salvo.getTempoEntregaMinutos(),
                                salvo.getAtivo()));
        }
}
