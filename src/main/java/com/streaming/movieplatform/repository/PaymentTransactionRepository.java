package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findAllByOrderByCreatedAtDesc();
    Optional<PaymentTransaction> findByExternalReference(String externalReference);
}
