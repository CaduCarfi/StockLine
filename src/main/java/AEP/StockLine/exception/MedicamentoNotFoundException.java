package AEP.StockLine.exception;

/**
 * Lançada quando se busca um medicamento por um id que não existe no banco.
 *
 * Por que existe:
 * o service não sabe (nem deve saber) o que é HTTP — ele trabalha com regra de
 * negócio. Então, em vez de devolver null ou um Optional vazio e deixar o
 * controller adivinhar o que aconteceu, ele lança um erro com nome próprio
 * dizendo exatamente qual foi o problema.
 *
 * Herda de RuntimeException (não de Exception) para não obrigar todo mundo que
 * chama o service a escrever try/catch. Quem traduz esse erro em resposta HTTP
 * é o GlobalExceptionHandler, num lugar só.
 */
public class MedicamentoNotFoundException extends RuntimeException {

    public MedicamentoNotFoundException(String id) {
        // A mensagem vai para o corpo da resposta, então inclui o id
        // procurado para ajudar quem estiver consumindo a API a entender o erro.
        super("Medicamento não encontrado com o id: " + id);
    }
}
