package AEP.StockLine.service;

import AEP.StockLine.dto.LoteVencimentoResponseDTO;
import AEP.StockLine.model.Lote;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.repository.MedicamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

    @Mock
    private MedicamentoRepository repository;

    @InjectMocks
    private LoteService service;

    private Medicamento medicamentoComLote(String id, String nome, String codigoLote,
                                           LocalDate validade, int saldo) {
        List<Lote> lotes = new ArrayList<>();
        lotes.add(new Lote(codigoLote, validade, saldo));
        return Medicamento.builder().id(id).nome(nome).lotes(lotes).build();
    }

    // ======= buscarVencidos =======

    @Test
    void deveRetornarLotesVencidos() {
        LocalDate ontem = LocalDate.now().minusDays(1);
        Medicamento med = medicamentoComLote("m1", "Paracetamol", "L1", ontem, 10);
        when(repository.findByLotesValidadeBefore(any())).thenReturn(List.of(med));

        List<LoteVencimentoResponseDTO> resultado = service.buscarVencidos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMedicamentoId()).isEqualTo("m1");
        assertThat(resultado.get(0).getMedicamentoNome()).isEqualTo("Paracetamol");
        assertThat(resultado.get(0).getLote()).isEqualTo("L1");
        assertThat(resultado.get(0).getQuantidade()).isEqualTo(10);
        assertThat(resultado.get(0).getValidade()).isEqualTo(ontem);
    }

    @Test
    void naoDeveRetornarLoteQueVenceHojeEmVencidos() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = medicamentoComLote("m1", "Paracetamol", "L1", hoje, 10);
        when(repository.findByLotesValidadeBefore(any())).thenReturn(List.of(med));

        List<LoteVencimentoResponseDTO> resultado = service.buscarVencidos();

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveRetornarListaVaziaDeVencidosQuandoNaoHouverResultados() {
        when(repository.findByLotesValidadeBefore(any())).thenReturn(List.of());

        List<LoteVencimentoResponseDTO> resultado = service.buscarVencidos();

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveFiltrarApenasOsLotesVencidosQuandoMedicamentoPossuiMultiplosLotes() {
        LocalDate hoje = LocalDate.now();
        List<Lote> lotes = new ArrayList<>();
        lotes.add(new Lote("L1", hoje.minusDays(5), 10));
        lotes.add(new Lote("L2", hoje.plusDays(10), 20));
        lotes.add(new Lote("L3", hoje.minusDays(1), 5));
        Medicamento med = Medicamento.builder().id("m1").nome("Dipirona").lotes(lotes).build();
        when(repository.findByLotesValidadeBefore(any())).thenReturn(List.of(med));

        List<LoteVencimentoResponseDTO> resultado = service.buscarVencidos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(LoteVencimentoResponseDTO::getLote)
                .containsExactlyInAnyOrder("L1", "L3");
    }

    @Test
    void deveIgnorarMedicamentoComLotesNulos() {
        Medicamento med = Medicamento.builder().id("m1").nome("Paracetamol").lotes(null).build();
        when(repository.findByLotesValidadeBefore(any())).thenReturn(List.of(med));

        List<LoteVencimentoResponseDTO> resultado = service.buscarVencidos();

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveChamarRepositorioComADataDeHojeParaVencidos() {
        when(repository.findByLotesValidadeBefore(any())).thenReturn(List.of());

        service.buscarVencidos();

        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);
        verify(repository).findByLotesValidadeBefore(captor.capture());
        assertThat(captor.getValue()).isEqualTo(LocalDate.now());
    }

    // ======= buscarAVencer =======

    @Test
    void deveRetornarLotesAVencer() {
        LocalDate amanha = LocalDate.now().plusDays(1);
        Medicamento med = medicamentoComLote("m1", "Dipirona", "L1", amanha, 20);
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of(med));

        List<LoteVencimentoResponseDTO> resultado = service.buscarAVencer(30);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getLote()).isEqualTo("L1");
        assertThat(resultado.get(0).getValidade()).isEqualTo(amanha);
    }

    @Test
    void deveRetornarLoteQueVenceHojeEmAVencer() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = medicamentoComLote("m1", "Dipirona", "L1", hoje, 20);
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of(med));

        List<LoteVencimentoResponseDTO> resultado = service.buscarAVencer(30);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getValidade()).isEqualTo(hoje);
    }

    @Test
    void deveRetornarLoteNoLimiteFinalDoPerido() {
        LocalDate limite = LocalDate.now().plusDays(30);
        Medicamento med = medicamentoComLote("m1", "Dipirona", "L1", limite, 20);
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of(med));

        List<LoteVencimentoResponseDTO> resultado = service.buscarAVencer(30);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getValidade()).isEqualTo(limite);
    }

    @Test
    void naoDeveRetornarLoteUmDiaAposOLimite() {
        LocalDate umDiaAposLimite = LocalDate.now().plusDays(31);
        Medicamento med = medicamentoComLote("m1", "Dipirona", "L1", umDiaAposLimite, 20);
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of(med));

        List<LoteVencimentoResponseDTO> resultado = service.buscarAVencer(30);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveRetornarLoteUmDiaAntesDoLimite() {
        LocalDate umDiaAntesDoLimite = LocalDate.now().plusDays(29);
        Medicamento med = medicamentoComLote("m1", "Dipirona", "L1", umDiaAntesDoLimite, 20);
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of(med));

        List<LoteVencimentoResponseDTO> resultado = service.buscarAVencer(30);

        assertThat(resultado).hasSize(1);
    }

    @Test
    void deveRespeitarDiferentiasValoresParaOParametroDias() {
        LocalDate daquiACincoDias = LocalDate.now().plusDays(5);
        Medicamento med = medicamentoComLote("m1", "Amoxicilina", "L1", daquiACincoDias, 15);
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of(med));

        assertThat(service.buscarAVencer(7)).hasSize(1);
        assertThat(service.buscarAVencer(30)).hasSize(1);
        assertThat(service.buscarAVencer(1)).isEmpty();
    }

    @Test
    void deveRetornarListaVaziaDeAVencerQuandoNaoHouverResultados() {
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of());

        List<LoteVencimentoResponseDTO> resultado = service.buscarAVencer(30);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveFiltrarApenasOsLotesAVencerDentroDoPerido() {
        LocalDate hoje = LocalDate.now();
        List<Lote> lotes = new ArrayList<>();
        lotes.add(new Lote("L1", hoje.plusDays(5), 10));
        lotes.add(new Lote("L2", hoje.plusDays(35), 20));
        lotes.add(new Lote("L3", hoje.minusDays(1), 5));
        Medicamento med = Medicamento.builder().id("m1").nome("Dipirona").lotes(lotes).build();
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of(med));

        List<LoteVencimentoResponseDTO> resultado = service.buscarAVencer(30);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getLote()).isEqualTo("L1");
    }

    @Test
    void deveIgnorarMedicamentoComLotesNulosEmAVencer() {
        Medicamento med = Medicamento.builder().id("m1").nome("Paracetamol").lotes(null).build();
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of(med));

        List<LoteVencimentoResponseDTO> resultado = service.buscarAVencer(30);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveChamarRepositorioComDatasCorretasParaAVencer() {
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of());

        service.buscarAVencer(7);

        ArgumentCaptor<LocalDate> captorInicio = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> captorFim = ArgumentCaptor.forClass(LocalDate.class);
        verify(repository).findByLotesValidadeBetween(captorInicio.capture(), captorFim.capture());
        assertThat(captorInicio.getValue()).isEqualTo(LocalDate.now());
        assertThat(captorFim.getValue()).isEqualTo(LocalDate.now().plusDays(7));
    }

    @Test
    void loteQueVenceHojeNaoDeveAparecerEmVencidosEDeveAparecerEmAVencer() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = medicamentoComLote("m1", "Paracetamol", "L1", hoje, 10);
        when(repository.findByLotesValidadeBefore(any())).thenReturn(List.of(med));
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of(med));

        assertThat(service.buscarVencidos()).isEmpty();
        assertThat(service.buscarAVencer(30)).hasSize(1);
    }

    @Test
    void medicamentoSemLotesNaoDeveAparecerNosResultados() {
        Medicamento med = Medicamento.builder().id("m1").nome("Paracetamol")
                .lotes(new ArrayList<>()).build();
        when(repository.findByLotesValidadeBefore(any())).thenReturn(List.of(med));
        when(repository.findByLotesValidadeBetween(any(), any())).thenReturn(List.of(med));

        assertThat(service.buscarVencidos()).isEmpty();
        assertThat(service.buscarAVencer(30)).isEmpty();
    }
}
