package AEP.StockLine.controller;

import AEP.StockLine.dto.BaixaRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.service.EntradaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medicamentos/{id}/baixas")
public class BaixaController {
    private final EntradaService service;

    public BaixaController(EntradaService service) {
        this.service = service;
    }

    @Operation(summary = "Registra uma baixa de estoque do medicamento por lote")
    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> registrar(@PathVariable String id,
            @Valid @RequestBody BaixaRequestDTO dto) {
        return ResponseEntity.ok(service.registrarBaixa(id, dto));
    }
}
