package com.deliverytech.service.impl;

import com.deliverytech.model.Restaurante;
import com.deliverytech.repository.RestauranteRepository;
import com.deliverytech.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private static final Logger logger = LoggerFactory.getLogger(RestauranteServiceImpl.class);

    @Override
    @CacheEvict(value = "restaurantes", allEntries = true)
    public Restaurante cadastrar(Restaurante restaurante) {
        logger.info("Salvando restaurante: {}", restaurante.getNome());
        return restauranteRepository.save(restaurante);
    }

    @Override
    public Optional<Restaurante> buscarPorId(Long id) {
        return restauranteRepository.findById(id);
    }

    @Override
    @Cacheable("restaurantes")
    public Page<Restaurante> listarTodos(Pageable pageable) {
        return restauranteRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "restaurantes", key = "#categoria")
    public List<Restaurante> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoria(categoria);
    }

    @Override
    @CacheEvict(value = "restaurantes", allEntries = true)
    public Restaurante atualizar(Long id, Restaurante atualizado) {
        return restauranteRepository.findById(id)
                .map(r -> {
                    r.setNome(atualizado.getNome());
                    r.setTelefone(atualizado.getTelefone());
                    r.setCategoria(atualizado.getCategoria());
                    r.setTaxaEntrega(atualizado.getTaxaEntrega());
                    r.setTempoEntregaMinutos(atualizado.getTempoEntregaMinutos());
                    return restauranteRepository.save(r);
                }).orElseThrow(() -> {
                    logger.error("Restaurante não encontrado para atualização, ID: {}", id);
                    return new RuntimeException("Restaurante não encontrado");
                });
    }
}
