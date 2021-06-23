package com.demo.bank.Service;

import com.demo.bank.constant.AccountStatus;
import com.demo.bank.model.entity.BankAccountsEntity;
import com.demo.bank.model.entity.BankBranchesEntity;
import com.demo.bank.model.entity.BankTransactionsEntity;
import com.demo.bank.model.request.BankTransactionRequest;
import com.demo.bank.model.request.BankTransferRequest;
import com.demo.bank.model.request.OpenBankAccountRequest;
import com.demo.bank.model.response.BankTransactionResponse;
import com.demo.bank.model.response.BankTransferResponse;
import com.demo.bank.model.response.CloseBankAccountResponse;
import com.demo.bank.model.response.CommonResponse;
import com.demo.bank.model.response.ErrorResponse;
import com.demo.bank.model.response.GetAllBankAccountResponse;
import com.demo.bank.model.response.OpenBankAccountResponse;
import com.demo.bank.repository.BankAccountsRepository;
import com.demo.bank.repository.BankBranchesRepository;
import com.demo.bank.repository.BankTransactionsRepository;
import com.demo.bank.repository.CustomerInformationRepository;
import com.demo.bank.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


@ExtendWith(MockitoExtension.class)
public class BankServiceTest {

    @InjectMocks
    private BankService bankService;

    @Mock
    private BankAccountsRepository bankAccountsRepository;
    @Mock
    private BankBranchesRepository bankBranchesRepository;
    @Mock
    private BankTransactionsRepository bankTransactionsRepository;
    @Mock
    private CustomerInformationRepository customerInformationRepository;

    @BeforeEach
    public void init(){
        ReflectionTestUtils.setField(bankService,"openAccountAttempt",3);
    }

    @Test
    public void success_openBankAccount() {

        BankBranchesEntity bankBranchesEntity = new BankBranchesEntity();
        Integer resultBranchId = new Random().nextInt(10);
        bankBranchesEntity.setBranchId(resultBranchId);
        bankBranchesEntity.setBranchName("mockBranchName");
        Mockito.when(bankBranchesRepository.findAllByBranchName("mockBranchName")).thenReturn(bankBranchesEntity);

        BankAccountsEntity expectedResult = new BankAccountsEntity();
            expectedResult.setAccountId(UUID.randomUUID());
            expectedResult.setAccountBranchId(bankBranchesEntity.getBranchId());
            expectedResult.setAccountNumber("0123456789");
            expectedResult.setAccountName("mockAccountName");
            expectedResult.setAccountBalance(BigDecimal.ZERO);
            expectedResult.setAccountStatus(AccountStatus.ACTIVATED.getValue());
            Date date = Calendar.getInstance().getTime();
            expectedResult.setAccountCreatedDate(date);
            expectedResult.setAccountUpdatedDate(date);

        Mockito.when(bankAccountsRepository.save(any())).thenReturn(expectedResult);

        OpenBankAccountRequest openBankAccountRequest = new OpenBankAccountRequest();
        openBankAccountRequest.setName("mockAccountName");
        openBankAccountRequest.setBranchName(bankBranchesEntity.getBranchName());

        CommonResponse commonResponse = bankService.openBankAccount(openBankAccountRequest);

        OpenBankAccountResponse openBankAccountResponse = (OpenBankAccountResponse) commonResponse.getData();

        assertEquals(expectedResult.getAccountName(),openBankAccountResponse.getAccountName());
        assertEquals(expectedResult.getAccountNumber(),openBankAccountResponse.getAccountNumber());
        assertEquals(resultBranchId,openBankAccountResponse.getBranchId());
        assertEquals("SUCCESS",commonResponse.getStatus());
        assertEquals(HttpStatus.CREATED,commonResponse.getHttpStatus());

    }

    @Test
    public void fail_openBankAccount_duplicateAccountNumber(){

        BankBranchesEntity bankBranchesEntity = new BankBranchesEntity();
        Integer resultBranchId = new Random().nextInt(10);
        bankBranchesEntity.setBranchId(resultBranchId);
        bankBranchesEntity.setBranchName("mockBranchName");
        Mockito.when(bankBranchesRepository.findAllByBranchName("mockBranchName")).thenReturn(bankBranchesEntity);

        BankAccountsEntity bankAccountsEntity = new BankAccountsEntity();
        bankAccountsEntity.setAccountNumber("0123456789");
        Mockito.when(bankAccountsRepository.findAllByAccountNumber(anyString())).thenReturn(bankAccountsEntity);

        BankAccountsEntity expectedResult = new BankAccountsEntity();
        expectedResult.setAccountNumber("0123456789");

        OpenBankAccountRequest openBankAccountRequest = new OpenBankAccountRequest();
        openBankAccountRequest.setName("MockName");
        openBankAccountRequest.setDateOfBirth("12-02-1994");
        openBankAccountRequest.setAddress("MockAddress");
        openBankAccountRequest.setBranchName(bankBranchesEntity.getBranchName());

        CommonResponse commonResponse = bankService.openBankAccount(openBankAccountRequest);

        ErrorResponse errorResponse = (ErrorResponse) commonResponse.getData();

        assertEquals("ERROR",commonResponse.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,commonResponse.getHttpStatus());
        assertEquals("ERROR",errorResponse.getError());

    }

    @Test
    public void fail_openBankAccount_notFoundBranchName(){

        BankBranchesEntity bankBranchesEntity = new BankBranchesEntity();
        bankBranchesEntity.setBranchName("MockBranchName");

        Mockito.when(bankBranchesRepository.findAllByBranchName(any())).thenReturn(null);

        OpenBankAccountRequest openBankAccountRequest = new OpenBankAccountRequest();
        openBankAccountRequest.setBranchName("MockBranchName");

        CommonResponse commonResponse = bankService.openBankAccount(openBankAccountRequest);

        ErrorResponse errorResponse = (ErrorResponse) commonResponse.getData();

        assertEquals("NOT_FOUND",commonResponse.getStatus());
        assertEquals(HttpStatus.NOT_FOUND,commonResponse.getHttpStatus());
        assertEquals("BRANCH NOT FOUND",errorResponse.getError());
    }

    @Test
    public void success_depositTransaction(){

        BankAccountsEntity bankAccountsEntity = new BankAccountsEntity();
        bankAccountsEntity.setAccountId(UUID.randomUUID());
        bankAccountsEntity.setAccountBranchId(1);
        bankAccountsEntity.setAccountName("MockAccountName");
        bankAccountsEntity.setAccountNumber("0123456789");
        bankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        bankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date date = Calendar.getInstance().getTime();
        bankAccountsEntity.setAccountCreatedDate(date);
        bankAccountsEntity.setAccountUpdatedDate(date);
        Mockito.when(bankAccountsRepository.findAllByAccountNumberAndAccountStatus
                ("0123456789",AccountStatus.ACTIVATED.getValue())).thenReturn(bankAccountsEntity);

        BankTransactionsEntity bankTransactionsEntity = new BankTransactionsEntity();
        bankTransactionsEntity.setTransactionId(UUID.randomUUID());
        bankTransactionsEntity.setAccountId(UUID.randomUUID());
        bankTransactionsEntity.setTransactionAmount(BigDecimal.valueOf(500));
        bankTransactionsEntity.setTransactionType("DEPOSIT");
        bankTransactionsEntity.setTransactionDate(Calendar.getInstance().getTime());
        Mockito.when(bankTransactionsRepository.save(any())).thenReturn(bankTransactionsEntity);

        BankAccountsEntity updatedBankAccount = new BankAccountsEntity();
        updatedBankAccount.setAccountId(bankAccountsEntity.getAccountId());
        updatedBankAccount.setAccountBranchId(bankAccountsEntity.getAccountBranchId());
        updatedBankAccount.setAccountNumber(bankAccountsEntity.getAccountNumber());
        updatedBankAccount.setAccountName(bankAccountsEntity.getAccountName());
        BigDecimal accountBalance = bankAccountsEntity.getAccountBalance();
        BigDecimal updatedAccountBalance = accountBalance.add(bankTransactionsEntity.getTransactionAmount());
        updatedBankAccount.setAccountBalance(updatedAccountBalance);
        updatedBankAccount.setAccountStatus(bankAccountsEntity.getAccountStatus());
        updatedBankAccount.setAccountCreatedDate(bankAccountsEntity.getAccountCreatedDate());
        updatedBankAccount.setAccountUpdatedDate(Calendar.getInstance().getTime());

        BankTransactionRequest bankTransactionRequest = new BankTransactionRequest();
        bankTransactionRequest.setAccountName("MockAccountName");
        bankTransactionRequest.setAccountNumber("0123456789");
        bankTransactionRequest.setAmount(BigDecimal.valueOf(500));

        CommonResponse commonResponse = bankService.depositTransaction(bankTransactionRequest);

        BankTransactionResponse bankTransactionResponse = (BankTransactionResponse) commonResponse.getData();

        assertEquals("SUCCESS",commonResponse.getStatus());
        assertEquals(HttpStatus.CREATED,commonResponse.getHttpStatus());
        assertEquals("MockAccountName",bankTransactionResponse.getAccountName());
        assertEquals("0123456789",bankTransactionResponse.getAccountNumber());
        assertEquals(bankTransactionsEntity.getTransactionAmount(),bankTransactionResponse.getAmount());
        assertEquals(updatedBankAccount.getAccountBalance(),bankTransactionResponse.getAccountBalance());
        assertEquals(bankTransactionsEntity.getTransactionDate(),bankTransactionResponse.getTransactionDate());

    }

    @Test
    public void fail_depositTransaction_notFoundBankAccount(){
        BankAccountsEntity bankAccountsEntity = new BankAccountsEntity();
        bankAccountsEntity.setAccountId(UUID.randomUUID());
        bankAccountsEntity.setAccountBranchId(1);
        bankAccountsEntity.setAccountName("MockAccountName");
        bankAccountsEntity.setAccountNumber("0123456789");
        bankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        bankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date date = Calendar.getInstance().getTime();
        bankAccountsEntity.setAccountCreatedDate(date);
        bankAccountsEntity.setAccountUpdatedDate(date);
        Mockito.when(bankAccountsRepository.findAllByAccountNumberAndAccountStatus
                ("0123456789",AccountStatus.ACTIVATED.getValue())).thenReturn(null);

        BankTransactionRequest bankTransactionRequest = new BankTransactionRequest();
        bankTransactionRequest.setAccountName("MockAccountName");
        bankTransactionRequest.setAccountNumber("0123456789");
        bankTransactionRequest.setAmount(BigDecimal.valueOf(500));

        CommonResponse commonResponse = bankService.depositTransaction(bankTransactionRequest);

        ErrorResponse errorResponse = (ErrorResponse) commonResponse.getData();

        assertEquals("NOT_FOUND",commonResponse.getStatus());
        assertEquals(HttpStatus.NOT_FOUND,commonResponse.getHttpStatus());
        assertEquals("BANK ACCOUNT NOT FOUND",errorResponse.getError());

    }

    @Test
    public void success_withdrawTransaction(){

        BankAccountsEntity bankAccountsEntity = new BankAccountsEntity();
        bankAccountsEntity.setAccountId(UUID.randomUUID());
        bankAccountsEntity.setAccountBranchId(1);
        bankAccountsEntity.setAccountName("MockAccountName");
        bankAccountsEntity.setAccountNumber("0123456789");
        bankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        bankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date date = Calendar.getInstance().getTime();
        bankAccountsEntity.setAccountCreatedDate(date);
        bankAccountsEntity.setAccountUpdatedDate(date);
        Mockito.when(bankAccountsRepository.findAllByAccountNumberAndAccountStatus
                ("0123456789",AccountStatus.ACTIVATED.getValue())).thenReturn(bankAccountsEntity);

        BankTransactionsEntity bankTransactionsEntity = new BankTransactionsEntity();
        bankTransactionsEntity.setTransactionId(UUID.randomUUID());
        bankTransactionsEntity.setAccountId(UUID.randomUUID());
        bankTransactionsEntity.setTransactionAmount(BigDecimal.valueOf(500));
        bankTransactionsEntity.setTransactionType("WITHDRAW");
        bankTransactionsEntity.setTransactionDate(Calendar.getInstance().getTime());
        Mockito.when(bankTransactionsRepository.save(any())).thenReturn(bankTransactionsEntity);

        BankAccountsEntity updatedBankAccount = new BankAccountsEntity();
        updatedBankAccount.setAccountId(bankAccountsEntity.getAccountId());
        updatedBankAccount.setAccountBranchId(bankAccountsEntity.getAccountBranchId());
        updatedBankAccount.setAccountNumber(bankAccountsEntity.getAccountNumber());
        updatedBankAccount.setAccountName(bankAccountsEntity.getAccountName());
        BigDecimal accountBalance = bankAccountsEntity.getAccountBalance();
        BigDecimal updatedAccountBalance = accountBalance.subtract(bankTransactionsEntity.getTransactionAmount());
        updatedBankAccount.setAccountBalance(updatedAccountBalance);
        updatedBankAccount.setAccountStatus(bankAccountsEntity.getAccountStatus());
        updatedBankAccount.setAccountCreatedDate(bankAccountsEntity.getAccountCreatedDate());
        updatedBankAccount.setAccountUpdatedDate(Calendar.getInstance().getTime());

        BankTransactionRequest bankTransactionRequest = new BankTransactionRequest();
        bankTransactionRequest.setAccountName("MockAccountName");
        bankTransactionRequest.setAccountNumber("0123456789");
        bankTransactionRequest.setAmount(BigDecimal.valueOf(500));

        CommonResponse commonResponse = bankService.withdrawTransaction(bankTransactionRequest);

        BankTransactionResponse bankTransactionResponse = (BankTransactionResponse) commonResponse.getData();

        assertEquals("SUCCESS",commonResponse.getStatus());
        assertEquals(HttpStatus.CREATED,commonResponse.getHttpStatus());
        assertEquals("MockAccountName",bankTransactionResponse.getAccountName());
        assertEquals("0123456789",bankTransactionResponse.getAccountNumber());
        assertEquals(bankTransactionsEntity.getTransactionAmount(),bankTransactionResponse.getAmount());
        assertEquals(updatedBankAccount.getAccountBalance(),bankTransactionResponse.getAccountBalance());
        assertEquals(bankTransactionsEntity.getTransactionDate(),bankTransactionResponse.getTransactionDate());

    }

    @Test
    public void fail_withdrawTransaction_notFoundBankAccount(){

        BankAccountsEntity bankAccountsEntity = new BankAccountsEntity();
        bankAccountsEntity.setAccountId(UUID.randomUUID());
        bankAccountsEntity.setAccountBranchId(1);
        bankAccountsEntity.setAccountName("MockAccountName");
        bankAccountsEntity.setAccountNumber("0123456789");
        bankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        bankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date date = Calendar.getInstance().getTime();
        bankAccountsEntity.setAccountCreatedDate(date);
        bankAccountsEntity.setAccountUpdatedDate(date);
        Mockito.when(bankAccountsRepository.findAllByAccountNumberAndAccountStatus
                ("0123456789",AccountStatus.ACTIVATED.getValue())).thenReturn(null);

        BankTransactionRequest bankTransactionRequest = new BankTransactionRequest();
        bankTransactionRequest.setAccountName("MockAccountName");
        bankTransactionRequest.setAccountNumber("0123456789");
        bankTransactionRequest.setAmount(BigDecimal.valueOf(500));

        CommonResponse commonResponse = bankService.withdrawTransaction(bankTransactionRequest);

        ErrorResponse errorResponse = (ErrorResponse) commonResponse.getData();

        assertEquals("NOT_FOUND",commonResponse.getStatus());
        assertEquals(HttpStatus.NOT_FOUND,commonResponse.getHttpStatus());
        assertEquals("BANK ACCOUNT NOT FOUND",errorResponse.getError());

    }

    @Test
    public void success_transferTransaction(){

        BankAccountsEntity senderBankAccountsEntity = new BankAccountsEntity();
        senderBankAccountsEntity.setAccountId(UUID.randomUUID());
        senderBankAccountsEntity.setAccountBranchId(1);
        senderBankAccountsEntity.setAccountName("MockSenderAccountName");
        senderBankAccountsEntity.setAccountNumber("1111111111");
        senderBankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        senderBankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date senderDate = Calendar.getInstance().getTime();
        senderBankAccountsEntity.setAccountCreatedDate(senderDate);
        senderBankAccountsEntity.setAccountUpdatedDate(senderDate);

        BankAccountsEntity receiverBankAccountsEntity = new BankAccountsEntity();
        receiverBankAccountsEntity.setAccountId(UUID.randomUUID());
        receiverBankAccountsEntity.setAccountBranchId(2);
        receiverBankAccountsEntity.setAccountName("MockReceiverAccountName");
        receiverBankAccountsEntity.setAccountNumber("2222222222");
        receiverBankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        receiverBankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date receiverDate = Calendar.getInstance().getTime();
        receiverBankAccountsEntity.setAccountCreatedDate(receiverDate);
        receiverBankAccountsEntity.setAccountUpdatedDate(receiverDate);
        Mockito.doReturn(senderBankAccountsEntity,receiverBankAccountsEntity).when(bankAccountsRepository)
                .findAllByAccountNumberAndAccountStatus(any(),anyString());

        BankTransactionsEntity bankTransactionsEntity = new BankTransactionsEntity();
        bankTransactionsEntity.setTransactionId(UUID.randomUUID());
        bankTransactionsEntity.setAccountId(senderBankAccountsEntity.getAccountId());
        bankTransactionsEntity.setTransactionAccountIdTo(receiverBankAccountsEntity.getAccountId());
        bankTransactionsEntity.setTransactionAmount(BigDecimal.valueOf(500));
        bankTransactionsEntity.setTransactionType("TRANSFER");
        bankTransactionsEntity.setTransactionDate(Calendar.getInstance().getTime());
        Mockito.when(bankTransactionsRepository.save(any())).thenReturn(bankTransactionsEntity);

        BankAccountsEntity updatedSenderBankAccount = new BankAccountsEntity();
        updatedSenderBankAccount.setAccountId(senderBankAccountsEntity.getAccountId());
        updatedSenderBankAccount.setAccountBranchId(senderBankAccountsEntity.getAccountBranchId());
        updatedSenderBankAccount.setAccountNumber(senderBankAccountsEntity.getAccountNumber());
        updatedSenderBankAccount.setAccountName(senderBankAccountsEntity.getAccountName());
        BigDecimal senderAccountBalance = senderBankAccountsEntity.getAccountBalance();
        BigDecimal updatedSenderAccountBalance = senderAccountBalance.subtract(bankTransactionsEntity.getTransactionAmount());
        updatedSenderBankAccount.setAccountBalance(updatedSenderAccountBalance);
        updatedSenderBankAccount.setAccountStatus(senderBankAccountsEntity.getAccountStatus());
        updatedSenderBankAccount.setAccountCreatedDate(senderBankAccountsEntity.getAccountCreatedDate());
        updatedSenderBankAccount.setAccountUpdatedDate(Calendar.getInstance().getTime());

        BankAccountsEntity updatedReceiverBankAccount = new BankAccountsEntity();
        updatedReceiverBankAccount.setAccountId(receiverBankAccountsEntity.getAccountId());
        updatedReceiverBankAccount.setAccountBranchId(receiverBankAccountsEntity.getAccountBranchId());
        updatedReceiverBankAccount.setAccountNumber(receiverBankAccountsEntity.getAccountNumber());
        updatedReceiverBankAccount.setAccountName(receiverBankAccountsEntity.getAccountName());
        BigDecimal receiverAccountBalance = receiverBankAccountsEntity.getAccountBalance();
        BigDecimal updatedReceiverAccountBalance = receiverAccountBalance.add(bankTransactionsEntity.getTransactionAmount());
        updatedReceiverBankAccount.setAccountBalance(updatedReceiverAccountBalance);
        updatedReceiverBankAccount.setAccountStatus(receiverBankAccountsEntity.getAccountStatus());
        updatedReceiverBankAccount.setAccountCreatedDate(receiverBankAccountsEntity.getAccountCreatedDate());
        updatedReceiverBankAccount.setAccountUpdatedDate(Calendar.getInstance().getTime());
        Mockito.doReturn(updatedSenderBankAccount,updatedReceiverBankAccount).when(bankAccountsRepository).save(any());

        BankTransferRequest bankTransferRequest = new BankTransferRequest();
        bankTransferRequest.setSenderAccountNumber(senderBankAccountsEntity.getAccountNumber());
        bankTransferRequest.setReceiverAccountNumber(receiverBankAccountsEntity.getAccountNumber());
        bankTransferRequest.setAmount(bankTransactionsEntity.getTransactionAmount());

        CommonResponse commonResponse = bankService.transferTransaction(bankTransferRequest);

        BankTransferResponse bankTransferResponse = (BankTransferResponse) commonResponse.getData();

        assertEquals("SUCCESS", commonResponse.getStatus());
        assertEquals(HttpStatus.CREATED,commonResponse.getHttpStatus());
        assertEquals(updatedSenderBankAccount.getAccountNumber(),bankTransferResponse.getSenderAccountNumber());
        assertEquals(updatedReceiverBankAccount.getAccountNumber(),bankTransferResponse.getReceiverAccountNumber());
        assertEquals(bankTransactionsEntity.getTransactionAmount(),bankTransferResponse.getAmount());
        assertEquals(updatedSenderBankAccount.getAccountBalance(),bankTransferResponse.getSenderAccountBalance());
        assertEquals(bankTransactionsEntity.getTransactionDate(),bankTransferResponse.getTransactionDate());

    }

    @Test
    public void fail_transferTransaction_notFoundSenderBankAccount(){

        BankAccountsEntity senderBankAccountsEntity = new BankAccountsEntity();
        senderBankAccountsEntity.setAccountId(UUID.randomUUID());
        senderBankAccountsEntity.setAccountBranchId(1);
        senderBankAccountsEntity.setAccountName("MockSenderAccountName");
        senderBankAccountsEntity.setAccountNumber("1111111111");
        senderBankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        senderBankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date senderDate = Calendar.getInstance().getTime();
        senderBankAccountsEntity.setAccountCreatedDate(senderDate);
        senderBankAccountsEntity.setAccountUpdatedDate(senderDate);
        Mockito.when(bankAccountsRepository.findAllByAccountNumberAndAccountStatus
                ("1111111111",AccountStatus.ACTIVATED.getValue())).thenReturn(null);

        BankTransferRequest bankTransferRequest = new BankTransferRequest();
        bankTransferRequest.setSenderAccountNumber(senderBankAccountsEntity.getAccountNumber());

        CommonResponse commonResponse = bankService.transferTransaction(bankTransferRequest);

        ErrorResponse errorResponse = (ErrorResponse) commonResponse.getData();

        assertEquals("NOT_FOUND",commonResponse.getStatus());
        assertEquals(HttpStatus.NOT_FOUND,commonResponse.getHttpStatus());
        assertEquals("SENDER BANK ACCOUNT NOT FOUND",errorResponse.getError());

    }

    @Test
    public void fail_transferTransaction_notFoundReceiverBankAccount(){

        BankAccountsEntity senderBankAccountsEntity = new BankAccountsEntity();
        senderBankAccountsEntity.setAccountId(UUID.randomUUID());
        senderBankAccountsEntity.setAccountBranchId(1);
        senderBankAccountsEntity.setAccountName("MockSenderAccountName");
        senderBankAccountsEntity.setAccountNumber("1111111111");
        senderBankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        senderBankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date senderDate = Calendar.getInstance().getTime();
        senderBankAccountsEntity.setAccountCreatedDate(senderDate);
        senderBankAccountsEntity.setAccountUpdatedDate(senderDate);

        BankAccountsEntity receiverBankAccountsEntity = new BankAccountsEntity();
        receiverBankAccountsEntity.setAccountId(UUID.randomUUID());
        receiverBankAccountsEntity.setAccountBranchId(2);
        receiverBankAccountsEntity.setAccountName("MockReceiverAccountName");
        receiverBankAccountsEntity.setAccountNumber("2222222222");
        receiverBankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        receiverBankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date receiverDate = Calendar.getInstance().getTime();
        receiverBankAccountsEntity.setAccountCreatedDate(receiverDate);
        receiverBankAccountsEntity.setAccountUpdatedDate(receiverDate);
        Mockito.doReturn(senderBankAccountsEntity,null).when(bankAccountsRepository)
                .findAllByAccountNumberAndAccountStatus(any(),anyString());

        BankTransferRequest bankTransferRequest = new BankTransferRequest();
        bankTransferRequest.setSenderAccountNumber(senderBankAccountsEntity.getAccountNumber());
        bankTransferRequest.setReceiverAccountNumber(receiverBankAccountsEntity.getAccountNumber());

        CommonResponse commonResponse = bankService.transferTransaction(bankTransferRequest);

        ErrorResponse errorResponse = (ErrorResponse) commonResponse.getData();

        assertEquals("NOT_FOUND",commonResponse.getStatus());
        assertEquals(HttpStatus.NOT_FOUND,commonResponse.getHttpStatus());
        assertEquals("RECEIVER BANK ACCOUNT NOT FOUND",errorResponse.getError());

    }

    @Test
    public void success_closeBankAccount(){

        BankAccountsEntity bankAccountsEntity = new BankAccountsEntity();
        bankAccountsEntity.setAccountId(UUID.randomUUID());
        bankAccountsEntity.setAccountBranchId(1);
        bankAccountsEntity.setAccountNumber("0123456789");
        bankAccountsEntity.setAccountName("mockAccountName");
        bankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        bankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date date = Calendar.getInstance().getTime();
        bankAccountsEntity.setAccountCreatedDate(date);
        bankAccountsEntity.setAccountUpdatedDate(date);
        Mockito.when(bankAccountsRepository.findAllByAccountNumberAndAccountStatus
                (anyString(),anyString())).thenReturn(bankAccountsEntity);

        BankAccountsEntity updatedBankAccount = new BankAccountsEntity();
        updatedBankAccount.setAccountId(bankAccountsEntity.getAccountId());
        updatedBankAccount.setAccountBranchId(bankAccountsEntity.getAccountBranchId());
        updatedBankAccount.setAccountNumber(bankAccountsEntity.getAccountNumber());
        updatedBankAccount.setAccountName(bankAccountsEntity.getAccountName());
        updatedBankAccount.setAccountBalance(bankAccountsEntity.getAccountBalance());
        updatedBankAccount.setAccountStatus(AccountStatus.DEACTIVATED.getValue());
        updatedBankAccount.setAccountCreatedDate(bankAccountsEntity.getAccountCreatedDate());
        updatedBankAccount.setAccountUpdatedDate(Calendar.getInstance().getTime());
        Mockito.when(bankAccountsRepository.save(any())).thenReturn(updatedBankAccount);

        BankBranchesEntity bankBranchesEntity = new BankBranchesEntity();
        bankBranchesEntity.setBranchId(1);
        bankBranchesEntity.setBranchName("MockBranchName");
        Mockito.when(bankBranchesRepository.findAllByBranchId(updatedBankAccount.getAccountBranchId()))
                .thenReturn(bankBranchesEntity);

        CommonResponse commonResponse = bankService.closeBankAccount(updatedBankAccount.getAccountNumber());

        CloseBankAccountResponse closeBankAccountResponse = (CloseBankAccountResponse) commonResponse.getData();

        assertEquals("SUCCESS",commonResponse.getStatus());
        assertEquals(HttpStatus.OK,commonResponse.getHttpStatus());
        assertEquals(updatedBankAccount.getAccountName(),closeBankAccountResponse.getAccountName());
        assertEquals(updatedBankAccount.getAccountNumber(),closeBankAccountResponse.getAccountNumber());
        assertEquals(updatedBankAccount.getAccountStatus(),closeBankAccountResponse.getAccountStatus());
        assertEquals(bankBranchesEntity.getBranchName(),closeBankAccountResponse.getBranchName());

    }

    @Test
    public void fail_closeBankAccount_notFoundBankAccount(){

        BankAccountsEntity bankAccountsEntity = new BankAccountsEntity();
        bankAccountsEntity.setAccountId(UUID.randomUUID());
        bankAccountsEntity.setAccountBranchId(1);
        bankAccountsEntity.setAccountName("MockAccountName");
        bankAccountsEntity.setAccountNumber("0123456789");
        bankAccountsEntity.setAccountBalance(BigDecimal.ZERO);
        bankAccountsEntity.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date date = Calendar.getInstance().getTime();
        bankAccountsEntity.setAccountCreatedDate(date);
        bankAccountsEntity.setAccountUpdatedDate(date);
        Mockito.when(bankAccountsRepository.findAllByAccountNumberAndAccountStatus
                (anyString(),anyString())).thenReturn(null);

        CommonResponse commonResponse = bankService.closeBankAccount(bankAccountsEntity.getAccountNumber());

        ErrorResponse errorResponse = (ErrorResponse) commonResponse.getData();

        assertEquals("NOT_FOUND",commonResponse.getStatus());
        assertEquals(HttpStatus.NOT_FOUND,commonResponse.getHttpStatus());
        assertEquals("BANK ACCOUNT NOT FOUND",errorResponse.getError());
    }

    @Test
    public void success_gelAllBankAccount(){

        BankAccountsEntity bankAccountsEntity1 = new BankAccountsEntity();
        bankAccountsEntity1.setAccountId(UUID.randomUUID());
        bankAccountsEntity1.setAccountBranchId(1);
        bankAccountsEntity1.setAccountName("MockAccountName1");
        bankAccountsEntity1.setAccountNumber("8888888888");
        bankAccountsEntity1.setAccountBalance(BigDecimal.ZERO);
        bankAccountsEntity1.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        Date date = Calendar.getInstance().getTime();
        bankAccountsEntity1.setAccountCreatedDate(date);
        bankAccountsEntity1.setAccountUpdatedDate(date);

        BankAccountsEntity bankAccountsEntity2 = new BankAccountsEntity();
        bankAccountsEntity2.setAccountId(UUID.randomUUID());
        bankAccountsEntity2.setAccountBranchId(2);
        bankAccountsEntity2.setAccountName("MockAccountName2");
        bankAccountsEntity2.setAccountNumber("9999999999");
        bankAccountsEntity2.setAccountBalance(BigDecimal.ZERO);
        bankAccountsEntity2.setAccountStatus(AccountStatus.ACTIVATED.getValue());
        date = Calendar.getInstance().getTime();
        bankAccountsEntity2.setAccountCreatedDate(date);
        bankAccountsEntity2.setAccountUpdatedDate(date);

        BankBranchesEntity bankBranchesEntity1 = new BankBranchesEntity();
        bankBranchesEntity1.setBranchId(1);
        bankBranchesEntity1.setBranchName("Mock Branch Name1");
        bankBranchesEntity1.setBranchCity("Mock City1");
        bankBranchesEntity1.setBranchAsset(BigDecimal.valueOf(0));
        bankBranchesEntity1.setBranchCreatedDate(Calendar.getInstance().getTime());

        BankBranchesEntity bankBranchesEntity2 = new BankBranchesEntity();
        bankBranchesEntity2.setBranchId(2);
        bankBranchesEntity2.setBranchName("Mock Branch Name2");
        bankBranchesEntity2.setBranchCity("Mock City2");
        bankBranchesEntity2.setBranchAsset(BigDecimal.valueOf(0));
        bankBranchesEntity2.setBranchCreatedDate(Calendar.getInstance().getTime());

        List<BankAccountsEntity> bankAccountsEntityList = new ArrayList<>();
        bankAccountsEntityList.add(bankAccountsEntity1);
        bankAccountsEntityList.add(bankAccountsEntity2);
        Mockito.when(bankAccountsRepository.findAllByAccountStatus(anyString())).thenReturn(bankAccountsEntityList);

        List<BankBranchesEntity> bankBranchesEntityList = new ArrayList<>();
        bankBranchesEntityList.add(bankBranchesEntity1);
        bankBranchesEntityList.add(bankBranchesEntity2);
        Mockito.doReturn(bankBranchesEntity1,bankBranchesEntity2).when(bankBranchesRepository).findAllByBranchId(any());

        CommonResponse commonResponse = bankService.getAllBankAccount();

        List<GetAllBankAccountResponse> getAllResponseList =  (List<GetAllBankAccountResponse>) commonResponse.getData();

        assertEquals("SUCCESS", commonResponse.getStatus());
        assertEquals(HttpStatus.OK, commonResponse.getHttpStatus());

        assertEquals(bankAccountsEntityList.size(), getAllResponseList.size());
        for (int i = 0; i < bankAccountsEntityList.size(); i++) {

            assertEquals(bankAccountsEntityList.get(i).getAccountName(), getAllResponseList.get(i).getAccountName());
            assertEquals(bankAccountsEntityList.get(i).getAccountNumber(), getAllResponseList.get(i).getAccountNumber());
            assertEquals(bankBranchesEntityList.get(i).getBranchName(),getAllResponseList.get(i).getBranchName());
            assertEquals(bankAccountsEntityList.get(i).getAccountBalance(),getAllResponseList.get(i).getAccountBalance());
        }

    }

    @Test
    public void fail_getAllBankAccount(){

        List<BankAccountsEntity> bankAccountsEntityList = new ArrayList<>();

        Mockito.when(bankAccountsRepository.findAllByAccountStatus(any())).thenReturn(null);

        CommonResponse commonResponse = bankService.getAllBankAccount();

        List<GetAllBankAccountResponse> getAllBankAccountResponseList = (List<GetAllBankAccountResponse>) commonResponse.getData();

        assertEquals(bankAccountsEntityList,getAllBankAccountResponseList);

    }
}
