package AEP.StockLine.service;

import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.mapper.MedicamentoMapper;
import AEP.StockLine.repository.MedicamentoRepository;
import AEP.StockLine.repository.CategoriaRepository;
import AEP.StockLine.exception.CategoriaNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {

    private final MedicamentoRepository repository;
    private final CategoriaRepository categoriaRepository;

    public MedicamentoService(MedicamentoRepository repository, CategoriaRepository categoriaRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<MedicamentoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(MedicamentoMapper::toResponseDTO)
                .toList();
    }

    public MedicamentoResponseDTO buscarPorId(String id) {
        return repository.findById(id)
                .map(MedicamentoMapper::toResponseDTO)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));
    }

    public MedicamentoResponseDTO criar(MedicamentoRequestDTO dto) {
        validarCategoria(dto.getCategoriaId());
        Medicamento medicamento = MedicamentoMapper.toEntity(dto);
        Medicamento salvo = repository.save(medicamento);
        return MedicamentoMapper.toResponseDTO(salvo);
    }

    public MedicamentoResponseDTO atualizar(String id, MedicamentoRequestDTO dto) {
        Medicamento medicamento = repository.findById(id)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));

        validarCategoria(dto.getCategoriaId());
        medicamento.setNome(dto.getNome());
        medicamento.setDescricao(dto.getDescricao());
        medicamento.setQuantidade(dto.getQuantidade());
        medicamento.setValidade(dto.getValidade());
        medicamento.setLote(dto.getLote());
        medicamento.setCategoriaId(dto.getCategoriaId());

        Medicamento atualizado = repository.save(medicamento);
        return MedicamentoMapper.toResponseDTO(atualizado);
    }

    public void excluir(String id) {
        Medicamento medicamento = repository.findById(id)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));
        repository.delete(medicamento);
    }

    private void validarCategoria(String categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new CategoriaNotFoundException(categoriaId);
        }
    }
}
