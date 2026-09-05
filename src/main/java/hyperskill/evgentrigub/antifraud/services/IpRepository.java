package hyperskill.evgentrigub.antifraud.services;

import hyperskill.evgentrigub.antifraud.models.entities.SuspiciousIp;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

public interface IpRepository extends CrudRepository<SuspiciousIp, Long> {
    boolean existsByIp(String ip);
    @Transactional
    void deleteByIp(String ip);
}
