package com.deliverytech.service.impl;

import com.deliverytech.model.Produto;
import com.deliverytech.repository.ProdutoRepository;
import com.deliverytech.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Override
    @CacheEvict(value = "produtos", allEntries = true)
    public Produto cadastrar(Produto produto) {
        return produtoRepository.save(produto);
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    @Override
    @Cacheable(value = "produtos", key = "#restauranteId")
    public List<Produto> buscarPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteId(restauranteId);
    }

    @Override
    @CacheEvict(value = "produtos", allEntries = true)
    public Produto atualizar(Long id, Produto atualizado) {
        return produtoRepository.findById(id)
                .map(p -> {
                    p.setNome(atualizado.getNome());
                    p.setDescricao(atualizado.getDescricao());
                    p.setCategoria(atualizado.getCategoria());
                    p.setPreco(atualizado.getPreco());
                    return produtoRepository.save(p);
                }).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    @Override
    @CacheEvict(value = "produtos", allEntries = true)
    public void alterarDisponibilidade(Long id, boolean disponivel) {
        produtoRepository.findById(id).ifPresent(p -> {
            p.setDisponivel(disponivel);
            produtoRepository.save(p);
        });
    }
}
