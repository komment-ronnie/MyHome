package com.myhome.services.springdatajpa;

import com.myhome.configuration.properties.mail.MailProperties;
import com.myhome.configuration.properties.mail.MailTemplatesNames;
import com.myhome.domain.SecurityToken;
import com.myhome.domain.User;
import com.myhome.services.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * is an implementation of the EmailSender interface in Spring Boot application,
 * providing email sending functionality using Thymeleaf templates and the JPA
 * framework. The class provides methods for sending emails, including account confirmed
 * emails, and utilizes the `mailSender` object to send emails. Additionally, it uses
 * a `LocaleContextHolder` to manage the current locale and a `messageSource` to
 * retrieve localized messages.
 */
@Service
@ConditionalOnProperty(value = "spring.mail.devMode", havingValue = "false", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class MailSDJpaService implements MailService {

  private final ITemplateEngine emailTemplateEngine;
  private final JavaMailSender mailSender;
  private final ResourceBundleMessageSource messageSource;
  private final MailProperties mailProperties;

  /**
   * sends a password recovery code via email to the registered email address of a user.
   * 
   * @param user user for whom the password recovery code is being generated and sent.
   * 
   * 	- `user.getName()`: The user's name.
   * 	- `randomCode`: A randomly generated code for password recovery.
   * 
   * @param randomCode 6-digit code that will be sent to the user's registered email
   * address for password recovery.
   * 
   * @returns a boolean value indicating whether an email was sent successfully to the
   * user's registered email address.
   */
  @Override
  public boolean sendPasswordRecoverCode(User user, String randomCode) {
    Map<String, Object> templateModel = new HashMap<>();
    templateModel.put("username", user.getName());
    templateModel.put("recoverCode", randomCode);
    String passwordRecoverSubject = getLocalizedMessage("locale.EmailSubject.passwordRecover");
    boolean mailSent = send(user.getEmail(), passwordRecoverSubject,
        MailTemplatesNames.PASSWORD_RESET.filename, templateModel);
    return mailSent;
  }

  /**
   * maps a user object to a map of variables and then sends an email with a personalized
   * subject based on a localized message and a template file named `PASSWORD_CHANGED`.
   * The function returns whether the mail was sent successfully.
   * 
   * @param user user for whom the password change notification email is to be sent.
   * 
   * 	- `user.getName()` represents the user's name.
   * 	- `user.getEmail()` holds the user's email address.
   * 
   * @returns a boolean value indicating whether an email was sent successfully to the
   * user's registered email address.
   */
  @Override
  public boolean sendPasswordSuccessfullyChanged(User user) {
    Map<String, Object> templateModel = new HashMap<>();
    templateModel.put("username", user.getName());
    String passwordChangedSubject = getLocalizedMessage("locale.EmailSubject.passwordChanged");
    boolean mailSent = send(user.getEmail(), passwordChangedSubject,
        MailTemplatesNames.PASSWORD_CHANGED.filename, templateModel);
    return mailSent;
  }

  /**
   * sends an email to a user's registered email address with a confirmation link to
   * verify their account creation.
   * 
   * @param user user whose account is being created and confirmed.
   * 
   * 	- `user.getName()` - the user's name
   * 
   * The function first creates a `Map` containing the user's name and the email
   * confirmation link (`emailConfirmLink`). Then, it sends an email with the subject
   * `getLocalizedMessage("locale.EmailSubject.accountCreated")` using the
   * `MailTemplatesNames.ACCOUNT_CREATED.filename` template file. The function returns
   * a boolean value indicating whether the email was sent successfully.
   * 
   * @param emailConfirmToken email confirmation token sent to the user's email address
   * for verifying their account creation, which is used as the link in the email
   * notification to confirm the account creation.
   * 
   * 	- `user`: A `User` object representing the user whose account has been created.
   * 	- `emailConfirmToken`: An instance of `SecurityToken` containing the email
   * confirmation link for the newly created account.
   * 
   * @returns a boolean value indicating whether an email was successfully sent to
   * confirm the account creation.
   */
  @Override
  public boolean sendAccountCreated(User user, SecurityToken emailConfirmToken) {
    Map<String, Object> templateModel = new HashMap<>();
    String emailConfirmLink = getAccountConfirmLink(user, emailConfirmToken);
    templateModel.put("username", user.getName());
    templateModel.put("emailConfirmLink", emailConfirmLink);
    String accountCreatedSubject = getLocalizedMessage("locale.EmailSubject.accountCreated");
    boolean mailSent = send(user.getEmail(), accountCreatedSubject,
        MailTemplatesNames.ACCOUNT_CREATED.filename, templateModel);
    return mailSent;
  }

  /**
   * sends an email to a user confirming their account status.
   * 
   * @param user User object containing the user's name and email address for sending
   * an account confirmation email.
   * 
   * 	- `username`: A String representing the user's name.
   * 
   * The function then performs the following operations:
   * 
   * 1/ Creates a new `Map` object called `templateModel`.
   * 2/ Adds a key-value pair to the `templateModel`, where the key is "username" and
   * the value is the deserialized input `user.getName()`.
   * 3/ Sets the subject of the email to be sent using the localized message "locale.EmailSubject.accountConfirmed".
   * 4/ Uses the `send` function to send an email to the user's registered email address
   * with the specified subject and filename.
   * 5/ Returns a boolean value indicating whether the email was successfully sent or
   * not.
   * 
   * @returns a boolean value indicating whether an email was sent to the user's email
   * address.
   */
  @Override
  public boolean sendAccountConfirmed(User user) {
    Map<String, Object> templateModel = new HashMap<>();
    templateModel.put("username", user.getName());
    String accountConfirmedSubject = getLocalizedMessage("locale.EmailSubject.accountConfirmed");
    boolean mailSent = send(user.getEmail(), accountConfirmedSubject,
        MailTemplatesNames.ACCOUNT_CONFIRMED.filename, templateModel);
    return mailSent;
  }

  /**
   * sends an HTML-formatted email message to a recipient using a MailSender object.
   * 
   * @param to email address of the recipient to whom the HTML message should be sent.
   * 
   * @param subject subject line of the sent email in the `MimeMessage` object created
   * by the `mailSender.createMimeMessage()` method.
   * 
   * @param htmlBody HTML content of the message that will be sent to the recipient.
   */
  private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setFrom(mailProperties.getUsername());
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(htmlBody, true);
    mailSender.send(message);
  }

  /**
   * sends an HTML email message to a specified recipient based on a provided template
   * name and model.
   * 
   * @param emailTo email address of the recipient to whom the message will be sent.
   * 
   * @param subject subject line of the email to be sent.
   * 
   * @param templateName name of the Thymeleaf template to be processed and rendered
   * into HTML content for sending as an email message.
   * 
   * @param templateModel mapping of Thymeleaf variables to be used in the email template,
   * which is passed to the `emailTemplateEngine.process()` method for rendering the
   * email template into an HTML body.
   * 
   * 	- `LocaleContextHolder`: A class that provides a way to access the current locale.
   * 	- `Map<String, Object>`: A map of key-value pairs where each key is a string and
   * each value is an object.
   * 	- `emailTo`: A string representing the email address to send the message to.
   * 	- `subject`: A string representing the subject of the message.
   * 	- `templateName`: A string representing the name of the Thymeleaf template to use
   * for the message body.
   * 	- `htmlBody`: A string representing the HTML content of the message body, generated
   * by calling the `process` method of an `emailTemplateEngine` object with the
   * `thymeleafContext` as input.
   * 
   * @returns a boolean value indicating whether the email was sent successfully or not.
   */
  private boolean send(String emailTo, String subject, String templateName, Map<String, Object> templateModel) {
    try {
      Context thymeleafContext = new Context(LocaleContextHolder.getLocale());
      thymeleafContext.setVariables(templateModel);
      String htmlBody = emailTemplateEngine.process(templateName, thymeleafContext);
      sendHtmlMessage(emailTo, subject, htmlBody);
    } catch (MailException | MessagingException mailException) {
      log.error("Mail send error!", mailException);
      return false;
    }
    return true;
  }

  /**
   * generates a URL for email confirmation of a user's account based on the current
   * context path and the user's ID, and token provided.
   * 
   * @param user User object containing information about the user for whom the
   * confirmation link is being generated.
   * 
   * 1/ `user`: A `User` object representing a user in the application. The `User` class
   * has attributes such as `userId`, `email`, and `password`.
   * 2/ `token`: A `SecurityToken` object representing a security token used for
   * authentication purposes. The `SecurityToken` class has attributes such as `token`
   * and `expiresAt`.
   * 
   * @param token email confirmation token for the specified user, which is used to
   * construct the URL for the email confirmation page.
   * 
   * 	- `token.getToken()`: This is a unique identifier for the user's email confirmation
   * request.
   * 
   * @returns a URL string containing the base URL and the user ID and security token
   * parameters.
   */
  private String getAccountConfirmLink(User user, SecurityToken token) {
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
        .replacePath(null)
        .build()
        .toUriString();
    return String.format("%s/users/%s/email-confirm/%s", baseUrl, user.getUserId(), token.getToken());
  }

  /**
   * takes a string parameter `prop` and returns a localized message from a message
   * source using the `getMessage` method. If an exception occurs, it returns a default
   * message indicating a localization error.
   * 
   * @param prop message key to be localized.
   * 
   * @returns a localized message for a given property name, generated from a message
   * source using the current locale.
   */
  private String getLocalizedMessage(String prop) {
    String message = "";
    try {
      message = messageSource.getMessage(prop, null, LocaleContextHolder.getLocale());
    } catch (Exception e) {
      message = prop + ": localization error";
    }
    return message;
  }

}
