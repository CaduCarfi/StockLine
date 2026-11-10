package AEP.StockLine.controller;

import AEP.StockLine.dto.EntradaRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.service.EntradaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medicamentos/{id}/entradas")
public class EntradaController {
    private final EntradaService service;

    public EntradaController(EntradaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> registrar(@PathVariable String id,
            @Valid @RequestBody EntradaRequestDTO dto) {
        return ResponseEntity.ok(service.registrar(id, dto));
    }
}
