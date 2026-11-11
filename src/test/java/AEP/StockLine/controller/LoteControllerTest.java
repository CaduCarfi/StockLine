package AEP.StockLine.controller;

import AEP.StockLine.dto.LoteVencimentoResponseDTO;
import AEP.StockLine.service.LoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoteController.class)
class LoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoteService service;

    @Test
    void deveRetornar200ComLotesVencidos() throws Exception {
        LoteVencimentoResponseDTO lote = new LoteVencimentoResponseDTO(
                "m1", "Paracetamol", "L001", 10, LocalDate.of(2020, 1, 1));
        when(service.buscarVencidos()).thenReturn(List.of(lote));

        mockMvc.perform(get("/lotes/vencidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].medicamentoId").value("m1"))
                .andExpect(jsonPath("$[0].medicamentoNome").value("Paracetamol"))
                .andExpect(jsonPath("$[0].lote").value("L001"))
                .andExpect(jsonPath("$[0].quantidade").value(10))
                .andExpect(jsonPath("$[0].validade").value("2020-01-01"));
    }

    @Test
    void deveRetornar200ComListaVaziaQuandoNaoHouverLotesVencidos() throws Exception {
        when(service.buscarVencidos()).thenReturn(List.of());

        mockMvc.perform(get("/lotes/vencidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveRetornar200ComLotesAVencerUsandoParametroPadraoDe30Dias() throws Exception {
        LoteVencimentoResponseDTO lote = new LoteVencimentoResponseDTO(
                "m1", "Dipirona", "L002", 20, LocalDate.now().plusDays(10));
        when(service.buscarAVencer(30)).thenReturn(List.of(lote));

        mockMvc.perform(get("/lotes/a-vencer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].medicamentoNome").value("Dipirona"));

        verify(service).buscarAVencer(30);
    }

    @Test
    void deveRetornar200ComLotesAVencerComParametroDiasInformado() throws Exception {
        LoteVencimentoResponseDTO lote = new LoteVencimentoResponseDTO(
                "m1", "Dipirona", "L002", 20, LocalDate.now().plusDays(5));
        when(service.buscarAVencer(7)).thenReturn(List.of(lote));

        mockMvc.perform(get("/lotes/a-vencer").param("dias", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].lote").value("L002"))
                .andExpect(jsonPath("$[0].quantidade").value(20));

        verify(service).buscarAVencer(7);
    }

    @Test
    void deveRetornar200ComListaVaziaQuandoNaoHouverLotesAVencer() throws Exception {
        when(service.buscarAVencer(30)).thenReturn(List.of());

        mockMvc.perform(get("/lotes/a-vencer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveRetornarMultiplosLotesVencidos() throws Exception {
        LoteVencimentoResponseDTO lote1 = new LoteVencimentoResponseDTO(
                "m1", "Paracetamol", "L001", 10, LocalDate.of(2020, 1, 1));
        LoteVencimentoResponseDTO lote2 = new LoteVencimentoResponseDTO(
                "m2", "Dipirona", "L002", 5, LocalDate.of(2019, 6, 15));
        when(service.buscarVencidos()).thenReturn(List.of(lote1, lote2));

        mockMvc.perform(get("/lotes/vencidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].medicamentoNome").value("Paracetamol"))
                .andExpect(jsonPath("$[1].medicamentoNome").value("Dipirona"));
    }

    @Test
    void deveRetornarTodosOsCamposDoLoteAVencer() throws Exception {
        LocalDate validade = LocalDate.now().plusDays(15);
        LoteVencimentoResponseDTO lote = new LoteVencimentoResponseDTO(
                "m3", "Amoxicilina", "L003", 50, validade);
        when(service.buscarAVencer(30)).thenReturn(List.of(lote));

        mockMvc.perform(get("/lotes/a-vencer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicamentoId").value("m3"))
                .andExpect(jsonPath("$[0].medicamentoNome").value("Amoxicilina"))
                .andExpect(jsonPath("$[0].lote").value("L003"))
                .andExpect(jsonPath("$[0].quantidade").value(50))
                .andExpect(jsonPath("$[0].validade").value(validade.toString()));
    }
}
