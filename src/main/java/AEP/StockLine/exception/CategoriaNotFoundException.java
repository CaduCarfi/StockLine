package AEP.StockLine.exception;

public class CategoriaNotFoundException extends RuntimeException {
    public CategoriaNotFoundException(String id) {
        super("Categoria não encontrada com o id: " + id);
    }
}
