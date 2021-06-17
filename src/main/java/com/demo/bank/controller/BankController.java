package com.demo.bank.controller;

import com.demo.bank.constant.Status;
import com.demo.bank.exception.ValidateException;
import com.demo.bank.model.request.BankTransactionRequest;
import com.demo.bank.model.request.BankTransferRequest;
import com.demo.bank.model.request.OpenBankAccountRequest;
import com.demo.bank.model.response.CommonResponse;
import com.demo.bank.model.response.ErrorResponse;
import com.demo.bank.service.BankService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

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
        logger.info("START IMPLEMENTING DEPOSIT TRANSACTION, transactionRequest : {}", request);
        CommonResponse commonResponse = bankService.depositTransaction(request);
        logger.info("END IMPLEMENTING DEPOSIT TRANSACTION, response : {}",commonResponse);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }

    @PostMapping(value = "/transaction/withdraw", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> withdrawTransaction(@Valid @RequestBody BankTransactionRequest request) {
        logger.info("START IMPLEMENTING WITHDRAW TRANSACTION, transactionRequest : {}", request);
        CommonResponse commonResponse = bankService.withdrawTransaction(request);
        logger.info("END IMPLEMENTING WITHDRAW TRANSACTION, response : {}",commonResponse);
        return new ResponseEntity<>(commonResponse, commonResponse.getHttpStatus());
    }

    @PostMapping(value = "/transaction/transfer",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> transferTransaction(@Valid @RequestBody BankTransferRequest request){
        logger.info("START IMPLEMENTING TRANSFER TRANSACTION, bankTransferRequest : {}", request);
        CommonResponse commonResponse = bankService.transferTransaction(request);
        logger.info("END IMPLEMENTING TRANSFER TRANSACTION, response : {}",commonResponse);
        return new ResponseEntity<>(commonResponse, commonResponse.getHttpStatus());
    }

    @DeleteMapping(value = "/accounts/{accountNumber}/deactivated", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> closeBankAccount(@PathVariable("accountNumber") String accountNumber) {
        logger.info("START IMPLEMENTING CLOSE BANK ACCOUNT");
        CommonResponse commonResponse = bankService.closeBankAccount(accountNumber);
        logger.info("END IMPLEMENTING CLOSE BANK ACCOUNT, response : {}",commonResponse);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }

    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllBankAccount(){
        logger.info("START IMPLEMENTING LIST ALL BANK ACCOUNTS");
        CommonResponse commonResponse = bankService.getAllBankAccount();
        logger.info("END IMPLEMENTING LIST ALL BANK ACCOUNTS, response : {}",commonResponse);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }

    @GetMapping(value = "/transactions/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllTransaction(@PathVariable("accountNumber") String accountNumber,
                                                            @RequestParam(value = "dateFrom", required = false) String dateFromStr,
                                                            @RequestParam(value = "dateTo", required = false) String dateToStr,
                                                            @RequestParam(value = "sort", required = false, defaultValue = "DESC") String sort) throws ParseException {
        logger.info("START IMPLEMENTING LIST ALL TRANSACTIONS");

        validateDateFromDateTo(dateFromStr, dateToStr);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date dateFrom = formatter.parse(dateFromStr);
        Date dateTo = formatter.parse(dateToStr);

        if (!"ASC".equalsIgnoreCase(sort) && !"DESC".equalsIgnoreCase(sort)) {
            logger.error("VALIDATION FAILED, sort : {}", sort);
            CommonResponse commonResponse = new CommonResponse();
            commonResponse.setStatus(Status.ERROR.getValue());
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError("sort is invalid, only ASC and DESC accepted");
            commonResponse.setData(errorResponse);
            commonResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(commonResponse, commonResponse.getHttpStatus());
        }

        CommonResponse commonResponse = bankService.getAllTransaction(accountNumber, dateFrom, dateTo, sort);
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



