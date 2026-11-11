package AEP.StockLine.service;

import AEP.StockLine.dto.CategoriaRequestDTO;
import AEP.StockLine.dto.CategoriaResponseDTO;
import AEP.StockLine.exception.CategoriaNotFoundException;
import AEP.StockLine.exception.EstoqueException;
import AEP.StockLine.model.Categoria;
import AEP.StockLine.repository.CategoriaRepository;
import AEP.StockLine.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository repository;
    private final MedicamentoRepository medicamentoRepository;

    public CategoriaService(CategoriaRepository repository, MedicamentoRepository medicamentoRepository) {
        this.repository = repository;
        this.medicamentoRepository = medicamentoRepository;
    }

    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome().trim());
        categoria.setDescricao(dto.getDescricao());
        return toDTO(repository.save(categoria));
    }

    public CategoriaResponseDTO atualizar(String id, CategoriaRequestDTO dto) {
        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
        categoria.setNome(dto.getNome().trim());
        categoria.setDescricao(dto.getDescricao());
        return toDTO(repository.save(categoria));
    }

    public void excluir(String id) {
        if (!repository.existsById(id)) {
            throw new CategoriaNotFoundException(id);
        }
        if (medicamentoRepository.existsByCategoriaId(id)) {
            throw new EstoqueException("Não é possível excluir: existem medicamentos vinculados a esta categoria.");
        }
        repository.deleteById(id);
    }

    public List<CategoriaResponseDTO> listarTodas() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    private CategoriaResponseDTO toDTO(Categoria c) {
        return new CategoriaResponseDTO(c.getId(), c.getNome(), c.getDescricao());
    }
}
