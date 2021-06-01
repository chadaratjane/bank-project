package com.demo.bank.repository;

import com.demo.bank.model.entity.BankBranchesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankBranchesRepository extends JpaRepository<BankBranchesEntity, Integer> {

    BankBranchesEntity findAllByBranchName(String branchName);
}


