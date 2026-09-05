package hyperskill.evgentrigub.antifraud.services;

import hyperskill.evgentrigub.antifraud.models.entities.Transaction;
import hyperskill.evgentrigub.antifraud.models.entities.TransactionLimits;
import hyperskill.evgentrigub.antifraud.models.transaction.TransactionRequestDto;
import hyperskill.evgentrigub.antifraud.models.transaction.TransactionResponseDto;
import hyperskill.evgentrigub.antifraud.models.transaction.TransactionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionValidationService {
    private final TransactionRepository transactionRepository;
    private final IpRepository suspiciousIpRepository;
    private final StolenCardRepository stolenCardRepository;
    private final TransactionLimitsService limitsService;

    public TransactionValidationService(TransactionRepository transactionRepository,
                                        IpRepository suspiciousIpRepository,
                                        StolenCardRepository stolenCardRepository,
                                        TransactionLimitsService limitsService) {
        this.transactionRepository = transactionRepository;
        this.suspiciousIpRepository = suspiciousIpRepository;
        this.stolenCardRepository = stolenCardRepository;
        this.limitsService = limitsService;
    }

    public TransactionResponseDto validateAndSave(TransactionRequestDto request) {
        validateFormat(request);

        TransactionLimits limits = limitsService.getCurrentLimits();
        Map<String, TransactionStatus> ruleResults = getRuleResults(request, limits);
        TransactionStatus result = ruleResults.values().stream()
                .max(Comparator.comparingInt(this::severity))
                .orElse(TransactionStatus.ALLOWED);

        String info = ruleResults.entrySet().stream()
                .filter(entry -> result != TransactionStatus.ALLOWED && entry.getValue() == result)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.joining(", "));
        if (info.isEmpty()) {
            info = "none";
        }

        transactionRepository.save(new Transaction(
                request.getIP(), request.getNumber(), request.getAmount(), request.getRegion(),
                LocalDateTime.parse(request.getDate()), result));
        return new TransactionResponseDto(result, info);
    }

    private void validateFormat(TransactionRequestDto request) {
        if (request == null
                || FormattingConstraints.isInvalidIp(request.getIP())
                || FormattingConstraints.isValidCardNumber(request.getNumber())
                || !FormattingConstraints.isValidAmount(request.getAmount())
                || !FormattingConstraints.isValidDate(request.getDate())
                || !FormattingConstraints.isValidRegion(request.getRegion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private Map<String, TransactionStatus> getRuleResults(TransactionRequestDto request,
                                                           TransactionLimits limits) {
        Map<String, TransactionStatus> results = new LinkedHashMap<>();
        results.put("amount", getResultByAmount(request.getAmount(), limits));
        results.put("card-number", stolenCardRepository.existsByNumber(request.getNumber())
                ? TransactionStatus.PROHIBITED : TransactionStatus.ALLOWED);
        results.put("ip", suspiciousIpRepository.existsByIp(request.getIP())
                ? TransactionStatus.PROHIBITED : TransactionStatus.ALLOWED);

        List<Transaction> recentTransactions = getTransactionsInLastHour(request);
        results.put("ip-correlation", getIpCorrelationResult(request, recentTransactions));
        results.put("region-correlation", getRegionCorrelationResult(request, recentTransactions));
        return results;
    }

    private TransactionStatus getResultByAmount(long amount, TransactionLimits limits) {
        if (amount <= limits.getAllowedLimit()) {
            return TransactionStatus.ALLOWED;
        }
        if (amount <= limits.getManualLimit()) {
            return TransactionStatus.MANUAL_PROCESSING;
        }
        return TransactionStatus.PROHIBITED;
    }

    private TransactionStatus getIpCorrelationResult(TransactionRequestDto request,
                                                       List<Transaction> recentTransactions) {
        List<String> ips = new ArrayList<>();
        for (Transaction transaction : recentTransactions) {
            if (!transaction.getIp().equals(request.getIP()) && !ips.contains(transaction.getIp())) {
                ips.add(transaction.getIp());
            }
        }
        return getCorrelationStatus(ips.size());
    }

    private TransactionStatus getRegionCorrelationResult(TransactionRequestDto request,
                                                           List<Transaction> recentTransactions) {
        List<String> regions = new ArrayList<>();
        for (Transaction transaction : recentTransactions) {
            if (!transaction.getRegion().equals(request.getRegion())
                    && !regions.contains(transaction.getRegion())) {
                regions.add(transaction.getRegion());
            }
        }
        return getCorrelationStatus(regions.size());
    }

    private TransactionStatus getCorrelationStatus(int distinctValues) {
        if (distinctValues < 2) {
            return TransactionStatus.ALLOWED;
        }
        if (distinctValues == 2) {
            return TransactionStatus.MANUAL_PROCESSING;
        }
        return TransactionStatus.PROHIBITED;
    }

    private List<Transaction> getTransactionsInLastHour(TransactionRequestDto request) {
        LocalDateTime requestTime = LocalDateTime.parse(request.getDate());
        return transactionRepository.findByNumberAndDateBetween(
                request.getNumber(), requestTime.minusHours(1), requestTime);
    }

    private int severity(TransactionStatus status) {
        return switch (status) {
            case ALLOWED -> 0;
            case MANUAL_PROCESSING -> 1;
            case PROHIBITED -> 2;
        };
    }
}
