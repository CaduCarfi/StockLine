package AEP.StockLine.service;

import AEP.StockLine.dto.LoteVencimentoResponseDTO;
import AEP.StockLine.model.Lote;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoteService {

    private final MedicamentoRepository repository;

    public LoteService(MedicamentoRepository repository) {
        this.repository = repository;
    }

    public List<LoteVencimentoResponseDTO> buscarVencidos() {
        LocalDate hoje = LocalDate.now();
        return repository.findByLotesValidadeBefore(hoje)
                .stream()
                .filter(med -> med.getLotes() != null)
                .flatMap(med -> med.getLotes().stream()
                        .filter(lote -> lote.getValidade().isBefore(hoje))
                        .map(lote -> toDTO(med, lote)))
                .toList();
    }

    public List<LoteVencimentoResponseDTO> buscarAVencer(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(dias);
        return repository.findByLotesValidadeBetween(hoje, limite)
                .stream()
                .filter(med -> med.getLotes() != null)
                .flatMap(med -> med.getLotes().stream()
                        .filter(lote -> !lote.getValidade().isBefore(hoje) && !lote.getValidade().isAfter(limite))
                        .map(lote -> toDTO(med, lote)))
                .toList();
    }

    private LoteVencimentoResponseDTO toDTO(Medicamento med, Lote lote) {
        return new LoteVencimentoResponseDTO(
                med.getId(),
                med.getNome(),
                lote.getCodigo(),
                lote.getSaldo(),
                lote.getValidade()
        );
    }
}
