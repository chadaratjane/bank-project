package com.demo.bank.controller;

import com.demo.bank.model.request.OpenBankAccountRequest;
import com.demo.bank.model.response.CommonResponse;
import com.demo.bank.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class BankAccountController {

    private static final Logger logger = LogManager.getLogger(BankAccountController.class);

    @Autowired
    private BankService bankService;

    @PostMapping(value = "/accounts",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> openBankAccount (@Valid @RequestBody OpenBankAccountRequest request){
        logger.info("START IMPLEMENTING OPEN BANK ACCOUNT, branchName : {}", request.getBranchName());
       CommonResponse commonResponse = bankService.openBankAccount(request);
        logger.info("END IMPLEMENTING OPEN BANK ACCOUNT, response : {}", commonResponse);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }

    @DeleteMapping(value = "/accounts/{accountNumber}/deactivated", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> closeBankAccount(@PathVariable("accountNumber") String accountNumber) {
        logger.info("START IMPLEMENTING CLOSE BANK ACCOUNT");
        CommonResponse commonResponse = bankService.closeBankAccount(accountNumber);
        logger.info("END IMPLEMENTING CLOSE BANK ACCOUNT, response : {}",commonResponse);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }

    //TODO @Operation(summary = "Get a book by its id")
    //TODO ADD ACCOUNT STATUS ACTIVATED, DEACTIVATED, ALL
    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllBankAccount(){
        logger.info("START IMPLEMENTING LIST ALL BANK ACCOUNTS");
        CommonResponse commonResponse = bankService.getAllBankAccount();
        logger.info("END IMPLEMENTING LIST ALL BANK ACCOUNTS, response : {}",commonResponse);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }

}



