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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import AEP.StockLine.model.Movimentacao;
import java.util.Objects;
import AEP.StockLine.exception.EstoqueException;

@Service
public class MedicamentoService {

    private final MedicamentoRepository repository;
    private final MedicamentoMapper mapper;
    private final CategoriaRepository categoriaRepository;

    public MedicamentoService(MedicamentoRepository repository, CategoriaRepository categoriaRepository, MedicamentoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.categoriaRepository = categoriaRepository;
    }

    public List<MedicamentoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public MedicamentoResponseDTO buscarPorId(String id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));
    }

    public MedicamentoResponseDTO criar(MedicamentoRequestDTO dto) {
        validarCategoria(dto.getCategoriaId());
        Medicamento medicamento = mapper.toEntity(dto);
        Medicamento salvo = repository.save(medicamento);
        return mapper.toResponseDTO(salvo);
    }

    public MedicamentoResponseDTO atualizar(String id, MedicamentoRequestDTO dto) {
        Medicamento medicamento = repository.findById(id)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));

        validarCategoria(dto.getCategoriaId());
        if (medicamento.getMovimentacoes() != null && !medicamento.getMovimentacoes().isEmpty()
                && (!Objects.equals(medicamento.getQuantidade(), dto.getQuantidade())
                || !Objects.equals(medicamento.getLote(), dto.getLote())
                || !Objects.equals(medicamento.getValidade(), dto.getValidade()))) {
            throw new EstoqueException("Use as movimentações para alterar o estoque deste medicamento.");
        }
        mapper.updateEntity(medicamento, dto);

        Medicamento atualizado = repository.save(medicamento);
        return mapper.toResponseDTO(atualizado);
    }

    public void excluir(String id) {
        Medicamento medicamento = repository.findById(id)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));
        if (medicamento.getMovimentacoes() != null && !medicamento.getMovimentacoes().isEmpty()) {
            throw new EstoqueException("Não é possível excluir um medicamento com movimentações.");
        }
        repository.delete(medicamento);
    }

    public List<Movimentacao> consultarHistorico(String id) {
        Medicamento medicamento = repository.findById(id)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));

        if (medicamento.getMovimentacoes() == null) {
            return new ArrayList<>();
        }
        List<Movimentacao> historico = new ArrayList<>(medicamento.getMovimentacoes());
        // Em datas iguais, a última movimentação registrada aparece primeiro.
        Collections.reverse(historico);
        historico.sort(Comparator.comparing(Movimentacao::getData).reversed());
        return historico;
    }

    private void validarCategoria(String categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new CategoriaNotFoundException(categoriaId);
        }
    }

    public MedicamentoResponseDTO ajustarQuantidade(String id, Integer delta) {
        Medicamento medicamento = repository.findById(id)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));
        if (medicamento.getMovimentacoes() != null && !medicamento.getMovimentacoes().isEmpty()) {
            throw new EstoqueException("Use entradas e baixas para alterar o estoque deste medicamento.");
        }
        if (delta == null) {
            throw new EstoqueException("Informe o ajuste de quantidade.");
        }
        long novaQuantidade = (long) medicamento.getQuantidade() + delta;
        if (novaQuantidade < 0 || novaQuantidade > Integer.MAX_VALUE) {
            throw new AEP.StockLine.exception.QuantidadeInvalidaException(id, delta);
        }
        medicamento.setQuantidade((int) novaQuantidade);
        return mapper.toResponseDTO(repository.save(medicamento));
    }

    public List<MedicamentoResponseDTO> listarEstoqueBaixo() {
        return repository.findAll()
                .stream()
                .filter(m -> m.getEstoqueMinimo() != null
                        && m.getQuantidade() != null
                        && m.getQuantidade() <= m.getEstoqueMinimo())
                .map(mapper::toResponseDTO)
                .toList();
    }
}