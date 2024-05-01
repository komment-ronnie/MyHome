package com.myhome.services.springdatajpa;

import com.myhome.domain.SecurityToken;
import com.myhome.domain.User;
import com.myhome.services.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

/**
 * is a Java class that provides mail services for sending various messages to users
 * in a Spring Boot application. It implements the MailService interface and provides
 * four methods for sending password recover codes, account confirmation messages,
 * password change success messages, and account creation messages. The class uses
 * log4j2 for logging and conditionally enables the service based on a property in
 * the application configuration.
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "spring.mail.dev-mode", havingValue = "true", matchIfMissing = true)
public class DevMailSDJpaService implements MailService {

  /**
   * sends a password recovery code to a specified user via logging the event and
   * returning `true`.
   * 
   * @param user User object that contains information about the user for whom the
   * password recovery code is being sent.
   * 
   * 	- `User user`: This parameter represents an object of type `User`, which has
   * fields such as `getUserId()` and `randomCode()`.
   * 
   * @param randomCode 6-digit password recover code that is sent to the user via email.
   * 
   * @returns a success message indicating that the password recover code has been sent
   * to the user.
   */
  @Override
  public boolean sendPasswordRecoverCode(User user, String randomCode) throws MailSendException {
    log.info(String.format("Password recover code sent to user with id= %s, code=%s", user.getUserId()), randomCode);
    return true;
  }

  /**
   * sends a message to a user confirming their account status.
   * 
   * @param user User object containing information about the user whose account
   * confirmation message should be sent.
   * 
   * 	- `user.getUserId()`: The unique identifier for the user.
   * 
   * @returns a boolean value indicating that the account confirmation message was
   * successfully sent to the user.
   */
  @Override
  public boolean sendAccountConfirmed(User user) {
    log.info(String.format("Account confirmed message sent to user with id=%s", user.getUserId()));
    return true;
  }

  /**
   * sends a message to a user indicating that their password has been successfully changed.
   * 
   * @param user User object containing information about the user whose password has
   * been successfully changed.
   * 
   * 	- `user.getUserId()`: This property retrieves the user ID of the user whose
   * password has been successfully changed.
   * 	- `log.info()`: This method logs an informational message to the application's
   * log file, providing details about the successful change of the user's password.
   * 
   * @returns a message indicating that the password has been successfully changed,
   * with the user's ID included.
   */
  @Override
  public boolean sendPasswordSuccessfullyChanged(User user) {
    log.info(String.format("Password successfully changed message sent to user with id=%s", user.getUserId()));
    return true;
  }


  /**
   * sends an account creation message to a user upon successful email confirmation.
   * 
   * @param user user whose account has been created and is used to log an information
   * message in the application's logger.
   * 
   * 	- `user.getUserId()`: This property represents the unique identifier of the user.
   * 
   * The function logs an informational message in the application log using `log.info()`.
   * The message includes the value of `user.getUserId()`, which is the primary key of
   * the user entity.
   * 
   * @param emailConfirmToken email confirmation token sent to the user's registered
   * email address for account verification purposes.
   * 
   * 	- `SecurityToken emailConfirmToken`: This represents an object that contains a
   * unique token for email confirmation, which is used to verify the user's identity
   * and authenticate their account creation. The token may contain attributes such as
   * the user ID, email address, and a one-time password or verification code.
   * 
   * @returns a boolean value indicating that the account creation message was successfully
   * sent to the user.
   */
  @Override
  public boolean sendAccountCreated(User user, SecurityToken emailConfirmToken) {
    log.info(String.format("Account created message sent to user with id=%s", user.getUserId()));
    return true;
  }


}
