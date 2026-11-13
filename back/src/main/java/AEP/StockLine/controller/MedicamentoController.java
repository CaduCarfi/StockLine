package AEP.StockLine.controller;

import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.dto.AjusteQuantidadeRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PatchMapping;
import AEP.StockLine.model.Movimentacao;
import AEP.StockLine.dto.MedicamentoRequestDTO;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import AEP.StockLine.service.MedicamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoService service;

    public MedicamentoController(MedicamentoService service) {
        this.service = service;
    }

    @Operation(summary = "Busca todos os medicamentos")
    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Busca medicamentos com estoque abaixo do mínimo")
    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<MedicamentoResponseDTO>> buscarEstoqueBaixo() {
        return ResponseEntity.ok(service.buscarComEstoqueBaixo());
    }

    @Operation(summary = "Busca o medicamento pelo Id")
    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Cadastra um novo medicamento no estoque")
    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> criar(@Valid @RequestBody MedicamentoRequestDTO dto) {
        MedicamentoResponseDTO medicamento = service.criar(dto);
        return ResponseEntity.created(URI.create("/medicamentos/" + medicamento.getId()))
                .body(medicamento);
    }

    @Operation(summary = "Atualiza um medicamento do estoque")
    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> atualizar(@PathVariable String id,
            @Valid @RequestBody MedicamentoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Exclui um medicamento sem movimentações")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Consulta o histórico de movimentações do medicamento")
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<Movimentacao>> consultarHistorico(@PathVariable String id) {
        return ResponseEntity.ok(service.consultarHistorico(id));
    }
    @Operation(summary = "Ajusta a quantidade antes das movimentações por lote")
    @PatchMapping("/{id}/quantidade")
    public ResponseEntity<MedicamentoResponseDTO> ajustarQuantidade(@PathVariable String id,
            @Valid @RequestBody AjusteQuantidadeRequestDTO dto) {
        return ResponseEntity.ok(service.ajustarQuantidade(id, dto.getDelta()));
    }
}
