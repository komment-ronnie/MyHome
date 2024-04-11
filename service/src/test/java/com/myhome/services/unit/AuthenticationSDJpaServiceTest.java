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
 * is a JUnit test class for testing the AuthenticationSDJpaService class. It verifies
 * the functionality of the class in various scenarios such as successful login, user
 * not found, and incorrect credentials. The test class uses mock objects to stub out
 * the dependencies of the class, including UserSDJpaService, AppJwtEncoderDecoder,
 * and PasswordEncoder.
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
   * verifies that a login request with a valid email and password can be successfully
   * authenticated, resulting in an JWT token being generated and returned to the client.
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
   * verifies that a `UserNotFoundException` is thrown when an email address not found
   * in the database is provided for login authentication.
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
   * tests whether the login credentials provided by the user are valid or not. It does
   * so by simulating the login process and verifying that the password does not match
   * the encrypted password stored in the database.
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
   * generates a default login request with an email address of `USER_EMAIL` and a
   * password of `REQUEST_PASSWORD`.
   * 
   * @returns a new `LoginRequest` object containing email and password parameters.
   * 
   * 	- `email`: This property is assigned with the value of `USER_EMAIL`, which
   * represents the email address of the user.
   * 	- `password`: This property is assigned with the value of `REQUEST_PASSWORD`,
   * which represents the password for the user.
   */
  private LoginRequest getDefaultLoginRequest() {
    return new LoginRequest().email(USER_EMAIL).password(REQUEST_PASSWORD);
  }

  /**
   * constructs a default `UserDto` object with user-specific details and an empty set
   * of community IDs.
   * 
   * @returns a `UserDto` object with predefined values for `userId`, `name`, `email`,
   * `encryptedPassword`, and `communityIds`.
   * 
   * 	- `userId`: The user ID of the default user DTO.
   * 	- `name`: The name of the default user DTO.
   * 	- `email`: The email address of the default user DTO.
   * 	- `encryptedPassword`: The encrypted password of the default user DTO.
   * 	- `communityIds`: A set of community IDs associated with the default user DTO.
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
   * generates an AppJwt token with a specified expiration time based on the current
   * date and user ID.
   * 
   * @param userDto user's details, including their ID, which are used to create a new
   * JWT token with a specified expiration time.
   * 
   * 	- `userId`: The user ID of the token recipient, represented as a long value.
   * 	- `expirationTime`: A `LocalDateTime` object representing the expiration time of
   * the token, calculated by adding the `TOKEN_LIFETIME` duration to the current date
   * and time.
   * 
   * @returns an AppJwt token with a generated expiration time based on the current
   * date and time, along with the user ID and other relevant details.
   * 
   * 1/ `userId`: The user ID associated with the JWT token.
   * 2/ `expiration`: The expiration time of the JWT token in LocalDateTime format,
   * calculated by adding the `TOKEN_LIFETIME` to the current date and time.
   * 3/ `build()`: This method is used to create a new instance of the `AppJwt` class
   * with the provided properties.
   */
  private AppJwt getDefaultJwtToken(UserDto userDto) {
    final LocalDateTime expirationTime = LocalDateTime.now().plus(TOKEN_LIFETIME);
    return AppJwt.builder()
        .userId(userDto.getUserId())
        .expiration(expirationTime)
        .build();
  }
}
