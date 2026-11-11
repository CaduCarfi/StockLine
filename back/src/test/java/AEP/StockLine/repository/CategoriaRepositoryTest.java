package AEP.StockLine.repository;

import AEP.StockLine.dto.CategoriaRequestDTO;
import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.service.CategoriaService;
import AEP.StockLine.service.MedicamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("teste")
@Import({CategoriaService.class, MedicamentoService.class, AEP.StockLine.mapper.MedicamentoMapper.class})
class CategoriaRepositoryTest {
    @Autowired private CategoriaService categoriaService;
    @Autowired private MedicamentoService medicamentoService;
    @Autowired private CategoriaRepository categorias;
    @Autowired private MedicamentoRepository medicamentos;

    @Test
    void deveSalvarCategoriaEVinculoDoMedicamento() {
        String categoriaId = categoriaService.criar(new CategoriaRequestDTO("Analgésicos")).getId();
        String medicamentoId = null;
        try {
            MedicamentoRequestDTO dados = new MedicamentoRequestDTO("Dipirona", "Analgésico", 20,
                    LocalDate.of(2027, 8, 15), "L001", categoriaId, null);
            medicamentoId = medicamentoService.criar(dados).getId();

            assertThat(categorias.findById(categoriaId)).get()
                    .extracting(categoria -> categoria.getNome()).isEqualTo("Analgésicos");
            Medicamento salvo = medicamentos.findById(medicamentoId).orElseThrow();
            assertThat(salvo.getCategoriaId()).isEqualTo(categoriaId);
            assertThat(medicamentoService.buscarPorId(medicamentoId).getCategoriaId()).isEqualTo(categoriaId);
        } finally {
            if (medicamentoId != null) medicamentos.deleteById(medicamentoId);
            categorias.deleteById(categoriaId);
        }
    }
}
