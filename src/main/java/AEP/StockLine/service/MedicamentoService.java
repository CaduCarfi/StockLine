package AEP.StockLine.service;

import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.mapper.MedicamentoMapper;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final MedicamentoMapper medicamentoMapper;

    public MedicamentoService(MedicamentoRepository medicamentoRepository, MedicamentoMapper medicamentoMapper) {
        this.medicamentoRepository = medicamentoRepository;
        this.medicamentoMapper = medicamentoMapper;
    }

    public MedicamentoResponseDTO buscarPorId(String id) {
        return medicamentoRepository.findById(id)
                .map(medicamentoMapper::toResponseDTO)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));
    }

    public List<MedicamentoResponseDTO> listarTodos() {
        return medicamentoRepository.findAll()
                .stream()
                .map(medicamentoMapper::toResponseDTO)
                .toList();
    }

    public MedicamentoResponseDTO cadastrar(MedicamentoRequestDTO request) {
        Medicamento medicamento = medicamentoMapper.toEntity(request);
        Medicamento salvo = medicamentoRepository.save(medicamento);
        return medicamentoMapper.toResponseDTO(salvo);
    }
}