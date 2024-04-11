package com.myhome.services.unit;

import com.myhome.domain.SecurityToken;
import com.myhome.domain.SecurityTokenType;
import com.myhome.domain.User;
import com.myhome.repositories.SecurityTokenRepository;
import com.myhome.services.springdatajpa.SecurityTokenSDJpaService;
import helpers.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * is a JUnit test class for testing the SecurityTokenSDJpaService class. The test
 * class sets up mocks for SecurityTokenRepository and injects the service under test.
 * The tests cover the creation of security tokens for password reset, email confirmation,
 * and resetting the password. Each test verifies that the created token has the
 * expected type, lifetime, and owner, and that the token is saved in the repository.
 */
public class SecurityTokenSDJpaServiceTest {

  private final Duration TEST_TOKEN_LIFETIME_SECONDS = Duration.ofDays(1);

  @Mock
  private SecurityTokenRepository securityTokenRepository;

  @InjectMocks
  private SecurityTokenSDJpaService securityTokenSDJpaService;

  /**
   * initializes MockitoAnnotations and sets fields on a `securityTokenSDJpaService`
   * object to simulate token lifetimes for testing purposes.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(securityTokenSDJpaService, "passResetTokenTime",
        TEST_TOKEN_LIFETIME_SECONDS);
    ReflectionTestUtils.setField(securityTokenSDJpaService, "emailConfirmTokenTime",
        TEST_TOKEN_LIFETIME_SECONDS);
  }

  /**
   * creates a new security token for a user based on their user ID, SecurityTokenType,
   * and generates a unique token owner. It also checks if the creation date is before
   * the expiry date and lifetime matches a predefined value. Finally, it verifies the
   * save operation was performed correctly.
   */
  @Test
  void createSecurityToken() {
    // given
    User user = new User();
    user.setUserId(TestUtils.General.generateUniqueId());
    SecurityTokenType testTokenType = SecurityTokenType.RESET;
    when(securityTokenRepository.save(any()))
        .then(returnsFirstArg());

    // when
    SecurityToken actualSecurityToken = securityTokenSDJpaService.createPasswordResetToken(user);
    LocalDate creationDate = actualSecurityToken.getCreationDate();
    LocalDate expiryDate = actualSecurityToken.getExpiryDate();
    Duration lifetime = Duration.between(creationDate.atStartOfDay(), expiryDate.atStartOfDay());

    // then
    assertEquals(actualSecurityToken.getTokenType(), testTokenType);
    assertTrue(creationDate.isBefore(expiryDate));
    assertEquals(lifetime, TEST_TOKEN_LIFETIME_SECONDS);
    assertEquals(user, actualSecurityToken.getTokenOwner());
    assertNotNull(actualSecurityToken.getToken());
    verify(securityTokenRepository).save(any());
  }

  /**
   * generates a security token for a user, sets its properties, and saves it to the repository.
   */
  @Test
  void createPasswordResetToken() {
    // given
    User user = new User();
    user.setUserId(TestUtils.General.generateUniqueId());
    when(securityTokenRepository.save(any()))
        .then(returnsFirstArg());

    // when
    SecurityToken actualSecurityToken = securityTokenSDJpaService.createPasswordResetToken(user);
    LocalDate creationDate = actualSecurityToken.getCreationDate();
    LocalDate expiryDate = actualSecurityToken.getExpiryDate();
    Duration lifetime = Duration.between(creationDate.atStartOfDay(), expiryDate.atStartOfDay());

    // then
    assertEquals(actualSecurityToken.getTokenType(), SecurityTokenType.RESET);
    assertTrue(creationDate.isBefore(expiryDate));
    assertEquals(lifetime, TEST_TOKEN_LIFETIME_SECONDS);
    assertEquals(user, actualSecurityToken.getTokenOwner());
    assertNotNull(actualSecurityToken.getToken());
    verify(securityTokenRepository).save(any());
  }

  /**
   * generates an email confirmation token for a user and saves it to the repository,
   * checking its validity and ownership.
   */
  @Test
  void createEmailConfirmToken() {
    // given
    User user = new User();
    user.setUserId(TestUtils.General.generateUniqueId());
    when(securityTokenRepository.save(any()))
        .then(returnsFirstArg());

    // when
    SecurityToken actualSecurityToken = securityTokenSDJpaService.createEmailConfirmToken(user);
    LocalDate creationDate = actualSecurityToken.getCreationDate();
    LocalDate expiryDate = actualSecurityToken.getExpiryDate();
    Duration lifetime = Duration.between(creationDate.atStartOfDay(), expiryDate.atStartOfDay());

    // then
    assertEquals(actualSecurityToken.getTokenType(), SecurityTokenType.EMAIL_CONFIRM);
    assertTrue(creationDate.isBefore(expiryDate));
    assertEquals(lifetime, TEST_TOKEN_LIFETIME_SECONDS);
    assertEquals(user, actualSecurityToken.getTokenOwner());
    assertNotNull(actualSecurityToken.getToken());
    verify(securityTokenRepository).save(any());
  }

}
