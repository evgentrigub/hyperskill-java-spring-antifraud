package hyperskill.evgentrigub.antifraud.services;

import hyperskill.evgentrigub.antifraud.exceptions.BadValidationException;
import hyperskill.evgentrigub.antifraud.models.StatusResponseDto;
import hyperskill.evgentrigub.antifraud.models.entities.Transaction;
import hyperskill.evgentrigub.antifraud.models.entities.TransactionLimits;
import hyperskill.evgentrigub.antifraud.models.transaction.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TransactionService {
    private final IpService ipService;
    private final StolenCardService cardService;
    private final TransactionValidationService validationService;
    private final TransactionRepository transactionRepository;
    private final TransactionLimitsService limitsService;

    public TransactionService(IpService ipService,
                              StolenCardService cardService,
                              TransactionValidationService validationService,
                              TransactionRepository transactionRepository,
                              TransactionLimitsService limitsService) {
        this.ipService = ipService;
        this.cardService = cardService;
        this.validationService = validationService;
        this.transactionRepository = transactionRepository;
        this.limitsService = limitsService;
    }

    public TransactionResponseDto getTransactionStatus(TransactionRequestDto request) {
        return validationService.validateAndSave(request);
    }

    public List<TransactionHistoryDto> getHistory() {
        return transactionRepository.findAllByOrderByIdAsc().stream()
                .map(TransactionHistoryDto::new)
                .toList();
    }

    public List<TransactionHistoryDto> getHistoryByNumber(String number) {
        if (FormattingConstraints.isValidCardNumber(number)) {
            throw new BadValidationException();
        }

        List<TransactionHistoryDto> history = transactionRepository.findByNumberOrderByIdAsc(number).stream()
                .map(TransactionHistoryDto::new)
                .toList();
        if (history.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return history;
    }

    @Transactional
    public TransactionHistoryDto addFeedback(FeedbackRequestDto request) {
        TransactionStatus feedback;
        try {
            feedback = TransactionStatus.valueOf(request.getFeedback());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (transaction.getFeedback() != null && !transaction.getFeedback().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (transaction.getResult() == feedback) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT);
        }

        TransactionLimits limits = limitsService.getCurrentLimitsForUpdate();
        updateLimits(limits, transaction.getResult(), feedback, transaction.getAmount());
        transaction.setFeedback(feedback.name());
        transactionRepository.save(transaction);
        return new TransactionHistoryDto(transaction);
    }

    private void updateLimits(TransactionLimits limits, TransactionStatus result,
                              TransactionStatus feedback, long amount) {
        switch (result) {
            case ALLOWED -> {
                limits.setAllowedLimit(adjustLimit(limits.getAllowedLimit(), amount, false));
                if (feedback == TransactionStatus.PROHIBITED) {
                    limits.setManualLimit(adjustLimit(limits.getManualLimit(), amount, false));
                }
            }
            case MANUAL_PROCESSING -> {
                if (feedback == TransactionStatus.ALLOWED) {
                    limits.setAllowedLimit(adjustLimit(limits.getAllowedLimit(), amount, true));
                } else {
                    limits.setManualLimit(adjustLimit(limits.getManualLimit(), amount, false));
                }
            }
            case PROHIBITED -> {
                limits.setManualLimit(adjustLimit(limits.getManualLimit(), amount, true));
                if (feedback == TransactionStatus.ALLOWED) {
                    limits.setAllowedLimit(adjustLimit(limits.getAllowedLimit(), amount, true));
                }
            }
        }
    }

    private long adjustLimit(long currentLimit, long amount, boolean increase) {
        long numerator = 4 * currentLimit + (increase ? amount : -amount);
        return -Math.floorDiv(-numerator, 5);
    }

    public StatusResponseDto deleteIp(String ip) {
        if (FormattingConstraints.isInvalidIp(ip)) {
            throw new BadValidationException();
        }
        ipService.delete(ip);
        return new StatusResponseDto("IP " + ip + " successfully removed!");
    }

    public StatusResponseDto deleteCard(String number) {
        if (cardService.isInvalidCreditCardNumber(number)) {
            throw new BadValidationException();
        }
        if (cardService.delete(number)) {
            return new StatusResponseDto("Card " + number + " successfully removed!");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
