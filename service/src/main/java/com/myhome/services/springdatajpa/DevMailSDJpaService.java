package com.myhome.services.springdatajpa;

import com.myhome.domain.SecurityToken;
import com.myhome.domain.User;
import com.myhome.services.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

/**
 * is a Java class that implements the MailService interface and provides methods for
 * sending messages to users. The class is configured to only execute when the property
 * "spring.mail.dev-mode" is set to "true".
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "spring.mail.dev-mode", havingValue = "true", matchIfMissing = true)
public class DevMailSDJpaService implements MailService {

  /**
   * sends a password recovery code to a user via email.
   * 
   * @param user User object whose password recovery code is being sent.
   * 
   * 	- `user`: A `User` object, containing attributes such as `userId`, `email`, and
   * potentially others.
   * 
   * @param randomCode 6-digit password recover code sent to the user via email.
   * 
   * @returns a boolean value indicating whether the password recover code was successfully
   * sent to the user.
   */
  @Override
  public boolean sendPasswordRecoverCode(User user, String randomCode) throws MailSendException {
    log.info(String.format("Password recover code sent to user with id= %s, code=%s", user.getUserId()), randomCode);
    return true;
  }

  /**
   * sends a message to a user with a specified ID indicating that their account has
   * been confirmed.
   * 
   * @param user User object containing information about the user whose account
   * confirmation message should be sent.
   * 
   * 	- `UserId`: an integer representing the unique identifier for the user.
   * 
   * The function logs an informational message using `log.info()` with a custom message
   * formatted by concatenating the string "Account confirmed message sent to user with
   * id=" followed by the value of `user.getUserId()`. Finally, the function returns `true`.
   * 
   * @returns a message indicating that the account has been confirmed for the specified
   * user.
   */
  @Override
  public boolean sendAccountConfirmed(User user) {
    log.info(String.format("Account confirmed message sent to user with id=%s", user.getUserId()));
    return true;
  }

  /**
   * sends a message to a user indicating that their password has been successfully changed.
   * 
   * @param user User object containing the user's information for whom the password
   * change notification is being sent.
   * 
   * 	- `user.getUserId()` - retrieves the user ID of the user whose password was
   * successfully changed.
   * 
   * @returns a message indicating that the password has been successfully changed,
   * along with the user's ID.
   */
  @Override
  public boolean sendPasswordSuccessfullyChanged(User user) {
    log.info(String.format("Password successfully changed message sent to user with id=%s", user.getUserId()));
    return true;
  }


  /**
   * sends an account creation message to a user with a unique identifier.
   * 
   * @param user User object containing information about the created account, which
   * is passed to the function for processing.
   * 
   * 	- `user`: The user object containing information such as user ID (`getUserId()`),
   * email address (`getEmail()`), and security token (`emailConfirmToken`).
   * 
   * @param emailConfirmToken SecurityToken that will be sent to the user's email address
   * for email confirmation.
   * 
   * 	- `User user`: The user object whose account has been created.
   * 	- `SecurityToken emailConfirmToken`: A SecurityToken object representing an email
   * confirmation token for the newly created account.
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
