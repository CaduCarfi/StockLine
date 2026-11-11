package AEP.StockLine.repository;

import AEP.StockLine.model.Medicamento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicamentoRepository extends MongoRepository<Medicamento, String> {

    @Query("{ 'lotes.validade': { $lt: ?0 } }")
    List<Medicamento> findByLotesValidadeBefore(LocalDate data);

    @Query("{ 'lotes.validade': { $gte: ?0, $lte: ?1 } }")
    List<Medicamento> findByLotesValidadeBetween(LocalDate inicio, LocalDate fim);
}