package com.demo.bank.repository;

import com.demo.bank.model.entity.BankTransactionsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface BankTransactionsRepository extends JpaRepository<BankTransactionsEntity, UUID> {

    @Query(value = "select * from bank_transactions where account_id = ?1 " +
            " AND transaction_date > ?2 AND transaction_date < ?3",nativeQuery = true)
    Page<BankTransactionsEntity> findAllByAccountIdAndDate (UUID accountId, Date dateFrom,
                                                           Date dateTo, Pageable pageable);

}
