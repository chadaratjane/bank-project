package com.demo.bank.service;

import com.demo.bank.model.entity.BankAccountsEntity;
import com.demo.bank.model.entity.BankBranchesEntity;
import com.demo.bank.model.request.BankRequestOpenBankAccount;
import com.demo.bank.model.response.BankResponseOpenBankAccount;
import com.demo.bank.model.response.CommonResponse;
import com.demo.bank.repository.BankAccountsRepository;
import com.demo.bank.repository.BankBranchesRepository;
import com.demo.bank.repository.BankTransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class BankService {

    @Autowired
    private BankBranchesRepository bankBranchesRepository;
    @Autowired
    private BankAccountsRepository bankAccountsRepository;
    @Autowired
    private BankTransactionsRepository bankTransactionsRepository;

    public CommonResponse openBankAccount(BankRequestOpenBankAccount request){

        BankAccountsEntity bankAccountsEntity = new BankAccountsEntity();
        UUID id = UUID.randomUUID();
        bankAccountsEntity.setAccountId(id);

        BankBranchesEntity findBranch = bankBranchesRepository.findAllByBranchName(request.getBranchName());
        bankAccountsEntity.setAccountBranchId(findBranch.getBranchId());

        while (true) {
            Random random = new Random();
            Integer[] accountNumber = new Integer[10];
            for (int i = 0; i < accountNumber.length; i++) {
                int randomAccountNumber = random.nextInt(9);
                accountNumber[i] = randomAccountNumber;
            }

            String stringAccountNumber = "";
            for (int num : accountNumber) {
                stringAccountNumber += num;
            }
            BankAccountsEntity findAccountNumber = bankAccountsRepository.findAllByAccountNumber(stringAccountNumber);
            if (findAccountNumber == null) {
                bankAccountsEntity.setAccountNumber(stringAccountNumber);
                break;
            }
        }

        bankAccountsEntity.setAccountName(request.getName());
        bankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        bankAccountsEntity.setAccountStatus("ACTIVATED");
        Date date = Calendar.getInstance().getTime();
        bankAccountsEntity.setAccountCreatedDate(date);
        bankAccountsEntity.setAccountUpdatedDate(date);

        BankAccountsEntity saveResult = bankAccountsRepository.save(bankAccountsEntity);

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setStatus("SUCCESS");
        BankResponseOpenBankAccount responseOpenBankAccount =new BankResponseOpenBankAccount();
        responseOpenBankAccount.setAccountName(saveResult.getAccountName());
        responseOpenBankAccount.setAccountNumber(saveResult.getAccountNumber());
        responseOpenBankAccount.setBranchId(saveResult.getAccountBranchId());
        responseOpenBankAccount.setBranchName(findBranch.getBranchName());
        commonResponse.setData(responseOpenBankAccount);
        commonResponse.setHttpStatus(HttpStatus.CREATED);

        return commonResponse;
    }
}
