package hyperskill.evgentrigub.antifraud.services;

import hyperskill.evgentrigub.antifraud.models.entities.StolenCard;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    boolean existsByNumber(String number);
    @Transactional
    void deleteByNumber(String number);
}
