package com.demo.bank.service;

import com.demo.bank.constant.Status;
import com.demo.bank.model.entity.BankAccountsEntity;
import com.demo.bank.model.entity.BankBranchesEntity;
import com.demo.bank.model.entity.BankTransactionsEntity;
import com.demo.bank.model.entity.CustomerInformationEntity;
import com.demo.bank.model.request.BankTransactionRequest;
import com.demo.bank.model.request.BankTransferRequest;
import com.demo.bank.model.request.OpenBankAccountRequest;
import com.demo.bank.model.response.BankTransactionResponse;
import com.demo.bank.model.response.BankTransferResponse;
import com.demo.bank.model.response.OpenBankAccountResponse;
import com.demo.bank.model.response.CommonResponse;
import com.demo.bank.model.response.ErrorResponse;
import com.demo.bank.repository.BankAccountsRepository;
import com.demo.bank.repository.BankBranchesRepository;
import com.demo.bank.repository.BankTransactionsRepository;
import com.demo.bank.repository.CustomerInformationRepository;
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
    @Autowired
    private CustomerInformationRepository customerInformationRepository;

    public CommonResponse openBankAccount(OpenBankAccountRequest request) {
        BankBranchesEntity findBranch = bankBranchesRepository.findAllByBranchName(request.getBranchName());
        CommonResponse commonResponse = new CommonResponse();

        if (findBranch != null) {
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

            BankAccountsEntity saveEntity = bankAccountsRepository.save(bankAccountsEntity);

            logger.info("OPEN BANK ACCOUNT SUCCESSFULLY");
            commonResponse.setStatus(Status.SUCCESS.getValue());
            OpenBankAccountResponse responseOpenBankAccount = new OpenBankAccountResponse();
            responseOpenBankAccount.setAccountName(saveEntity.getAccountName());
            responseOpenBankAccount.setAccountNumber(saveEntity.getAccountNumber());
            responseOpenBankAccount.setBranchId(saveEntity.getAccountBranchId());
            responseOpenBankAccount.setBranchName(findBranch.getBranchName());
            commonResponse.setData(responseOpenBankAccount);
            commonResponse.setHttpStatus(HttpStatus.CREATED);

            CustomerInformationEntity customerInformationEntity = new CustomerInformationEntity();
            logger.info("SAVE CUSTOMER INFORMATION SUCCESSFULLY");
            UUID id = UUID.randomUUID();
            customerInformationEntity.setCustomerId(id);
            customerInformationEntity.setCustomerName(saveEntity.getAccountName());
            customerInformationEntity.setCustomerDateOfBirth(request.getDateOfBirth());
            customerInformationEntity.setCustomerAddress(request.getAddress());
            customerInformationRepository.save(customerInformationEntity);

        } else {
            logger.error("BRANCH NOT FOUND");
            commonResponse.setStatus(Status.NOT_FOUND.getValue());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError("BRANCH NOT FOUND");
            commonResponse.setData(errorResponse);
            commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        }
        return commonResponse;
    }

    public CommonResponse depositTransaction(BankTransactionRequest request) {
        BankAccountsEntity bankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(request.getAccountNumber(), "ACTIVATED");
        CommonResponse commonResponse = new CommonResponse();
        if (bankAccountsEntity != null) {
            logger.info("BANK ACCOUNT FOUND");
            BankTransactionsEntity bankTransactionsEntity = prepareDepositTransactionsEntity(request, bankAccountsEntity);

            BankTransactionsEntity saveEntity = bankTransactionsRepository.save(bankTransactionsEntity);

            logger.info("DEPOSIT TRANSACTION SUCCESSFULLY");
            commonResponse.setStatus(Status.SUCCESS.getValue());
            BankTransactionResponse bankTransactionResponse = new BankTransactionResponse();
            bankTransactionResponse.setAccountName(bankAccountsEntity.getAccountName());
            bankTransactionResponse.setAccountNumber(bankAccountsEntity.getAccountNumber());
            bankTransactionResponse.setAmount(saveEntity.getTransactionAmount());
            BigDecimal accountBalance = bankAccountsEntity.getAccountBalance();
            BigDecimal updatedAccountBalance = accountBalance.add(saveEntity.getTransactionAmount());
            bankTransactionResponse.setAccountBalance(updatedAccountBalance);
            //TODO **fix time** is not Thailand Time in postman response
            bankTransactionResponse.setTransactionDate(saveEntity.getTransactionDate());
            commonResponse.setData(bankTransactionResponse);
            commonResponse.setHttpStatus(HttpStatus.CREATED);

            bankAccountsRepository.save(updateBankAccountsEntity(bankAccountsEntity, updatedAccountBalance));
            logger.info("UPDATE BANK ACCOUNT SUCCESSFULLY");

        } else {
            logger.error("BANK ACCOUNT NOT FOUND");
            commonResponse.setStatus(Status.NOT_FOUND.getValue());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError("BANK ACCOUNT NOT FOUND");
            commonResponse.setData(errorResponse);
            commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        }
        return commonResponse;
    }

    public CommonResponse withdrawTransaction(BankTransactionRequest request) {
        BankAccountsEntity bankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(request.getAccountNumber(), "ACTIVATED");
        CommonResponse commonResponse = new CommonResponse();
        if (bankAccountsEntity != null) {
            logger.info("BANK ACCOUNT FOUND");
            BankTransactionsEntity bankTransactionsEntity = prepareWithdrawTransactionEntity(request, bankAccountsEntity);

            BankTransactionsEntity saveEntity = bankTransactionsRepository.save(bankTransactionsEntity);

            logger.info("WITHDRAW TRANSACTION SUCCESSFULLY");
            commonResponse.setStatus(Status.SUCCESS.getValue());
            BankTransactionResponse bankTransactionResponse = new BankTransactionResponse();
            bankTransactionResponse.setAccountName(bankAccountsEntity.getAccountName());
            bankTransactionResponse.setAccountNumber(bankAccountsEntity.getAccountNumber());
            bankTransactionResponse.setAmount(saveEntity.getTransactionAmount());
            BigDecimal accountBalance = bankAccountsEntity.getAccountBalance();
            BigDecimal updatedAccountBalance = accountBalance.subtract(saveEntity.getTransactionAmount());
            bankTransactionResponse.setAccountBalance(updatedAccountBalance);
            //TODO **fix time** is not Thailand Time in postman response
            bankTransactionResponse.setTransactionDate(saveEntity.getTransactionDate());
            commonResponse.setData(bankTransactionResponse);
            commonResponse.setHttpStatus(HttpStatus.CREATED);

            bankAccountsRepository.save(updateBankAccountsEntity(bankAccountsEntity, updatedAccountBalance));
            logger.info("UPDATE BANK ACCOUNT SUCCESSFULLY");

        }else {
            logger.error("BANK ACCOUNT NOT FOUND");
            commonResponse.setStatus(Status.NOT_FOUND.getValue());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError("BANK ACCOUNT NOT FOUND");
            commonResponse.setData(errorResponse);
            commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        }

        return commonResponse;
    }

    public CommonResponse transferTransaction(BankTransferRequest request) {
        BankAccountsEntity senderBankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(request.getSenderAccountNumber(), "ACTIVATED");
        CommonResponse commonResponse = new CommonResponse();
        if (senderBankAccountsEntity != null) {
            BankAccountsEntity receiverBankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(request.getReceiverAccountNumber(), "ACTIVATED");
            if (receiverBankAccountsEntity != null) {
                logger.info("FOUND SENDER AND RECEIVER BANK ACCOUNT");
                BankTransactionsEntity bankTransactionsEntity = prepareTransferTransactionEntity(request, senderBankAccountsEntity, receiverBankAccountsEntity);

                BankTransactionsEntity saveEntity = bankTransactionsRepository.save(bankTransactionsEntity);

                logger.info("TRANSFER TRANSACTION SUCCESSFULLY");
                commonResponse.setStatus(Status.SUCCESS.getValue());
                BankTransferResponse bankTransferResponse = new BankTransferResponse();
                bankTransferResponse.setSenderAccountNumber(senderBankAccountsEntity.getAccountNumber());
                bankTransferResponse.setReceiverAccountNumber(receiverBankAccountsEntity.getAccountNumber());
                bankTransferResponse.setAmount(saveEntity.getTransactionAmount());
                BigDecimal senderAccountBalance = senderBankAccountsEntity.getAccountBalance();
                BigDecimal updatedSenderAccountBalance = senderAccountBalance.subtract(saveEntity.getTransactionAmount());
                bankTransferResponse.setSenderAccountBalance(updatedSenderAccountBalance);
                bankTransferResponse.setTransactionDate(saveEntity.getTransactionDate());
                commonResponse.setData(bankTransferResponse);
                commonResponse.setHttpStatus(HttpStatus.CREATED);

                bankAccountsRepository.save(updateBankAccountsEntity(senderBankAccountsEntity, updatedSenderAccountBalance));

                BigDecimal receiverAccountBalance = receiverBankAccountsEntity.getAccountBalance();
                BigDecimal updatedReceiverAccountBalance = receiverAccountBalance.add(saveEntity.getTransactionAmount());
                bankAccountsRepository.save(updateBankAccountsEntity(receiverBankAccountsEntity, updatedReceiverAccountBalance));

                logger.info("UPDATE BANK ACCOUNT SUCCESSFULLY");

            }else {
                logger.error("RECEIVER BANK ACCOUNT NOT FOUND");
                commonResponse.setStatus(Status.NOT_FOUND.getValue());
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setError("RECEIVER BANK ACCOUNT NOT FOUND");
                commonResponse.setData(errorResponse);
                commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);

            }

        } else {
            logger.error("SENDER BANK ACCOUNT NOT FOUND");
            commonResponse.setStatus(Status.NOT_FOUND.getValue());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError("SENDER BANK ACCOUNT NOT FOUND");
            commonResponse.setData(errorResponse);
            commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);

        }
        return commonResponse;
    }

    private BankTransactionsEntity prepareTransferTransactionEntity(BankTransferRequest request, BankAccountsEntity senderBankAccountsEntity, BankAccountsEntity receiverBankAccountsEntity) {
        BankTransactionsEntity bankTransactionsEntity = new BankTransactionsEntity();
        bankTransactionsEntity.setTransactionId(UUID.randomUUID());
        bankTransactionsEntity.setAccountId(senderBankAccountsEntity.getAccountId());
        bankTransactionsEntity.setTransactionAccountIdTo(receiverBankAccountsEntity.getAccountId());
        bankTransactionsEntity.setTransactionAmount(request.getAmount());
        bankTransactionsEntity.setTransactionType("TRANSFER");
        bankTransactionsEntity.setTransactionDate(Calendar.getInstance().getTime());
        return bankTransactionsEntity;
    }

    private BankTransactionsEntity prepareWithdrawTransactionEntity(BankTransactionRequest request, BankAccountsEntity bankAccountsEntity) {
        BankTransactionsEntity bankTransactionsEntity = new BankTransactionsEntity();
        bankTransactionsEntity.setTransactionId(UUID.randomUUID());
        bankTransactionsEntity.setAccountId(bankAccountsEntity.getAccountId());
        bankTransactionsEntity.setTransactionAmount(request.getAmount());
        bankTransactionsEntity.setTransactionType("WITHDRAW");
        bankTransactionsEntity.setTransactionDate(Calendar.getInstance().getTime());
        return bankTransactionsEntity;
    }

    private BankAccountsEntity updateBankAccountsEntity(BankAccountsEntity accountsEntity, BigDecimal updatedAccountBalance) {
        BankAccountsEntity bankAccountsEntity = new BankAccountsEntity();
        bankAccountsEntity.setAccountId(accountsEntity.getAccountId());
        bankAccountsEntity.setAccountBranchId(accountsEntity.getAccountBranchId());
        bankAccountsEntity.setAccountNumber(accountsEntity.getAccountNumber());
        bankAccountsEntity.setAccountName(accountsEntity.getAccountName());
        bankAccountsEntity.setAccountBalance(updatedAccountBalance);
        bankAccountsEntity.setAccountStatus(accountsEntity.getAccountStatus());
        bankAccountsEntity.setAccountCreatedDate(accountsEntity.getAccountCreatedDate());
        bankAccountsEntity.setAccountUpdatedDate(Calendar.getInstance().getTime());
        return bankAccountsEntity;
    }

    private BankTransactionsEntity prepareDepositTransactionsEntity(BankTransactionRequest request, BankAccountsEntity bankAccountsEntity) {
        BankTransactionsEntity bankTransactionsEntity = new BankTransactionsEntity();
        bankTransactionsEntity.setTransactionId(UUID.randomUUID());
        bankTransactionsEntity.setAccountId(bankAccountsEntity.getAccountId());
        bankTransactionsEntity.setTransactionAmount(request.getAmount());
        bankTransactionsEntity.setTransactionType("DEPOSIT");
        bankTransactionsEntity.setTransactionDate(Calendar.getInstance().getTime());
        return bankTransactionsEntity;
    }


    private BankAccountsEntity prepareBankAccountsEntity(OpenBankAccountRequest request, Integer branchId, String accountNumber) {
        BankAccountsEntity bankAccountsEntity = new BankAccountsEntity();
        bankAccountsEntity.setAccountId(UUID.randomUUID());
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
