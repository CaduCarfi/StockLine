package AEP.StockLine.repository;

import AEP.StockLine.dto.EntradaRequestDTO;
import AEP.StockLine.model.Lote;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.service.EntradaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("teste")
@Import({EntradaService.class, AEP.StockLine.mapper.MedicamentoMapper.class})
class EntradaRepositoryTest {
    @Autowired private MedicamentoRepository repository;
    @Autowired private EntradaService service;

    @Test
    void devePersistirLotesEEntradasNoMongoDB() {
        LocalDate validade = LocalDate.of(2027, 8, 15);
        Medicamento medicamento = repository.save(Medicamento.builder().nome("Teste de entrada")
                .quantidade(10).lote("L1").validade(validade).build());
        String id = medicamento.getId();
        try {
            service.registrar(id, new EntradaRequestDTO("L1", validade, 5));
            service.registrar(id, new EntradaRequestDTO("L2", validade.plusMonths(1), 20));
            Medicamento salvo = repository.findById(id).orElseThrow();
            assertThat(salvo.getQuantidade()).isEqualTo(35);
            assertThat(salvo.getLotes()).containsExactly(
                    new Lote("L1", validade, 15), new Lote("L2", validade.plusMonths(1), 20));
            assertThat(salvo.getMovimentacoes()).hasSize(2);
            assertThat(salvo.getMovimentacoes().getFirst().getTipo()).isEqualTo("ENTRADA");
            assertThat(salvo.getMovimentacoes().getFirst().getData()).isNotNull();

            assertThatThrownBy(() -> service.registrar(id, new EntradaRequestDTO("L1", validade.plusDays(1), 5)))
                    .isInstanceOf(AEP.StockLine.exception.EstoqueException.class);
            salvo = repository.findById(id).orElseThrow();
            assertThat(salvo.getQuantidade()).isEqualTo(35);
            assertThat(salvo.getMovimentacoes()).hasSize(2);
        } finally {
            repository.deleteById(id);
        }
    }
}
