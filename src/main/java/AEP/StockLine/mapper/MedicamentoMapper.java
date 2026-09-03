package AEP.StockLine.mapper;

import AEP.StockLine.dto.MedicamentoRequestDTO;
import AEP.StockLine.dto.MedicamentoResponseDTO;
import AEP.StockLine.model.Medicamento;

public class MedicamentoMapper {

    public static Medicamento toEntity(MedicamentoRequestDTO dto) {
        return Medicamento.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .quantidade(dto.getQuantidade())
                .build();
    }

    public static MedicamentoResponseDTO toResponseDTO(Medicamento medicamento) {
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