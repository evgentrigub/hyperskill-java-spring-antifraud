package hyperskill.evgentrigub.antifraud.services;

import hyperskill.evgentrigub.antifraud.models.entities.TransactionLimits;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TransactionLimitsRepository extends CrudRepository<TransactionLimits, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select limits from TransactionLimits limits where limits.id = 1")
    Optional<TransactionLimits> findForUpdate();
}
