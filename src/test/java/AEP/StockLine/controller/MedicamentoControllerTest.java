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
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
                "L001", "cat1"
        );

        MedicamentoResponseDTO dipirona = new MedicamentoResponseDTO(
                "2",
                "Dipirona",
                "Analgésico",
                50,
                LocalDate.of(2028, 8, 31),
                "L002", "cat1"
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

    @Test
    void deveRetornar200ComOMedicamentoQuandoOIdExistir() throws Exception {
        MedicamentoResponseDTO paracetamol = new MedicamentoResponseDTO(
                "1",
                "Paracetamol",
                "Analgésico e antitérmico",
                100,
                LocalDate.of(2027, 3, 31),
                "L001", "cat1"
        );

        when(service.buscarPorId("1")).thenReturn(paracetamol);

        mockMvc.perform(get("/medicamentos/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.nome").value("Paracetamol"))
                .andExpect(jsonPath("$.descricao").value("Analgésico e antitérmico"))
                .andExpect(jsonPath("$.quantidade").value(100))
                .andExpect(jsonPath("$.validade").value("2027-03-31"))
                .andExpect(jsonPath("$.lote").value("L001"));
    }

    @Test
    void deveRetornar404QuandoOIdNaoExistir() throws Exception {
        when(service.buscarPorId("999"))
                .thenThrow(new MedicamentoNotFoundException("999"));

        mockMvc.perform(get("/medicamentos/{id}", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Not Found"))
                .andExpect(jsonPath("$.mensagem").value("Medicamento não encontrado com o id: 999"));
    }

    private String dadosValidos() {
        return """
                {"nome":"Dipirona","descricao":"Analgésico","quantidade":20,
                 "validade":"2027-08-15","lote":"L002","categoriaId":"cat1"}
                """;
    }

    private MedicamentoResponseDTO resposta() {
        return new MedicamentoResponseDTO("1", "Dipirona", "Analgésico", 20,
                LocalDate.of(2027, 8, 15), "L002", "cat1");
    }

    @Test
    void deveCadastrarComStatus201() throws Exception {
        when(service.criar(any())).thenReturn(resposta());
        mockMvc.perform(post("/medicamentos").contentType("application/json").content(dadosValidos()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/medicamentos/1"))
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void deveAtualizarComStatus200() throws Exception {
        when(service.atualizar(eq("1"), any())).thenReturn(resposta());
        mockMvc.perform(put("/medicamentos/1").contentType("application/json").content(dadosValidos()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Dipirona"));
    }

    @Test
    void deveExcluirComStatus204() throws Exception {
        mockMvc.perform(delete("/medicamentos/1")).andExpect(status().isNoContent());
        verify(service).excluir("1");
    }

    @Test
    void deveRetornar404AoAtualizarInexistente() throws Exception {
        when(service.atualizar(eq("999"), any())).thenThrow(new MedicamentoNotFoundException("999"));
        mockMvc.perform(put("/medicamentos/999").contentType("application/json").content(dadosValidos()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404AoExcluirInexistente() throws Exception {
        doThrow(new MedicamentoNotFoundException("999")).when(service).excluir("999");
        mockMvc.perform(delete("/medicamentos/999")).andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarCadastroSemCamposObrigatorios() throws Exception {
        mockMvc.perform(post("/medicamentos").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void deveRejeitarQuantidadeNegativaNoCadastroENaEdicao() throws Exception {
        String dadosInvalidos = dadosValidos().replace("\"quantidade\":20", "\"quantidade\":-1");
        mockMvc.perform(post("/medicamentos").contentType("application/json").content(dadosInvalidos))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/medicamentos/1").contentType("application/json").content(dadosInvalidos))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void deveRejeitarNomeELoteEmBranco() throws Exception {
        String dadosInvalidos = dadosValidos().replace("Dipirona", " ").replace("L002", " ");
        mockMvc.perform(post("/medicamentos").contentType("application/json").content(dadosInvalidos))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void deveRejeitarMedicamentoSemCategoria() throws Exception {
        String dados = dadosValidos().replace(",\"categoriaId\":\"cat1\"", "");
        mockMvc.perform(post("/medicamentos").contentType("application/json").content(dados))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void deveRetornar404QuandoCategoriaNaoExiste() throws Exception {
        when(service.criar(any())).thenThrow(new AEP.StockLine.exception.CategoriaNotFoundException("cat1"));
        mockMvc.perform(post("/medicamentos").contentType("application/json").content(dadosValidos()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Categoria não encontrada com o id: cat1"));
    }
}
