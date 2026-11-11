package AEP.StockLine.exception;

/**
 * Lançada quando um ajuste de quantidade (incremento ou decremento) resultaria
 * em uma quantidade negativa no estoque.
 *
 * Por que existe:
 * assim como a MedicamentoNotFoundException, o service não deve devolver um
 * erro genérico quando a regra de negócio é violada. Aqui o problema não é
 * "recurso não encontrado" (404), é "requisição inválida" (400) — o cliente
 * pediu um ajuste que não é permitido dado o estado atual do estoque.
 *
 * Quem traduz esse erro em resposta HTTP é o GlobalExceptionHandler.
 */
public class QuantidadeInvalidaException extends RuntimeException {

    public QuantidadeInvalidaException(String id, Integer delta) {
        super("Ajuste de quantidade inválido para o medicamento " + id +
                ": delta " + delta + " resultaria em quantidade negativa");
    }
}