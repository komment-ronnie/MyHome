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
 * Implements {@link UserService} and uses Spring Data JPA repository to does its work.
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
   * generates a unique user ID, encrypts the password, creates a new user in the
   * repository, sends an email confirmation token, and maps the user to a UserDto
   * object for return.
   * 
   * @param request UserDto object containing the user's details to be created, which
   * is used to generate a unique user ID, encrypt the password, create the user in the
   * repository, and send an email confirmation token.
   * 
   * @returns an optional `UserDto` object representing the newly created user.
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

  @Override
  public Set<User> listAll() {
    return listAll(PageRequest.of(0, 200));
  }

  /**
   * from the provided code returns a set of all users in the user repository, fetched
   * from the database using the `findAll` method and paginated using the `pageable` parameter.
   * 
   * @param pageable pagination information for retrieving a subset of users from the
   * repository.
   * 
   * @returns a set of `User` objects.
   */
  @Override
  public Set<User> listAll(Pageable pageable) {
    return userRepository.findAll(pageable).toSet();
  }

  /**
   * retrieves user details from a repository and maps them to a `UserDto`. It then
   * returns an optional instance of `UserDto`.
   * 
   * @param userId identifier of the user for whom details are being retrieved.
   * 
   * @returns an `Optional` instance containing a `UserDto` object with the user's
   * community IDs and details.
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
   * maps a user repository findByEmail query to a UserDto object, transforming the
   * user's community IDs into a set.
   * 
   * @param userEmail email address of the user for whom the method is searching.
   * 
   * @returns a `Optional<UserDto>` object containing the user's community IDs and other
   * information.
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
   * resets a user's password by generating a new security token and sending a password
   * recovery code to the user's registered email address.
   * 
   * @param forgotPasswordRequest email address of the user who is requesting a password
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
   * resets a user's password by verifying the provided token, finding the user with
   * the matching email address, generating a new security token, and sending a
   * notification to the user.
   * 
   * @param passwordResetRequest ForgotPasswordRequest object containing the user's
   * email address and a token for password reset, which is used to retrieve the user's
   * security token from the database and update their password.
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
   * verifies if a user's email is confirmed by checking for a valid email confirmation
   * token and marking the user as confirmed in the database.
   * 
   * @param userId user ID of the user for whom the email confirmation needs to be checked.
   * 
   * @param emailConfirmToken 10-digit token that is generated when an email address
   * is confirmed, which is used to confirm the user's email address in the database.
   * 
   * @returns a boolean value indicating whether the email confirmation token was
   * successfully verified and the user's email confirmed.
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
   * resends an email confirmation token to a user if they have not confirmed their
   * email address.
   * 
   * @param userId ID of the user for whom the email confirmation status is to be checked
   * and updated.
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
   * saves a user's encrypted password to the database after updating it with a new
   * password provided as an argument.
   * 
   * @param user User object that contains the user's information and password, which
   * is updated with the new password provided in the `newPassword` parameter before
   * saving it to the database by the `userRepository.save()` method.
   * 
   * @param newPassword encrypted password for the user, which is then saved to the
   * database by the `saveTokenForUser` function.
   * 
   * @returns a saved User object with an encrypted password.
   */
  private User saveTokenForUser(User user, String newPassword) {
    user.setEncryptedPassword(passwordEncoder.encode(newPassword));
    return userRepository.save(user);
  }

  /**
   * queries the database for a SecurityToken that matches the input token, user, and
   * security token type, and returns an Optional<SecurityToken> containing the matching
   * token if found, otherwise returns an empty Optional.
   * 
   * @param token SecurityToken to be checked for validity.
   * 
   * @param user user for whom the valid user token is being searched.
   * 
   * @param securityTokenType token type that the function is searching for, and it is
   * used to filter the stream of user tokens to only include tokens with the specified
   * type.
   * 
   * @returns an `Optional` of a `SecurityToken` if one exists and meets the specified
   * criteria, otherwise `Optional.empty`.
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
   * creates a new user entity and saves it to the repository, using the provided UserDto
   * as input.
   * 
   * @param request UserDto object containing information about the user to be created,
   * which is used by the `userMapper` to convert it into a corresponding `User` entity
   * before saving it to the repository.
   * 
   * @returns a saved user object in the repository.
   */
  private User createUserInRepository(UserDto request) {
    User user = userMapper.userDtoToUser(request);
    log.trace("saving user with id[{}] to repository", request.getId());
    return userRepository.save(user);
  }

  /**
   * updates a user's email confirmation status to `true`, sends a notification to the
   * user's registered email address, and saves the updated user object in the repository.
   * 
   * @param user User object to be updated with the `emailConfirmed` field set to true
   * and then saved in the user repository.
   */
  private void confirmEmail(User user) {
    user.setEmailConfirmed(true);
    mailService.sendAccountConfirmed(user);
    userRepository.save(user);
  }

  /**
   * encrypts a user's password using a password encoder and stores the encrypted
   * password in the `request` object.
   * 
   * @param request UserDto object containing the user's login details, which are
   * encrypted using the `passwordEncoder.encode()` method and returned as an encrypted
   * password.
   */
  private void encryptUserPassword(UserDto request) {
    request.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));
  }

  /**
   * generates a unique user ID for a given UserDto object using the `UUID.randomUUID()`
   * method and assigns it to the `UserId` property of the request object.
   * 
   * @param request UserDto object that contains the user's information, and it is used
   * to set the user's unique ID generated by the function.
   */
  private void generateUniqueUserId(UserDto request) {
    request.setUserId(UUID.randomUUID().toString());
  }
}
