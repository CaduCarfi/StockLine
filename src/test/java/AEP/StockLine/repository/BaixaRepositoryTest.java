package AEP.StockLine.repository;

import AEP.StockLine.dto.BaixaRequestDTO;
import AEP.StockLine.dto.EntradaRequestDTO;
import AEP.StockLine.model.Lote;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.service.EntradaService;
import AEP.StockLine.service.MedicamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("teste")
@Import({EntradaService.class, MedicamentoService.class})
class BaixaRepositoryTest {
    @Autowired private MedicamentoRepository repository;
    @Autowired private EntradaService service;
    @Autowired private MedicamentoService medicamentoService;

    @Test
    void deveSalvarBaixaESaldoNoMongoDB() {
        LocalDate validade = LocalDate.of(2027, 8, 15);
        Medicamento medicamento = repository.save(Medicamento.builder().nome("Teste de baixa").quantidade(0).build());
        String id = medicamento.getId();
        try {
            service.registrar(id, new EntradaRequestDTO("L1", validade, 10));
            service.registrar(id, new EntradaRequestDTO("L2", validade.plusMonths(1), 20));
            service.registrarBaixa(id, new BaixaRequestDTO("L1", 4));
            Medicamento salvo = repository.findById(id).orElseThrow();
            assertThat(salvo.getQuantidade()).isEqualTo(26);
            assertThat(salvo.getLotes()).containsExactly(
                    new Lote("L1", validade, 6), new Lote("L2", validade.plusMonths(1), 20));
            assertThat(salvo.getMovimentacoes()).hasSize(3);
            var baixa = salvo.getMovimentacoes().getLast();
            assertThat(baixa.getTipo()).isEqualTo("BAIXA");
            assertThat(baixa.getLote()).isEqualTo("L1");
            assertThat(baixa.getQuantidade()).isEqualTo(4);
            assertThat(baixa.getData()).isNotNull();
            var historico = medicamentoService.consultarHistorico(id);
            assertThat(historico).extracting(movimentacao -> movimentacao.getTipo())
                    .containsExactly("BAIXA", "ENTRADA", "ENTRADA");
            assertThat(historico).extracting(movimentacao -> movimentacao.getQuantidade())
                    .containsExactly(4, 20, 10);

            assertThatThrownBy(() -> service.registrarBaixa(id, new BaixaRequestDTO("L1", 7)))
                    .isInstanceOf(AEP.StockLine.exception.EstoqueException.class);
            salvo = repository.findById(id).orElseThrow();
            assertThat(salvo.getQuantidade()).isEqualTo(26);
            assertThat(salvo.getMovimentacoes()).hasSize(3);

            service.registrarBaixa(id, new BaixaRequestDTO("L1", 6));
            salvo = repository.findById(id).orElseThrow();
            assertThat(salvo.getLotes().getFirst().getSaldo()).isZero();
            assertThat(salvo.getQuantidade()).isEqualTo(20);
        } finally {
            repository.deleteById(id);
        }
    }
}
