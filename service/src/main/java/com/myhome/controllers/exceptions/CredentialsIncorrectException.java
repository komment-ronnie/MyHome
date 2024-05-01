package com.myhome.controllers.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * is a sub-class of AuthenticationException with an additional constructor to log
 * error messages for incorrect user credentials using Lombok's @Slf4j annotation.
 */
@Slf4j
public class CredentialsIncorrectException extends AuthenticationException {
  public CredentialsIncorrectException(String userId) {
    super();
    log.info("Credentials are incorrect for userId: " + userId);
  }
}
