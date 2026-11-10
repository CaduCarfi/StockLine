package AEP.StockLine.controller;

import AEP.StockLine.dto.BaixaRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.exception.EstoqueException;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.service.EntradaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BaixaController.class)
class BaixaControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private EntradaService service;
    private final String dados = "{\"lote\":\"L1\",\"quantidade\":4}";

    @Test
    void deveRegistrarBaixaComStatus200() throws Exception {
        var resposta = new MedicamentoResponseDTO();
        resposta.setId("m1");
        resposta.setQuantidade(26);
        when(service.registrarBaixa(eq("m1"), any())).thenReturn(resposta);
        mockMvc.perform(post("/medicamentos/m1/baixas").contentType("application/json").content(dados))
                .andExpect(status().isOk()).andExpect(jsonPath("$.quantidade").value(26));
        verify(service).registrarBaixa("m1", new BaixaRequestDTO("L1", 4));
    }

    @Test
    void deveRejeitarCamposInvalidos() throws Exception {
        for (String corpo : List.of("{}", dados.replace(":4", ":0"), dados.replace(":4", ":-1"),
                dados.replace("L1", " "), dados.replace(":4", ":null"))) {
            mockMvc.perform(post("/medicamentos/m1/baixas").contentType("application/json").content(corpo))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(service);
    }

    @Test
    void deveRetornar400ParaSaldoInsuficiente() throws Exception {
        when(service.registrarBaixa(eq("m1"), any())).thenThrow(new EstoqueException("Saldo insuficiente"));
        mockMvc.perform(post("/medicamentos/m1/baixas").contentType("application/json").content(dados))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Saldo insuficiente"));
    }

    @Test
    void deveRetornar404ParaMedicamentoInexistente() throws Exception {
        when(service.registrarBaixa(eq("x"), any())).thenThrow(new MedicamentoNotFoundException("x"));
        mockMvc.perform(post("/medicamentos/x/baixas").contentType("application/json").content(dados))
                .andExpect(status().isNotFound());
    }
}
