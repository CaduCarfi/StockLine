package AEP.StockLine.controller;

import AEP.StockLine.dto.CategoriaRequestDTO;
import AEP.StockLine.dto.CategoriaResponseDTO;
import AEP.StockLine.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CategoriaService service;

    @Test
    void deveCadastrarComStatus201() throws Exception {
        when(service.criar(any())).thenReturn(new CategoriaResponseDTO("cat1", "Analgésicos", null));
        mockMvc.perform(post("/categorias").contentType("application/json")
                .content("{\"nome\":\"Analgésicos\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("cat1"))
                .andExpect(jsonPath("$.nome").value("Analgésicos"));
        verify(service).criar(new CategoriaRequestDTO("Analgésicos", null));
    }

    @Test
    void deveListarCategorias() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(new CategoriaResponseDTO("cat1", "Analgésicos", null)));
        mockMvc.perform(get("/categorias")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("cat1"))
                .andExpect(jsonPath("$[0].nome").value("Analgésicos"));
    }

    @Test
    void deveRetornarListaVazia() throws Exception {
        when(service.listarTodas()).thenReturn(List.of());
        mockMvc.perform(get("/categorias")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveRejeitarNomeVazioOuAusente() throws Exception {
        for (String dados : List.of("{}", "{\"nome\":\"\"}", "{\"nome\":\"   \"}")) {
            mockMvc.perform(post("/categorias").contentType("application/json").content(dados))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(service);
    }
}
