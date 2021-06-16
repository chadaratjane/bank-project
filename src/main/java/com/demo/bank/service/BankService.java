package com.demo.bank.service;

import com.demo.bank.constant.AccountStatus;
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
import com.demo.bank.model.response.CloseBankAccountResponse;
import com.demo.bank.model.response.CommonResponse;
import com.demo.bank.model.response.ErrorResponse;
import com.demo.bank.model.response.GetAllBankAccountResponse;
import com.demo.bank.model.response.GetAllTransactionResponse;
import com.demo.bank.model.response.OpenBankAccountResponse;
import com.demo.bank.repository.BankAccountsRepository;
import com.demo.bank.repository.BankBranchesRepository;
import com.demo.bank.repository.BankTransactionsRepository;
import com.demo.bank.repository.CustomerInformationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
        BankAccountsEntity bankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(request.getAccountNumber(), AccountStatus.ACTIVATED.getValue());
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
        BankAccountsEntity bankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(request.getAccountNumber(), AccountStatus.ACTIVATED.getValue());
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
        BankAccountsEntity senderBankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(request.getSenderAccountNumber(), AccountStatus.ACTIVATED.getValue());
        CommonResponse commonResponse = new CommonResponse();
        if (senderBankAccountsEntity != null) {
            BankAccountsEntity receiverBankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(request.getReceiverAccountNumber(), AccountStatus.ACTIVATED.getValue());
            if (receiverBankAccountsEntity != null) {
                logger.info("FOUND SENDER AND RECEIVER BANK ACCOUNT");
                BankTransactionsEntity bankTransactionsEntity = prepareTransferTransactionEntity(request, senderBankAccountsEntity, receiverBankAccountsEntity);

                BankTransactionsEntity saveEntity = bankTransactionsRepository.save(bankTransactionsEntity);

                logger.info("SAVED BANK TRANSACTION SUCCESSFULLY");
                BigDecimal senderAccountBalance = senderBankAccountsEntity.getAccountBalance();
                BigDecimal updatedSenderAccountBalance = senderAccountBalance.subtract(saveEntity.getTransactionAmount());
                bankAccountsRepository.save(updateBankAccountsEntity(senderBankAccountsEntity, updatedSenderAccountBalance));

                BigDecimal receiverAccountBalance = receiverBankAccountsEntity.getAccountBalance();
                BigDecimal updatedReceiverAccountBalance = receiverAccountBalance.add(saveEntity.getTransactionAmount());
                bankAccountsRepository.save(updateBankAccountsEntity(receiverBankAccountsEntity, updatedReceiverAccountBalance));

                BankTransferResponse bankTransferResponse = getBankTransferResponse(senderBankAccountsEntity, receiverBankAccountsEntity, saveEntity, updatedSenderAccountBalance);
                commonResponse.setStatus(Status.SUCCESS.getValue());
                commonResponse.setData(bankTransferResponse);
                commonResponse.setHttpStatus(HttpStatus.CREATED);
                logger.info("TRANSFERRED AND UPDATED BANK ACCOUNT SUCCESSFULLY");

            } else {
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

    public CommonResponse closeBankAccount(String accountNumber) {
        BankAccountsEntity bankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(accountNumber, AccountStatus.ACTIVATED.getValue());
        CommonResponse commonResponse = new CommonResponse();

        if (bankAccountsEntity != null) {
            logger.info("BANK ACCOUNT FOUND");
            BankAccountsEntity entity = prepareCloseBankAccountsEntity(bankAccountsEntity);

            BankAccountsEntity saveEntity = bankAccountsRepository.save(entity);

            logger.info("CLOSE BANK ACCOUNT SUCCESSFULLY");
            commonResponse.setStatus(Status.SUCCESS.getValue());
            CloseBankAccountResponse closeBankAccountResponse = new CloseBankAccountResponse();
            closeBankAccountResponse.setAccountName(saveEntity.getAccountName());
            closeBankAccountResponse.setAccountNumber(saveEntity.getAccountNumber());
            BankBranchesEntity findBranchName = bankBranchesRepository.findAllByBranchId(saveEntity.getAccountBranchId());
            closeBankAccountResponse.setBranchName(findBranchName.getBranchName());
            closeBankAccountResponse.setAccountStatus(saveEntity.getAccountStatus());
            commonResponse.setData(closeBankAccountResponse);
            commonResponse.setHttpStatus(HttpStatus.OK);

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

    public CommonResponse getAllBankAccount() {
        List<BankAccountsEntity> bankAccountsEntityList = bankAccountsRepository.findAllByAccountStatus(AccountStatus.ACTIVATED.getValue());
        ArrayList<GetAllBankAccountResponse> list = new ArrayList<>();
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setStatus(Status.SUCCESS.getValue());
        commonResponse.setHttpStatus(HttpStatus.OK);
        if (CollectionUtils.isEmpty(bankAccountsEntityList)) {
            logger.info("NO BANK ACCOUNT TO RETRIEVE");
            commonResponse.setData(new ArrayList<GetAllBankAccountResponse>());

        } else {
            logger.info("RETRIEVE BANK ACCOUNT SUCCESSFULLY");
            for (BankAccountsEntity tran : bankAccountsEntityList) {
                GetAllBankAccountResponse item = new GetAllBankAccountResponse();
                item.setAccountName(tran.getAccountName());
                item.setAccountNumber(tran.getAccountNumber());
                BankBranchesEntity bankBranchesEntity = bankBranchesRepository.findAllByBranchId(tran.getAccountBranchId());
                item.setBranchName(bankBranchesEntity.getBranchName());
                item.setAccountBalance(tran.getAccountBalance());
                list.add(item);
            }
            commonResponse.setData(list);
        }
        return commonResponse;
    }

    public CommonResponse getAllTransaction(String accountNumber ,Date dateFrom, Date dateTo, String sort){
    BankAccountsEntity bankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(accountNumber,AccountStatus.ACTIVATED.getValue());
    CommonResponse commonResponse = new CommonResponse();
    if (bankAccountsEntity != null){
        logger.info("BANK ACCOUNT FOUND");

        Direction sortMethod ;
        if ("ASC".equalsIgnoreCase(sort)) {
            sortMethod = Direction.ASC;
        }else {
            sortMethod = Direction.DESC;
        }
        Pageable page =  PageRequest.of(0,20000, sortMethod,"transaction_date");

        List<BankTransactionsEntity> getAccountBetweenDateRange = bankTransactionsRepository.findAllByAccountIdAndDate(bankAccountsEntity.getAccountId(),dateFrom,addingDate(dateTo,1),page);

        ArrayList<GetAllTransactionResponse> list = new ArrayList<>();
        commonResponse.setStatus(Status.SUCCESS.getValue());
        commonResponse.setHttpStatus(HttpStatus.OK);
        if (CollectionUtils.isEmpty(getAccountBetweenDateRange)){
            logger.info("NO TRANSACTION TO RETRIEVE");
            commonResponse.setData(new ArrayList<GetAllTransactionResponse>());

        }else{
            logger.info("RETRIEVE TRANSACTION SUCCESSFULLY");
            for (BankTransactionsEntity tran : getAccountBetweenDateRange) {
                GetAllTransactionResponse item = new GetAllTransactionResponse();
                item.setTransactionDate(tran.getTransactionDate());
                item.setAmount(tran.getTransactionAmount());
                item.setTransactionType(tran.getTransactionType());
                list.add(item);

            }
        }
        commonResponse.setData(list);

    }else {
        logger.info("BANK ACCOUNT NOT FOUND");
        commonResponse.setStatus(Status.NOT_FOUND.getValue());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("BANK ACCOUNT NOT FOUND");
        commonResponse.setData(errorResponse);
        commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);

    }
        return commonResponse;
    }

    private BankTransferResponse getBankTransferResponse(BankAccountsEntity senderBankAccountsEntity, BankAccountsEntity receiverBankAccountsEntity, BankTransactionsEntity saveEntity, BigDecimal updatedSenderAccountBalance) {
        BankTransferResponse bankTransferResponse = new BankTransferResponse();
        bankTransferResponse.setSenderAccountNumber(senderBankAccountsEntity.getAccountNumber());
        bankTransferResponse.setReceiverAccountNumber(receiverBankAccountsEntity.getAccountNumber());
        bankTransferResponse.setAmount(saveEntity.getTransactionAmount());
        bankTransferResponse.setSenderAccountBalance(updatedSenderAccountBalance);
        bankTransferResponse.setTransactionDate(saveEntity.getTransactionDate());
        return bankTransferResponse;
    }

    private BankAccountsEntity prepareCloseBankAccountsEntity(BankAccountsEntity bankAccountsEntity) {
        BankAccountsEntity entity = new BankAccountsEntity();
        entity.setAccountId(bankAccountsEntity.getAccountId());
        entity.setAccountBranchId(bankAccountsEntity.getAccountBranchId());
        entity.setAccountNumber(bankAccountsEntity.getAccountNumber());
        entity.setAccountName(bankAccountsEntity.getAccountName());
        entity.setAccountBalance(bankAccountsEntity.getAccountBalance());
        entity.setAccountStatus(AccountStatus.DEACTIVATED.getValue());
        entity.setAccountCreatedDate(bankAccountsEntity.getAccountCreatedDate());
        entity.setAccountUpdatedDate(Calendar.getInstance().getTime());
        return entity;
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
        bankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
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

    private Date addingDate(Date date, int amount){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, amount);
        Date result = c.getTime();
    return result;
    }


}
