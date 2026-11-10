package AEP.StockLine.service;

import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.repository.MedicamentoRepository;
import AEP.StockLine.repository.CategoriaRepository;
import AEP.StockLine.exception.CategoriaNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import AEP.StockLine.model.Movimentacao;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicamentoServiceTest {

    @Mock
    private MedicamentoRepository repository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @org.mockito.Spy
    private AEP.StockLine.mapper.MedicamentoMapper mapper = new AEP.StockLine.mapper.MedicamentoMapper();

    @InjectMocks
    private MedicamentoService service;

    @Test
    void deveListarTodosOsMedicamentos() {
        Medicamento paracetamol = Medicamento.builder()
                .id("1")
                .nome("Paracetamol")
                .descricao("Analgésico e antitérmico")
                .quantidade(100)
                .validade(LocalDate.of(2027, 3, 31))
                .lote("L001")
                .build();

        Medicamento dipirona = Medicamento.builder()
                .id("2")
                .nome("Dipirona")
                .descricao("Analgésico")
                .quantidade(50)
                .validade(LocalDate.of(2028, 8, 31))
                .lote("L002")
                .build();

        when(repository.findAll()).thenReturn(List.of(paracetamol, dipirona));

        List<MedicamentoResponseDTO> resultado = service.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado)
                .extracting(MedicamentoResponseDTO::getNome)
                .containsExactly("Paracetamol", "Dipirona");

        verify(repository).findAll();
    }

    @Test
    void deveConverterTodosOsCamposDoMedicamentoParaOResponseDTO() {
        Medicamento medicamento = Medicamento.builder()
                .id("1")
                .nome("Paracetamol")
                .descricao("Analgésico e antitérmico")
                .quantidade(100)
                .validade(LocalDate.of(2027, 3, 31))
                .lote("L001")
                .build();

        when(repository.findAll()).thenReturn(List.of(medicamento));

        MedicamentoResponseDTO dto = service.listarTodos().getFirst();

        assertThat(dto.getId()).isEqualTo("1");
        assertThat(dto.getNome()).isEqualTo("Paracetamol");
        assertThat(dto.getDescricao()).isEqualTo("Analgésico e antitérmico");
        assertThat(dto.getQuantidade()).isEqualTo(100);
        assertThat(dto.getValidade()).isEqualTo(LocalDate.of(2027, 3, 31));
        assertThat(dto.getLote()).isEqualTo("L001");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverMedicamentos() {
        when(repository.findAll()).thenReturn(List.of());

        List<MedicamentoResponseDTO> resultado = service.listarTodos();

        assertThat(resultado).isEmpty();
        verify(repository).findAll();
    }

    @Test
    void deveBuscarMedicamentoPorId() {
        Medicamento medicamento = Medicamento.builder()
                .id("1")
                .nome("Paracetamol")
                .descricao("Analgésico e antitérmico")
                .quantidade(100)
                .validade(LocalDate.of(2027, 3, 31))
                .lote("L001")
                .build();

        when(repository.findById("1")).thenReturn(Optional.of(medicamento));

        MedicamentoResponseDTO resultado = service.buscarPorId("1");

        assertThat(resultado.getId()).isEqualTo("1");
        assertThat(resultado.getNome()).isEqualTo("Paracetamol");
        assertThat(resultado.getDescricao()).isEqualTo("Analgésico e antitérmico");
        assertThat(resultado.getQuantidade()).isEqualTo(100);
        assertThat(resultado.getValidade()).isEqualTo(LocalDate.of(2027, 3, 31));
        assertThat(resultado.getLote()).isEqualTo("L001");

        verify(repository).findById("1");
    }

    @Test
    void deveLancarExcecaoQuandoMedicamentoNaoForEncontrado() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("999"))
                .isInstanceOf(MedicamentoNotFoundException.class)
                .hasMessageContaining("999");

        verify(repository).findById("999");
    }

    private MedicamentoRequestDTO novoMedicamento() {
        return new MedicamentoRequestDTO("Dipirona", "Analgésico", 20,
                LocalDate.of(2027, 8, 15), "L002", "cat1");
    }

    @Test
    void deveCadastrarMedicamento() {
        when(categoriaRepository.existsById("cat1")).thenReturn(true);
        Medicamento salvo = medicamentoSalvo();
        when(repository.save(any(Medicamento.class))).thenReturn(salvo);

        MedicamentoResponseDTO resultado = service.criar(novoMedicamento());

        assertThat(resultado.getId()).isEqualTo("1");
        assertThat(resultado.getNome()).isEqualTo("Dipirona");
        ArgumentCaptor<Medicamento> captor = ArgumentCaptor.forClass(Medicamento.class);
        verify(repository).save(captor.capture());
        Medicamento recebido = captor.getValue();
        assertThat(recebido.getId()).isNull();
        assertThat(recebido.getCategoriaId()).isEqualTo("cat1");
        assertThat(recebido.getNome()).isEqualTo("Dipirona");
        assertThat(recebido.getDescricao()).isEqualTo("Analgésico");
        assertThat(recebido.getQuantidade()).isEqualTo(20);
        assertThat(recebido.getValidade()).isEqualTo(LocalDate.of(2027, 8, 15));
        assertThat(recebido.getLote()).isEqualTo("L002");
    }

    private Medicamento medicamentoSalvo() {
        return Medicamento.builder().id("1").nome("Dipirona").descricao("Analgésico")
                .quantidade(20).validade(LocalDate.of(2027, 8, 15)).lote("L002").build();
    }

    @Test
    void deveAtualizarMedicamentoPreservandoId() {
        when(categoriaRepository.existsById("cat1")).thenReturn(true);
        Medicamento existente = Medicamento.builder().id("1").nome("Nome antigo").build();
        when(repository.findById("1")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        MedicamentoResponseDTO resultado = service.atualizar("1", novoMedicamento());

        assertThat(resultado.getId()).isEqualTo("1");
        assertThat(resultado.getNome()).isEqualTo("Dipirona");
        assertThat(resultado.getDescricao()).isEqualTo("Analgésico");
        assertThat(resultado.getQuantidade()).isEqualTo(20);
        assertThat(resultado.getValidade()).isEqualTo(LocalDate.of(2027, 8, 15));
        assertThat(resultado.getLote()).isEqualTo("L002");
        assertThat(resultado.getCategoriaId()).isEqualTo("cat1");
        verify(repository).save(existente);
    }

    @Test
    void naoDeveAtualizarMedicamentoInexistente() {
        assertThatThrownBy(() -> service.atualizar("999", novoMedicamento()))
                .isInstanceOf(MedicamentoNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveExcluirMedicamento() {
        Medicamento medicamento = Medicamento.builder().id("1").build();
        when(repository.findById("1")).thenReturn(Optional.of(medicamento));

        service.excluir("1");

        verify(repository).delete(medicamento);
    }

    @Test
    void naoDeveExcluirMedicamentoInexistente() {
        assertThatThrownBy(() -> service.excluir("999"))
                .isInstanceOf(MedicamentoNotFoundException.class);
        verify(repository, never()).delete(any(Medicamento.class));
    }

    @Test
    void naoDeveCadastrarComCategoriaInexistente() {
        assertThatThrownBy(() -> service.criar(novoMedicamento()))
                .isInstanceOf(CategoriaNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveAtualizarComCategoriaInexistente() {
        Medicamento medicamento = medicamentoSalvo();
        when(repository.findById("1")).thenReturn(Optional.of(medicamento));
        assertThatThrownBy(() -> service.atualizar("1", novoMedicamento()))
                .isInstanceOf(CategoriaNotFoundException.class);
        assertThat(medicamento.getNome()).isEqualTo("Dipirona");
        verify(repository, never()).save(any());
    }

    @Test
    void deveConsultarHistoricoDaDataMaisRecenteParaAMaisAntiga() {
        LocalDateTime data = LocalDateTime.of(2026, 11, 9, 10, 0);
        Movimentacao entrada = new Movimentacao("ENTRADA", "L1", 20, data);
        Movimentacao baixa = new Movimentacao("BAIXA", "L1", 5, data.plusHours(1));
        Movimentacao outraEntrada = new Movimentacao("ENTRADA", "L2", 10, data.plusMinutes(30));
        Medicamento medicamento = Medicamento.builder().id("1")
                .movimentacoes(new ArrayList<>(List.of(entrada, baixa, outraEntrada))).build();
        when(repository.findById("1")).thenReturn(Optional.of(medicamento));

        assertThat(service.consultarHistorico("1")).containsExactly(baixa, outraEntrada, entrada);
        assertThat(medicamento.getMovimentacoes()).containsExactly(entrada, baixa, outraEntrada);
        verify(repository, never()).save(any());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverMovimentacoes() {
        when(repository.findById("1")).thenReturn(Optional.of(Medicamento.builder().id("1").build()));
        assertThat(service.consultarHistorico("1")).isEmpty();
    }

    @Test
    void deveConsultarCadastroAntigoSemListaDeMovimentacoes() {
        Medicamento medicamento = Medicamento.builder().id("1").movimentacoes(null).build();
        when(repository.findById("1")).thenReturn(Optional.of(medicamento));
        assertThat(service.consultarHistorico("1")).isEmpty();
    }

    @Test
    void deveRetornarErroAoConsultarHistoricoDeMedicamentoInexistente() {
        assertThatThrownBy(() -> service.consultarHistorico("999"))
                .isInstanceOf(MedicamentoNotFoundException.class);
    }

    @Test
    void deveMostrarUltimaInclusaoPrimeiroQuandoAsDatasForemIguais() {
        LocalDateTime data = LocalDateTime.of(2026, 11, 9, 10, 0);
        Movimentacao entrada = new Movimentacao("ENTRADA", "L1", 20, data);
        Movimentacao baixa = new Movimentacao("BAIXA", "L1", 5, data);
        Medicamento medicamento = Medicamento.builder().id("1")
                .movimentacoes(List.of(entrada, baixa)).build();
        when(repository.findById("1")).thenReturn(Optional.of(medicamento));
        assertThat(service.consultarHistorico("1")).containsExactly(baixa, entrada);
    }

    @Test
    void deveAjustarQuantidadeAntesDasMovimentacoes() {
        Medicamento medicamento = medicamentoSalvo();
        when(repository.findById("1")).thenReturn(Optional.of(medicamento));
        when(repository.save(medicamento)).thenReturn(medicamento);
        assertThat(service.ajustarQuantidade("1", 5).getQuantidade()).isEqualTo(25);
        assertThat(service.ajustarQuantidade("1", -25).getQuantidade()).isZero();
    }

    @Test
    void deveRejeitarAjusteNegativoOuAcimaDoLimite() {
        Medicamento medicamento = medicamentoSalvo();
        when(repository.findById("1")).thenReturn(Optional.of(medicamento));
        assertThatThrownBy(() -> service.ajustarQuantidade("1", -21))
                .isInstanceOf(AEP.StockLine.exception.QuantidadeInvalidaException.class);
        assertThatThrownBy(() -> service.ajustarQuantidade("1", Integer.MAX_VALUE))
                .isInstanceOf(AEP.StockLine.exception.QuantidadeInvalidaException.class);
        assertThat(medicamento.getQuantidade()).isEqualTo(20);
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarAjusteDiretoAposEntradasPorLote() {
        Medicamento medicamento = medicamentoSalvo();
        medicamento.getMovimentacoes().add(new Movimentacao("ENTRADA", "L002", 20, LocalDateTime.now()));
        when(repository.findById("1")).thenReturn(Optional.of(medicamento));
        assertThatThrownBy(() -> service.ajustarQuantidade("1", 5))
                .isInstanceOf(AEP.StockLine.exception.EstoqueException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarAjusteSemQuantidadeOuMedicamento() {
        when(repository.findById("1")).thenReturn(Optional.of(medicamentoSalvo()));
        assertThatThrownBy(() -> service.ajustarQuantidade("1", null))
                .isInstanceOf(AEP.StockLine.exception.EstoqueException.class);
        assertThatThrownBy(() -> service.ajustarQuantidade("999", 5))
                .isInstanceOf(MedicamentoNotFoundException.class);
    }
}
