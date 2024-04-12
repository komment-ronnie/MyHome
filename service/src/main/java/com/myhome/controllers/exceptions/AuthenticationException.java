package com.myhome.controllers.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * provides a customized response status for unauthorized requests with a predefined
 * error message.
 * Fields:
 * 	- ERROR_MESSAGE (String): in the AuthenticationException class represents a message
 * indicating that the user's credentials are incorrect or they do not exist.
 */
@Slf4j
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends RuntimeException {
  private static final String ERROR_MESSAGE = "Credentials are incorrect or user does not exists";
  public AuthenticationException() {
    super(ERROR_MESSAGE);
  }
}
