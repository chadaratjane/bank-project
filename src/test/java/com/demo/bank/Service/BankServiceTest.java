package com.demo.bank.Service;

import com.demo.bank.constant.AccountStatus;
import com.demo.bank.model.entity.BankAccountsEntity;
import com.demo.bank.model.entity.BankBranchesEntity;
import com.demo.bank.model.request.OpenBankAccountRequest;
import com.demo.bank.model.response.CommonResponse;
import com.demo.bank.model.response.OpenBankAccountResponse;
import com.demo.bank.repository.BankAccountsRepository;
import com.demo.bank.repository.BankBranchesRepository;
import com.demo.bank.repository.BankTransactionsRepository;
import com.demo.bank.repository.CustomerInformationRepository;
import com.demo.bank.service.BankService;
import org.aspectj.lang.annotation.Before;
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
import java.util.Calendar;
import java.util.Date;
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

}
