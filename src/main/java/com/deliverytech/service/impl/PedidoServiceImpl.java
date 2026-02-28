package com.deliverytech.service.impl;

import com.deliverytech.model.Pedido;
import com.deliverytech.model.StatusPedido;
import com.deliverytech.repository.PedidoRepository;
import com.deliverytech.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private static final Logger logger = LoggerFactory.getLogger(PedidoServiceImpl.class);

    @Override
    public Pedido criar(Pedido pedido) {
        pedido.setStatus(StatusPedido.CRIADO);
        logger.info("Criando pedido para cliente ID: {}", pedido.getCliente().getId());
        return pedidoRepository.save(pedido);
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Override
    public List<Pedido> listarPorRestaurante(Long restauranteId) {
        return pedidoRepository.findByRestauranteId(restauranteId);
    }

    @Override
    public Pedido atualizarStatus(Long id, StatusPedido status) {
        return pedidoRepository.findById(id)
                .map(p -> {
                    p.setStatus(status);
                    return pedidoRepository.save(p);
                }).orElseThrow(() -> {
                    logger.error("Pedido não encontrado para atualização de status, ID: {}", id);
                    return new RuntimeException("Pedido não encontrado");
                });
    }

    @Override
    public void cancelar(Long id) {
        logger.info("Cancelando pedido ID: {}", id);
        pedidoRepository.findById(id).ifPresent(p -> {
            p.setStatus(StatusPedido.CANCELADO);
            pedidoRepository.save(p);
        });
    }
}
