package com.demo.bank.service;

import com.demo.bank.constant.AccountStatus;
import com.demo.bank.constant.Status;
import com.demo.bank.constant.TransactionType;
import com.demo.bank.exception.ValidateException;
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
import com.demo.bank.model.response.GetAllTransactionContentsResponse;
import com.demo.bank.model.response.GetAllTransactionPageResponse;
import com.demo.bank.model.response.OpenBankAccountResponse;
import com.demo.bank.repository.BankAccountsRepository;
import com.demo.bank.repository.BankBranchesRepository;
import com.demo.bank.repository.BankTransactionsRepository;
import com.demo.bank.repository.CustomerInformationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    @Value("${openaccount.random.max.attempt}")
    private Integer openAccountAttempt;

    private static final String ACCOUNT_NOTFOUND_ERROR = "BANK ACCOUNT NOT FOUND OR INVALID ACCOUNT STATUS";

    @Transactional(rollbackFor = {Exception.class})
    public CommonResponse openBankAccount(OpenBankAccountRequest request) throws ParseException {
        BankBranchesEntity findBranch = bankBranchesRepository.findAllByBranchName(request.getBranchName());
        CommonResponse commonResponse = new CommonResponse();

        if (findBranch != null) {
            logger.info("BRANCH FOUND");
            String accountNumber = generateAccountNumber();
            boolean errorDuplicate = true;
            for (int i = 0; i < openAccountAttempt-1; i++) {
                BankAccountsEntity findAccountNumber = bankAccountsRepository.findAllByAccountNumber(accountNumber);
                if (findAccountNumber == null) {
                    errorDuplicate = false;
                    break;
                }
                accountNumber = generateAccountNumber();
            }
            if (errorDuplicate) {
                logger.error("GENERATE ACCOUNT NUMBER FAILED, DUPLICATE VALUE");
                CommonResponse errorResponse = new CommonResponse();
                errorResponse.setStatus(Status.ERROR.getValue());
                ErrorResponse error = new ErrorResponse();
                error.setError("ERROR");
                errorResponse.setData(error);
                errorResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return errorResponse;
            } else {
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
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                Date dob = formatter.parse(request.getDateOfBirth());
                customerInformationEntity.setCustomerDateOfBirth(dob);
                customerInformationEntity.setCustomerAddress(request.getAddress());
                customerInformationRepository.save(customerInformationEntity);
            }
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

    @Transactional(rollbackFor = {Exception.class})
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
            bankTransactionResponse.setTransactionDate(saveEntity.getTransactionDate());
            commonResponse.setData(bankTransactionResponse);
            commonResponse.setHttpStatus(HttpStatus.CREATED);

            bankAccountsRepository.save(updateBankAccountsEntity(bankAccountsEntity, updatedAccountBalance));
            logger.info("UPDATE BANK ACCOUNT SUCCESSFULLY");

        } else {
            logger.error("BANK ACCOUNT NOT FOUND OR INVALID ACCOUNT STATUS");
            commonResponse.setStatus(Status.NOT_FOUND.getValue());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(ACCOUNT_NOTFOUND_ERROR);
            commonResponse.setData(errorResponse);

            commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        }
        return commonResponse;
    }

    @Transactional(rollbackFor = {Exception.class})
    public CommonResponse withdrawTransaction(BankTransactionRequest request) {
        BankAccountsEntity bankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(request.getAccountNumber(), AccountStatus.ACTIVATED.getValue());

        CommonResponse commonResponse = new CommonResponse();
        if (bankAccountsEntity != null) {
            logger.info("BANK ACCOUNT FOUND");
            if (request.getAmount().compareTo(bankAccountsEntity.getAccountBalance())>0){
            throw new ValidateException("WITHDRAW AMOUNT IS OVER ACCOUNT BALANCE","INSUFFICIENT ACCOUNT BALANCE");
            }

            BankTransactionsEntity bankTransactionsEntity = prepareWithdrawTransactionEntity(request, bankAccountsEntity);

            BankTransactionsEntity saveEntity = bankTransactionsRepository.save(bankTransactionsEntity);

            logger.info("WITHDRAW TRANSACTION SUCCESSFULLY");
            BigDecimal accountBalance = bankAccountsEntity.getAccountBalance();
            BigDecimal updatedAccountBalance = accountBalance.subtract(saveEntity.getTransactionAmount());

            bankAccountsRepository.save(updateBankAccountsEntity(bankAccountsEntity, updatedAccountBalance));
            logger.info("UPDATE BANK ACCOUNT SUCCESSFULLY");

            commonResponse.setStatus(Status.SUCCESS.getValue());
            BankTransactionResponse bankTransactionResponse = new BankTransactionResponse();
            bankTransactionResponse.setAccountName(bankAccountsEntity.getAccountName());
            bankTransactionResponse.setAccountNumber(bankAccountsEntity.getAccountNumber());
            bankTransactionResponse.setAmount(saveEntity.getTransactionAmount());
            bankTransactionResponse.setAccountBalance(updatedAccountBalance);
            bankTransactionResponse.setTransactionDate(saveEntity.getTransactionDate());
            commonResponse.setData(bankTransactionResponse);
            commonResponse.setHttpStatus(HttpStatus.CREATED);

        } else {
            logger.error("BANK ACCOUNT NOT FOUND OR INVALID ACCOUNT STATUS");
            commonResponse.setStatus(Status.NOT_FOUND.getValue());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(ACCOUNT_NOTFOUND_ERROR);
            commonResponse.setData(errorResponse);
            commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        }

        return commonResponse;
    }

    @Transactional(rollbackFor = {Exception.class})
    public CommonResponse transferTransaction(BankTransferRequest request) {
        if (request.getSenderAccountNumber().equals(request.getReceiverAccountNumber())){
            throw new ValidateException("DUPLICATE SENDER AND RECEIVER BANK ACCOUNT","DUPLICATE SENDER AND RECEIVER BANK ACCOUNT");
        }
        BankAccountsEntity senderBankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(request.getSenderAccountNumber(), AccountStatus.ACTIVATED.getValue());
        CommonResponse commonResponse = new CommonResponse();
        if (senderBankAccountsEntity != null) {
            if (request.getAmount().compareTo(senderBankAccountsEntity.getAccountBalance())>0){
                throw new ValidateException("TRANSFER AMOUNT IS OVER ACCOUNT BALANCE","INSUFFICIENT ACCOUNT BALANCE");
            }

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

    @Transactional(rollbackFor = {Exception.class})
    public CommonResponse closeBankAccount(String accountNumber) {
        BankAccountsEntity bankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(accountNumber, AccountStatus.ACTIVATED.getValue());
        CommonResponse commonResponse = new CommonResponse();

        if (bankAccountsEntity != null) {
            logger.info("BANK ACCOUNT FOUND");
            BigDecimal receivedBalance = bankAccountsEntity.getAccountBalance();

            if (receivedBalance.compareTo(BigDecimal.ZERO) > 0) {
                BankTransactionsEntity bankTransactionsEntity = new BankTransactionsEntity();
                bankTransactionsEntity.setTransactionId(UUID.randomUUID());
                bankTransactionsEntity.setAccountId(bankAccountsEntity.getAccountId());
                bankTransactionsEntity.setTransactionAmount(receivedBalance);
                bankTransactionsEntity.setTransactionType(TransactionType.WITHDRAW.getValue());
                bankTransactionsEntity.setTransactionDate(Calendar.getInstance().getTime());
                bankTransactionsRepository.save(bankTransactionsEntity);
                logger.info("WITHDRAW ACCOUNT BALANCE SUCCESSFULLY");

            }

            BankAccountsEntity entity = prepareCloseBankAccountsEntity(bankAccountsEntity);

            BankAccountsEntity saveEntity = bankAccountsRepository.save(entity);

            logger.info("CLOSE BANK ACCOUNT SUCCESSFULLY");
            commonResponse.setStatus(Status.SUCCESS.getValue());
            CloseBankAccountResponse closeBankAccountResponse = new CloseBankAccountResponse();
            closeBankAccountResponse.setAccountName(saveEntity.getAccountName());
            closeBankAccountResponse.setAccountNumber(saveEntity.getAccountNumber());
            BankBranchesEntity findBranchName = bankBranchesRepository.findAllByBranchId(saveEntity.getAccountBranchId());
            closeBankAccountResponse.setBranchName(findBranchName.getBranchName());
            closeBankAccountResponse.setAccountBalance(receivedBalance);
            closeBankAccountResponse.setAccountStatus(saveEntity.getAccountStatus());
            commonResponse.setData(closeBankAccountResponse);
            commonResponse.setHttpStatus(HttpStatus.OK);

        } else {
            logger.error("BANK ACCOUNT NOT FOUND OR INVALID ACCOUNT STATUS");
            commonResponse.setStatus(Status.NOT_FOUND.getValue());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(ACCOUNT_NOTFOUND_ERROR);
            commonResponse.setData(errorResponse);
            commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);

        }
        return commonResponse;
    }

    public CommonResponse getAllBankAccount(String accountStatus) {

        List<BankAccountsEntity> bankAccountsEntityList;
        if ("ALL".equals(accountStatus)) {

            bankAccountsEntityList = bankAccountsRepository.findAll();
        } else {

            bankAccountsEntityList = bankAccountsRepository.findAllByAccountStatus(accountStatus);
        }

        ArrayList<GetAllBankAccountResponse> list = new ArrayList<>();
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setStatus(Status.SUCCESS.getValue());
        commonResponse.setHttpStatus(HttpStatus.OK);
        if (CollectionUtils.isEmpty(bankAccountsEntityList)) {
            logger.error("NO BANK ACCOUNT TO RETRIEVE");
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
                item.setAccountStatus(tran.getAccountStatus());
                list.add(item);
            }
            commonResponse.setData(list);
        }
        return commonResponse;
    }

    public CommonResponse getAllTransaction(String accountNumber, Date dateFrom, Date dateTo, String sort, Integer pageNumber, Integer perPage) {
        BankAccountsEntity bankAccountsEntity = bankAccountsRepository.findAllByAccountNumberAndAccountStatus(
                accountNumber, AccountStatus.ACTIVATED.getValue());
        CommonResponse commonResponse = new CommonResponse();
        if (bankAccountsEntity != null) {
            logger.info("BANK ACCOUNT FOUND");

            Direction sortMethod;
            if ("ASC".equalsIgnoreCase(sort)) {
                sortMethod = Direction.ASC;
            } else {
                sortMethod = Direction.DESC;
            }

            Pageable page = PageRequest.of(pageNumber - 1, perPage, sortMethod, "transaction_date");

            Page<BankTransactionsEntity> pageResult = bankTransactionsRepository.findAllByAccountIdAndDate(
                    bankAccountsEntity.getAccountId(), dateFrom, addingDate(dateTo, 1), page);

            ArrayList<GetAllTransactionContentsResponse> list = new ArrayList<>();
            commonResponse.setStatus(Status.SUCCESS.getValue());
            commonResponse.setHttpStatus(HttpStatus.OK);
            if (!pageResult.hasContent()) {
                logger.error("NO TRANSACTION TO RETRIEVE");
                GetAllTransactionPageResponse getAllTransactionPageResponse = new GetAllTransactionPageResponse();
                getAllTransactionPageResponse.setTotalItem(Long.valueOf(pageResult.getTotalElements()).intValue());
                getAllTransactionPageResponse.setTotalPage(pageResult.getTotalPages());
                getAllTransactionPageResponse.setCurrentPage(pageResult.getNumber()+1);
                getAllTransactionPageResponse.setContents(new ArrayList<>());
                commonResponse.setData(getAllTransactionPageResponse);

            } else {
                logger.info("RETRIEVE TRANSACTION SUCCESSFULLY");
                GetAllTransactionPageResponse getAllTransactionPageResponse = new GetAllTransactionPageResponse();
                getAllTransactionPageResponse.setTotalItem(Long.valueOf(pageResult.getTotalElements()).intValue());
                getAllTransactionPageResponse.setTotalPage(pageResult.getTotalPages());
                getAllTransactionPageResponse.setCurrentPage(pageResult.getNumber()+1);

                for (BankTransactionsEntity tran : pageResult.getContent()) {
                    GetAllTransactionContentsResponse item = new GetAllTransactionContentsResponse();
                    item.setTransactionDate(tran.getTransactionDate());
                    item.setAmount(tran.getTransactionAmount());
                    item.setTransactionType(tran.getTransactionType());
                    if (tran.getTransactionType().equals(TransactionType.TRANSFER.getValue())) {
                        Optional<BankAccountsEntity> bankAccountResultOptional = bankAccountsRepository.findById(tran.getTransactionAccountIdTo());
                        if (bankAccountResultOptional.isPresent()) {
                            BankAccountsEntity bankAccountResult = bankAccountResultOptional.get();
                            item.setReceiverAccountNumber(bankAccountResult.getAccountNumber());
                            item.setReceiverAccountName(bankAccountResult.getAccountName());
                        }
                    }
                    list.add(item);
                }
                getAllTransactionPageResponse.setContents(list);
                commonResponse.setData(getAllTransactionPageResponse);
            }

        } else {
            logger.error("BANK ACCOUNT NOT FOUND OR INVALID ACCOUNT STATUS");
            commonResponse.setStatus(Status.NOT_FOUND.getValue());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(ACCOUNT_NOTFOUND_ERROR);
            commonResponse.setData(errorResponse);
            commonResponse.setHttpStatus(HttpStatus.NOT_FOUND);

    }
        return commonResponse;
    }

    private BankTransferResponse getBankTransferResponse(BankAccountsEntity senderBankAccountsEntity, BankAccountsEntity receiverBankAccountsEntity, BankTransactionsEntity saveEntity, BigDecimal updatedSenderAccountBalance) {
        BankTransferResponse bankTransferResponse = new BankTransferResponse();
        bankTransferResponse.setSenderAccountName(senderBankAccountsEntity.getAccountName());

        bankTransferResponse.setSenderAccountNumber(senderBankAccountsEntity.getAccountNumber());
        bankTransferResponse.setReceiverAccountName(receiverBankAccountsEntity.getAccountName());
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
        entity.setAccountBalance(BigDecimal.ZERO);
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
        bankTransactionsEntity.setTransactionType(TransactionType.TRANSFER.getValue());
        bankTransactionsEntity.setTransactionDate(Calendar.getInstance().getTime());
        return bankTransactionsEntity;
    }

    private BankTransactionsEntity prepareWithdrawTransactionEntity(BankTransactionRequest request, BankAccountsEntity bankAccountsEntity) {
        BankTransactionsEntity bankTransactionsEntity = new BankTransactionsEntity();
        bankTransactionsEntity.setTransactionId(UUID.randomUUID());
        bankTransactionsEntity.setAccountId(bankAccountsEntity.getAccountId());
        bankTransactionsEntity.setTransactionAmount(request.getAmount());
        bankTransactionsEntity.setTransactionType(TransactionType.WITHDRAW.getValue());
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
        bankTransactionsEntity.setTransactionType(TransactionType.DEPOSIT.getValue());
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
