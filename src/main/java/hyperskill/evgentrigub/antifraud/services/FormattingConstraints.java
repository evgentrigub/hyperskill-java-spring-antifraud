package hyperskill.evgentrigub.antifraud.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class FormattingConstraints {

    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || !cardNumber.matches("\\d+") || cardNumber.length() != 16) {
            return true;
        }

        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Integer.parseInt(cardNumber.substring(i, i + 1));

            if (alternate) {
                digit *= 2;

                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return sum % 10 != 0;
    }

    public static boolean isInvalidIp(String ip) {
        String zeroTo255 = "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";

        String IPRegex = String.format("%s\\.%s\\.%s\\.%s", zeroTo255, zeroTo255, zeroTo255, zeroTo255);

        return ip == null || !ip.matches(IPRegex);
    }

    public static boolean isValidAmount(Long amount) {
        return amount != null && amount > 0;
    }

    public static boolean isValidRegion(String region) {
        return region != null && region.matches("EAP|ECA|HIC|LAC|MENA|SA|SSA");
    }

    public static boolean isValidDate(String date) {
        if (date == null) {
            return false;
        }
        try {
            LocalDateTime.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
