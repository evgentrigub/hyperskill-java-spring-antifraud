package hyperskill.evgentrigub.antifraud.controllers;

import hyperskill.evgentrigub.antifraud.models.StatusResponseDto;
import hyperskill.evgentrigub.antifraud.models.entities.StolenCard;
import hyperskill.evgentrigub.antifraud.models.entities.SuspiciousIp;
import hyperskill.evgentrigub.antifraud.models.transaction.FeedbackRequestDto;
import hyperskill.evgentrigub.antifraud.models.transaction.TransactionHistoryDto;
import hyperskill.evgentrigub.antifraud.models.transaction.TransactionRequestDto;
import hyperskill.evgentrigub.antifraud.models.transaction.TransactionResponseDto;
import hyperskill.evgentrigub.antifraud.services.IpService;
import hyperskill.evgentrigub.antifraud.services.StolenCardService;
import hyperskill.evgentrigub.antifraud.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {

    @Autowired
    IpService ipService;
    @Autowired
    StolenCardService cardService;

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<TransactionResponseDto> transaction(@Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        return ResponseEntity.ok(this.transactionService.getTransactionStatus(transactionRequestDto));
    }

    @PutMapping("/transaction")
    public ResponseEntity<TransactionHistoryDto> addFeedback(
            @Valid @RequestBody FeedbackRequestDto feedbackRequestDto) {
        return ResponseEntity.ok(transactionService.addFeedback(feedbackRequestDto));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistory() {
        return ResponseEntity.ok(transactionService.getHistory());
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistoryByNumber(
            @PathVariable String number) {
        return ResponseEntity.ok(transactionService.getHistoryByNumber(number));
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<Object> addSuspiciousIp(@Valid @RequestBody SuspiciousIp suspiciousIp) {
        return ResponseEntity.ok(ipService.save(suspiciousIp));
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<StatusResponseDto> deleteSuspiciousIp(@PathVariable String ip) {
        return ResponseEntity.ok(this.transactionService.deleteIp(ip));
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<SuspiciousIp>> getAllSuspiciousIp() {
        return ResponseEntity.ok(ipService.getAll());
    }

    @PostMapping("/stolencard")
    public ResponseEntity<Object> addStolenCard(@Valid @RequestBody StolenCard card) {
        return ResponseEntity.ok(cardService.save(card));
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<StatusResponseDto> deleteStolenCard(@PathVariable String number) {
        return ResponseEntity.ok(this.transactionService.deleteCard(number));
    }

    @GetMapping("/stolencard")
    public ResponseEntity<Object> getAllStolenCards() {
        return ResponseEntity.ok(cardService.getAll());
    }
}
