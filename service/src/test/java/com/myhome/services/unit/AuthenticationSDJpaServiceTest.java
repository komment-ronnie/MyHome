package com.myhome.services.unit;

import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.exceptions.CredentialsIncorrectException;
import com.myhome.controllers.exceptions.UserNotFoundException;
import com.myhome.domain.AuthenticationData;
import com.myhome.model.LoginRequest;
import com.myhome.security.jwt.AppJwt;
import com.myhome.security.jwt.AppJwtEncoderDecoder;
import com.myhome.services.springdatajpa.AuthenticationSDJpaService;
import com.myhome.services.springdatajpa.UserSDJpaService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * is a unit test for the AuthenticationSDJpaService class, which handles user
 * authentication tasks using JPA and Spring Security. The test class provides various
 * test methods to verify the functionality of the AuthenticationSDJpaService class,
 * including testing the login process, handling of invalid credentials, and generating
 * of AppJwt tokens with expiration times based on the current date and user ID.
 */
public class AuthenticationSDJpaServiceTest {

  private final String USER_ID = "test-user-id";
  private final String USERNAME = "test-user-name";
  private final String USER_EMAIL = "test-user-email";
  private final String USER_PASSWORD = "test-user-password";
  private final String REQUEST_PASSWORD = "test-request-password";
  private final Duration TOKEN_LIFETIME = Duration.ofDays(1);
  private final String SECRET = "secret";

  @Mock
  private final UserSDJpaService userSDJpaService = mock(UserSDJpaService.class);
  @Mock
  private final AppJwtEncoderDecoder appJwtEncoderDecoder = mock(AppJwtEncoderDecoder.class);
  @Mock
  private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
  private final AuthenticationSDJpaService authenticationSDJpaService =
      new AuthenticationSDJpaService(TOKEN_LIFETIME, SECRET, userSDJpaService, appJwtEncoderDecoder,
          passwordEncoder);

  /**
   * tests the login functionality of a system by providing a valid user request and
   * password, and verifying that the resulting JWT token is correctly generated and
   * matches the expected values.
   */
  @Test
  void loginSuccess() {
    // given
    LoginRequest request = getDefaultLoginRequest();
    UserDto userDto = getDefaultUserDtoRequest();
    AppJwt appJwt = getDefaultJwtToken(userDto);
    String encodedJwt = appJwtEncoderDecoder.encode(appJwt, SECRET);
    given(userSDJpaService.findUserByEmail(request.getEmail()))
        .willReturn(Optional.of(userDto));
    given(passwordEncoder.matches(request.getPassword(), userDto.getEncryptedPassword()))
        .willReturn(true);
    given(appJwtEncoderDecoder.encode(appJwt, SECRET))
        .willReturn(encodedJwt);

    // when
    AuthenticationData authenticationData = authenticationSDJpaService.login(request);

    // then
    assertNotNull(authenticationData);
    assertEquals(authenticationData.getUserId(), userDto.getUserId());
    assertEquals(authenticationData.getJwtToken(), encodedJwt);
    verify(userSDJpaService).findUserByEmail(request.getEmail());
    verify(passwordEncoder).matches(request.getPassword(), userDto.getEncryptedPassword());
    verify(appJwtEncoderDecoder).encode(appJwt, SECRET);
  }

  /**
   * tests whether an exception is thrown when a user with the provided email address
   * is not found in the database.
   */
  @Test
  void loginUserNotFound() {
    // given
    LoginRequest request = getDefaultLoginRequest();
    given(userSDJpaService.findUserByEmail(request.getEmail()))
        .willReturn(Optional.empty());

    // when and then
    assertThrows(UserNotFoundException.class,
        () -> authenticationSDJpaService.login(request));
  }

  /**
   * tests whether an invalid login attempt leads to a CredentialsIncorrectException
   * being thrown by the `authenticationSDJpaService`.
   */
  @Test
  void loginCredentialsAreIncorrect() {
    // given
    LoginRequest request = getDefaultLoginRequest();
    UserDto userDto = getDefaultUserDtoRequest();
    given(userSDJpaService.findUserByEmail(request.getEmail()))
        .willReturn(Optional.of(userDto));
    given(passwordEncoder.matches(request.getPassword(), userDto.getEncryptedPassword()))
        .willReturn(false);

    // when and then
    assertThrows(CredentialsIncorrectException.class,
        () -> authenticationSDJpaService.login(request));
  }

  /**
   * creates a new `LoginRequest` object with email address set to `USER_EMAIL` and
   * password set to `REQUEST_PASSWORD`.
   * 
   * @returns a `LoginRequest` object containing email and password parameters.
   * 
   * 	- `email`: The email address of the user to be authenticated.
   * 	- `password`: The password of the user to be authenticated.
   */
  private LoginRequest getDefaultLoginRequest() {
    return new LoginRequest().email(USER_EMAIL).password(REQUEST_PASSWORD);
  }

  /**
   * constructs a default `UserDto` object with user-specific values for `userId`,
   * `name`, `email`, `encryptedPassword`, and `communityIds`.
   * 
   * @returns a `UserDto` object with default values for various user properties.
   * 
   * 	- `userId`: The user ID of the default user DTO.
   * 	- `name`: The name of the default user.
   * 	- `email`: The email address of the default user.
   * 	- `encryptedPassword`: The encrypted password of the default user.
   * 	- `communityIds`: A set of community IDs associated with the default user.
   * 
   * These properties are used to create a default user DTO that can be used in various
   * scenarios, such as testing or mocking user authentication functionality.
   */
  private UserDto getDefaultUserDtoRequest() {
    return UserDto.builder()
        .userId(USER_ID)
        .name(USERNAME)
        .email(USER_EMAIL)
        .encryptedPassword(USER_PASSWORD)
        .communityIds(new HashSet<>())
        .build();
  }

  /**
   * generates a JWT token with the user ID and expiration time calculated based on the
   * `TOKEN_LIFETIME`.
   * 
   * @param userDto user details, which are used to generate the JWT token's `userId`.
   * 
   * 	- `userId`: The user ID of the token's intended recipient.
   * 	- `expirationTime`: A `LocalDateTime` object representing the expiration time of
   * the token in milliseconds since the Unix epoch (January 1, 1970, 00:00:00 UTC).
   * 
   * @returns a JWT token containing the user ID and an expiration time calculated based
   * on the `TOKEN_LIFETIME`.
   * 
   * 	- The `AppJwt` object is constructed by calling the `builder()` method and
   * specifying the user ID using the `userId` property, followed by the expiration
   * time in milliseconds since the epoch using the `expiration` property.
   * 	- The `AppJwt` object represents a JSON Web Token (JWT) that contains claims about
   * the user, such as their ID, which are encoded and signed using a secret key.
   */
  private AppJwt getDefaultJwtToken(UserDto userDto) {
    final LocalDateTime expirationTime = LocalDateTime.now().plus(TOKEN_LIFETIME);
    return AppJwt.builder()
        .userId(userDto.getUserId())
        .expiration(expirationTime)
        .build();
  }
}
