package com.deliverytech.controller;

import com.deliverytech.config.TestSecurityConfig;
import com.deliverytech.model.Cliente;
import com.deliverytech.model.Produto;
import com.deliverytech.model.Restaurante;
import com.deliverytech.repository.ClienteRepository;
import com.deliverytech.repository.ProdutoRepository;
import com.deliverytech.repository.RestauranteRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@Transactional
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private Long clienteId;
    private Long restauranteId;
    private Long produtoId;

    @BeforeEach
    void setUp() {
        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .email("cliente@teste.com")
                .ativo(true)
                .build();
        clienteId = clienteRepository.save(cliente).getId();

        Restaurante restaurante = Restaurante.builder()
                .nome("Restaurante Teste")
                .categoria("Brasileira")
                .telefone("11999999999")
                .taxaEntrega(new BigDecimal("5.00"))
                .tempoEntregaMinutos(30)
                .ativo(true)
                .build();
        restauranteId = restauranteRepository.save(restaurante).getId();

        Produto produto = Produto.builder()
                .nome("Pizza Margherita")
                .categoria("Pizzas")
                .descricao("Pizza tradicional")
                .preco(new BigDecimal("39.90"))
                .disponivel(true)
                .restaurante(restaurante)
                .build();
        produtoId = produtoRepository.save(produto).getId();
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso e retornar 200 OK")
    void deveCriarPedidoComSucesso() throws Exception {
        String json = """
                {
                    "clienteId": %d,
                    "restauranteId": %d,
                    "enderecoEntrega": {
                        "rua": "Rua das Flores",
                        "numero": "123",
                        "bairro": "Centro",
                        "cidade": "São Paulo",
                        "estado": "SP",
                        "cep": "01001-000"
                    },
                    "itens": [
                        { "produtoId": %d, "quantidade": 2 }
                    ]
                }
                """.formatted(clienteId, restauranteId, produtoId);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.restauranteId").value(restauranteId))
                .andExpect(jsonPath("$.status").value("CRIADO"))
                .andExpect(jsonPath("$.total").value(79.80))
                .andExpect(jsonPath("$.itens").isArray())
                .andExpect(jsonPath("$.itens.length()").value(1));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando campos obrigatórios estão ausentes")
    void deveRetornar400QuandoCamposObrigatoriosAusentes() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando lista de itens é nula")
    void deveRetornar400QuandoItensNulo() throws Exception {
        String json = """
                {
                    "clienteId": %d,
                    "restauranteId": %d,
                    "enderecoEntrega": {
                        "rua": "Rua das Flores",
                        "numero": "123",
                        "bairro": "Centro",
                        "cidade": "São Paulo",
                        "estado": "SP",
                        "cep": "01001-000"
                    }
                }
                """.formatted(clienteId, restauranteId);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não existe")
    void deveRetornarErroQuandoClienteNaoExiste() {
        String json = """
                {
                    "clienteId": 99999,
                    "restauranteId": %d,
                    "enderecoEntrega": {
                        "rua": "Rua das Flores",
                        "numero": "123",
                        "bairro": "Centro",
                        "cidade": "São Paulo",
                        "estado": "SP",
                        "cep": "01001-000"
                    },
                    "itens": [
                        { "produtoId": %d, "quantidade": 1 }
                    ]
                }
                """.formatted(restauranteId, produtoId);

        Exception thrown = Assertions.assertThrows(ServletException.class,
                () -> mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)));
        Assertions.assertTrue(thrown.getCause().getMessage().contains("Cliente"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando restaurante não existe")
    void deveRetornarErroQuandoRestauranteNaoExiste() {
        String json = """
                {
                    "clienteId": %d,
                    "restauranteId": 99999,
                    "enderecoEntrega": {
                        "rua": "Rua das Flores",
                        "numero": "123",
                        "bairro": "Centro",
                        "cidade": "São Paulo",
                        "estado": "SP",
                        "cep": "01001-000"
                    },
                    "itens": [
                        { "produtoId": %d, "quantidade": 1 }
                    ]
                }
                """.formatted(clienteId, produtoId);

        Exception thrown = Assertions.assertThrows(ServletException.class,
                () -> mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)));
        Assertions.assertTrue(thrown.getCause().getMessage().contains("Restaurante"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não existe")
    void deveRetornarErroQuandoProdutoNaoExiste() {
        String json = """
                {
                    "clienteId": %d,
                    "restauranteId": %d,
                    "enderecoEntrega": {
                        "rua": "Rua das Flores",
                        "numero": "123",
                        "bairro": "Centro",
                        "cidade": "São Paulo",
                        "estado": "SP",
                        "cep": "01001-000"
                    },
                    "itens": [
                        { "produtoId": 99999, "quantidade": 1 }
                    ]
                }
                """.formatted(clienteId, restauranteId);

        Exception thrown = Assertions.assertThrows(ServletException.class,
                () -> mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)));
        Assertions.assertTrue(thrown.getCause().getMessage().contains("Produto"));
    }
}
