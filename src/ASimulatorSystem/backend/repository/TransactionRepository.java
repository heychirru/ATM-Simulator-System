package ASimulatorSystem.backend.repository;

import ASimulatorSystem.backend.entity.BankTransaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findTop20ByAccountIdOrderByCreatedAtDescIdDesc(Long accountId);
}
