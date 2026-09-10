package AEP.StockLine.controller;

import AEP.StockLine.dto.AjusteQuantidadeRequestDTO;
import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.service.MedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoService service;

    public MedicamentoController(MedicamentoService service) {
        this.service = service;
    }

    @Operation(summary = "Busca o medicamento pelo Id")
    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Busca todos os medicamentos")
    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Cadastra um novo medicamento no estoque")
    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> cadastrar(@RequestBody @Valid MedicamentoRequestDTO request) {
        MedicamentoResponseDTO medicamentoResponseDTO = service.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoResponseDTO);
    }


    @Operation(summary = "Atualiza um Medicamento do Estoque")
    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> atualizar(@PathVariable String id,
                                                            @RequestBody @Valid MedicamentoRequestDTO request) {
        MedicamentoResponseDTO medicamentoResponseDTO = service.atualizar(request, id);
        return ResponseEntity.ok(medicamentoResponseDTO);
    }

    @Operation(summary = "Ajusta a quantidade de Medicamentos no Estoque")
    @PatchMapping("/{id}/quantidade")
    public ResponseEntity<MedicamentoResponseDTO> ajustarQuantidade(@PathVariable String id, @RequestBody @Valid AjusteQuantidadeRequestDTO delta) {
        MedicamentoResponseDTO medicamentoResponseDTO = service.ajustarQuantidade(id, delta.getDelta());
        return ResponseEntity.ok(medicamentoResponseDTO);
    }

    @Operation(summary = "Deleta um Medicamento do Estoque")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
