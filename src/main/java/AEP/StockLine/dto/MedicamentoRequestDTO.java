package AEP.StockLine.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicamentoRequestDTO {

    @NotBlank(message = "O nome do medicamento é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantidade;

    @NotNull(message = "A validade é obrigatória")
    private LocalDate validade;

    @NotBlank(message = "O lote é obrigatório")
    private String lote;
}