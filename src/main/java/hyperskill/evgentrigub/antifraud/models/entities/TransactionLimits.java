package hyperskill.evgentrigub.antifraud.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "transaction_limits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLimits {
    public static final long SINGLETON_ID = 1L;
    public static final long DEFAULT_ALLOWED_LIMIT = 200L;
    public static final long DEFAULT_MANUAL_LIMIT = 1500L;

    @Id
    private long id = SINGLETON_ID;

    @Column(nullable = false)
    private long allowedLimit = DEFAULT_ALLOWED_LIMIT;

    @Column(nullable = false)
    private long manualLimit = DEFAULT_MANUAL_LIMIT;
}
