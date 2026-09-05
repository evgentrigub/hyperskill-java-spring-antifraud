package hyperskill.evgentrigub.antifraud.models.entities;

import hyperskill.evgentrigub.antifraud.models.transaction.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "transactions")
@NoArgsConstructor
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "id")
    private long id;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private TransactionStatus result;

    @Column(name = "feedback")
    private String feedback = "";

    public Transaction(String ip, String number, long amount, String region, LocalDateTime date,
                       TransactionStatus result) {
        this.ip = ip;
        this.number = number;
        this.amount = amount;
        this.region = region;
        this.date = date;
        this.result = result;
        this.feedback = "";
    }
}
