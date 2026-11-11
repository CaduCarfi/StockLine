package AEP.StockLine.service;

import AEP.StockLine.dto.CategoriaRequestDTO;
import AEP.StockLine.dto.CategoriaResponseDTO;
import AEP.StockLine.model.Categoria;
import AEP.StockLine.repository.CategoriaRepository;
import AEP.StockLine.repository.MedicamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {
    @Mock
    private CategoriaRepository repository;
    @Mock
    private MedicamentoRepository medicamentoRepository;
    @InjectMocks
    private CategoriaService service;

    @Test
    void deveCadastrarCategoria() {
        when(repository.save(any(Categoria.class))).thenReturn(new Categoria("cat1", "Analgésicos", null));
        CategoriaResponseDTO resposta = service.criar(new CategoriaRequestDTO(" Analgésicos ", null));
        assertThat(resposta.getId()).isEqualTo("cat1");
        assertThat(resposta.getNome()).isEqualTo("Analgésicos");
        ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getNome()).isEqualTo("Analgésicos");
    }

    @Test
    void deveListarCategorias() {
        when(repository.findAll()).thenReturn(List.of(new Categoria("cat1", "Analgésicos", null)));
        List<CategoriaResponseDTO> resultado = service.listarTodas();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo("cat1");
        assertThat(resultado.get(0).getNome()).isEqualTo("Analgésicos");
    }

    @Test
    void deveRetornarListaVazia() {
        when(repository.findAll()).thenReturn(List.of());
        assertThat(service.listarTodas()).isEmpty();
    }
}
