package com.deliverytech.controller;

import com.deliverytech.config.TestSecurityConfig;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@Transactional
public class ClienteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Deve criar cliente com sucesso e retornar 200 OK")
    void deveCriarClienteComSucesso() throws Exception {
        String json = "{\"nome\":\"Alexandre\",\"email\":\"alexandre@teste.com\"}";

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando dados são inválidos")
    void naoDeveCriarClienteComCpfInvalido() throws Exception {
        String json = "{\"nome\":\"Alexandre\",\"cpf\":\"000\"}";

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}
