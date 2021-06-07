package com.demo.bank.controller;

import com.demo.bank.model.request.BankTransactionRequest;
import com.demo.bank.model.request.OpenBankAccountRequest;
import com.demo.bank.model.response.CommonResponse;
import com.demo.bank.service.BankService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
public class BankController {

    private static final Logger logger = LogManager.getLogger(BankController.class);

    @Autowired
    private BankService bankService;

    @PostMapping(value = "/accounts",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> openBankAccount (@Valid @RequestBody OpenBankAccountRequest request){
        logger.info("START IMPLEMENTING OPEN BANK ACCOUNT, branchName : {}", request.getBranchName());
       CommonResponse commonResponse = bankService.openBankAccount(request);
        logger.info("END IMPLEMENTING OPEN BANK ACCOUNT, response : {}", commonResponse);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }

    @PostMapping(value = "/transaction/deposit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> depositTransaction(@Valid @RequestBody BankTransactionRequest request) {
        logger.info("START IMPLEMENTING DEPOSIT TRANSACTION, transactionAmount : {}", request.getAmount());
        CommonResponse commonResponse = bankService.depositTransaction(request);
        logger.info("END IMPLEMENTING DEPOSIT TRANSACTION, response : {}",commonResponse);
        return new ResponseEntity<>(commonResponse, commonResponse.getHttpStatus());
    }


    }



