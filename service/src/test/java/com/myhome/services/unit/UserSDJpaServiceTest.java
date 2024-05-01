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
 * is an unit test for the User SDJpa Service, which handles user management tasks
 * such as creating, reading, updating and deleting users in a Java-based application.
 * The test class provides various methods to test different scenarios, including
 * testing whether the service can reset a password correctly, whether it can find a
 * user by email with tokens, and whether it can create a new security token.
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
   * initialization and Mockito Annotations integration for testing purposes
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * tests create a new user using a provided DTO and returns the created user as a
   * DTO, while also creating an email confirm token for the user.
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
   * verifies that a user with the same email as the given request does not exist in
   * the repository before creating a new user using the service.
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
   * with the same details, and asserts that the created Dto is identical to the original
   * Dto.
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
   * tests the UserService by calling the getUserDetails method and verifying that the
   * user details are retrieved successfully along with their community IDs.
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
   * tests whether user details are retrieved successfully when they do not exist in
   * the database by calling the `userRepository.findByUserIdWithCommunities` method
   * and verifying the result with the `assertFalse` method.
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
   * verifies that an email address is confirmed for a user by checking if the user has
   * been marked as email confirmed after using a security token to confirm their email
   * address.
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
   * tests the user service's method for confirming an email address. It creates a
   * security token with an invalid token and passes it to the service, which returns
   * false when attempting to confirm the email.
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
   * tests whether an email confirmation token is correctly associated with a user's
   * account by attempting to confirm it and verifying the result and interactions with
   * other services.
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
   * verifies that an email is not confirmed for a given user ID by saving the user to
   * the repository without a token, and verifying that the email is not marked as
   * confirmed and no interactions with security token or mail services are made.
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
   * verifies that an email is already confirmed for a user by checking if the email
   * confirmation token matches a known token and if the user's email status is set to
   * confirmed. If both conditions are true, the function returns false indicating that
   * the email is already confirmed.
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
   * tests the user service's ability to find a user by their email address. Given a
   * default user DTO, it calls the repository and mapper methods to retrieve the
   * corresponding user object and user DTO, asserts that both are present and equal,
   * and verifies the call to the repository method.
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
   * verifies that a user can be retrieved by email and that their community IDs are
   * correctly populated in the resulting UserDto.
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
   * verifies that a user is not found in the repository when the email address is
   * invalid or does not exist in the database.
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
   * requests a password reset for a user by creating a password reset token and sending
   * an email with a recover code to the user's registered email address. It also updates
   * the user's security token and saves the user in the database.
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
   * verifies that a password reset request is not triggered when the user does not
   * exist in the database.
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
   * resets a user's password by generating a new security token, encrypting the user's
   * password, sending a password change notification to the user, and updating the
   * user's encrypted password in the database.
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
   * tests whether the user password is reset correctly when the user does not exist
   * in the database.
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
   * tests the password reset feature for an expired security token. It given a valid
   * email, retrieves the user's tokens, and resets the password without updating the
   * encryption or token status. The function verifies the expected behavior of the
   * service and repositories involved.
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
   * tests the user service's ability to reset a password for an email address that
   * does not have a token associated with it. It verifies that the encryption of the
   * new password is different from the original password and that all dependencies are
   * properly interacted with.
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
   * tests the user service's `resetPassword` method by providing a security token that
   * does not match the one stored in the user's tokens, and verifying that the password
   * is not reset and the correct security token is generated.
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
   * builds a default user DTO with prepopulated values for USER_ID, USERNAME, USER_EMAIL,
   * and USER_PASSWORD, and an empty set of community IDs.
   * 
   * @returns a `UserDto` object with pre-populated values for `userId`, `name`, `email`,
   * `encryptedPassword`, and `communityIds`.
   * 
   * 	- `userId`: An integer value representing the user's ID.
   * 	- `name`: A string value representing the user's name.
   * 	- `email`: A string value representing the user's email address.
   * 	- `encryptedPassword`: A string value representing the encrypted password for the
   * user.
   * 	- `communityIds`: A set of integers representing the communities to which the
   * user belongs.
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
   * creates a new `User` object from a `UserDto` request, setting name, user ID, email,
   * and password fields, and initializing encryption and roles sets to empty lists.
   * 
   * @param request UserDto object containing the necessary data to create a new `User`
   * instance.
   * 
   * 	- `request.getName()`: String containing the user's name
   * 	- `request.getUserId()`: Integer representing the user's ID
   * 	- `request.getEmail()`: String containing the user's email address
   * 	- `request.getEncryptedPassword()`: String containing the encrypted password
   * 	- `request.getHashSet<>()`: Collection of hash sets, each containing a single
   * value and a boolean indicating whether the value is present in the set
   * 	- `request.getHashSet<>()`: Collection of hash sets, each containing a single
   * value and a boolean indicating whether the value is present in the set
   * 
   * @returns a `User` object with name, ID, email, and other attributes filled in based
   * on the input `UserDto` request.
   * 
   * 	- `name`: The user's name as passed in the `request`.
   * 	- `userId`: The user ID as passed in the `request`.
   * 	- `email`: The user's email address as passed in the `request`.
   * 	- `isAdmin`: A boolean indicating whether the user is an administrator or not.
   * 	- `encryptedPassword`: The encrypted password of the user, as passed in the `request`.
   * 	- `groups`: An empty set, indicating that the user has no groups assigned.
   * 	- `roles`: An empty set, indicating that the user has no roles assigned.
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
   * retrieves a user's security token based on the specified `tokenType`. It streams
   * through the user's tokens, filters by token type, and returns the first matching
   * token or `null` if no match is found.
   * 
   * @param user user for whom the security token is being retrieved, and it is used
   * to filter the stream of user tokens based on the `tokenType` parameter.
   * 
   * 	- `user`: The input parameter representing a User object containing information
   * about a user.
   * 	- `tokenType`: A SecurityTokenType enumeration value specifying the type of
   * security token to retrieve.
   * 
   * @param tokenType type of security token being searched for, and is used to filter
   * the stream of user tokens to return only those that match the specified type.
   * 
   * 	- `User`: The user whose security token is being retrieved.
   * 	- `SecurityTokenType`: The type of security token sought.
   * 	- `getUserTokens()`: A method that returns a stream of all security tokens
   * associated with the specified user.
   * 	- `stream()`: Operator for converting the user token collection to a stream.
   * 	- `filter()`: Operator for filtering out tokens that do not meet the specified
   * type requirement.
   * 	- `findFirst()`: Method that finds and returns the first token in the filtered
   * stream that matches the required type, or returns `null` if no such token is found.
   * 
   * @returns a `SecurityToken` object representing the user's security token of the
   * specified type.
   * 
   * 	- `user`: The user object that is passed as a parameter to the function.
   * 	- `tokenType`: The type of security token that is being searched for in the user's
   * tokens stream.
   * 	- `userTokens`: A stream of security tokens associated with the user.
   * 	- `findFirst()`: Returns the first token from the `userTokens` stream that matches
   * the `tokenType`, or `null` if no such token exists.
   * 	- `orElse()`: Provides an alternative value to return if the `findFirst()` method
   * returns `null`.
   */
  private SecurityToken getUserSecurityToken(User user, SecurityTokenType tokenType) {
    return user.getUserTokens()
        .stream()
        .filter(token -> token.getTokenType() == tokenType)
        .findFirst()
        .orElse(null);
  }

  /**
   * retrieves a default user from a request and returns it as an instance of `User`.
   * 
   * @returns a `User` object retrieved from a DTO request.
   * 
   * 	- The function returns an object of type `User`.
   * 	- The object contains information about a default user, such as their name and
   * email address.
   * 	- The `getUserFromDto` method is called to generate the user object from a request
   * DTO (Data Transfer Object).
   * 	- The request DTO includes the necessary data to create a new user account.
   */
  private User getDefaultUser() {
    return getUserFromDto(getDefaultUserDtoRequest());
  }

  /**
   * creates a new `ForgotPasswordRequest` object with email, new password, and token
   * for password reset process.
   * 
   * @returns a `ForgotPasswordRequest` object containing the user's email, new password,
   * and password reset token.
   * 
   * 	- `request`: This is the ForgotPasswordRequest object that contains the email
   * address of the user to whom the password reset link will be sent, along with two
   * other attributes - `NEW_USER_PASSWORD` and `PASSWORD_RESET_TOKEN`.
   * 	- `USER_EMAIL`: This is the email address of the user for whom the password reset
   * link will be generated.
   * 	- `NEW_USER_PASSWORD`: This is the new password that will be set for the user.
   * 	- `PASSWORD_RESET_TOKEN`: This is a token that is used to verify that the request
   * is legitimate and has not been tampered with.
   */
  private ForgotPasswordRequest getForgotPasswordRequest() {
    ForgotPasswordRequest request = new ForgotPasswordRequest();
    request.setEmail(USER_EMAIL);
    request.setNewPassword(NEW_USER_PASSWORD);
    request.setToken(PASSWORD_RESET_TOKEN);
    return request;
  }

  /**
   * generates a test security token with a password reset token and a lifespan of
   * TOKEN_LIFETIME days less than the current date, indicating that the token is about
   * to expire.
   * 
   * @returns a SecurityToken object representing an expired token with a random password
   * reset token and expiration date.
   * 
   * 	- `SecurityTokenType`: The type of token returned is `RESET`.
   * 	- `PASSWORD_RESET_TOKEN`: The token is a password reset token.
   * 	- `LocalDate.now()`: The token's issuance date is the current date.
   * 	- `LocalDate.now().minusDays(TOKEN_LIFETIME.toDays())`: The token's expiration
   * date is the current date minus the specified lifetime in days (TOKEN_LIFETIME).
   * 	- `false`: The token is not valid (i.e., has expired).
   * 	- `null`: The token's user ID is null.
   */
  private SecurityToken getExpiredTestToken() {
    return new SecurityToken(SecurityTokenType.RESET, PASSWORD_RESET_TOKEN, LocalDate.now(),
        LocalDate.now().minusDays(TOKEN_LIFETIME.toDays()), false, null);
  }

  /**
   * creates a new security token with the specified type, token, and lifetime, and
   * sets the expiration date to `now` plus the specified number of days. It also sets
   * the token as false for the user.
   * 
   * @param tokenType type of security token being generated, which determines its
   * properties and behavior.
   * 
   * 	- `tokenType`: Represents the type of security token, which can be one of the
   * following values: `Active`, `Expired`, or `Invalid`.
   * 	- `lifetime`: Represents the duration for which the security token is valid,
   * measured in days.
   * 	- `token`: A unique identifier for the security token.
   * 	- `user`: The user associated with the security token.
   * 
   * @param lifetime duration for which the security token is valid, and it is used to
   * calculate the expiration date of the token.
   * 
   * 	- `toDays()`: This method returns the number of days represented by the `Duration`
   * object passed as an argument.
   * 	- `LocalDate.now()` and `LocalDate.now().plusDays()`: These methods represent the
   * current date and time, and the date and time plus a specified number of days, respectively.
   * 
   * @param token 16-digit security token number to be generated by the `getSecurityToken()`
   * method.
   * 
   * @param user user who is associated with the security token being generated.
   * 
   * 	- `user`: This represents an instance of the `User` class, which likely has
   * attributes such as `username`, `password`, and `role`.
   * 
   * @returns a new security token instance with the specified type, token, and expiration
   * date.
   * 
   * 	- The `tokenType` parameter specifies the type of security token being generated,
   * which is represented by an enumeration value.
   * 	- The `token` parameter contains a unique identifier for the security token.
   * 	- The `expireDate` parameter represents the date and time after which the security
   * token will expire, calculated by adding the `lifetime` parameter to the current
   * date and time in days.
   * 	- The `user` parameter represents the user for whom the security token is being
   * generated.
   */
  private SecurityToken getSecurityToken(SecurityTokenType tokenType, Duration lifetime,
      String token, User user) {
    LocalDate expireDate = LocalDate.now().plusDays(lifetime.toDays());
    return new SecurityToken(tokenType, token, LocalDate.now(), expireDate, false, user);
  }

  /**
   * creates a new security token with the specified type and token, sets an expiration
   * date one day from now, and sets the token as not revocable.
   * 
   * @param tokenType type of security token being generated, which determines the
   * format and content of the token.
   * 
   * 	- `LocalDate.now()` represents the current date and time.
   * 	- `Duration.ofDays(1).toDays()` calculates a duration of 1 day in days.
   * 	- `expireDate` is set to the current date plus 1 day, indicating that the token
   * will expire within a day.
   * 	- `false` represents that the token is not revoked or invalidated.
   * 
   * @param token 16-digit security token number for the user.
   * 
   * @param user user who is requesting the security token.
   * 
   * 	- `LocalDate.now()` returns the current date and time.
   * 	- `Duration.ofDays(1).toDays()` calculates a duration of 1 day in days.
   * 	- `expireDate` represents the expiration date of the security token, which is set
   * to the current date plus 1 day.
   * 	- `false` indicates that the security token is not revoked.
   * 	- `user` is a `User` object containing various attributes related to the user,
   * such as their username, email address, and any other relevant information.
   * 
   * @returns a newly generated security token instance with the specified type and
   * token value.
   * 
   * 	- The `SecurityToken` object represents a security token with the specified type
   * (`tokenType`) and token value (`token`).
   * 	- The `LocalDate` fields represent the date and time of the token's activation
   * and expiration, respectively, with an additional day added to the expiration date.
   * 	- The `false` value for the `isActive` field indicates that the token is not
   * currently active.
   * 	- The `User` field represents the user for whom the token was generated.
   */
  private SecurityToken getSecurityToken(SecurityTokenType tokenType, String token, User user) {
    LocalDate expireDate = LocalDate.now().plusDays(Duration.ofDays(1).toDays());
    return new SecurityToken(tokenType, token, LocalDate.now(), expireDate, false, user);
  }
}