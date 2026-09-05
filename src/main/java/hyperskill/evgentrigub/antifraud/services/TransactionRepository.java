package hyperskill.evgentrigub.antifraud.services;

import hyperskill.evgentrigub.antifraud.models.entities.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByNumberAndDateBetween(String number, LocalDateTime startTime, LocalDateTime endTime);
    List<Transaction> findAllByOrderByIdAsc();
    List<Transaction> findByNumberOrderByIdAsc(String number);
}
