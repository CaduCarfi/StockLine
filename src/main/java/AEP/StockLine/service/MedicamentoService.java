package AEP.StockLine.service;

import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.mapper.MedicamentoMapper;
import AEP.StockLine.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {

    private final MedicamentoRepository repository;

    public MedicamentoService(MedicamentoRepository repository) {
        this.repository = repository;
    }

    public MedicamentoResponseDTO buscarPorId(String id) {
        return repository.findById(id)
                .map(MedicamentoMapper::toResponseDTO)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));
    }

    public List<MedicamentoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(MedicamentoMapper::toResponseDTO)
                .toList();
    }
}
