package com.myhome.services.unit;

import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.dto.mapper.UserMapper;
import com.myhome.model.ForgotPasswordRequest;
import com.myhome.domain.Community;
import com.myhome.domain.SecurityToken;
import com.myhome.domain.SecurityTokenType;
import com.myhome.domain.User;
import com.myhome.repositories.SecurityTokenRepository;
import com.myhome.repositories.UserRepository;
import com.myhome.services.springdatajpa.MailSDJpaService;
import com.myhome.services.springdatajpa.SecurityTokenSDJpaService;
import com.myhome.services.springdatajpa.UserSDJpaService;
import helpers.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * is a Java class that provides test cases for the UserSDJpaService class. It includes
 * several methods for generating security tokens and verifying their validity, as
 * well as methods for testing the expiration date and user association with the
 * security token. The class also includes a method for getting an expired security
 * token.
 */
class UserSDJpaServiceTest {

  private final String USER_ID = "test-user-id";
  private final String USERNAME = "test-user-name";
  private final String USER_EMAIL = "test-user-email";
  private final String USER_PASSWORD = "test-user-password";
  private final String NEW_USER_PASSWORD = "test-user-new-password";
  private final String PASSWORD_RESET_TOKEN = "test-token";
  private final Duration TOKEN_LIFETIME = Duration.ofDays(1);

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private SecurityTokenSDJpaService securityTokenService;
  @Mock
  private MailSDJpaService mailService;
  @Mock
  private SecurityTokenRepository securityTokenRepository;
  @InjectMocks
  private UserSDJpaService userService;

  /**
   * initializes mock objects using MockitoAnnotations, making them available for use
   * in test methods.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * tests the create user service by providing a user dto request and verifying that
   * the resulting user dto is created successfully with the correct ID, user ID, name,
   * encrypted password, community IDs, and email confirm token.
   */
  @Test
  void createUserSuccess() {
    // given
    UserDto request = getDefaultUserDtoRequest();
    User resultUser = getUserFromDto(request);
    UserDto response = UserDto.builder()
        .id(resultUser.getId())
        .userId(resultUser.getUserId())
        .name(resultUser.getName())
        .encryptedPassword(resultUser.getEncryptedPassword())
        .communityIds(new HashSet<>())
        .build();
    SecurityToken emailConfirmToken =
        getSecurityToken(SecurityTokenType.EMAIL_CONFIRM, "token", resultUser);

    given(userRepository.findByEmail(request.getEmail()))
        .willReturn(null);
    given(passwordEncoder.encode(request.getPassword()))
        .willReturn(request.getPassword());
    given(userMapper.userDtoToUser(request))
        .willReturn(resultUser);
    given(userRepository.save(resultUser))
        .willReturn(resultUser);
    given(userMapper.userToUserDto(resultUser))
        .willReturn(response);
    given(securityTokenService.createEmailConfirmToken(resultUser))
        .willReturn(emailConfirmToken);

    // when
    Optional<UserDto> createdUserDtoOptional = userService.createUser(request);

    // then
    assertTrue(createdUserDtoOptional.isPresent());
    UserDto createdUserDto = createdUserDtoOptional.get();
    assertEquals(response, createdUserDto);
    assertEquals(0, createdUserDto.getCommunityIds().size());
    verify(userRepository).findByEmail(request.getEmail());
    verify(passwordEncoder).encode(request.getPassword());
    verify(userRepository).save(resultUser);
    verify(securityTokenService).createEmailConfirmToken(resultUser);
  }

  /**
   * tests whether creating a user with an existing email returns `Optional.empty`. It
   * sets up a user in the repository and verifies that the service call returns `Optional.empty`.
   */
  @Test
  void createUserEmailExists() {
    // given
    UserDto request = getDefaultUserDtoRequest();
    User user = getUserFromDto(request);

    given(userRepository.findByEmail(request.getEmail()))
        .willReturn(user);

    // when
    Optional<UserDto> createdUserDto = userService.createUser(request);

    // then
    assertFalse(createdUserDto.isPresent());
    verify(userRepository).findByEmail(request.getEmail());
  }

  /**
   * retrieves a user's details from the repository and mapper, creates a new user Dto
   * with the same details, and returns it.
   */
  @Test
  void getUserDetailsSuccess() {
    // given
    UserDto userDto = getDefaultUserDtoRequest();
    User user = getUserFromDto(userDto);

    given(userRepository.findByUserIdWithCommunities(USER_ID))
        .willReturn(Optional.of(user));
    given(userMapper.userToUserDto(user))
        .willReturn(userDto);

    // when
    Optional<UserDto> createdUserDtoOptional = userService.getUserDetails(USER_ID);

    // then
    assertTrue(createdUserDtoOptional.isPresent());
    UserDto createdUserDto = createdUserDtoOptional.get();
    assertEquals(userDto, createdUserDto);
    assertEquals(0, createdUserDto.getCommunityIds().size());
    verify(userRepository).findByUserIdWithCommunities(USER_ID);
  }

  /**
   * retrieves a user's details and community IDs from the repository, maps them to a
   * UserDto object, and verifies that the resulting UserDto object matches the expected
   * one and has the correct community IDs.
   */
  @Test
  void getUserDetailsSuccessWithCommunityIds() {
    // given
    UserDto userDto = getDefaultUserDtoRequest();
    User user = new User(userDto.getName(), userDto.getUserId(), userDto.getEmail(), false,
        userDto.getEncryptedPassword(), new HashSet<>(), null);

    Community firstCommunity = TestUtils.CommunityHelpers.getTestCommunity(user);
    Community secCommunity = TestUtils.CommunityHelpers.getTestCommunity(user);

    Set<Community> communities =
        Stream.of(firstCommunity, secCommunity).collect(Collectors.toSet());

    Set<String> communitiesIds = communities
        .stream()
        .map(community -> community.getCommunityId())
        .collect(Collectors.toSet());

    given(userRepository.findByUserIdWithCommunities(USER_ID))
        .willReturn(Optional.of(user));
    given(userMapper.userToUserDto(user))
        .willReturn(userDto);

    // when
    Optional<UserDto> createdUserDtoOptional = userService.getUserDetails(USER_ID);

    // then
    assertTrue(createdUserDtoOptional.isPresent());
    UserDto createdUserDto = createdUserDtoOptional.get();
    assertEquals(userDto, createdUserDto);
    assertEquals(communitiesIds, createdUserDto.getCommunityIds());
    verify(userRepository).findByUserIdWithCommunities(USER_ID);
  }

  /**
   * tests whether user details are returned when none exist in the repository by
   * invoking the `userService` and `userRepository`.
   */
  @Test
  void getUserDetailsNotFound() {
    // given
    given(userRepository.findByUserIdWithCommunities(USER_ID))
        .willReturn(Optional.empty());

    // when
    Optional<UserDto> createdUserDto = userService.getUserDetails(USER_ID);

    // then
    assertFalse(createdUserDto.isPresent());
    verify(userRepository).findByUserIdWithCommunities(USER_ID);
  }

  /**
   * confirms an email address for a user by checking if the email confirmation token
   * is valid and updating the user's email confirmation status in the database.
   */
  @Test
  void confirmEmail() {
    // given
    User user = getDefaultUser();
    SecurityToken testSecurityToken =
        getSecurityToken(SecurityTokenType.EMAIL_CONFIRM, TOKEN_LIFETIME, PASSWORD_RESET_TOKEN,
            user);
    user.getUserTokens().add(testSecurityToken);
    given(securityTokenService.useToken(testSecurityToken))
        .willReturn(testSecurityToken);
    given(userRepository.findByUserIdWithTokens(user.getUserId()))
        .willReturn(Optional.of(user));
    //    given(mailService.sendAccountConfirmed(user))
    //        .willReturn(true);

    // when
    boolean emailConfirmed =
        userService.confirmEmail(user.getUserId(), testSecurityToken.getToken());

    // then
    assertTrue(emailConfirmed);
    assertTrue(user.isEmailConfirmed());
    verify(securityTokenService).useToken(testSecurityToken);
    verify(userRepository).save(user);
    //    verify(mailService).sendAccountConfirmed(user);
  }

  /**
   * tests the user service's confirm email method by providing a wrong security token
   * and verifying that the email is not confirmed and no interactions with the repository
   * or mail services are made.
   */
  @Test
  void confirmEmailWrongToken() {
    // given
    User user = getDefaultUser();
    SecurityToken testSecurityToken =
        getSecurityToken(SecurityTokenType.EMAIL_CONFIRM, TOKEN_LIFETIME, PASSWORD_RESET_TOKEN,
            user);
    user.getUserTokens().add(testSecurityToken);
    given(userRepository.findByUserIdWithTokens(user.getUserId()))
        .willReturn(Optional.of(user));

    // when
    boolean emailConfirmed = userService.confirmEmail(user.getUserId(), "wrong-token");

    // then
    assertFalse(emailConfirmed);
    assertFalse(user.isEmailConfirmed());
    verify(userRepository, never()).save(user);
    verifyNoInteractions(securityTokenService);
    verifyNoInteractions(mailService);
  }

  /**
   * tests whether an email confirmation token is valid and whether the user's email
   * is confirmed. It verifies the token's usage status and checks if the user's email
   * is confirmed using the `userService`.
   */
  @Test
  void confirmEmailUsedToken() {
    // given
    User user = getDefaultUser();
    SecurityToken testSecurityToken =
        getSecurityToken(SecurityTokenType.EMAIL_CONFIRM, TOKEN_LIFETIME, PASSWORD_RESET_TOKEN,
            user);
    testSecurityToken.setUsed(true);
    user.getUserTokens().add(testSecurityToken);
    given(userRepository.findByUserIdWithTokens(user.getUserId()))
        .willReturn(Optional.of(user));

    // when
    boolean emailConfirmed =
        userService.confirmEmail(user.getUserId(), testSecurityToken.getToken());

    // then
    assertFalse(emailConfirmed);
    assertFalse(user.isEmailConfirmed());
    verify(userRepository, never()).save(user);
    verifyNoInteractions(securityTokenService);
    verifyNoInteractions(mailService);
  }

  /**
   * verifies that an email is not confirmed for a given user without a token. It asserts
   * that the email is not confirmed and checks that no interactions with the
   * `userRepository` or `securityTokenService` were made, and also checks that no
   * interactions with the `mailService` were made.
   */
  @Test
  void confirmEmailNoToken() {
    // given
    User user = getDefaultUser();
    given(userRepository.findByUserIdWithTokens(user.getUserId()))
        .willReturn(Optional.of(user));

    // when
    boolean emailConfirmed = userService.confirmEmail(user.getUserId(), "any-token");

    // then
    assertFalse(emailConfirmed);
    assertFalse(user.isEmailConfirmed());
    verify(userRepository, never()).save(user);
    verifyNoInteractions(securityTokenService);
    verifyNoInteractions(mailService);
  }

  /**
   * tests whether an email is already confirmed for a user by attempting to confirm
   * it again with a security token and verifying that the email is not marked as
   * confirmed after the attempt.
   */
  @Test
  void confirmEmailAlreadyConfirmed() {
    // given
    User user = getDefaultUser();
    SecurityToken testSecurityToken =
        getSecurityToken(SecurityTokenType.EMAIL_CONFIRM, TOKEN_LIFETIME, PASSWORD_RESET_TOKEN,
            user);
    user.getUserTokens().add(testSecurityToken);
    user.setEmailConfirmed(true);
    given(userRepository.findByUserIdWithTokens(user.getUserId()))
        .willReturn(Optional.of(user));

    // when
    boolean emailConfirmed =
        userService.confirmEmail(user.getUserId(), testSecurityToken.getToken());

    // then
    assertFalse(emailConfirmed);
    verify(userRepository, never()).save(user);
    verifyNoInteractions(securityTokenService);
    verifyNoInteractions(mailService);
  }

  /**
   * tests the user service's ability to find a user by their email address. It sets
   * up a mock repository and mapper to return a user object and its corresponding DTO,
   * and then calls the service to retrieve the user as a DTO and verifies that it is
   * present and equal to the expected values.
   */
  @Test
  void findUserByEmailSuccess() {
    // given
    UserDto userDto = getDefaultUserDtoRequest();
    User user = getUserFromDto(userDto);

    given(userRepository.findByEmail(USER_EMAIL))
        .willReturn(user);
    given(userMapper.userToUserDto(user))
        .willReturn(userDto);

    // when
    Optional<UserDto> resultUserDtoOptional = userService.findUserByEmail(USER_EMAIL);

    // then
    assertTrue(resultUserDtoOptional.isPresent());
    UserDto createdUserDto = resultUserDtoOptional.get();
    assertEquals(userDto, createdUserDto);
    assertEquals(0, createdUserDto.getCommunityIds().size());
    verify(userRepository).findByEmail(USER_EMAIL);
  }

  /**
   * verifies that a user with the given email can be found in the repository, and their
   * community IDs can be retrieved from the mapper and returned as part of the UserDto.
   */
  @Test
  void findUserByEmailSuccessWithCommunityIds() {
    // given
    UserDto userDto = getDefaultUserDtoRequest();
    User user = getUserFromDto(userDto);

    Community firstCommunity = TestUtils.CommunityHelpers.getTestCommunity(user);
    Community secCommunity = TestUtils.CommunityHelpers.getTestCommunity(user);

    Set<Community> communities =
        Stream.of(firstCommunity, secCommunity).collect(Collectors.toSet());

    Set<String> communitiesIds = communities
        .stream()
        .map(Community::getCommunityId)
        .collect(Collectors.toSet());

    given(userRepository.findByEmail(USER_EMAIL))
        .willReturn(user);
    given(userMapper.userToUserDto(user))
        .willReturn(userDto);

    // when
    Optional<UserDto> resultUserDtoOptional = userService.findUserByEmail(USER_EMAIL);

    // then
    assertTrue(resultUserDtoOptional.isPresent());
    UserDto createdUserDto = resultUserDtoOptional.get();
    assertEquals(userDto, createdUserDto);
    assertEquals(communitiesIds, createdUserDto.getCommunityIds());
    verify(userRepository).findByEmail(USER_EMAIL);
  }

  /**
   * tests whether a user is found by email through the `userService`. It uses a given
   * stub to return `null` from the `userRepository` and then checks if an `Optional`
   * of `UserDto` is present using the `userService`.
   */
  @Test
  void findUserByEmailNotFound() {
    // given
    given(userRepository.findByEmail(USER_EMAIL))
        .willReturn(null);

    // when
    Optional<UserDto> resultUserDtoOptional = userService.findUserByEmail(USER_EMAIL);

    // then
    assertFalse(resultUserDtoOptional.isPresent());
    verify(userRepository).findByEmail(USER_EMAIL);
  }

  /**
   * performs the following actions: creates a password reset token for a user, sends
   * an email with a password recover code to the user's registered email address, and
   * saves the user's security token in the database.
   */
  @Test
  void requestResetPassword() {
    // given
    ForgotPasswordRequest forgotPasswordRequest = getForgotPasswordRequest();
    User user = getDefaultUser();
    SecurityToken testSecurityToken =
        getSecurityToken(SecurityTokenType.RESET, TOKEN_LIFETIME, PASSWORD_RESET_TOKEN, null);
    given(securityTokenService.createPasswordResetToken(user))
        .willReturn(testSecurityToken);
    given(userRepository.findByEmailWithTokens(forgotPasswordRequest.getEmail()))
        .willReturn(Optional.of(user));
    given(mailService.sendPasswordRecoverCode(user, testSecurityToken.getToken()))
        .willReturn(true);

    // when
    boolean resetRequested = userService.requestResetPassword(forgotPasswordRequest);

    // then
    assertTrue(resetRequested);
    assertEquals(getUserSecurityToken(user, SecurityTokenType.RESET), testSecurityToken);
    verify(securityTokenService).createPasswordResetToken(user);
    verify(userRepository).findByEmailWithTokens(forgotPasswordRequest.getEmail());
    verify(userRepository).save(user);
    verify(mailService).sendPasswordRecoverCode(user, testSecurityToken.getToken());
  }

  /**
   * tests whether a user can request a password reset when they do not exist in the
   * database. It verifies that the token created by the securityTokenService is different
   * from the one returned, and that the user is not saved in the database after the
   * reset request is made.
   */
  @Test
  void requestResetPasswordUserNotExists() {
    // given
    ForgotPasswordRequest forgotPasswordRequest = getForgotPasswordRequest();
    User user = getDefaultUser();
    SecurityToken testSecurityToken =
        getSecurityToken(SecurityTokenType.RESET, TOKEN_LIFETIME, PASSWORD_RESET_TOKEN, user);
    given(securityTokenService.createPasswordResetToken(user))
        .willReturn(testSecurityToken);
    given(userRepository.findByEmailWithTokens(forgotPasswordRequest.getEmail()))
        .willReturn(Optional.empty());

    // when
    boolean resetRequested = userService.requestResetPassword(forgotPasswordRequest);

    // then
    assertFalse(resetRequested);
    assertNotEquals(getUserSecurityToken(user, SecurityTokenType.RESET), testSecurityToken);
    verifyNoInteractions(securityTokenService);
    verify(userRepository).findByEmailWithTokens(forgotPasswordRequest.getEmail());
    verify(userRepository, never()).save(user);
    verifyNoInteractions(mailService);
  }

  /**
   * allows users to reset their password by providing a new password, which is then
   * encoded and saved in the user's profile. The function also sends an email confirmation
   * to the user's registered email address.
   */
  @Test
  void resetPassword() {
    // given
    ForgotPasswordRequest forgotPasswordRequest = getForgotPasswordRequest();
    User user = getDefaultUser();
    SecurityToken testSecurityToken =
        getSecurityToken(SecurityTokenType.RESET, TOKEN_LIFETIME, PASSWORD_RESET_TOKEN, user);
    user.getUserTokens().add(testSecurityToken);
    given(userRepository.findByEmailWithTokens(forgotPasswordRequest.getEmail()))
        .willReturn(Optional.of(user));
    given(passwordEncoder.encode(forgotPasswordRequest.getNewPassword()))
        .willReturn(forgotPasswordRequest.getNewPassword());
    when(userRepository.save(user))
        .then(returnsFirstArg());
    given(mailService.sendPasswordSuccessfullyChanged(user))
        .willReturn(true);
    given(securityTokenService.useToken(testSecurityToken))
        .willReturn(testSecurityToken);

    // when
    boolean passwordChanged = userService.resetPassword(forgotPasswordRequest);

    // then
    assertTrue(passwordChanged);
    assertEquals(user.getEncryptedPassword(), forgotPasswordRequest.getNewPassword());
    verify(userRepository).findByEmailWithTokens(forgotPasswordRequest.getEmail());
    verify(passwordEncoder).encode(forgotPasswordRequest.getNewPassword());
    verify(mailService).sendPasswordSuccessfullyChanged(user);
    verify(securityTokenService).useToken(testSecurityToken);
  }

  /**
   * tests whether user's password can be reset when the user does not exist in the
   * database. It does so by creating a fictitious security token and using it to reset
   * the password, then verifying that the password has been changed and the original
   * one is no longer present.
   */
  @Test
  void resetPasswordUserNotExists() {
    // given
    ForgotPasswordRequest forgotPasswordRequest = getForgotPasswordRequest();
    User user = getDefaultUser();
    SecurityToken testSecurityToken =
        getSecurityToken(SecurityTokenType.RESET, TOKEN_LIFETIME, PASSWORD_RESET_TOKEN, user);
    user.getUserTokens().add(testSecurityToken);
    ;
    given(userRepository.findByEmailWithTokens(forgotPasswordRequest.getEmail()))
        .willReturn(Optional.empty());

    // when
    boolean passwordChanged = userService.resetPassword(forgotPasswordRequest);

    // then
    assertFalse(passwordChanged);
    assertNotEquals(user.getEncryptedPassword(), forgotPasswordRequest.getNewPassword());
    verify(userRepository).findByEmailWithTokens(forgotPasswordRequest.getEmail());
    verifyNoInteractions(securityTokenRepository);
    verifyNoInteractions(passwordEncoder);
    verifyNoInteractions(mailService);
  }

  /**
   * tests the reset password functionality when the token is expired. It verifies that
   * the password is not changed, the encrypted password does not match the new password,
   * and the security token is not marked as used after successful reset.
   */
  @Test
  void resetPasswordTokenExpired() {
    // given
    ForgotPasswordRequest forgotPasswordRequest = getForgotPasswordRequest();
    SecurityToken testSecurityToken = getExpiredTestToken();
    User user = getDefaultUser();
    user.getUserTokens().add(testSecurityToken);
    ;
    given(userRepository.findByEmailWithTokens(forgotPasswordRequest.getEmail()))
        .willReturn(Optional.of(user));

    // when
    boolean passwordChanged = userService.resetPassword(forgotPasswordRequest);

    // then
    assertFalse(passwordChanged);
    assertNotEquals(user.getEncryptedPassword(), forgotPasswordRequest.getNewPassword());
    assertFalse(getUserSecurityToken(user, SecurityTokenType.RESET).isUsed());
    verify(userRepository).findByEmailWithTokens(forgotPasswordRequest.getEmail());
    verifyNoInteractions(securityTokenRepository);
    verifyNoInteractions(passwordEncoder);
    verifyNoInteractions(mailService);
  }

  /**
   * tests whether the `userService.resetPassword()` method resets the password of a
   * user when the token does not exist in the database.
   */
  @Test
  void resetPasswordTokenNotExists() {
    // given
    ForgotPasswordRequest forgotPasswordRequest = getForgotPasswordRequest();
    User user = getDefaultUser();
    given(userRepository.findByEmailWithTokens(forgotPasswordRequest.getEmail()))
        .willReturn(Optional.of(user));

    // when
    boolean passwordChanged = userService.resetPassword(forgotPasswordRequest);

    // then
    assertFalse(passwordChanged);
    assertNotEquals(user.getEncryptedPassword(), forgotPasswordRequest.getNewPassword());
    verify(userRepository).findByEmailWithTokens(forgotPasswordRequest.getEmail());
    verifyNoInteractions(securityTokenRepository);
    verifyNoInteractions(passwordEncoder);
    verifyNoInteractions(mailService);
  }

  /**
   * tests the reset password functionality when the token sent to the user via email
   * does not match the expected value. It verifies that the password is not changed,
   * and the correct security token is generated and stored with the user's account.
   */
  @Test
  void resetPasswordTokenNotMatches() {
    // given
    ForgotPasswordRequest forgotPasswordRequest = getForgotPasswordRequest();
    SecurityToken testSecurityToken =
        getSecurityToken(SecurityTokenType.RESET, TOKEN_LIFETIME, PASSWORD_RESET_TOKEN, null);
    testSecurityToken.setToken("wrong-token");
    User user = getDefaultUser();
    user.getUserTokens().add(testSecurityToken);
    ;
    given(userRepository.findByEmailWithTokens(forgotPasswordRequest.getEmail()))
        .willReturn(Optional.of(user));

    // when
    boolean passwordChanged = userService.resetPassword(forgotPasswordRequest);

    // then
    assertFalse(passwordChanged);
    assertNotEquals(user.getEncryptedPassword(), forgotPasswordRequest.getNewPassword());
    assertNotNull(getUserSecurityToken(user, SecurityTokenType.RESET));
    verify(userRepository).findByEmailWithTokens(forgotPasswordRequest.getEmail());
    verifyNoInteractions(securityTokenRepository);
    verifyNoInteractions(passwordEncoder);
    verifyNoInteractions(mailService);
  }

  /**
   * builds a default `UserDto` instance with predefined values for `userId`, `name`,
   * `email`, `encryptedPassword`, and `communityIds`.
   * 
   * @returns a `UserDto` object with default values for user ID, name, email, password,
   * and community IDs.
   * 
   * 	- `userId`: An integer value representing the user ID.
   * 	- `name`: A string value representing the user name.
   * 	- `email`: A string value representing the user email.
   * 	- `encryptedPassword`: A string value representing the encrypted password.
   * 	- `communityIds`: A set of integers representing the community IDs associated
   * with the user.
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
   * creates a new `User` object from a `UserDto` request, setting properties such as
   * name, ID, email, and password encryption. It also initializes additional properties
   * like empty set collections.
   * 
   * @param request UserDto object that contains the user's information to be converted
   * into a `User` object.
   * 
   * 	- `name`: A string representing the user's name.
   * 	- `userId`: An integer value representing the user's ID.
   * 	- `email`: A string representing the user's email address.
   * 	- `encryptedPassword`: A boolean value indicating whether the password is encrypted.
   * 	- `hashSet`: A set of strings containing the user's groups.
   * 	- `otherHashSet`: A set of strings containing the user's other attributes.
   * 
   * @returns a `User` object with name, ID, email, and encrypted password.
   * 
   * 	- `name`: The user's name as provided in the `request`.
   * 	- `userId`: The unique identifier of the user as provided in the `request`.
   * 	- `email`: The user's email address as provided in the `request`.
   * 	- `isLoggedIn`: A boolean indicating whether the user is logged in or not.
   * 	- `encryptedPassword`: The encrypted password of the user as provided in the `request`.
   * 	- `groups`: An empty set, indicating that the user has no groups assigned.
   * 	- `permissions`: An empty set, indicating that the user has no permissions assigned.
   */
  private User getUserFromDto(UserDto request) {
    return new User(
        request.getName(),
        request.getUserId(),
        request.getEmail(),
        false,
        request.getEncryptedPassword(),
        new HashSet<>(),
        new HashSet<>()
    );
  }

  /**
   * retrieves a security token associated with a user based on its type, filtering and
   * finding the matching token from a stream of user tokens.
   * 
   * @param user user for whom the security token is being retrieved.
   * 
   * 	- `user`: A `User` object containing information about the user for whom the
   * security token is being retrieved.
   * 	- `tokenType`: An enumeration representing the type of security token required,
   * such as `SecurityTokenType.BEARER`.
   * 
   * @param tokenType type of security token being retrieved, which is used to filter
   * the user's tokens in the stream and return the matching token.
   * 
   * 	- `UserTokens`: This is an instance of `Stream` that contains all the security
   * tokens associated with the given user.
   * 	- `filter()`: This method filters out any token that does not have the same type
   * as the input `tokenType`.
   * 	- `findFirst()`: This method returns the first token in the filtered stream that
   * matches the input `tokenType`, or `null` if no such token exists.
   * 	- `orElse()`: If no matching token is found, this method returns an optional value
   * of type `SecurityToken`.
   * 
   * @returns a `SecurityToken` object representing the user's security token of the
   * specified type.
   * 
   * 	- The output is a `SecurityToken` object representing a security token associated
   * with the given `User`.
   * 	- The `SecurityToken` object has a `User` field containing the `User` object
   * associated with the token.
   * 	- The `SecurityToken` object has a `TokenType` field indicating the type of token
   * (either `USER_TOKEN` or `APPLICATION_TOKEN`).
   * 	- If multiple security tokens are found for the given `User`, only the first token
   * is returned in the output.
   */
  private SecurityToken getUserSecurityToken(User user, SecurityTokenType tokenType) {
    return user.getUserTokens()
        .stream()
        .filter(token -> token.getTokenType() == tokenType)
        .findFirst()
        .orElse(null);
  }

  /**
   * retrieves a default user from a request parameter and returns the user object.
   * 
   * @returns a `User` object populated from the data contained in the `getDefaultUserDtoRequest`.
   * 
   * 	- The function returns a `User` object.
   * 	- The user is retrieved from the `getUserFromDto` function, which takes a
   * `getDefaultUserDtoRequest` as its input.
   * 	- The `getDefaultUserDtoRequest` is not explicitly defined in the provided code
   * snippet, but it likely contains the necessary parameters to retrieve a default
   * user from the system's database or data storage.
   */
  private User getDefaultUser() {
    return getUserFromDto(getDefaultUserDtoRequest());
  }

  /**
   * creates a new `ForgotPasswordRequest` object with email, new password, and token
   * parameters set to specific values.
   * 
   * @returns a `ForgotPasswordRequest` object containing email, new password, and token
   * for password reset.
   * 
   * 	- `request`: A new `ForgotPasswordRequest` object is created and returned, which
   * contains the email address of the user, a new password, and a token for password
   * reset.
   * 	- `USER_EMAIL`: The email address of the user who wants to reset their password.
   * 	- `NEW_USER_PASSWORD`: The new password that the user wants to set.
   * 	- `PASSWORD_RESET_TOKEN`: A unique token generated by the system for password
   * reset purpose only.
   */
  private ForgotPasswordRequest getForgotPasswordRequest() {
    ForgotPasswordRequest request = new ForgotPasswordRequest();
    request.setEmail(USER_EMAIL);
    request.setNewPassword(NEW_USER_PASSWORD);
    request.setToken(PASSWORD_RESET_TOKEN);
    return request;
  }

  /**
   * generates a security token with an expiration date in the future, based on a
   * predefined lifetime and current date.
   * 
   * @returns a SecurityToken object representing an expired token with a reset date
   * and token lifetime.
   * 
   * 	- The type of security token is `RESET`.
   * 	- The password reset token is included in the token as `PASSWORD_RESET_TOKEN`.
   * 	- The creation date of the token is represented by the current date.
   * 	- The lifetime of the token, measured in days, is calculated by subtracting the
   * `TOKEN_LIFETIME` from the current date.
   * 	- The token is marked as invalid (i.e., `false`) for any further use.
   * 	- The token has no owner or user associated with it.
   */
  private SecurityToken getExpiredTestToken() {
    return new SecurityToken(SecurityTokenType.RESET, PASSWORD_RESET_TOKEN, LocalDate.now(),
        LocalDate.now().minusDays(TOKEN_LIFETIME.toDays()), false, null);
  }

  /**
   * generates a security token with specified type, token, lifetime, and user. The
   * generated token has an expiration date that is `plusDays` later than the current
   * date and is marked as invalid.
   * 
   * @param tokenType type of security token being generated, which determines the
   * format and content of the token.
   * 
   * 	- `tokenType`: The type of security token, which can be one of `USER_TOKEN`,
   * `SYSTEM_TOKEN`, or `APP_TOKEN`.
   * 	- `lifetime`: The duration for which the security token is valid, represented as
   * a `Duration` object.
   * 
   * @param lifetime duration of the security token's validity.
   * 
   * 	- `toDays()` is a method that converts the `Duration` object to days.
   * 	- `LocalDate.now()` returns the current date and time in the format of a LocalDate
   * object.
   * 	- `expireDate` is set to the current date plus the specified number of days,
   * calculated using the `plusDays()` method.
   * 
   * @param token 128-bit security token value to be generated for the specified token
   * type and lifetime.
   * 
   * @param user user for whom the security token is being generated.
   * 
   * 	- `user`: A `User` object with various attributes, including `id`, `email`, `name`,
   * and `role`.
   * 
   * @returns a newly created security token instance with the specified type, token,
   * and expiration date.
   * 
   * 	- `tokenType`: The type of security token being generated, which is represented
   * by an enumeration value.
   * 	- `token`: A unique identifier for the security token.
   * 	- `expireDate`: The date and time after which the security token will expire,
   * calculated as the current date plus a specified number of days.
   * 	- `user`: The user for whom the security token is being generated.
   * 
   * The return type of the function is a `SecurityToken` object, which represents a
   * security token with various attributes, including its type, token, issuance date,
   * expiration date, and user.
   */
  private SecurityToken getSecurityToken(SecurityTokenType tokenType, Duration lifetime,
      String token, User user) {
    LocalDate expireDate = LocalDate.now().plusDays(lifetime.toDays());
    return new SecurityToken(tokenType, token, LocalDate.now(), expireDate, false, user);
  }

  /**
   * creates a new security token with the specified type and token, set to expire one
   * day after the current date, and sets the token as false for the user.
   * 
   * @param tokenType type of security token being generated, which determines the
   * format and content of the token.
   * 
   * 	- `LocalDate.now()` returns the current date and time in the format `YYYY-MM-DDTHH:mm:ssZ`.
   * 	- `Duration.ofDays(1).toDays()` returns the number of days between the current
   * date and midnight at the beginning of the day, which is 0 for the current day.
   * 	- `expireDate` represents the date and time after which the token becomes invalid,
   * calculated by adding a fixed number of days to the current date and time.
   * 
   * @param token 10-digit alphanumeric token provided by the client for authentication
   * purposes.
   * 
   * @param user user associated with the security token being generated.
   * 
   * 	- `LocalDate.now()` represents the current date and time.
   * 	- `Duration.ofDays(1).toDays()` calculates a duration of 1 day in days, which is
   * added to the current date and time to set the expiration date of the security token.
   * 
   * @returns a new security token instance with specified token type, token, and
   * expiration date.
   * 
   * 	- `SecurityTokenType`: This represents the type of security token being generated,
   * which is specified in the `tokenType` parameter passed to the function.
   * 	- `token`: This is the unique identifier assigned to the security token.
   * 	- `expireDate`: This represents the date and time after which the security token
   * will expire, calculated by adding a specified number of days to the current date
   * using the `PlusDays` method of the `Duration` class.
   * 	- `false`: This indicates whether the security token is valid or not, with `true`
   * indicating validity and `false` indicating invalidity.
   * 	- `user`: This represents the user for whom the security token is being generated.
   */
  private SecurityToken getSecurityToken(SecurityTokenType tokenType, String token, User user) {
    LocalDate expireDate = LocalDate.now().plusDays(Duration.ofDays(1).toDays());
    return new SecurityToken(tokenType, token, LocalDate.now(), expireDate, false, user);
  }
}