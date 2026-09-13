package ASimulatorSystem.backend.repository;

import ASimulatorSystem.backend.entity.BankTransaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findTop10ByAccount_IdOrderByCreatedAtDescIdDesc(Long accountId);

    @Query("select coalesce(sum(t.amount), 0) from BankTransaction t where t.account.id = :accountId and t.type = :type and t.createdAt >= :since")
    BigDecimal totalByTypeSince(@Param("accountId") Long accountId, @Param("type") BankTransaction.Type type, @Param("since") Instant since);
}
