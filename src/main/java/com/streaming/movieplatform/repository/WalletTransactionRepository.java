package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.WalletTransaction;
import com.streaming.movieplatform.enums.TransactionStatus;
import com.streaming.movieplatform.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<WalletTransaction> findTop10ByOrderByCreatedAtDesc();
    List<WalletTransaction> findByStatusAndTypeOrderByCreatedAtDesc(TransactionStatus status, TransactionType type);
}
