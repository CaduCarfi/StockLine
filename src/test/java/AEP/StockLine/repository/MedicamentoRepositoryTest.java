package AEP.StockLine.repository;

import AEP.StockLine.model.Medicamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("teste")
class MedicamentoRepositoryTest {

    @Autowired
    private MedicamentoRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    void deveSalvarMedicamento() {
        Medicamento medicamento = Medicamento.builder()
                .nome("Paracetamol")
                .descricao("Analgésico e antitérmico")
                .quantidade(100)
                .build();

        Medicamento salvo = repository.save(medicamento);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Paracetamol");
        assertThat(salvo.getDescricao()).isEqualTo("Analgésico e antitérmico");
        assertThat(salvo.getQuantidade()).isEqualTo(100);
    }

    @Test
    void deveBuscarMedicamentoPorId() {
        Medicamento medicamento = Medicamento.builder()
                .nome("Dipirona")
                .descricao("Analgésico")
                .quantidade(50)
                .build();

        Medicamento salvo = repository.save(medicamento);

        Optional<Medicamento> resultado =
                repository.findById(salvo.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(salvo.getId());
        assertThat(resultado.get().getNome()).isEqualTo("Dipirona");
        assertThat(resultado.get().getQuantidade()).isEqualTo(50);
    }

    @Test
    void deveRetornarListaDeMedicamentos() {
        Medicamento primeiro = Medicamento.builder()
                .nome("Paracetamol")
                .quantidade(100)
                .build();

        Medicamento segundo = Medicamento.builder()
                .nome("Dipirona")
                .quantidade(50)
                .build();

        repository.save(primeiro);
        repository.save(segundo);

        List<Medicamento> medicamentos = repository.findAll();

        assertThat(medicamentos).hasSize(2);
        assertThat(medicamentos)
                .extracting(Medicamento::getNome)
                .containsExactlyInAnyOrder(
                        "Paracetamol",
                        "Dipirona"
                );
    }

    @Test
    void deveAtualizarMedicamento() {
        Medicamento medicamento = Medicamento.builder()
                .nome("Paracetamol")
                .descricao("Descrição antiga")
                .quantidade(100)
                .build();

        Medicamento salvo = repository.save(medicamento);

        salvo.setDescricao("Descrição atualizada");
        salvo.setQuantidade(150);

        Medicamento atualizado = repository.save(salvo);

        assertThat(atualizado.getId()).isEqualTo(salvo.getId());
        assertThat(atualizado.getDescricao())
                .isEqualTo("Descrição atualizada");
        assertThat(atualizado.getQuantidade()).isEqualTo(150);
    }

    @Test
    void deveExcluirMedicamento() {
        Medicamento medicamento = Medicamento.builder()
                .nome("Amoxicilina")
                .quantidade(30)
                .build();

        Medicamento salvo = repository.save(medicamento);

        repository.deleteById(salvo.getId());

        Optional<Medicamento> resultado =
                repository.findById(salvo.getId());

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveVerificarSeMedicamentoExiste() {
        Medicamento medicamento = Medicamento.builder()
                .nome("Ibuprofeno")
                .quantidade(80)
                .build();

        Medicamento salvo = repository.save(medicamento);

        boolean existe = repository.existsById(salvo.getId());

        assertThat(existe).isTrue();
    }
}