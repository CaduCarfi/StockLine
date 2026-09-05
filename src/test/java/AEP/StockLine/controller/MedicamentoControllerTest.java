package AEP.StockLine.controller;

import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.service.MedicamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MedicamentoController.class)
class MedicamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicamentoService service;

    @Test
    void deveRetornar200ComOMedicamentoQuandoOIdExistir() throws Exception {
        MedicamentoResponseDTO paracetamol = new MedicamentoResponseDTO(
                "1", "Paracetamol", "Analgésico", 100, LocalDate.of(2027, 3, 31), "L001");

        when(service.buscarPorId("1")).thenReturn(paracetamol);

        mockMvc.perform(get("/api/medicamentos/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.nome").value("Paracetamol"));
    }

    @Test
    void deveRetornar404QuandoOIdNaoExistir() throws Exception {
        when(service.buscarPorId("999"))
                .thenThrow(new MedicamentoNotFoundException("999"));

        mockMvc.perform(get("/api/medicamentos/{id}", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
