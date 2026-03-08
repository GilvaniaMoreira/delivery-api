package com.deliverytech.controller;

import com.deliverytech.dto.request.LoginRequest;
import com.deliverytech.dto.request.RegisterRequest;
import com.deliverytech.model.Role;
import com.deliverytech.model.Usuario;
import com.deliverytech.repository.UsuarioRepository;
import com.deliverytech.security.JwtUtil;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de registro e login de usuários")
public class AuthController {

        private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

        private final UsuarioRepository usuarioRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtUtil jwtUtil;

        @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário e retorna o token JWT para autenticação.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso — retorna token JWT", content = @Content(schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiJ9..."))),
                        @ApiResponse(responseCode = "400", description = "Email já cadastrado ou dados inválidos", content = @Content(schema = @Schema(type = "string")))
        })
        @PostMapping("/register")
        public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
                logger.info("Tentativa de registro para email: {}", request.getEmail());
                if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                        logger.warn("Registro rejeitado - email já cadastrado: {}", request.getEmail());
                        return ResponseEntity.badRequest().body("Email já cadastrado");
                }

                Usuario usuario = Usuario.builder()
                                .email(request.getEmail())
                                .senha(passwordEncoder.encode(request.getSenha()))
                                .nome(request.getNome())
                                .role(request.getRole() != null ? request.getRole() : Role.CLIENTE)
                                .ativo(true)
                                .restauranteId(request.getRestauranteId())
                                .build();

                usuarioRepository.save(usuario);
                logger.info("Usuário registrado com sucesso: {} (role: {})", usuario.getEmail(), usuario.getRole());
                String token = jwtUtil.generateToken(User.withUsername(usuario.getEmail()).password(usuario.getSenha())
                                .authorities("ROLE_" + usuario.getRole().name()).build(), usuario);
                return ResponseEntity.ok(token);
        }

        @Operation(summary = "Login de usuário", description = "Autentica o usuário e retorna o token JWT.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso — retorna token JWT", content = @Content(schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiJ9..."))),
                        @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        })
        @PostMapping("/login")
        public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
                logger.info("Tentativa de login para: {}", request.getEmail());
                authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),
                                                request.getSenha()));
                Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                String token = jwtUtil.generateToken(User.withUsername(usuario.getEmail()).password(usuario.getSenha())
                                .authorities("ROLE_" + usuario.getRole().name()).build(), usuario);
                return ResponseEntity.ok(token);
        }
}
