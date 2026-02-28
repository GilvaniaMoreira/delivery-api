package com.deliverytech.service;

import com.deliverytech.dto.response.ViaCepResponse;
import com.deliverytech.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private static final Logger logger = LoggerFactory.getLogger(ViaCepService.class);
    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    private final RestTemplate restTemplate;

    /**
     * Consulta o endereço pelo CEP usando a API do ViaCEP.
     *
     * @param cep CEP no formato 12345678 ou 12345-678
     * @return ViaCepResponse com os dados do endereço
     * @throws BusinessException se o CEP não for encontrado
     */
    public ViaCepResponse buscarEnderecoPorCep(String cep) {
        String cepLimpo = cep.replaceAll("\\D", "");

        try {
            logger.info("Consultando ViaCEP para o CEP: {}", cepLimpo);
            ViaCepResponse response = restTemplate.getForObject(VIACEP_URL, ViaCepResponse.class, cepLimpo);

            if (response == null || response.isErro()) {
                throw new BusinessException("CEP não encontrado: " + cep);
            }

            logger.debug("ViaCEP retornou: {} - {}/{}", response.getLogradouro(), response.getLocalidade(),
                    response.getUf());
            return response;

        } catch (RestClientException ex) {
            logger.error("Falha ao consultar ViaCEP para o CEP {}: {}", cepLimpo, ex.getMessage());
            return fallback(cepLimpo);
        }
    }

    /**
     * Fallback caso o serviço ViaCEP esteja indisponível.
     * Retorna um objeto parcial apenas com o CEP informado.
     */
    private ViaCepResponse fallback(String cep) {
        logger.warn("ViaCEP indisponível. Usando fallback para o CEP: {}", cep);

        ViaCepResponse fallbackResponse = new ViaCepResponse();
        fallbackResponse.setCep(cep);
        fallbackResponse.setLogradouro("Endereço não disponível (serviço temporariamente fora do ar)");
        fallbackResponse.setLocalidade("N/A");
        fallbackResponse.setUf("N/A");
        fallbackResponse.setBairro("N/A");

        return fallbackResponse;
    }
}
