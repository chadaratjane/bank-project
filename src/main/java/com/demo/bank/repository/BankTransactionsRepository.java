package com.demo.bank.repository;

import com.demo.bank.model.entity.BankTransactionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BankTransactionsRepository extends JpaRepository<BankTransactionsEntity, UUID> {
}
