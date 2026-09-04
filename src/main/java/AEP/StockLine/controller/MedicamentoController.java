package AEP.StockLine.controller;

import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.service.MedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @Operation(summary = "Cadastra um novo medicamento no estoque")
    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> cadastrar(@RequestBody @Valid MedicamentoRequestDTO request) {
        MedicamentoResponseDTO medicamentoResponseDTO = medicamentoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoResponseDTO);
    }
}
