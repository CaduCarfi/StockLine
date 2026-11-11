package AEP.StockLine.service;

import AEP.StockLine.dto.CategoriaRequestDTO;
import AEP.StockLine.dto.CategoriaResponseDTO;
import AEP.StockLine.model.Categoria;
import AEP.StockLine.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome().trim());
        Categoria salva = repository.save(categoria);
        return new CategoriaResponseDTO(salva.getId(), salva.getNome());
    }

    public List<CategoriaResponseDTO> listarTodas() {
        return repository.findAll().stream()
                .map(categoria -> new CategoriaResponseDTO(categoria.getId(), categoria.getNome()))
                .toList();
    }
}
