package hyperskill.evgentrigub.antifraud.services;

import hyperskill.evgentrigub.antifraud.models.entities.TransactionLimits;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TransactionLimitsService {
    private final TransactionLimitsRepository repository;

    public TransactionLimitsService(TransactionLimitsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TransactionLimits getCurrentLimits() {
        return repository.findById(TransactionLimits.SINGLETON_ID)
                .orElseGet(() -> repository.save(new TransactionLimits()));
    }

    @Transactional
    public TransactionLimits getCurrentLimitsForUpdate() {
        if (!repository.existsById(TransactionLimits.SINGLETON_ID)) {
            repository.save(new TransactionLimits());
        }
        return repository.findForUpdate().orElseThrow();
    }
}
