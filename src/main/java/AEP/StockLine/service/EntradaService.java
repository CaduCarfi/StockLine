package AEP.StockLine.service;

import AEP.StockLine.dto.EntradaRequestDTO;
import AEP.StockLine.dto.BaixaRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.exception.EstoqueException;
import AEP.StockLine.exception.MedicamentoNotFoundException;
import AEP.StockLine.mapper.MedicamentoMapper;
import AEP.StockLine.model.Lote;
import AEP.StockLine.model.Medicamento;
import AEP.StockLine.model.Movimentacao;
import AEP.StockLine.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class EntradaService {
    private final MedicamentoRepository repository;
    private final MedicamentoMapper mapper;

    public EntradaService(MedicamentoRepository repository, MedicamentoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public synchronized MedicamentoResponseDTO registrar(String id, EntradaRequestDTO dto) {
        if (dto.getQuantidade() == null || dto.getQuantidade() <= 0) {
            throw new EstoqueException("A quantidade deve ser maior que zero.");
        }
        if (dto.getLote() == null || dto.getLote().isBlank() || dto.getValidade() == null) {
            throw new EstoqueException("Informe o lote e a validade.");
        }
        Medicamento medicamento = repository.findById(id)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));
        prepararLotes(medicamento);

        Lote loteEncontrado = null;
        long total = 0;
        for (Lote lote : medicamento.getLotes()) {
            total += lote.getSaldo();
            if (lote.getCodigo().equals(dto.getLote())) {
                loteEncontrado = lote;
            }
        }
        if (loteEncontrado != null && !loteEncontrado.getValidade().equals(dto.getValidade())) {
            throw new EstoqueException("O lote já está cadastrado com outra validade.");
        }
        total += dto.getQuantidade();
        if (total > Integer.MAX_VALUE) {
            throw new EstoqueException("A quantidade ultrapassa o limite permitido.");
        }
        if (loteEncontrado == null) {
            loteEncontrado = new Lote(dto.getLote(), dto.getValidade(), 0);
            medicamento.getLotes().add(loteEncontrado);
        }
        loteEncontrado.setSaldo(loteEncontrado.getSaldo() + dto.getQuantidade());
        medicamento.setQuantidade((int) total);
        medicamento.getMovimentacoes().add(new Movimentacao(
                "ENTRADA", dto.getLote(), dto.getQuantidade(), LocalDateTime.now()));

        Medicamento salvo = repository.save(medicamento);
        return mapper.toResponseDTO(salvo);
    }

    public synchronized MedicamentoResponseDTO registrarBaixa(String id, BaixaRequestDTO dto) {
        if (dto.getQuantidade() == null || dto.getQuantidade() <= 0) {
            throw new EstoqueException("A quantidade deve ser maior que zero.");
        }
        if (dto.getLote() == null || dto.getLote().isBlank()) {
            throw new EstoqueException("Informe o lote.");
        }
        Medicamento medicamento = repository.findById(id)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));
        prepararLotes(medicamento);

        Lote loteEncontrado = null;
        for (Lote lote : medicamento.getLotes()) {
            if (lote.getCodigo().equals(dto.getLote())) {
                loteEncontrado = lote;
                break;
            }
        }
        if (loteEncontrado == null) {
            throw new EstoqueException("Lote não encontrado neste medicamento.");
        }
        if (dto.getQuantidade() > loteEncontrado.getSaldo()) {
            throw new EstoqueException("A quantidade da baixa é maior que o saldo do lote.");
        }

        loteEncontrado.setSaldo(loteEncontrado.getSaldo() - dto.getQuantidade());
        int total = 0;
        for (Lote lote : medicamento.getLotes()) {
            total += lote.getSaldo();
        }
        medicamento.setQuantidade(total);
        medicamento.getMovimentacoes().add(new Movimentacao(
                "BAIXA", dto.getLote(), dto.getQuantidade(), LocalDateTime.now()));

        Medicamento salvo = repository.save(medicamento);
        return mapper.toResponseDTO(salvo);
    }

    private void prepararLotes(Medicamento medicamento) {
        if (medicamento.getLotes() == null) {
            medicamento.setLotes(new ArrayList<>());
        }
        if (medicamento.getMovimentacoes() == null) {
            medicamento.setMovimentacoes(new ArrayList<>());
        }
        // Mantém o saldo que já existia antes da primeira movimentação por lote.
        if (medicamento.getLotes().isEmpty() && medicamento.getQuantidade() != null
                && medicamento.getQuantidade() > 0) {
            if (medicamento.getLote() == null || medicamento.getValidade() == null) {
                throw new EstoqueException("Atualize o lote e a validade do cadastro antes de movimentar o estoque.");
            }
            medicamento.getLotes().add(new Lote(medicamento.getLote(),
                    medicamento.getValidade(), medicamento.getQuantidade()));
        }
    }
}
