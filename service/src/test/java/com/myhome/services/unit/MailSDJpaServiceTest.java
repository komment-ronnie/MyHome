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
 * is a testing class for the MailSDJpaService class, which is responsible for sending
 * emails through JavaMail API. The test class provides mock objects for JavaMailSender,
 * ITemplateEngine, and ResourceBundleMessageSource, and sets up a mock HTTP request
 * to simulate email sending. The tests verify that email sending methods throw
 * expected exceptions when encountering issues with the mail server or email template
 * processing.
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
   * initializes the Mockito annotations, creates a mock HTTP request object, and sets
   * up the RequestContextHolder with the mock request attributes. It also creates an
   * instance of the `MailSDJpaService`.
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
   * tests whether an exception is thrown when attempting to send a password recover
   * code via email using the `mailSender` service. It does so by mocking the
   * `emailTemplateEngine` and `mailSender` services to throw a `MailSendException`,
   * and then asserting that the `mailSent` variable is set to `false`.
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
   * tests whether a MailSendException is thrown when sending an email for password
   * successfully changed notification.
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
   * tests whether an exception is thrown when attempting to send a confirmed mail for
   * an account using the `mailSender`.
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
   * tests the mailSDJpaService's sendAccountCreated method by throwing a MailSendException
   * when creating an email message.
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
   * creates a new `User` object with an email address of "test-email". The function
   * returns the created `User` object.
   * 
   * @returns a `User` object with an email address of "test-email".
   * 
   * 	- `email`: The email address of the user, set to `"test-email"`.
   * 	- `User`: The class type of the user object, indicating its attributes and methods.
   */
  private User getTestUser() {
    User user = new User();
    user.setEmail("test-email");
    return user;
  }

}