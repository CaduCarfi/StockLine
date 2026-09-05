package AEP.StockLine.exception;

public class MedicamentoNotFoundException extends RuntimeException {

    public MedicamentoNotFoundException(String id) {
        super("Medicamento não encontrado com o id: " + id);
    }
}
