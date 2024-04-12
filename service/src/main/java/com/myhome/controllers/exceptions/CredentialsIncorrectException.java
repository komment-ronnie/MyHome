package com.myhome.controllers.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * extends AuthenticationException and has a constructor that logs an error message
 * for incorrect credentials for a specified user ID.
 */
@Slf4j
public class CredentialsIncorrectException extends AuthenticationException {
  public CredentialsIncorrectException(String userId) {
    super();
    log.info("Credentials are incorrect for userId: " + userId);
  }
}
