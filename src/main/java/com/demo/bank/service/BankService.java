package com.demo.bank.service;

import com.demo.bank.constant.Status;
import com.demo.bank.model.entity.BankAccountsEntity;
import com.demo.bank.model.entity.BankBranchesEntity;
import com.demo.bank.model.request.OpenBankAccountRequest;
import com.demo.bank.model.response.BankResponseOpenBankAccount;
import com.demo.bank.model.response.CommonResponse;
import com.demo.bank.model.response.ErrorResponse;
import com.demo.bank.repository.BankAccountsRepository;
import com.demo.bank.repository.BankBranchesRepository;
import com.demo.bank.repository.BankTransactionsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(BankService.class);

    @Autowired
    private BankBranchesRepository bankBranchesRepository;
    @Autowired
    private BankAccountsRepository bankAccountsRepository;
    @Autowired
    private BankTransactionsRepository bankTransactionsRepository;

    public CommonResponse openBankAccount(OpenBankAccountRequest request){
        CommonResponse commonResponse = new CommonResponse();
        BankBranchesEntity findBranch = bankBranchesRepository.findAllByBranchName(request.getBranchName());
        if (findBranch!=null) {
            logger.info("BRANCH FOUND");
            String accountNumber;
            while (true) {
                accountNumber = generateAccountNumber();
                BankAccountsEntity findAccountNumber = bankAccountsRepository.findAllByAccountNumber(accountNumber);
                if (findAccountNumber == null) {
                    break;
                }
            }
            BankAccountsEntity bankAccountsEntity = prepareBankAccountsEntity(request, findBranch.getBranchId(), accountNumber);

            BankAccountsEntity saveResult = bankAccountsRepository.save(bankAccountsEntity);

            if (saveResult != null) {
                logger.info("OPEN BANK ACCOUNT SUCCESSFULLY");
                commonResponse.setStatus(Status.SUCCESS.getValue());
                BankResponseOpenBankAccount responseOpenBankAccount = new BankResponseOpenBankAccount();
                responseOpenBankAccount.setAccountName(saveResult.getAccountName());
                responseOpenBankAccount.setAccountNumber(saveResult.getAccountNumber());
                responseOpenBankAccount.setBranchId(saveResult.getAccountBranchId());
                responseOpenBankAccount.setBranchName(findBranch.getBranchName());
                commonResponse.setData(responseOpenBankAccount);
                commonResponse.setHttpStatus(HttpStatus.CREATED);
            }else{
                logger.error("OPEN BANK ACCOUNT UNSUCCESSFULLY");
                commonResponse.setStatus(Status.ERROR.getValue());
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setError("OPEN BANK ACCOUNT UNSUCCESSFULLY");
                commonResponse.setData(errorResponse);
                commonResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            logger.error("BRANCH NOT FOUND");
            commonResponse.setStatus(Status.NOT_FOUND.getValue());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError("BRANCH NOT FOUND");
            commonResponse.setData(errorResponse);
            commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        }
        return commonResponse;
    }

    private BankAccountsEntity prepareBankAccountsEntity(OpenBankAccountRequest request, Integer branchId, String accountNumber) {
        BankAccountsEntity bankAccountsEntity = new BankAccountsEntity();
        UUID id = UUID.randomUUID();
        bankAccountsEntity.setAccountId(id);
        bankAccountsEntity.setAccountBranchId(branchId);
        bankAccountsEntity.setAccountNumber(accountNumber);
        bankAccountsEntity.setAccountName(request.getName());
        bankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        bankAccountsEntity.setAccountStatus("ACTIVATED");
        Date date = Calendar.getInstance().getTime();
        bankAccountsEntity.setAccountCreatedDate(date);
        bankAccountsEntity.setAccountUpdatedDate(date);
        return bankAccountsEntity;
    }

    private String generateAccountNumber() {
        Random random = new Random();
        String stringAccountNumber = "";
        for (int i = 1; i <= 10; i++) {
            int randomAccountNumber = random.nextInt(10);
            stringAccountNumber += randomAccountNumber;
        }
        return stringAccountNumber;
    }
}
