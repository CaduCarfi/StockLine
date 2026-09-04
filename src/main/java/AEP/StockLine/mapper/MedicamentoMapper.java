package AEP.StockLine.mapper;

import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.model.Medicamento;
import org.springframework.stereotype.Component;

@Component
public class MedicamentoMapper {

    public Medicamento toEntity(MedicamentoRequestDTO dto) {
        return Medicamento.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .quantidade(dto.getQuantidade())
                .validade(dto.getValidade())
                .lote(dto.getLote())
                .build();
    }

    public MedicamentoResponseDTO toResponseDTO(Medicamento medicamento) {
        return new MedicamentoResponseDTO(
                medicamento.getId(),
                medicamento.getNome(),
                medicamento.getDescricao(),
                medicamento.getQuantidade(),
                medicamento.getValidade(),
                medicamento.getLote()
        );
    }
}