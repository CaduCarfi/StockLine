package AEP.StockLine.controller;

import AEP.StockLine.dto.CategoriaRequestDTO;
import AEP.StockLine.dto.CategoriaResponseDTO;
import AEP.StockLine.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastra uma nova categoria")
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(@Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Atualiza uma categoria pelo ID")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable String id,
            @Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(summary = "Exclui uma categoria pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista todas as categorias")
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }
}
