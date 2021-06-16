package com.demo.bank.repository;

import com.demo.bank.model.entity.BankTransactionsEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface BankTransactionsRepository extends JpaRepository<BankTransactionsEntity, UUID> {

    @Query(value = "select * from bank_transactions where account_id = ?1 " +
            " AND transaction_date > ?2 AND transaction_date < ?3" +
            " ORDER BY ?#{#pageable}",nativeQuery = true)
    List<BankTransactionsEntity> findAllByAccountIdAndDate(UUID accountId, Date dateFrom,
                                                           Date dateTo, Pageable pageable);
}
