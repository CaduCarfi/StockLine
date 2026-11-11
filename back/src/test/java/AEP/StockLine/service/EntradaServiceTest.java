package AEP.StockLine.service;

import AEP.StockLine.dto.EntradaRequestDTO;
import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.exception.EstoqueException;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.model.Lote;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.repository.MedicamentoRepository;
import AEP.StockLine.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EntradaServiceTest {
    @Mock private MedicamentoRepository repository;
    @Mock private CategoriaRepository categorias;
    private EntradaService service;
    private Medicamento medicamento;
    private final LocalDate validade = LocalDate.of(2027, 8, 15);

    @BeforeEach
    void preparar() {
        service = new EntradaService(repository, new AEP.StockLine.mapper.MedicamentoMapper());
        medicamento = Medicamento.builder().id("m1").quantidade(0).build();
    }

    private void simularBanco() {
        when(repository.findById("m1")).thenReturn(Optional.of(medicamento));
        when(repository.save(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void deveCriarLoteERegistrarEntrada() {
        simularBanco();
        var resposta = service.registrar("m1", new EntradaRequestDTO("L1", validade, 10));
        assertThat(resposta.getQuantidade()).isEqualTo(10);
        assertThat(resposta.getLotes()).containsExactly(new Lote("L1", validade, 10));
        assertThat(medicamento.getMovimentacoes()).hasSize(1);
        var entrada = medicamento.getMovimentacoes().getFirst();
        assertThat(entrada.getTipo()).isEqualTo("ENTRADA");
        assertThat(entrada.getLote()).isEqualTo("L1");
        assertThat(entrada.getQuantidade()).isEqualTo(10);
        assertThat(entrada.getData()).isNotNull();
    }

    @Test
    void deveAbastecerMesmoLoteSemDuplicar() {
        simularBanco();
        service.registrar("m1", new EntradaRequestDTO("L1", validade, 10));
        service.registrar("m1", new EntradaRequestDTO("L1", validade, 5));
        assertThat(medicamento.getLotes()).containsExactly(new Lote("L1", validade, 15));
        assertThat(medicamento.getQuantidade()).isEqualTo(15);
        assertThat(medicamento.getMovimentacoes()).hasSize(2);
    }

    @Test
    void deveSomarSaldosDeLotesDiferentes() {
        simularBanco();
        service.registrar("m1", new EntradaRequestDTO("L1", validade, 10));
        service.registrar("m1", new EntradaRequestDTO("L2", validade.plusMonths(1), 20));
        assertThat(medicamento.getQuantidade()).isEqualTo(30);
        assertThat(medicamento.getLotes()).containsExactly(
                new Lote("L1", validade, 10), new Lote("L2", validade.plusMonths(1), 20));
    }

    @Test
    void naoDeveAceitarOutraValidadeParaMesmoLote() {
        when(repository.findById("m1")).thenReturn(Optional.of(medicamento));
        medicamento.getLotes().add(new Lote("L1", validade, 10));
        assertThatThrownBy(() -> service.registrar("m1", new EntradaRequestDTO("L1", validade.plusDays(1), 5)))
                .isInstanceOf(EstoqueException.class);
        assertThat(medicamento.getLotes().getFirst().getSaldo()).isEqualTo(10);
        assertThat(medicamento.getMovimentacoes()).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveAceitarQuantidadeInvalida() {
        for (Integer quantidade : Arrays.asList(null, 0, -1)) {
            assertThatThrownBy(() -> service.registrar("m1", new EntradaRequestDTO("L1", validade, quantidade)))
                    .isInstanceOf(EstoqueException.class);
        }
        verifyNoInteractions(repository);
    }

    @Test
    void naoDeveAceitarLoteOuValidadeAusente() {
        assertThatThrownBy(() -> service.registrar("m1", new EntradaRequestDTO(" ", validade, 1)))
                .isInstanceOf(EstoqueException.class);
        assertThatThrownBy(() -> service.registrar("m1", new EntradaRequestDTO(null, validade, 1)))
                .isInstanceOf(EstoqueException.class);
        assertThatThrownBy(() -> service.registrar("m1", new EntradaRequestDTO("L1", null, 1)))
                .isInstanceOf(EstoqueException.class);
    }

    @Test
    void naoDeveAceitarMedicamentoInexistente() {
        assertThatThrownBy(() -> service.registrar("x", new EntradaRequestDTO("L1", validade, 1)))
                .isInstanceOf(MedicamentoNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void devePreservarSaldoDoCadastroAntigo() {
        simularBanco();
        medicamento.setQuantidade(10);
        medicamento.setLote("ANTIGO");
        medicamento.setValidade(validade);
        medicamento.setLotes(null);
        medicamento.setMovimentacoes(null);
        service.registrar("m1", new EntradaRequestDTO("L2", validade, 5));
        assertThat(medicamento.getQuantidade()).isEqualTo(15);
        assertThat(medicamento.getLotes()).containsExactly(
                new Lote("ANTIGO", validade, 10), new Lote("L2", validade, 5));
        assertThat(medicamento.getMovimentacoes()).hasSize(1);
    }

    @Test
    void devePedirCorrecaoDeCadastroAntigoSemValidade() {
        when(repository.findById("m1")).thenReturn(Optional.of(medicamento));
        medicamento.setQuantidade(10);
        assertThatThrownBy(() -> service.registrar("m1", new EntradaRequestDTO("L1", validade, 1)))
                .isInstanceOf(EstoqueException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveUltrapassarLimiteDaQuantidade() {
        when(repository.findById("m1")).thenReturn(Optional.of(medicamento));
        medicamento.getLotes().add(new Lote("L1", validade, Integer.MAX_VALUE));
        assertThatThrownBy(() -> service.registrar("m1", new EntradaRequestDTO("L2", validade, 1)))
                .isInstanceOf(EstoqueException.class);
        assertThat(medicamento.getLotes()).hasSize(1);
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveApagarHistoricoNemAlterarSaldoPeloCadastro() {
        simularBanco();
        service.registrar("m1", new EntradaRequestDTO("L1", validade, 10));
        MedicamentoService cadastro = new MedicamentoService(repository, categorias, new AEP.StockLine.mapper.MedicamentoMapper());
        assertThatThrownBy(() -> cadastro.excluir("m1")).isInstanceOf(EstoqueException.class);
        when(categorias.existsById("cat1")).thenReturn(true);
        var dados = new MedicamentoRequestDTO("Dipirona", null, 0, validade, "L1", "cat1", null);
        assertThatThrownBy(() -> cadastro.atualizar("m1", dados)).isInstanceOf(EstoqueException.class);
        assertThat(medicamento.getQuantidade()).isEqualTo(10);
        verify(repository, never()).delete(any(Medicamento.class));
    }
}
