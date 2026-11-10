package AEP.StockLine.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AjusteQuantidadeRequestDTO {

    @NotNull(message = "O delta é obrigatório")
    @Min(value = -10, message = "O delta não pode ser menor que -10")
    @Max(value = 10, message = "O delta não pode ser maior que 10")
    private Integer delta;
}