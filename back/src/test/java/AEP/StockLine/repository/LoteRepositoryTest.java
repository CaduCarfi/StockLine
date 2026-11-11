package AEP.StockLine.repository;

import AEP.StockLine.model.Lote;
import AEP.StockLine.model.Medicamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("teste")
class LoteRepositoryTest {

    @Autowired
    private MedicamentoRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    // ======= findByLotesValidadeBefore =======

    @Test
    void deveBuscarMedicamentosComLotesVencidos() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = Medicamento.builder()
                .nome("Paracetamol")
                .lotes(List.of(new Lote("L1", hoje.minusDays(1), 10)))
                .build();
        repository.save(med);

        List<Medicamento> resultado = repository.findByLotesValidadeBefore(hoje);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Paracetamol");
    }

    @Test
    void naoDeveRetornarMedicamentoComLoteQueVenceHojeEmVencidos() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = Medicamento.builder()
                .nome("Paracetamol")
                .lotes(List.of(new Lote("L1", hoje, 10)))
                .build();
        repository.save(med);

        List<Medicamento> resultado = repository.findByLotesValidadeBefore(hoje);

        assertThat(resultado).isEmpty();
    }

    @Test
    void naoDeveRetornarMedicamentoComLoteFuturo() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = Medicamento.builder()
                .nome("Dipirona")
                .lotes(List.of(new Lote("L1", hoje.plusDays(10), 20)))
                .build();
        repository.save(med);

        List<Medicamento> resultado = repository.findByLotesValidadeBefore(hoje);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveRetornarMedicamentoComPeloMenosUmLoteVencidoMesmoQueOutrosNaoSejam() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = Medicamento.builder()
                .nome("Amoxicilina")
                .lotes(List.of(
                        new Lote("L1", hoje.minusDays(5), 10),
                        new Lote("L2", hoje.plusDays(30), 20)))
                .build();
        repository.save(med);

        List<Medicamento> resultado = repository.findByLotesValidadeBefore(hoje);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getLotes()).hasSize(2);
    }

    @Test
    void deveRetornarListaVaziaDeVencidosQuandoNaoHouverMedicamentosComLotesExpirados() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = Medicamento.builder()
                .nome("Ibuprofeno")
                .lotes(List.of(new Lote("L1", hoje.plusDays(60), 15)))
                .build();
        repository.save(med);

        List<Medicamento> resultado = repository.findByLotesValidadeBefore(hoje);

        assertThat(resultado).isEmpty();
    }

    @Test
    void naoDeveRetornarMedicamentoSemLotesEmVencidos() {
        LocalDate hoje = LocalDate.now();
        repository.save(Medicamento.builder().nome("Vitamina C").build());

        List<Medicamento> resultado = repository.findByLotesValidadeBefore(hoje);

        assertThat(resultado).isEmpty();
    }

    // ======= findByLotesValidadeBetween =======

    @Test
    void deveBuscarMedicamentosComLotesAVencerNoPeriodo() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = Medicamento.builder()
                .nome("Dipirona")
                .lotes(List.of(new Lote("L1", hoje.plusDays(15), 20)))
                .build();
        repository.save(med);

        List<Medicamento> resultado = repository.findByLotesValidadeBetween(hoje, hoje.plusDays(30));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Dipirona");
    }

    @Test
    void deveRetornarMedicamentoComLoteQueVenceHoje() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = Medicamento.builder()
                .nome("Paracetamol")
                .lotes(List.of(new Lote("L1", hoje, 10)))
                .build();
        repository.save(med);

        List<Medicamento> resultado = repository.findByLotesValidadeBetween(hoje, hoje.plusDays(30));

        assertThat(resultado).hasSize(1);
    }

    @Test
    void deveRetornarMedicamentoComLoteNoLimiteFinalDoPeriodo() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(30);
        Medicamento med = Medicamento.builder()
                .nome("Amoxicilina")
                .lotes(List.of(new Lote("L1", limite, 5)))
                .build();
        repository.save(med);

        List<Medicamento> resultado = repository.findByLotesValidadeBetween(hoje, limite);

        assertThat(resultado).hasSize(1);
    }

    @Test
    void naoDeveRetornarMedicamentoComLoteUmDiaAposOLimite() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(30);
        Medicamento med = Medicamento.builder()
                .nome("Amoxicilina")
                .lotes(List.of(new Lote("L1", limite.plusDays(1), 5)))
                .build();
        repository.save(med);

        List<Medicamento> resultado = repository.findByLotesValidadeBetween(hoje, limite);

        assertThat(resultado).isEmpty();
    }

    @Test
    void naoDeveRetornarMedicamentoComLoteVencidoEmAVencer() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = Medicamento.builder()
                .nome("Dipirona")
                .lotes(List.of(new Lote("L1", hoje.minusDays(1), 10)))
                .build();
        repository.save(med);

        List<Medicamento> resultado = repository.findByLotesValidadeBetween(hoje, hoje.plusDays(30));

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveRetornarListaVaziaDeAVencerQuandoNaoHouverMedicamentosNoPeriodo() {
        LocalDate hoje = LocalDate.now();
        repository.save(Medicamento.builder().nome("Ibuprofeno")
                .lotes(List.of(new Lote("L1", hoje.plusDays(60), 15)))
                .build());

        List<Medicamento> resultado = repository.findByLotesValidadeBetween(hoje, hoje.plusDays(30));

        assertThat(resultado).isEmpty();
    }

    @Test
    void naoDeveRetornarMedicamentoSemLotesEmAVencer() {
        LocalDate hoje = LocalDate.now();
        repository.save(Medicamento.builder().nome("Vitamina C").build());

        List<Medicamento> resultado = repository.findByLotesValidadeBetween(hoje, hoje.plusDays(30));

        assertThat(resultado).isEmpty();
    }

    @Test
    void loteQueVenceHojeNaoDeveAparecerEmVencidosEDeveAparecerEmAVencer() {
        LocalDate hoje = LocalDate.now();
        repository.save(Medicamento.builder()
                .nome("Paracetamol")
                .lotes(List.of(new Lote("L1", hoje, 10)))
                .build());

        assertThat(repository.findByLotesValidadeBefore(hoje)).isEmpty();
        assertThat(repository.findByLotesValidadeBetween(hoje, hoje.plusDays(30))).hasSize(1);
    }

    @Test
    void deveRetornarMedicamentoComPeloMenosUmLoteNoPeriodoDaBuscaAVencer() {
        LocalDate hoje = LocalDate.now();
        Medicamento med = Medicamento.builder()
                .nome("Complexo B")
                .lotes(List.of(
                        new Lote("L1", hoje.plusDays(10), 10),
                        new Lote("L2", hoje.plusDays(60), 20)))
                .build();
        repository.save(med);

        List<Medicamento> resultado = repository.findByLotesValidadeBetween(hoje, hoje.plusDays(30));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getLotes()).hasSize(2);
    }
}
