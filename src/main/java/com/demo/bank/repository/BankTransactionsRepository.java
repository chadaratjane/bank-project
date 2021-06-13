package com.demo.bank.repository;

import com.demo.bank.model.entity.BankTransactionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankTransactionsRepository extends JpaRepository<BankTransactionsEntity, UUID> {

    List<BankTransactionsEntity> findAllByAccountId(UUID accountId);
}
