package AEP.StockLine.controller;

import AEP.StockLine.dto.EntradaRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.exception.EstoqueException;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.model.Lote;
import AEP.StockLine.service.EntradaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EntradaController.class)
class EntradaControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private EntradaService service;
    private final String dados = """
            {"lote":"L1","validade":"2027-08-15","quantidade":10}
            """;

    @Test
    void deveRegistrarEntrada() throws Exception {
        var resposta = new MedicamentoResponseDTO();
        resposta.setId("m1");
        resposta.setQuantidade(10);
        resposta.setLotes(List.of(new Lote("L1", LocalDate.of(2027, 8, 15), 10)));
        when(service.registrar(eq("m1"), any())).thenReturn(resposta);
        mockMvc.perform(post("/medicamentos/m1/entradas").contentType("application/json").content(dados))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade").value(10))
                .andExpect(jsonPath("$.lotes[0].codigo").value("L1"))
                .andExpect(jsonPath("$.lotes[0].saldo").value(10));
        verify(service).registrar("m1", new EntradaRequestDTO("L1", LocalDate.of(2027, 8, 15), 10));
    }

    @Test
    void deveRejeitarCamposInvalidos() throws Exception {
        for (String corpo : List.of("{}", dados.replace(":10", ":0"), dados.replace(":10", ":-1"),
                dados.replace("L1", " "), dados.replace("\"2027-08-15\"", "null"))) {
            mockMvc.perform(post("/medicamentos/m1/entradas").contentType("application/json").content(corpo))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(service);
    }

    @Test
    void deveRetornar404ParaMedicamentoInexistente() throws Exception {
        when(service.registrar(eq("x"), any())).thenThrow(new MedicamentoNotFoundException("x"));
        mockMvc.perform(post("/medicamentos/x/entradas").contentType("application/json").content(dados))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400ParaValidadeDivergente() throws Exception {
        when(service.registrar(eq("m1"), any())).thenThrow(new EstoqueException("Validade diferente"));
        mockMvc.perform(post("/medicamentos/m1/entradas").contentType("application/json").content(dados))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Validade diferente"));
    }
}
