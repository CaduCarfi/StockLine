package AEP.StockLine.controller;

import AEP.StockLine.dto.LoteVencimentoResponseDTO;
import AEP.StockLine.service.LoteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lotes")
public class LoteController {

    private final LoteService service;

    public LoteController(LoteService service) {
        this.service = service;
    }

    @Operation(summary = "Retorna os lotes de medicamentos com validade expirada")
    @GetMapping("/vencidos")
    public ResponseEntity<List<LoteVencimentoResponseDTO>> buscarVencidos() {
        return ResponseEntity.ok(service.buscarVencidos());
    }

    @Operation(summary = "Retorna os lotes de medicamentos que vencerão dentro do período informado")
    @GetMapping("/a-vencer")
    public ResponseEntity<List<LoteVencimentoResponseDTO>> buscarAVencer(
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(service.buscarAVencer(dias));
    }
}
