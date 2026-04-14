package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.WalletTransaction;
import com.streaming.movieplatform.enums.TransactionStatus;
import com.streaming.movieplatform.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<WalletTransaction> findTop10ByOrderByCreatedAtDesc();
    List<WalletTransaction> findByStatusAndTypeOrderByCreatedAtDesc(TransactionStatus status, TransactionType type);

    @Query("""
            select coalesce(sum(tx.amount), 0)
            from WalletTransaction tx
            where tx.user.id = :userId
              and tx.status = :status
              and tx.type = :type
            """)
    BigDecimal sumAmountByUserIdAndStatusAndType(@Param("userId") Long userId,
                                                 @Param("status") TransactionStatus status,
                                                 @Param("type") TransactionType type);
}
