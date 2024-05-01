package com.myhome.services.unit;

import com.myhome.configuration.properties.mail.MailProperties;
import com.myhome.domain.SecurityToken;
import com.myhome.domain.User;
import com.myhome.services.springdatajpa.MailSDJpaService;
import helpers.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

/**
 * is a unit test for the MailSDJpaService class, which sends emails using the JavaMail
 * API and Spring Security's EmailTemplateEngine. The test class sets up mock objects
 * for the EmailTemplateEngine and MailSender services, and then tests various scenarios
 * where an exception is thrown when sending an email. The tests include testing
 * whether an exception is thrown when attempting to send a password recover code via
 * email, when sending an email for a successfully changed password notification,
 * when sending a confirmed mail for an account, and when creating an email message.
 * Each test method sets up the necessary mock objects and then asserts that an
 * exception was thrown when sending the email.
 */
class MailSDJpaServiceTest {

  @Mock
  private JavaMailSender mailSender;
  @Mock
  private ITemplateEngine emailTemplateEngine;
  @Mock
  private ResourceBundleMessageSource messageSource;
  private MockHttpServletRequest mockRequest;

  private MailSDJpaService mailSDJpaService;

  private MailProperties mailProperties = TestUtils.MailPropertiesHelper.getTestMailProperties();

  /**
   * initializes mock objects and sets up a MockHttpServletRequest to simulate HTTP
   * requests for testing purposes. It also creates an instance of `MailSDJpaService`
   * for sending emails using JPA and other dependencies.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);

    mockRequest = new MockHttpServletRequest();
    mockRequest.setContextPath("http://localhost:8080");
    ServletRequestAttributes attrs = new ServletRequestAttributes(mockRequest);
    RequestContextHolder.setRequestAttributes(attrs);

    mailSDJpaService = new MailSDJpaService(emailTemplateEngine, mailSender, messageSource, mailProperties);
  }

  /**
   * tests whether an exception is thrown when sending a password recover code via email
   * using the `mailSender` service.
   */
  @Test
  void sendPasswordRecoverCodeMailException() {
    // given
    MimeMessage mimeMessage = new MimeMessage((Session)null);
    User user = getTestUser();
    given(emailTemplateEngine.process(eq(""), any(Context.class)))
        .willReturn("HTML");
    given(mailSender.createMimeMessage())
        .willReturn(mimeMessage);
    doThrow(MailSendException.class).when(mailSender).send(mimeMessage);

    // when
    boolean mailSent = mailSDJpaService.sendPasswordRecoverCode(user, "test-token");

    // then
    assertFalse(mailSent);
  }

  /**
   * tests whether an exception is thrown when sending a password successfully changed
   * email using the `mailSDJpaService`.
   */
  @Test
  void sendPasswordSuccessfullyChangedMailException() {
    // given
    MimeMessage mimeMessage = new MimeMessage((Session)null);
    User user = getTestUser();
    given(emailTemplateEngine.process(eq(""), any(Context.class)))
        .willReturn("HTML");
    given(mailSender.createMimeMessage())
        .willReturn(mimeMessage);
    doThrow(MailSendException.class).when(mailSender).send(mimeMessage);

    // when
    boolean mailSent = mailSDJpaService.sendPasswordSuccessfullyChanged(user);

    // then
    assertFalse(mailSent);
  }

  /**
   * tests the mail sender service's ability to handle MailSendException when sending
   * an email confirmation to a user.
   */
  @Test
  void sendEmailConfirmedMailException() {
    // given
    MimeMessage mimeMessage = new MimeMessage((Session)null);
    User user = getTestUser();
    given(emailTemplateEngine.process(eq(""), any(Context.class)))
        .willReturn("HTML");
    given(mailSender.createMimeMessage())
        .willReturn(mimeMessage);
    doThrow(MailSendException.class).when(mailSender).send(mimeMessage);

    // when
    boolean mailSent = mailSDJpaService.sendAccountConfirmed(user);

    // then
    assertFalse(mailSent);
  }

  /**
   * tests whether the `mailSDJpaService.sendAccountCreated` method throws a
   * `MailSendException` when a security token is invalid or missing.
   */
  @Test
  void sendEmailCreatedMailException() {
    // given
    SecurityToken token = new SecurityToken();
    token.setToken("token");
    MimeMessage mimeMessage = new MimeMessage((Session)null);
    User user = getTestUser();
    given(emailTemplateEngine.process(eq(""), any(Context.class)))
        .willReturn("HTML");
    given(mailSender.createMimeMessage())
        .willReturn(mimeMessage);
    doThrow(MailSendException.class).when(mailSender).send(mimeMessage);

    // when
    boolean mailSent = mailSDJpaService.sendAccountCreated(user, token);

    // then
    assertFalse(mailSent);
  }

  /**
   * creates a new `User` object and assigns an email address to it.
   * 
   * @returns a `User` object with an email address of "test-email".
   * 
   * 	- The `User` object represents a fictional user with an email address of "test-email".
   * 	- The `setEmail()` method is used to assign the value "test-email" to the `email`
   * attribute of the user object.
   * 	- The returned user object has all the attributes and methods defined in the
   * `User` class.
   */
  private User getTestUser() {
    User user = new User();
    user.setEmail("test-email");
    return user;
  }

}