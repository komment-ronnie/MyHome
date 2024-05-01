package com.myhome.services.springdatajpa;

import com.myhome.domain.SecurityToken;
import com.myhome.domain.User;
import com.myhome.services.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

/**
 * is a mail service implementation that sends password recovery codes, account
 * confirmation messages, and account creation messages to users via email. It also
 * provides methods for sending password successfully changed notifications and email
 * confirmation tokens for newly created accounts.
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "spring.mail.dev-mode", havingValue = "true", matchIfMissing = true)
public class DevMailSDJpaService implements MailService {

  /**
   * sends a password recover code to a specified user via log messages and returns `true`.
   * 
   * @param user User object containing the details of the user for whom the password
   * recovery code is being sent.
   * 
   * 	- `user.getUserId()`: This property returns the user ID of the user for whom the
   * password recovery code is being sent.
   * 
   * The function then logs an information message using the `log.info()` method, which
   * includes the user ID and the random code generated for password recovery. Finally,
   * the function returns `true`.
   * 
   * @param randomCode 4-digit password recover code sent to the user via email.
   * 
   * @returns a string representing the password recover code sent to the specified user.
   */
  @Override
  public boolean sendPasswordRecoverCode(User user, String randomCode) throws MailSendException {
    log.info(String.format("Password recover code sent to user with id= %s, code=%s", user.getUserId()), randomCode);
    return true;
  }

  /**
   * sends a message to a user with their ID when the account is confirmed.
   * 
   * @param user User object containing information about the user whose account
   * confirmation message is being sent.
   * 
   * 	- `user.getUserId()` returns an integer representing the user's unique identifier.
   * 
   * The function logs an informative message using the `log.info()` method and then
   * returns `true`.
   * 
   * @returns a message indicating that the account has been confirmed for the provided
   * user with their ID.
   */
  @Override
  public boolean sendAccountConfirmed(User user) {
    log.info(String.format("Account confirmed message sent to user with id=%s", user.getUserId()));
    return true;
  }

  /**
   * informs a user via a log message that their password has been successfully changed.
   * 
   * @param user User object containing information about the user for whom the password
   * change was successfuly completed.
   * 
   * 	- `user.getUserId()` - returns the user ID of the user whose password has been
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
   * sends a message to a user upon account creation, logging the event and returning
   * `true`.
   * 
   * @param user User object containing information about the created account, which
   * is passed to the function for processing.
   * 
   * 	- `user`: The user object contains several attributes such as `UserId`, `Email`,
   * `DisplayName`, and `SecurityToken`.
   * 	- `UserId`: A unique identifier for the user.
   * 	- `Email`: The email address associated with the user's account.
   * 	- `DisplayName`: The user's display name.
   * 	- `SecurityToken`: An email confirmation token generated by the system to verify
   * the user's identity.
   * 
   * @param emailConfirmToken email confirmation token sent to the user's registered
   * email address for verification purposes before their account is activated.
   * 
   * 	- `User user`: A `User` object representing the user whose account was created.
   * 	- `SecurityToken emailConfirmToken`: An instance of `SecurityToken` that contains
   * information about the user's email confirmation token.
   * 
   * @returns a message indicating that an account has been created and sent to the
   * user with their ID.
   */
  @Override
  public boolean sendAccountCreated(User user, SecurityToken emailConfirmToken) {
    log.info(String.format("Account created message sent to user with id=%s", user.getUserId()));
    return true;
  }


}
