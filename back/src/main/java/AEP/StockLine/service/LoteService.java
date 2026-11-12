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
        return repository.findAll()
                .stream()
                .flatMap(med -> lotesEfetivos(med).stream()
                        .filter(lote -> lote.getValidade() != null && lote.getValidade().isBefore(hoje))
                        .map(lote -> toDTO(med, lote)))
                .toList();
    }

    public List<LoteVencimentoResponseDTO> buscarAVencer(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(dias);
        return repository.findAll()
                .stream()
                .flatMap(med -> lotesEfetivos(med).stream()
                        .filter(lote -> lote.getValidade() != null
                                && !lote.getValidade().isBefore(hoje)
                                && !lote.getValidade().isAfter(limite))
                        .map(lote -> toDTO(med, lote)))
                .toList();
    }

    private List<Lote> lotesEfetivos(Medicamento med) {
        if (med.getLotes() != null && !med.getLotes().isEmpty()) {
            return med.getLotes();
        }
        if (med.getLote() != null && med.getValidade() != null) {
            int saldo = med.getQuantidade() != null ? med.getQuantidade() : 0;
            return List.of(new Lote(med.getLote(), med.getValidade(), saldo));
        }
        return List.of();
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
