package hyperskill.evgentrigub.antifraud.services;

import hyperskill.evgentrigub.antifraud.exceptions.UserAlreadyExistsException;
import hyperskill.evgentrigub.antifraud.models.entities.SuspiciousIp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class IpService {
    @Autowired
    IpRepository repository;

    public SuspiciousIp save(SuspiciousIp ip) {
        if (repository.existsByIp(ip.getIp())) {
            throw new UserAlreadyExistsException();
        }
        return repository.save(ip);
    }

    public void delete(String ip) {
        if (!repository.existsByIp(ip)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteByIp(ip);
    }

    public List<SuspiciousIp> getAll() {
        List<SuspiciousIp> ips = new ArrayList<>();
        for (SuspiciousIp ip : repository.findAll()) {
            ips.add(ip);
        }
        return ips;
    }
}
