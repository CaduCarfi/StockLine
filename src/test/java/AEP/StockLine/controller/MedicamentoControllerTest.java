package AEP.StockLine.controller;

import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.service.MedicamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

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
    void deveRetornar200ComAListaDeMedicamentos() throws Exception {
        MedicamentoResponseDTO paracetamol = new MedicamentoResponseDTO(
                "1",
                "Paracetamol",
                "Analgésico e antitérmico",
                100,
                LocalDate.of(2027, 3, 31),
                "L001"
        );

        MedicamentoResponseDTO dipirona = new MedicamentoResponseDTO(
                "2",
                "Dipirona",
                "Analgésico",
                50,
                LocalDate.of(2028, 8, 31),
                "L002"
        );

        when(service.listarTodos()).thenReturn(List.of(paracetamol, dipirona));

        mockMvc.perform(get("/medicamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].nome").value("Paracetamol"))
                .andExpect(jsonPath("$[0].quantidade").value(100))
                .andExpect(jsonPath("$[0].validade").value("2027-03-31"))
                .andExpect(jsonPath("$[0].lote").value("L001"))
                .andExpect(jsonPath("$[1].nome").value("Dipirona"));
    }

    @Test
    void deveRetornar200ComListaVaziaQuandoNaoHouverMedicamentos() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/medicamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
