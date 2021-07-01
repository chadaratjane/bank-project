package com.demo.bank.controller;

import com.demo.bank.exception.ValidateException;
import com.demo.bank.model.request.OpenBankAccountRequest;
import com.demo.bank.model.response.CommonResponse;
import com.demo.bank.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;

@RestController
public class BankAccountController {

    private static final Logger logger = LogManager.getLogger(BankAccountController.class);

    @Autowired
    private BankService bankService;

    @Operation(summary = "Open bank account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "OPEN SUCCESSFULLY"),
            @ApiResponse(responseCode = "400",description = "VALIDATE FAILED"),
            @ApiResponse(responseCode = "404",description = "DATA NOT FOUND"),
            @ApiResponse(responseCode = "500",description = "INTERNAL SERVER ERROR")
    })
    @PostMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> openBankAccount(@Valid @RequestBody OpenBankAccountRequest request) throws ParseException {
        logger.info("START IMPLEMENTING OPEN BANK ACCOUNT, branchName : {}", request.getBranchName());

        CommonResponse commonResponse = bankService.openBankAccount(request);
        logger.info("END IMPLEMENTING OPEN BANK ACCOUNT, response : {}", commonResponse);
        return new ResponseEntity<>(commonResponse, commonResponse.getHttpStatus());
    }

    @Operation(summary = "Close bank account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "CLOSE SUCCESSFULLY"),
            @ApiResponse(responseCode = "400",description = "VALIDATE FAILED"),
            @ApiResponse(responseCode = "404",description = "DATA NOT FOUND"),
            @ApiResponse(responseCode = "500",description = "INTERNAL SERVER ERROR")
    })
    @DeleteMapping(value = "/accounts/{accountNumber}/deactivated", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> closeBankAccount(
            @Parameter(example = "0123456789")
            @PathVariable("accountNumber") String accountNumber) {
        logger.info("START IMPLEMENTING CLOSE BANK ACCOUNT");
        CommonResponse commonResponse = bankService.closeBankAccount(accountNumber);
        logger.info("END IMPLEMENTING CLOSE BANK ACCOUNT, response : {}",commonResponse);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }

    @Operation(summary = "Get all bank account by account status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "RETRIEVE SUCCESSFULLY"),
            @ApiResponse(responseCode = "400",description = "VALIDATE FAILED"),
            @ApiResponse(responseCode = "404",description = "DATA NOT FOUND"),
            @ApiResponse(responseCode = "500",description = "INTERNAL SERVER ERROR")
    })
    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllBankAccount(
            @RequestParam(value = "accountStatus",required = false,defaultValue = "ALL") String accountStatus){

        logger.info("START IMPLEMENTING LIST ALL BANK ACCOUNTS");

        if (!"ACTIVATED".equalsIgnoreCase(accountStatus) && !"DEACTIVATED".equalsIgnoreCase(accountStatus) && !"ALL".equalsIgnoreCase(accountStatus)) {
            logger.error("VALIDATION FAILED, sort : {}", accountStatus);
            throw new ValidateException("ACCOUNT STATUS IS INVALID", "ACCOUNT STATUS IS INVALID : ONLY [ACTIVATED] , [DEACTIVATED] AND [ALL] ACCEPTED");

        }

        CommonResponse commonResponse = bankService.getAllBankAccount(accountStatus.toUpperCase());
        logger.info("END IMPLEMENTING LIST ALL BANK ACCOUNTS, response : {}", commonResponse);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }

}



