package com.demo.bank.controller;

import com.demo.bank.exception.ValidateException;
import com.demo.bank.model.request.BankTransactionRequest;
import com.demo.bank.model.request.BankTransferRequest;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class BankTransactionController {

    private static final Logger logger = LogManager.getLogger(BankAccountController.class);

    @Autowired
    private BankService bankService;

    @Operation(summary = "Deposit transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "DEPOSIT SUCCESSFULLY"),
            @ApiResponse(responseCode = "400",description = "VALIDATE FAILED"),
            @ApiResponse(responseCode = "404",description = "DATA NOT FOUND"),
            @ApiResponse(responseCode = "500",description = "INTERNAL SERVER ERROR")
    })
    @PostMapping(value = "/transactions/deposit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> depositTransaction(@Valid @RequestBody BankTransactionRequest request) {
        logger.info("START IMPLEMENTING DEPOSIT TRANSACTION, transactionRequest : {}", request);
        CommonResponse commonResponse = bankService.depositTransaction(request);
        logger.info("END IMPLEMENTING DEPOSIT TRANSACTION, response : {}",commonResponse);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }

    @Operation(summary = "Withdraw transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "WITHDRAW SUCCESSFULLY"),
            @ApiResponse(responseCode = "400",description = "VALIDATE FAILED"),
            @ApiResponse(responseCode = "404",description = "DATA NOT FOUND"),
            @ApiResponse(responseCode = "500",description = "INTERNAL SERVER ERROR")
    })
    @PostMapping(value = "/transactions/withdraw", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> withdrawTransaction(@Valid @RequestBody BankTransactionRequest request) {
        logger.info("START IMPLEMENTING WITHDRAW TRANSACTION, transactionRequest : {}", request);
        CommonResponse commonResponse = bankService.withdrawTransaction(request);
        logger.info("END IMPLEMENTING WITHDRAW TRANSACTION, response : {}",commonResponse);
        return new ResponseEntity<>(commonResponse, commonResponse.getHttpStatus());
    }

    @Operation(summary = "Transfer transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "TRANSFER SUCCESSFULLY"),
            @ApiResponse(responseCode = "400",description = "VALIDATE FAILED"),
            @ApiResponse(responseCode = "404",description = "DATA NOT FOUND"),
            @ApiResponse(responseCode = "500",description = "INTERNAL SERVER ERROR")
    })
    @PostMapping(value = "/transactions/transfer", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> transferTransaction(@Valid @RequestBody BankTransferRequest request) {
        logger.info("START IMPLEMENTING TRANSFER TRANSACTION, bankTransferRequest : {}", request);
        CommonResponse commonResponse = bankService.transferTransaction(request);
        logger.info("END IMPLEMENTING TRANSFER TRANSACTION, response : {}", commonResponse);
        return new ResponseEntity<>(commonResponse, commonResponse.getHttpStatus());
    }

    @Operation(summary = "Get all transaction by its account number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "RETRIEVE SUCCESSFULLY"),
            @ApiResponse(responseCode = "400",description = "VALIDATE FAILED"),
            @ApiResponse(responseCode = "404",description = "DATA NOT FOUND"),
            @ApiResponse(responseCode = "500",description = "INTERNAL SERVER ERROR")
    })
    @GetMapping(value = "/transactions/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllTransaction(
            @Parameter(example = "0123456789")
            @PathVariable("accountNumber") String accountNumber,
            @Parameter(description = "dd-MM-yyyy", example = "21-12-2021")
            @RequestParam(value = "dateFrom", required = false) String dateFromStr,
            @Parameter(description = "dd-MM-yyyy", example = "31-12-2021")
            @RequestParam(value = "dateTo", required = false) String dateToStr,
            @RequestParam(value = "sort", required = false, defaultValue = "DESC") String sort,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "perPage", required = false, defaultValue = "10") Integer perPage) throws ParseException {

        logger.info("START IMPLEMENTING LIST ALL TRANSACTIONS");

        validateDateFromDateTo(dateFromStr, dateToStr);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date dateFrom = formatter.parse(dateFromStr);
        Date dateTo = formatter.parse(dateToStr);

        if (!"ASC".equalsIgnoreCase(sort) && !"DESC".equalsIgnoreCase(sort)) {
            logger.error("VALIDATION FAILED, sort : {}", sort);
            throw new ValidateException("SORT IS INVALID", "SORT IS INVALID : ONLY [ASC] AND [DESC] ACCEPTED");

        }

        CommonResponse commonResponse = bankService.getAllTransaction(accountNumber, dateFrom, dateTo, sort, pageNumber, perPage);
        logger.info("END IMPLEMENTING LIST ALL TRANSACTIONS, response : {}", commonResponse);
        return new ResponseEntity<>(commonResponse, commonResponse.getHttpStatus());
    }

    private void validateDateFromDateTo(@RequestParam(value = "dateFrom", required = false) String dateFromStr, @RequestParam(value = "dateTo", required = false) String dateToStr) {
        if (!StringUtils.hasText(dateFromStr)) {
            throw new ValidateException("dateFrom is null","dateFrom is invalid");
        }
        if (!StringUtils.hasText(dateToStr)) {
            throw new ValidateException("dateTo is null","dateTo is invalid");

        }

        String pattern = "^([0-9]{2})-([0-9]{2})-([0-9]{4})$";
        boolean dateFromFormat = dateFromStr.matches(pattern);
        boolean dateToFormat = dateToStr.matches(pattern);

        if (!dateFromFormat){
            throw new ValidateException("dateFrom is invalid format","dateFrom is invalid format" );

        }
        if (!dateToFormat){
            throw new ValidateException("dateTo is invalid format","dateTo is invalid format" );

        }
    }

}
