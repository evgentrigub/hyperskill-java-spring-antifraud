package hyperskill.evgentrigub.antifraud.models.transaction;

import hyperskill.evgentrigub.antifraud.models.entities.Transaction;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionHistoryDto {
    private final long transactionId;
    private final long amount;
    private final String ip;
    private final String number;
    private final String region;
    private final LocalDateTime date;
    private final TransactionStatus result;
    private final String feedback;

    public TransactionHistoryDto(Transaction transaction) {
        this.transactionId = transaction.getId();
        this.amount = transaction.getAmount();
        this.ip = transaction.getIp();
        this.number = transaction.getNumber();
        this.region = transaction.getRegion();
        this.date = transaction.getDate();
        this.result = transaction.getResult();
        this.feedback = transaction.getFeedback() == null ? "" : transaction.getFeedback();
    }
}
