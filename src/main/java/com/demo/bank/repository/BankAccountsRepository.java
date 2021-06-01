package com.demo.bank.repository;

import com.demo.bank.model.entity.BankAccountsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BankAccountsRepository extends JpaRepository<BankAccountsEntity, UUID> {

    BankAccountsEntity findAllByAccountNumber(String accountNumber);
}
