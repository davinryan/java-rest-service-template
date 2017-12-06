package com.davinryan.service.util;

import com.davinryan.common.restservice.domain.response.Response;
import com.davinryan.common.restservice.domain.response.SpringValidationFailureResponse;
import com.davinryan.service.dao.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.jms.JMSException;

/**
 * Handles all REST exception thrown by spring.
 */
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class.getName());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response processValidationError(MethodArgumentNotValidException e) {
        LOGGER.warn(e.getMessage(), e.getBindingResult().getAllErrors());
        if (LOGGER.isDebugEnabled()) {
            return new SpringValidationFailureResponse(e.getBindingResult());
        } else {
            return new Response(Response.ResponseStatus.FAILURE, "Field validation for submit failed. Please check " +
                    "server logs for more details or run this application with logging level set to debug.");
        }
    }

    @ExceptionHandler(DatabaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response processDatabaseError(DatabaseException e) {
        LOGGER.error("DatabaseException", e);
        return new Response(Response.ResponseStatus.FAILURE, "Failed with Database Exception. Check the server logs for more information.");
    }

    @ExceptionHandler(IllegalAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public String unauthorisedAccessError(IllegalAccessException e) {
        LOGGER.error("IllegalAccessException", e);
        return "Failed with Illegal Access Exception. Check the server logs for more information.";
    }

    @ExceptionHandler(JMSException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response processJMSError(JMSException e) {
        LOGGER.error("JMSException", e);
        return new Response(Response.ResponseStatus.FAILURE, "Failed with JMS Exception. Check the server logs for more information.");
    }
}
