package hyperskill.evgentrigub.antifraud.services;

import hyperskill.evgentrigub.antifraud.exceptions.BadValidationException;
import hyperskill.evgentrigub.antifraud.exceptions.UserAlreadyExistsException;
import hyperskill.evgentrigub.antifraud.models.entities.StolenCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StolenCardService {
    @Autowired
    StolenCardRepository repository;

    public StolenCard save(StolenCard card) {
        if (repository.existsByNumber(card.getNumber())) {
            throw new UserAlreadyExistsException();
        }

        if (isInvalidCreditCardNumber(card.getNumber())) {
            throw new BadValidationException();
        }

        return repository.save(card);
    }

    public boolean delete(String number) {
        if (repository.existsByNumber(number)) {
            repository.deleteByNumber(number);
            return true;
        }
        return false;
    }

    public List<StolenCard> getAll() {
        List<StolenCard> cards = new ArrayList<>();
        for (StolenCard card : repository.findAll()) {
            cards.add(card);
        }
        return cards;
    }

    public boolean isInvalidCreditCardNumber(String number) {
        return FormattingConstraints.isValidCardNumber(number);
    }
}
