package com.demo.bank.configuration;

import com.demo.bank.constant.Status;
import com.demo.bank.exception.ValidateException;
import com.demo.bank.model.response.CommonResponse;
import com.demo.bank.model.response.ErrorResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ErrorHandler {

    private static final Logger logger = LogManager.getLogger(ErrorHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handlerAllError (Exception e){
        logger.error("UNEXPECTED ERROR",e);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setStatus("INTERNAL SERVER ERROR");
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("INTERNAL SERVER ERROR");
        commonResponse.setData(errorResponse);
        commonResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus()) ;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse> handlerValidation(MethodArgumentNotValidException e) {
        logger.error("VALIDATION FAILED",e);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setStatus("BAD REQUEST");
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(e.getFieldError().getDefaultMessage());
        commonResponse.setData(errorResponse);
        commonResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus()) ;
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<CommonResponse> handlerDataAccess(DataAccessException e){
        logger.error("DATA ACCESS FAILED",e);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setStatus("DATA ACCESS FAILED");
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("DATA ACCESS FAILED");
        commonResponse.setData(errorResponse);
        commonResponse.setHttpStatus(HttpStatus.REQUEST_TIMEOUT);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<CommonResponse> handlerManualValidateException(ValidateException e){
        logger.error("VALIDATION FAILED, {}",e.getErrorCause());
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setStatus(Status.ERROR.getValue());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(e.getErrorMessage());
        commonResponse.setData(errorResponse);
        commonResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(commonResponse,commonResponse.getHttpStatus());
    }
}
