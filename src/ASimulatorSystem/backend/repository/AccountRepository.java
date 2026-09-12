package ASimulatorSystem.backend.repository;

import ASimulatorSystem.backend.entity.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByCardNumber(String cardNumber);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findLockedById(Long id);
}
