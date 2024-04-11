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
 * is a Java-based implementation of a mail service for a Spring Boot application.
 * It provides methods for sending emails with customizable templates and handles the
 * mail sending process through JavaMailSender and EmailTemplateEngine interfaces.
 * The class also uses a ResourceBundleMessageSource for message localization.
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
   * sends an email with a password recover code to the user's registered email address
   * if the email is valid and the code is generated successfully.
   * 
   * @param user User object whose password recovery email is being sent.
   * 
   * 	- `user.getName()` represents the user's name.
   * 	- `randomCode` is a String that contains a randomly generated password recover code.
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
   * maps user data to a message and sends an email to the user's registered address
   * with the subject "password changed".
   * 
   * @param user user for whom the password change notification should be sent.
   * 
   * 	- `name`: The user's name.
   * 
   * The function then performs the following actions:
   * 
   * 1/ Creates a new `Map<String, Object>` object called `templateModel`.
   * 2/ Adds an entry to the map with the key `"username"` and the value of the
   * `user.getName()` property.
   * 3/ Sets the `passwordChangedSubject` variable to the localized message "locale.EmailSubject.passwordChanged".
   * 4/ Uses the `send()` function to send an email to the user's email address with
   * the subject set to `passwordChangedSubject`.
   * 5/ Returns a boolean value indicating whether the email was sent successfully or
   * not.
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
   * takes a user and an email confirmation token as input, generates a customized email
   * template with the user's name and the link to confirm their account, and sends it
   * to the user's email address using the `send` method. If the mail is sent successfully,
   * the function returns `true`.
   * 
   * @param user User object containing the user's information.
   * 
   * 	- `user`: A `User` object containing attributes such as `name`, which is used to
   * put into the template model.
   * 
   * A map with two key-value pairs is created from the `templateModel`. The first key
   * is "username," which gets assigned the value of `user.name`. The second key is
   * "emailConfirmLink," which gets assigned the value of `getAccountConfirmLink(user,
   * emailConfirmToken)`.
   * 
   * The function then sends an email using the `send` method. The subject of the email
   * is retrieved from a localized message using the `getLocalizedMessage` method. The
   * filename for the template used in the mail is retrieved from the `MailTemplatesNames`.
   * 
   * @param emailConfirmToken token that is sent to the user's email address for email
   * confirmation.
   * 
   * 	- `user`: A `User` object representing the user whose account was created.
   * 	- `securityToken`: An object of type `SecurityToken` containing the confirmation
   * link for the user's email address.
   * 
   * @returns a boolean value indicating whether an email was successfully sent to
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
   * sends an email to a user confirming their account.
   * 
   * @param user User object containing the user's name and email address for sending
   * an account confirmation email.
   * 
   * 	- `user.getName()` - retrieves the user's name.
   * 
   * The template model is created by assigning key-value pairs to it using the curly
   * braces `{}`. These keys correspond to placeholders in the email template, which
   * will be replaced with actual values during rendering.
   * 
   * In the `send` method, the email is sent using the provided subject and template
   * file name. The `templateModel` is passed as an argument to this method for populating
   * the email's placeholders with actual data.
   * 
   * @returns a boolean value indicating whether an email was sent successfully to the
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
   * sends an HTML-formatted message to a recipient through a messaging system using
   * the `mailSender` object's `createMimeMessage` method and sets various parameters
   * such as from, to, subject, and text.
   * 
   * @param to email address of the recipient to whom the HTML message is being sent.
   * 
   * @param subject subject line of the email that is to be sent.
   * 
   * @param htmlBody HTML message body that will be sent to the recipient.
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
   * sends an HTML email to a specified recipient using Thymeleaf template engine. It
   * takes a template name, model variables, and locale as input and returns a boolean
   * value indicating whether the email was sent successfully.
   * 
   * @param emailTo email address of the recipient to whom the email message is being
   * sent.
   * 
   * @param subject subject line of the email to be sent.
   * 
   * @param templateName name of the Thymeleaf template to be processed and rendered
   * into HTML content.
   * 
   * @param templateModel data that will be used to populate the email template's
   * placeholders, allowing for dynamic content and personalization in the email message.
   * 
   * 	- `LocaleContextHolder`: This is an instance of the `LocaleContextHolder` class,
   * which provides access to the current locale and can be used to retrieve the current
   * locale.
   * 	- `thymeleafContext`: This is a new instance of the `Context` class, which is
   * created with the current locale set using the `LocaleContextHolder`. The context
   * contains variables that are passed in through the `templateModel`.
   * 	- `emailTo`: This is a string representing the email address to send the message
   * to.
   * 	- `subject`: This is a string representing the subject line of the email.
   * 	- `templateName`: This is a string representing the name of the Thymeleaf template
   * to use for sending the email.
   * 	- `templateModel`: This is a map of key-value pairs that contain variables that
   * are passed in through the function and can be used in the Thymeleaf template.
   * 
   * @returns a boolean value indicating whether the email was sent successfully.
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
   * generates a hyperlink for an email confirmation process, based on the user ID and
   * security token.
   * 
   * @param user User object whose email confirmation link is to be generated.
   * 
   * 	- `user`: A `User` object with properties such as `UserId`, `Email`, and `Token`.
   * 
   * The function returns a string in the format of a URL, constructed by combining the
   * base URL `ServletUriComponentsBuilder.fromCurrentContextPath()` with the user ID,
   * token, and `/email-confirm/` path segment.
   * 
   * @param token email confirmation token for the specified user, which is used to
   * generate the final URL for the email confirmation link.
   * 
   * 	- `token`: A SecurityToken object with attributes such as `getToken()` for
   * retrieving the token value.
   * 
   * @returns a URL string constructed from the current context path and the parameters
   * `user.getUserId()` and `token.getToken()`.
   */
  private String getAccountConfirmLink(User user, SecurityToken token) {
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
        .replacePath(null)
        .build()
        .toUriString();
    return String.format("%s/users/%s/email-confirm/%s", baseUrl, user.getUserId(), token.getToken());
  }

  /**
   * retrieves a message from a message source based on a given property name and returns
   * the resulting localized message.
   * 
   * @param prop property key to be localized.
   * 
   * @returns a localized message based on a provided property and the current locale.
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
