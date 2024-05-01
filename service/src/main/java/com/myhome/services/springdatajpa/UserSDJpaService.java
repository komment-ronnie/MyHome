/*
 * Copyright 2020 Prathab Murugan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myhome.services.springdatajpa;

import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.dto.mapper.UserMapper;
import com.myhome.domain.Community;
import com.myhome.domain.SecurityToken;
import com.myhome.domain.SecurityTokenType;
import com.myhome.domain.User;
import com.myhome.model.ForgotPasswordRequest;
import com.myhome.repositories.UserRepository;
import com.myhome.services.MailService;
import com.myhome.services.SecurityTokenService;
import com.myhome.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * is responsible for managing user-related operations in a Java-based application.
 * It provides methods for creating and updating users, confirming their email
 * addresses, encrypting their passwords, and generating unique IDs. The service uses
 * JPA (Java Persistence API) to interact with the repository and save the updated
 * user records.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserSDJpaService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final SecurityTokenService securityTokenService;
  private final MailService mailService;

  /**
   * creates a new user in the system by generating a unique ID, encrypting the password,
   * and sending an email confirmation token to the user's registered email address.
   * 
   * @param request UserDto object containing the details of the user to be created,
   * which is used to create a new user in the application and send an email confirmation
   * token to the user's registered email address.
   * 
   * 	- `getEmail()`: retrieves the email address of the user to be created
   * 	- `getPassword()`: retrieves the password of the user to be created
   * 	- `generateUniqueUserId()`: generates a unique identifier for the user
   * 	- `encryptUserPassword()`: encrypts the user password using a specified algorithm
   * 	- `createUserInRepository()`: creates a new user object in the repository
   * 	- `securityTokenService.createEmailConfirmToken()`: creates an email confirmation
   * token for the newly created user
   * 	- `mailService.sendAccountCreated()`: sends an email to the user's registered
   * email address with the email confirmation token
   * 
   * @returns an optional `UserDto` object representing the newly created user.
   * 
   * 	- `Optional<UserDto>`: This indicates that the function may return an optional
   * value representing a `UserDto`, which is a data transfer object (DTO) representing
   * a user entity.
   * 	- `createUserInRepository(request)`: This method creates a new `User` entity in
   * the repository, using the provided `UserDto` as a parameter. The returned `User`
   * entity is stored in the repository.
   * 	- `securityTokenService.createEmailConfirmToken(newUser)`: This method creates
   * an email confirmation token for the newly created `User`. The token is used to
   * verify the user's email address during account creation.
   * 	- `mailService.sendAccountCreated(newUser, emailConfirmToken)`: This method sends
   * an email notification to the user's registered email address with a link to confirm
   * their email address.
   * 	- `userMapper.userToUserDto(newUser)`: This method maps the newly created `User`
   * entity to a `UserDto`, which is a DTO representing the user entity. The resulting
   * `UserDto` object contains the same data as the original `User` entity.
   */
  @Override
  public Optional<UserDto> createUser(UserDto request) {
    if (userRepository.findByEmail(request.getEmail()) == null) {
      generateUniqueUserId(request);
      encryptUserPassword(request);
      User newUser = createUserInRepository(request);
      SecurityToken emailConfirmToken = securityTokenService.createEmailConfirmToken(newUser);
      mailService.sendAccountCreated(newUser, emailConfirmToken);
      UserDto newUserDto = userMapper.userToUserDto(newUser);
      return Optional.of(newUserDto);
    } else {
      return Optional.empty();
    }
  }

  /**
   * in Java returns a set of `User` objects based on a page request parameter.
   * 
   * @returns a set of all users in the application.
   * 
   * 	- The type of the output is Set<User>. This indicates that the function returns
   * a set of User objects.
   * 	- The parameter "PageRequest.of(0, 200)" is used to specify the page number and
   * maximum number of items to return. This parameter determines the pagination of the
   * output.
   */
  @Override
  public Set<User> listAll() {
    return listAll(PageRequest.of(0, 200));
  }

  /**
   * returns a set of all users retrieved from the `userRepository`. It uses the
   * `pageable` parameter to filter and sort the results as desired.
   * 
   * @param pageable pagination information for retrieving a subset of users from the
   * database.
   * 
   * The `Pageable` interface is used to represent a page of data that can be paginated.
   * It has two methods: `getPageNumber()` and `getPageSize()`, which indicate the
   * current page number and the number of items per page, respectively. These values
   * can be modified to change the way the list is displayed or sorted.
   * 
   * @returns a set of `User` objects retrieved from the database.
   * 
   * 	- `Set<User>` represents a set of user objects, containing the list of users
   * retrieved from the database.
   * 	- `userRepository.findAll(pageable)` is the method call that retrieves the list
   * of users from the database using the specified pageable parameter.
   * 	- The returned set contains the list of users, which can be further processed or
   * manipulated as per the requirements.
   */
  @Override
  public Set<User> listAll(Pageable pageable) {
    return userRepository.findAll(pageable).toSet();
  }

  /**
   * retrieves user details from the database and maps them to a `UserDto` object,
   * including community IDs.
   * 
   * @param userId id of the user for whom the user details are being retrieved.
   * 
   * @returns an optional object containing a user details DTO with community IDs and
   * a user details DTO without community IDs if no user is found.
   * 
   * 	- The output is an `Optional` object, indicating that the user details may or may
   * not be available.
   * 	- If the output is non-empty, it contains a `UserDto` object representing the
   * user details.
   * 	- The `UserDto` object has a `setCommunityIds()` method that sets the community
   * IDs of the user.
   * 	- The `UserDto` object also has a `userMapper` attribute that maps the original
   * user entity to the DTO format.
   * 	- The function returns an `Optional` object with the `UserDto` object as its
   * contents, or an empty `Optional` if no user details are available.
   */
  @Override
  public Optional<UserDto> getUserDetails(String userId) {
    Optional<User> userOptional = userRepository.findByUserIdWithCommunities(userId);
    return userOptional.map(admin -> {
      Set<String> communityIds = admin.getCommunities().stream()
          .map(Community::getCommunityId)
          .collect(Collectors.toSet());

      UserDto userDto = userMapper.userToUserDto(admin);
      userDto.setCommunityIds(communityIds);
      return Optional.of(userDto);
    }).orElse(Optional.empty());
  }

  /**
   * retrieves a UserDto object containing community IDs from the user repository and
   * maps them to the original User object using the user mapper.
   * 
   * @param userEmail email address of the user to search for in the user repository.
   * 
   * @returns an `Optional` object containing a `UserDto` instance with community IDs.
   * 
   * 	- `Optional<UserDto>` represents an optional user object, where `UserDto` is a
   * custom data transfer object (DTO) containing information about the user.
   * 	- The method returns an `Optional` instance that may contain a non-null `UserDto`
   * object or `null`, depending on whether a user with the provided email exists in
   * the database.
   * 	- The `findByEmail` method of the `userRepository` class is called to retrieve a
   * `User` object based on the input `userEmail`.
   * 	- The `map` method of the `Optional` instance is used to transform the retrieved
   * `User` object into a `UserDto` object, which contains a set of community IDs
   * associated with the user.
   * 	- The `userMapper` class is responsible for mapping the original `User` object
   * to the custom `UserDto` object.
   */
  public Optional<UserDto> findUserByEmail(String userEmail) {
    return Optional.ofNullable(userRepository.findByEmail(userEmail))
        .map(user -> {
          Set<String> communityIds = user.getCommunities().stream()
              .map(Community::getCommunityId)
              .collect(Collectors.toSet());

          UserDto userDto = userMapper.userToUserDto(user);
          userDto.setCommunityIds(communityIds);
          return userDto;
        });
  }

  /**
   * in Java is responsible for sending a password recovery code to a user's registered
   * email address upon request. It retrieves the user's token from the repository,
   * creates a new security token, and adds it to the user's tokens. Finally, it sends
   * an email with the recovery code to the user.
   * 
   * @param forgotPasswordRequest ForgotPasswordRequest object containing information
   * about the user who wants to reset their password.
   * 
   * 	- `forgotPasswordRequest`: The Forgot Password Request object containing the email
   * address of the user requesting password reset.
   * 	- `getEmail()`: Returns the email address of the user in the input object.
   * 	- `userRepository.findByEmailWithTokens(email)`: This method is used to find the
   * user record based on the provided email address, and it returns a `Optional` object
   * containing the user record if found, or an empty `Optional` otherwise. The
   * `withTokens` parameter indicates that the user record should be fetched along with
   * any password reset tokens associated with the email address.
   * 	- `map(user -> {...})`: This method maps the user record to a new `User` object
   * containing additional properties, such as the user's name and surname. The resulting
   * `User` object is then used to create a new password reset token using the
   * `securityTokenService.createPasswordResetToken()` method.
   * 	- `userRepository.save(user)`: This method saves the updated `User` object in the
   * repository, which persists the changes made to the user record.
   * 	- `mailService.sendPasswordRecoverCode(user, newSecurityToken.getToken())`: This
   * method sends an email containing the password reset token to the user's registered
   * email address.
   * 
   * @returns a boolean value indicating whether a password reset link was successfully
   * sent to the user's email address.
   */
  @Override
  public boolean requestResetPassword(ForgotPasswordRequest forgotPasswordRequest) {
    return Optional.ofNullable(forgotPasswordRequest)
        .map(ForgotPasswordRequest::getEmail)
        .flatMap(email -> userRepository.findByEmailWithTokens(email)
            .map(user -> {
              SecurityToken newSecurityToken = securityTokenService.createPasswordResetToken(user);
              user.getUserTokens().add(newSecurityToken);
              userRepository.save(user);
              return mailService.sendPasswordRecoverCode(user, newSecurityToken.getToken());
            }))
        .orElse(false);
  }

  /**
   * receives a `ForgotPasswordRequest`, checks if a user with the provided email exists,
   * retrieves their security token, verifies its validity, and updates the user's
   * password by sending an email notification.
   * 
   * @param passwordResetRequest ForgotPasswordRequest object containing the user's
   * email and a token generated by the application, which is used to retrieve the
   * user's security token and update their password.
   * 
   * 	- `ForgotPasswordRequest passwordResetRequest`: This object contains information
   * about the user attempting to reset their password, including their email address
   * and the token provided for authentication.
   * 	- `getEmail()`: This method returns the email address of the user associated with
   * the request.
   * 	- `getToken()`: This method returns the token provided by the user for authentication.
   * 	- `SecurityTokenType.RESET`: This is an enumeration value representing the type
   * of token being reset, indicating that the token was previously generated by the
   * system for password reset purposes.
   * 
   * The function then processes the input using a series of intermediate steps:
   * 
   * 1/ `Optional<User> userWithToken =
   * Optional.ofNullable(passwordResetRequest).map(ForgotPasswordRequest::getEmail).flatMap(userRepository::findByEmailWithTokens);`:
   * This step retrieves the user associated with the input request from the database,
   * using the email address provided in the `getEmail()` method. If no user is found,
   * the function returns `Optional.empty()`.
   * 2/ `final Optional<User> user = userWithToken.flatMap(user ->
   * findValidUserToken(passwordResetRequest.getToken(), user, SecurityTokenType.RESET));`:
   * This step retrieves the security token associated with the user and the provided
   * token, using the `findValidUserToken()` method. If no valid token is found, the
   * function returns `Optional.empty()`.
   * 3/ `final Optional<SecurityToken> securityToken = user.flatMap(user ->
   * findTokenForUser(user, SecurityTokenType.RESET));`: This step retrieves the security
   * token associated with the user and the provided token, using the `findTokenForUser()`
   * method. If no valid token is found, the function returns `Optional.empty()`.
   * 4/ `final Optional<Boolean> result = securityToken.map(token -> useToken(token));`:
   * This step uses the retrieved security token to generate a new password for the
   * user, using the `useToken()` method. The resulting password is then saved in the
   * database using the `saveTokenForUser()` method.
   * 5/ `final Optional<Boolean> result = result.map(token -> sendPasswordSuccessfullyChanged());`:
   * This step sends an email to the user informing them that their password has been
   * successfully changed, using the `sendPasswordSuccessfullyChanged()` method. If no
   * email can be sent, the function returns `Optional.empty()`.
   * 
   * In summary, the `resetPassword` function processes a password reset request by
   * retrieving the associated user and security token, using the provided token to
   * generate a new password, and then saving the updated password in the database and
   * sending an email to the user confirming the change.
   * 
   * @returns a boolean value indicating whether the password reset was successful.
   */
  @Override
  public boolean resetPassword(ForgotPasswordRequest passwordResetRequest) {
    final Optional<User> userWithToken = Optional.ofNullable(passwordResetRequest)
        .map(ForgotPasswordRequest::getEmail)
        .flatMap(userRepository::findByEmailWithTokens);
    return userWithToken
        .flatMap(user -> findValidUserToken(passwordResetRequest.getToken(), user, SecurityTokenType.RESET))
        .map(securityTokenService::useToken)
        .map(token -> saveTokenForUser(userWithToken.get(), passwordResetRequest.getNewPassword()))
        .map(mailService::sendPasswordSuccessfullyChanged)
        .orElse(false);
  }

  /**
   * verifies if an email address is confirmed for a user based on a token retrieved
   * from the user repository, and updates the user's email confirmation status accordingly.
   * 
   * @param userId unique identifier of a user for whom the email confirmation is to
   * be checked.
   * 
   * @param emailConfirmToken token issued by the system to confirm the user's email address.
   * 
   * @returns a boolean value indicating whether the email confirmation process was
   * successful or not.
   * 
   * 	- `map(token -> true).orElse(false)` returns `true` if the email confirmation was
   * successful, otherwise `false`.
   * 	- The `Optional` object represents the possibility that there may not be a valid
   * email confirmation token found.
   * 	- The `filter` method is used to filter the user list to only include users who
   * have not confirmed their email.
   * 	- The `map` method is used to map the filtered user list to a SecurityToken, which
   * is then used to confirm the email.
   * 	- The `useToken` method is called on the resulting SecurityToken object to confirm
   * the email.
   * 
   * The output of the `confirmEmail` function can be destructured as follows:
   * 
   * 	- If the output is `true`, it means that the email confirmation was successful.
   * 	- If the output is `false`, it means that there was an error in the email
   * confirmation process.
   */
  @Override
  public Boolean confirmEmail(String userId, String emailConfirmToken) {
    final Optional<User> userWithToken = userRepository.findByUserIdWithTokens(userId);
    Optional<SecurityToken> emailToken = userWithToken
        .filter(user -> !user.isEmailConfirmed())
        .map(user -> findValidUserToken(emailConfirmToken, user, SecurityTokenType.EMAIL_CONFIRM)
        .map(token -> {
          confirmEmail(user);
          return token;
        })
        .map(securityTokenService::useToken)
        .orElse(null));
    return emailToken.map(token -> true).orElse(false);
  }

  /**
   * retrieves a user from the repository, checks if their email is confirmed, and
   * resends an email confirmation token if necessary.
   * 
   * @param userId User ID of the user for whom the email confirmation status needs to
   * be checked.
   * 
   * @returns a boolean value indicating whether an email confirmation token was
   * successfully sent to the user.
   */
  @Override
  public boolean resendEmailConfirm(String userId) {
    return userRepository.findByUserId(userId).map(user -> {
      if(!user.isEmailConfirmed()) {
        SecurityToken emailConfirmToken = securityTokenService.createEmailConfirmToken(user);
        user.getUserTokens().removeIf(token -> token.getTokenType() == SecurityTokenType.EMAIL_CONFIRM && !token.isUsed());
        userRepository.save(user);
        boolean mailSend = mailService.sendAccountCreated(user, emailConfirmToken);
        return mailSend;
      } else {
        return false;
      }
    }).orElse(false);
  }

  /**
   * encodes a new password for a user using a password encoder and saves the updated
   * user object in the repository.
   * 
   * @param user User object to be updated with a new encrypted password.
   * 
   * 	- `user`: The User object contains properties such as `id`, `username`, `email`,
   * and `password`, which are essential for authentication purposes.
   * 
   * @param newPassword encrypted password for the user, which is generated and saved
   * in the `saveTokenForUser` function.
   * 
   * @returns a saved `User` object with an updated encrypted password.
   * 
   * 	- `user`: The updated user object with an encrypted password.
   * 	- `passwordEncoder`: A reference to the password encoder used for encrypting the
   * new password.
   * 	- `userRepository`: A reference to the user repository where the updated user
   * object is saved.
   */
  private User saveTokenForUser(User user, String newPassword) {
    user.setEncryptedPassword(passwordEncoder.encode(newPassword));
    return userRepository.save(user);
  }

  /**
   * searches for an unused security token belonging to a specific user and with the
   * same token type and value as a given token, and whose expiration date is after the
   * current date. It returns an optional instance of `SecurityToken`.
   * 
   * @param token token to be verified for validity and matching with the provided
   * `user` and `securityTokenType`.
   * 
   * @param user User object that is being searched for a valid security token.
   * 
   * 	- `user`: A `User` object representing the user for whom the token is being checked.
   * 	- `token`: The token to be validated.
   * 	- `securityTokenType`: The type of security token being checked.
   * 
   * The function then streams through the user's tokens, filters them based on several
   * conditions, and finds the first token that matches all the conditions.
   * 
   * @param securityTokenType type of security token being searched for, and is used
   * to filter the stream of user tokens to only include those with the specified type.
   * 
   * 	- `isUsed()` - indicates whether the token has been used already or not.
   * 	- `tokenType` - represents the type of security token.
   * 	- `token` - stores the value of the security token.
   * 	- `expiryDate` - marks the expiration date of the token.
   * 
   * These properties are utilized in the filtering and finding process of the function
   * to locate a valid user token.
   * 
   * @returns an `Optional` containing a `SecurityToken` object if a valid token is
   * found, or `None` otherwise.
   * 
   * 	- `Optional<SecurityToken>`: The output is an optional Security Token, which means
   * it may or may not be present depending on the input conditions.
   * 	- `userPasswordResetToken`: This variable contains a Security Token that belongs
   * to the specified user.
   * 	- `isUsed()`: This method checks whether the token has been used or not. If it
   * has been used, the token is no longer valid for password reset.
   * 	- `getTokenType()`: This method returns the type of Security Token.
   * 	- `getToken()`: This method returns the actual Security Token value.
   * 	- `getExpiryDate().isAfter(LocalDate.now())`: This method checks whether the
   * expiry date of the token is after the current date. If the token has expired, it
   * cannot be used for password reset.
   */
  private Optional<SecurityToken> findValidUserToken(String token, User user, SecurityTokenType securityTokenType) {
    Optional<SecurityToken> userPasswordResetToken = user.getUserTokens()
        .stream()
        .filter(tok -> !tok.isUsed()
            && tok.getTokenType() == securityTokenType
            && tok.getToken().equals(token)
            && tok.getExpiryDate().isAfter(LocalDate.now()))
        .findFirst();
    return userPasswordResetToken;
  }

  /**
   * creates a new user object from a `UserDto` object and saves it to the repository
   * using the `save()` method.
   * 
   * @param request UserDto object containing the data for creating a new user in the
   * repository.
   * 
   * 	- `request.getId()`: an integer representing the unique identifier for the user
   * being created
   * 	- `userMapper.userDtoToUser(request)`: a conversion process to map the input DTO
   * to a corresponding `User` object
   * 
   * @returns a saved User object in the repository.
   * 
   * 	- User user: The created user object saved in the repository.
   * 	- Id: The unique identifier assigned to the user.
   * 	- Log trace message: A log statement with the format "saving user with id[{} to
   * repository".
   */
  private User createUserInRepository(UserDto request) {
    User user = userMapper.userDtoToUser(request);
    log.trace("saving user with id[{}] to repository", request.getId());
    return userRepository.save(user);
  }

  /**
   * updates a user's email confirmation status to `true`, sends an account confirmation
   * notification to the user via the mail service, and saves the updated user object
   * in the repository.
   * 
   * @param user User object that contains information about the user whose email is
   * being confirmed, and it is used to update the user's `emailConfirmed` field and
   * send a notification to the mail service.
   * 
   * 	- `user.setEmailConfirmed(true)` sets a Boolean value indicating if the user's
   * email is confirmed or not.
   * 	- `mailService.sendAccountConfirmed(user)` sends an account confirmation notification
   * to the user's registered email address.
   * 	- `userRepository.save(user)` saves the modified user object in the repository
   * for further processing.
   */
  private void confirmEmail(User user) {
    user.setEmailConfirmed(true);
    mailService.sendAccountConfirmed(user);
    userRepository.save(user);
  }

  /**
   * encrypts a user's password by encoding it using the provided password encoder.
   * 
   * @param request UserDto object containing the user's password, which is then encrypted
   * and its encrypted value is set as the new value of the `encryptedPassword` field.
   * 
   * 	- `request`: It is an object of type `UserDto`.
   * 	- `setEncryptedPassword()`: This method sets the `encryptedPassword` attribute
   * of `request` to a password that has been encrypted using `passwordEncoder.encode()`.
   */
  private void encryptUserPassword(UserDto request) {
    request.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));
  }

  /**
   * generates a unique user ID for a given `UserDto` object using the `UUID.randomUUID()`
   * method and assigns it to the `UserDto` object's `userId` field.
   * 
   * @param request UserDto object that contains the user's information and is used to
   * generate a unique user ID for the user.
   * 
   * 	- `request`: An instance of the `UserDto` class, containing various attributes
   * and properties relevant to user data.
   */
  private void generateUniqueUserId(UserDto request) {
    request.setUserId(UUID.randomUUID().toString());
  }
}
