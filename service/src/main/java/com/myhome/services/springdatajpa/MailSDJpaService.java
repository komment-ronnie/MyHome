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
 * is responsible for sending emails through Java mail API using Thymeleaf templates
 * for email bodies. It provides methods for sending emails with customizable subject
 * lines, email bodies, and attachments. The service also handles security tokens for
 * email confirmation links and provides localized messages from a message source
 * using the current locale.
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
   * generates a random password recovery code for a user and sends it to their registered
   * email address via an email with a customized subject.
   * 
   * @param user user for whom the password recovery code is being generated and sent.
   * 
   * 	- `user.getName()`: Returns the user's name as a string.
   * 	- `user.getEmail()`: Returns the user's email address as a string.
   * 
   * @param randomCode 6-digit code sent to the user's email address for password recovery.
   * 
   * @returns a boolean value indicating whether an email was successfully sent to the
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
   * maps a user's name and email to a password change notification subject and sends
   * an email with the template contents.
   * 
   * @param user user whose password is being sent a notification of successful change.
   * 
   * 	- `user`: The input parameter, representing a `User` object containing information
   * about the user whose password has been successfully changed.
   * 	- `getName()`: A method of the `User` class returning the user's name.
   * 	- `getEmail()`: A method of the `User` class returning the user's email address.
   * 	- `getLocalizedMessage()`: A method of the `user` object returning a localized
   * message for the specified key.
   * 
   * @returns a boolean value indicating whether an email was successfully sent to the
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
   * 	- `user`: A `User` object containing information about the user who created an
   * account. Its properties include `getName()` (a string representing the user's
   * name), and `getEmail()` (a string representing the user's email address).
   * 
   * @param emailConfirmToken email confirmation token sent to the user's email address
   * for verifying their email address during account creation.
   * 
   * 	- `User user`: The user whose account was created.
   * 	- `SecurityToken emailConfirmToken`: A token used to confirm the user's email address.
   * 	- `getAccountConfirmLink(user, emailConfirmToken)`: A function that generates a
   * link for the user to confirm their email address.
   * 	- `getLocalizedMessage("locale.EmailSubject.accountCreated")`: A function that
   * returns a localized message for the subject of an email sent to confirm the user's
   * account creation.
   * 	- `send(user.getEmail(), accountCreatedSubject, MailTemplatesNames.ACCOUNT_CREATED.filename,
   * templateModel)`: A function that sends an email to confirm the user's account
   * creation using a pre-defined template file named `MailTemplatesNames.ACCOUNT_CREATED`.
   * 
   * @returns a boolean value indicating whether an email was sent successfully to
   * confirm the user's account creation.
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
   * 	- `user`: A `User` object representing the user for whom account confirmation is
   * being sent. The `User` class has properties such as `getName()`, `getEmail()` and
   * others.
   * 
   * @returns a boolean value indicating whether an email was successfully sent to the
   * user's registered email address.
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
   * sends an HTML-formatted message through a messaging system using the `mailSender`.
   * 
   * @param to email address of the recipient to whom the HTML message is being sent.
   * 
   * @param subject subject of an email that is being sent through the `mailSender` object.
   * 
   * @param htmlBody HTML message body that will be sent to the recipient through email.
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
   * takes an email address, subject line, template name, and a map of template model
   * variables as input. It uses Thymeleaf to process the template and generates an
   * HTML message body, which is then sent via email using the `sendHtmlMessage` method.
   * If any errors occur during email sending, the function returns `false`.
   * 
   * @param emailTo email address to which the email message will be sent.
   * 
   * @param subject subject line of the email to be sent.
   * 
   * @param templateName name of the Thymeleaf template to be processed and rendered
   * into an HTML message.
   * 
   * @param templateModel map of data that is used to populate the Thymeleaf template,
   * which is then rendered as an HTML message and sent via email.
   * 
   * 	- `LocaleContextHolder`: The Locale context holder is used to obtain the current
   * locale.
   * 	- `TemplateEngine`: An instance of the `EmailTemplateEngine` class, which is
   * responsible for rendering the email template.
   * 	- `Map<String, Object>`: A map containing key-value pairs representing the variables
   * that can be used in the template. These variables are passed as arguments to the
   * `process()` method of the `EmailTemplateEngine` instance.
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
   * generates a unique URL for email confirmation of a user's account based on the
   * current context path and the user's ID, using a predefined format string.
   * 
   * @param user User object containing information about the user for whom the
   * confirmation link is being generated.
   * 
   * 	- `user`: A `User` object containing information about the user whose email
   * confirmation link is being generated. The object may have attributes such as
   * `UserId`, `Email`, and `Username`.
   * 	- `token`: An instance of `SecurityToken` representing the token used to generate
   * the confirmation link. The token may contain properties such as `Token` and `Issuer`.
   * 
   * @param token SecurityToken returned by the email confirmation endpoint, which is
   * used to verify the user's identity and retrieve their email confirmation status.
   * 
   * 	- `user`: The user object passed as an argument, which contains the `UserId` property.
   * 	- `SecurityToken`: The token object that contains additional attributes such as
   * `token`, `expiresIn`, and `iat`.
   * 
   * @returns a URL string that includes the user ID and security token for email confirmation.
   */
  private String getAccountConfirmLink(User user, SecurityToken token) {
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
        .replacePath(null)
        .build()
        .toUriString();
    return String.format("%s/users/%s/email-confirm/%s", baseUrl, user.getUserId(), token.getToken());
  }

  /**
   * retrieves a localized message from a message source based on a given property name,
   * handling exceptions and providing a fallback message when localization fails.
   * 
   * @param prop property key to be localized, which is passed to the `getMessage()`
   * method of the `MessageSource` interface to retrieve the localized message.
   * 
   * @returns a localized message for a given property name.
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
