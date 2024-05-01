package com.myhome.controllers.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * is an extension of AuthenticationException with a constructor logging an error
 * message for incorrect credentials of a specified user ID using Lombok's @Slf4j
 * annotation to log the error message.
 */
@Slf4j
public class CredentialsIncorrectException extends AuthenticationException {
  public CredentialsIncorrectException(String userId) {
    super();
    log.info("Credentials are incorrect for userId: " + userId);
  }
}
