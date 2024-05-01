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
 * provides methods for managing user accounts in a Java-based application. It includes
 * functions to create new users, update existing users, validate user tokens, and
 * encrypt user passwords. Additionally, the class generates unique user IDs for new
 * users and sends confirmation emails to users upon account creation.
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
   * creates a new user in the application, generates a unique ID, encrypts the password,
   * and sends an email confirmation token to the user's registered email address. It
   * returns an optional `UserDto` representing the created user.
   * 
   * @param request UserDto object containing the user's information to be created,
   * which is then used to create a new user in the repository and send an email
   * confirmation token to the user's registered email address.
   * 
   * 	- `request.getEmail()`: The email address of the new user.
   * 	- `userRepository.findByEmail(request.getEmail())`: Checks if a user with the
   * same email address already exists in the repository. If yes, returns the existing
   * user. If no, proceeds to the next step.
   * 	- `generateUniqueUserId(request)`: Generates a unique ID for the new user.
   * 	- `encryptUserPassword(request)`: Encrypts the password of the new user.
   * 	- `createUserInRepository(request)`: Creates a new user object in the repository.
   * 	- `securityTokenService.createEmailConfirmToken(newUser)`: Creates an email
   * confirmation token for the new user.
   * 	- `mailService.sendAccountCreated(newUser, emailConfirmToken)`: Sends an email
   * to the new user's registered email address with the email confirmation token.
   * 
   * @returns an `Optional<UserDto>` containing a `UserDto` object representing the
   * created user.
   * 
   * 	- `Optional<UserDto> createUser(UserDto request)`: This is the method that takes
   * in a `UserDto` object and creates a new user in the system.
   * 	- `if (userRepository.findByEmail(request.getEmail()) == null)`: If no user with
   * the same email address already exists in the system, then this code block is executed.
   * 	- `generateUniqueUserId(request);`: This method generates a unique ID for the new
   * user.
   * 	- `encryptUserPassword(request);`: The password of the new user is encrypted using
   * a security token service.
   * 	- `createUserInRepository(request)`: This method creates a new user object in the
   * repository.
   * 	- `SecurityToken emailConfirmToken = securityTokenService.createEmailConfirmToken(newUser);`:
   * An email confirmation token is generated and stored in the security token service.
   * 	- `mailService.sendAccountCreated(newUser, emailConfirmToken);`: The account
   * creation email is sent to the new user's email address using the mail service.
   * 	- `UserDto newUserDto = userMapper.userToUserDto(newUser);`: A UserDto object is
   * created from the newly created user object.
   * 	- `return Optional.of(newUserDto);`: The returned value is an `Optional` containing
   * the `UserDto` object of the newly created user.
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
   * returns a set of users, retrieved from a database using a paginated query.
   * 
   * @returns a set of `User` objects.
   * 
   * The output is a `Set` of `User` objects. This means that the function returns a
   * collection of user objects, where each user object represents a unique user in the
   * system.
   * 
   * The `Set` is implemented using a `HashSet`, which means that the function guarantees
   * that no duplicate user objects will be returned.
   * 
   * The `List<User>` returned by the function contains up to 200 user objects, as
   * specified by the `PageRequest`. If there are fewer than 200 user objects in the
   * system, the list will contain only those user objects.
   * 
   * The order of the user objects in the list is determined by the `PageRequest`, which
   * allows for paging through the list of users.
   */
  @Override
  public Set<User> listAll() {
    return listAll(PageRequest.of(0, 200));
  }

  /**
   * returns a set of all users stored in the `userRepository`. It uses the `findAll`
   * method to retrieve a pageable list of users and converts it to a set for efficient
   * use.
   * 
   * @param pageable pagination information for the users, allowing the listAll method
   * to fetch a subset of the user data from the repository based on the specified page
   * number and size.
   * 
   * The `Pageable` object provided as an argument to this function is a parameter for
   * filtering and paging user entities. It contains the page number and size (represented
   * by the integer values `pageNumber` and `pageSize`, respectively) that specify the
   * range of users to be retrieved. These values determine which user entities are
   * returned in response.
   * 
   * @returns a set of all users retrieved from the user repository.
   * 
   * 	- The output is a `Set` of `User` objects, indicating that the method returns a
   * collection of user objects.
   * 	- The `pageable` parameter passed to the `findAll` method is used to specify how
   * the users should be retrieved, with options for pagination and sorting.
   * 	- The `toSet` method is used to convert the `List<User>` returned by `findAll`
   * into a `Set`, which ensures that no duplicates are present in the output.
   */
  @Override
  public Set<User> listAll(Pageable pageable) {
    return userRepository.findAll(pageable).toSet();
  }

  /**
   * retrieves a user's details from the database and their community membership
   * information, then maps them to a `UserDto` object and returns an optional instance
   * of `UserDto`.
   * 
   * @param userId identifier of the user for whom the detailed user information is
   * being retrieved.
   * 
   * @returns an optional `UserDto` object containing the user's community IDs and details.
   * 
   * 	- `Optional<UserDto>`: This represents an optional result, which means that the
   * function may return `None` if no user details are found.
   * 	- `userOptional`: This is an optional reference to a `User` object, which is
   * obtained from the `userRepository`. If no user is found, this will be `None`.
   * 	- `communityIds`: This is a set of strings that represent the IDs of the communities
   * to which the user belongs.
   * 	- `userMapper`: This is a function that maps a `User` object to a `UserDto` object.
   * The resulting `UserDto` object contains the same properties as the original `User`
   * object, but with simplified attributes.
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
   * returns an Optional<UserDto> object containing a user's details and community IDs.
   * It first retrieves the user from the repository, then maps the user to a UserDto
   * object with community IDs.
   * 
   * @param userEmail email address of the user for whom the repository is being queried,
   * and it is used to filter the results of the `findByEmail()` method.
   * 
   * @returns an `Optional` containing a `UserDto` object with community IDs.
   * 
   * 	- The function returns an `Optional` object containing a `UserDto` instance, which
   * represents the user with their communities' IDs.
   * 	- The `UserDto` object has a field called `communities`, which is a set of strings
   * representing the IDs of the communities the user belongs to.
   * 	- The `userMapper` is responsible for mapping the original `User` entity to a
   * `UserDto` instance.
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
   * handles password reset requests by finding the user with the provided email,
   * creating a new security token, and sending a password recovery code to the user's
   * registered email address.
   * 
   * @param forgotPasswordRequest request for password reset sent by the user, and it
   * contains the email address of the user to whom the password reset link should be
   * sent.
   * 
   * 	- `Optional.ofNullable(forgotPasswordRequest)`: This line returns an `Optional`
   * object containing the `ForgotPasswordRequest` instance or `null`, depending on
   * whether one was provided in the function call.
   * 	- `map(ForgotPasswordRequest::getEmail)`: If the `ForgotPasswordRequest` instance
   * is non-null, this line calls the `getEmail()` method on it and returns its result
   * as an `Optional` object.
   * 	- `flatMap(email -> userRepository.findByEmailWithTokens(email))`: This line
   * performs a second level of mapping by calling the `findByEmailWithTokens()` method
   * on the `userRepository`, passing in the result of the previous `map()` operation
   * (i.e., the email address). The method returns an `Optional` object containing the
   * `User` instance or `null`, depending on whether one was found with the matching
   * email address.
   * 	- `map(user -> { ... })`: If a `User` instance is returned by the `findByEmailWithTokens()`
   * method, this line maps it to a new `SecurityToken` object using the
   * `securityTokenService.createPasswordResetToken()` method. The resulting `SecurityToken`
   * instance contains the user's ID and token value.
   * 	- `userRepository.save(user)`: This line saves the updated `User` instance in the
   * repository, so that its changes are persisted to the database.
   * 	- `orElse(false)`: If the previous mappings do not produce a non-null result,
   * this line returns `false`.
   * 
   * In summary, the function takes a `ForgotPasswordRequest` instance as input and
   * uses various mapping operations to retrieve a user's security token for password
   * reset.
   * 
   * @returns a boolean value indicating whether the password reset process was successful.
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
   * resets a user's password by retrieving their user token from the repository,
   * validating it with the given token, saving a new token for the user, and sending
   * a notification to the user's registered email address.
   * 
   * @param passwordResetRequest Forgot Password request from the user, containing the
   * email address and the reset token.
   * 
   * 	- `ForgotPasswordRequest passwordResetRequest`: This class represents a request
   * to reset a user's password. It contains an email address belonging to the user who
   * wants their password reset.
   * 	- `getEmail()`: returns the email address of the user who made the request.
   * 	- `getToken()`: returns the security token provided by the user for resetting
   * their password.
   * 	- `getNewPassword()`: returns the new password that the user wants to set.
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
   * verifies an email address's confirmation status by querying a repository, filtering
   * user objects based on their token status, and invoking a `useToken` method to
   * confirm the email address if necessary. It returns a boolean value indicating
   * whether the email address is confirmed.
   * 
   * @param userId unique identifier of the user for whom the email confirmation status
   * is being checked.
   * 
   * @param emailConfirmToken token for confirming the user's email address.
   * 
   * @returns a boolean value indicating whether the email confirmation process was
   * successful for the provided user ID and token.
   * 
   * 	- `userWithToken`: An optional `User` object that contains information about the
   * user whose email is being confirmed.
   * 	- `emailToken`: An optional `SecurityToken` object that represents the email
   * confirmation token for the specified user.
   * 	- `token`: A `SecurityToken` object that represents the email confirmation token
   * for the specified user.
   * 	- `useToken`: A `SecurityToken` object that represents the email confirmation
   * token for the specified user, which is used to confirm the email address.
   * 
   * The function returns a boolean value indicating whether the email confirmation was
   * successful or not. If the `emailToken` is present and has a non-null value, the
   * function returns `true`. Otherwise, it returns `false`.
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
   * retrieves a user from the repository based on their ID, resets their email
   * confirmation status if necessary, and sends an email confirmation token via the
   * mail service.
   * 
   * @param userId User ID of the user for whom the email confirmation should be resent.
   * 
   * @returns a boolean value indicating whether an email confirmation token was sent
   * to the user.
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
   * saves a user's encrypted password to the repository after updating the user object
   * with the new password.
   * 
   * @param user User object to be updated with a new encrypted password.
   * 
   * 	- `user`: This represents an object of the `User` class, which contains properties
   * such as `id`, `username`, `email`, and `password`.
   * 	- `newPassword`: A string that represents the new password to be saved for the user.
   * 
   * @param newPassword encrypted password for the user that is being saved.
   * 
   * @returns a saved User object with an encrypted password.
   * 
   * 	- `user`: The user object that is being updated with the new password.
   * 	- `newPassword`: The new password to be saved for the user.
   * 	- `passwordEncoder`: A password encoder used to encode the new password before
   * saving it in the database.
   * 	- `userRepository`: The repository where the user object is being saved after
   * updating its encrypted password.
   */
  private User saveTokenForUser(User user, String newPassword) {
    user.setEncryptedPassword(passwordEncoder.encode(newPassword));
    return userRepository.save(user);
  }

  /**
   * searches for a valid SecurityToken within a User's tokens based on specified
   * criteria, including token type and value matching, and expiration date being after
   * the current date.
   * 
   * @param token token that needs to be validated against the user's tokens.
   * 
   * @param user User object whose user tokens are being searched for a valid token.
   * 
   * 	- `user`: A `User` object containing information about the user, such as their
   * username and email address.
   * 	- `token`: A string representing the user's token.
   * 	- `securityTokenType`: An enumeration value indicating the type of security token
   * (e.g., password reset token).
   * 
   * The function then filters through the `user.getUserTokens()` stream to find a token
   * that meets the specified criteria:
   * 
   * 	- `isUsed()`: Whether the token has been used before.
   * 	- `tokenType()`: The type of security token (e.g., password reset token).
   * 	- `token()`: The actual token value.
   * 	- `expiryDate()`: The expiration date of the token, measured in LocalDate format.
   * 
   * Finally, the function returns an `Optional` containing the found token if it meets
   * all the criteria, otherwise returns `Optional.empty()`.
   * 
   * @param securityTokenType token type to filter the user tokens by, which is used
   * to determine which tokens are eligible for password reset.
   * 
   * 	- `isUsed`: a boolean indicating whether the token has been used or not.
   * 	- `tokenType`: an enumeration representing the type of security token, which can
   * be one of the constants defined in the class.
   * 	- `token`: a string representing the token value.
   * 	- `expiryDate`: a `LocalDate` object representing the expiration date of the token.
   * 
   * @returns an `Optional` containing a `SecurityToken` if one exists and meets the
   * specified criteria.
   * 
   * 	- `Optional<SecurityToken>`: This is a container for holding a `SecurityToken`
   * object, which represents a valid user token. The `Optional` class provides a way
   * to safely handle null or non-existent values.
   * 	- `SecurityToken`: This is the actual token that is being searched for, which has
   * various attributes such as `tokenType`, `token`, and `expiryDate`.
   * 	- `user`: This is the user object that the token belongs to, which contains other
   * attributes such as `id` and `password`.
   * 	- `securityTokenType`: This is the type of security token being searched for,
   * which can be one of the predefined constants in the code.
   * 
   * Overall, this function provides a way to find a valid user token based on certain
   * criteria, such as the token not being used yet, having the correct type, and
   * matching the provided token value.
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
   * creates a new User object from a `UserDto` input and saves it to the repository
   * for persistence.
   * 
   * @param request UserDto object containing the data for the user to be saved, which
   * is mapped by the `userMapper` to a corresponding `User` object before being saved
   * into the repository.
   * 
   * 	- `request.getId()` represents the unique identifier for the user being created.
   * 
   * @returns a saved user object in the repository.
   * 
   * 	- `User user`: The created user object, containing the data from the `request`
   * parameter and any additional information stored in the repository.
   * 	- `userRepository.save(user)`: The method call that saves the user object to the
   * repository, which persists the data to a storage system.
   */
  private User createUserInRepository(UserDto request) {
    User user = userMapper.userDtoToUser(request);
    log.trace("saving user with id[{}] to repository", request.getId());
    return userRepository.save(user);
  }

  /**
   * updates a user's email status to confirmed and sends a notification to the mail
   * service, then saves the updated user record in the repository.
   * 
   * @param user User object that contains the email address to be confirmed and is
   * used to update the `emailConfirmed` field to `true`, send an account confirmation
   * notification using the `mailService`, and save the updated User object in the repository.
   * 
   * 	- `setEmailConfirmed(true)` sets the `emailConfirmed` field to `true`.
   * 	- `mailService.sendAccountConfirmed(user)` sends an account confirmation notification
   * using the `mailService`.
   * 	- `userRepository.save(user)` saves the updated user object in the repository.
   */
  private void confirmEmail(User user) {
    user.setEmailConfirmed(true);
    mailService.sendAccountConfirmed(user);
    userRepository.save(user);
  }

  /**
   * encrypts a user's password by encoding it using a password encoder.
   * 
   * @param request UserDto object containing the user's password to be encrypted.
   * 
   * 	- `request.setEncryptedPassword`: The method sets an encrypted password for the
   * user by calling `passwordEncoder.encode()` on the original password.
   */
  private void encryptUserPassword(UserDto request) {
    request.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));
  }

  /**
   * generates a unique user ID for a given `UserDto` object using the `UUID.randomUUID()`
   * method and assigns it to the `UserId` field of the request object.
   * 
   * @param request `UserDto` object containing information about the user for whom a
   * unique ID is being generated.
   * 
   * 	- `request`: an instance of `UserDto`, which contains user-related information.
   */
  private void generateUniqueUserId(UserDto request) {
    request.setUserId(UUID.randomUUID().toString());
  }
}
