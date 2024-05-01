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

package com.myhome.controllers;

import com.myhome.api.UsersApi;
import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.dto.mapper.HouseMemberMapper;
import com.myhome.controllers.mapper.UserApiMapper;
import com.myhome.domain.PasswordActionType;
import com.myhome.domain.User;
import com.myhome.model.CreateUserRequest;
import com.myhome.model.CreateUserResponse;
import com.myhome.model.ForgotPasswordRequest;
import com.myhome.model.GetUserDetailsResponse;
import com.myhome.model.GetUserDetailsResponseUser;
import com.myhome.model.ListHouseMembersResponse;
import com.myhome.services.HouseService;
import com.myhome.services.UserService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * Controller for facilitating user actions.
 */
/**
 * is a RESTful API for managing users and their details, passwords, and email
 * confirmations. It provides endpoints for signing up new users, listing all users,
 * getting the details of a specific user, resetting or resetting a user's password,
 * and resending an email confirmation request. Additionally, it also handles the
 * mappings between different data structures and returns appropriate responses to
 * client requests.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController implements UsersApi {

  private final UserService userService;
  private final UserApiMapper userApiMapper;
  private final HouseService houseService;
  private final HouseMemberMapper houseMemberMapper;

  /**
   * Handles the creation of a new user account. It receives a `CreateUserRequest`
   * object, converts it to a `UserDto`, creates a new user using the `userService`,
   * and returns the created user's details in a `CreateUserResponse`.
   * 
   * @param request CreateUserRequest object passed in from the client, providing the
   * necessary information for creating a new user account.
   * 
   * @returns a `ResponseEntity` with a status of `CREATED` and the created user's
   * details in the body.
   */
  @Override
  public ResponseEntity<CreateUserResponse> signUp(@Valid CreateUserRequest request) {
    log.trace("Received SignUp request");
    UserDto requestUserDto = userApiMapper.createUserRequestToUserDto(request);
    Optional<UserDto> createdUserDto = userService.createUser(requestUserDto);
    return createdUserDto
        .map(userDto -> {
          CreateUserResponse response = userApiMapper.userDtoToCreateUserResponse(userDto);
          return ResponseEntity.status(HttpStatus.CREATED).body(response);
        })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
  }

  /**
   * Receives a pageable request from the client and returns a list of users in the
   * form of a `GetUserDetailsResponse`. It uses the `userService` to retrieve the list
   * of users, and then maps them to the corresponding REST API response using `userApiMapper`.
   * 
   * @param pageable page request parameters, such as the number of users to be listed
   * and the sorting criteria.
   * 
   * @returns a list of user details in the form of a `GetUserDetailsResponse`.
   */
  @Override
  public ResponseEntity<GetUserDetailsResponse> listAllUsers(Pageable pageable) {
    log.trace("Received request to list all users");

    Set<User> userDetails = userService.listAll(pageable);
    Set<GetUserDetailsResponseUser> userDetailsResponse =
        userApiMapper.userSetToRestApiResponseUserSet(userDetails);

    GetUserDetailsResponse response = new GetUserDetailsResponse();
    response.setUsers(userDetailsResponse);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Receives a user ID as input and retrieves the corresponding user details from the
   * service, mapping them to a `GetUserDetailsResponse` object, and returning it as
   * an HTTP response entity.
   * 
   * @param userId unique identifier of the user for whom details are to be retrieved.
   * 
   * @returns a `ResponseEntity` object with a status code of HTTP 200 and a body
   * containing the details of the user with the specified ID.
   */
  @Override
  public ResponseEntity<GetUserDetailsResponseUser> getUserDetails(String userId) {
    log.trace("Received request to get details of user with Id[{}]", userId);

    return userService.getUserDetails(userId)
        .map(userApiMapper::userDtoToGetUserDetailsResponse)
        .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * Processes password reset requests. It checks the action type and performs the
   * corresponding operation: sending a password reset link or resetting the password.
   * If successful, it returns an `OK` response entity; otherwise, it returns a
   * `BAD_REQUEST` response entity.
   * 
   * @param action password action to be performed, which is either FORGOT or RESET,
   * and is used to determine the appropriate password reset process.
   * 
   * @param forgotPasswordRequest Forgot Password Request object containing the user's
   * email address and other information needed to initiate the password reset process.
   * 
   * @returns an `ResponseEntity` object with a status code of either `ok` or `badRequest`,
   * depending on whether the password reset was successful or not.
   */
  @Override
  public ResponseEntity<Void> usersPasswordPost(@NotNull @Valid String action, @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
    boolean result = false;
    PasswordActionType parsedAction = PasswordActionType.valueOf(action);
    if (parsedAction == PasswordActionType.FORGOT) {
      result = true;
      userService.requestResetPassword(forgotPasswordRequest);
    } else if (parsedAction == PasswordActionType.RESET) {
      result = userService.resetPassword(forgotPasswordRequest);
    }
    if (result) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Retrieves a list of members for all houses associated with a user, maps them to a
   * response entity, and returns it.
   * 
   * @param userId unique identifier of the user for whom the list of housemates is
   * being requested.
   * 
   * @param pageable page number and page size of the list of house members that the
   * user wants to view, which allows for pagination of the list.
   * 
   * @returns a `ResponseEntity` object representing the list of house members for the
   * specified user.
   */
  @Override
  public ResponseEntity<ListHouseMembersResponse> listAllHousemates(String userId, Pageable pageable) {
    log.trace("Received request to list all members of all houses of user with Id[{}]", userId);

    return houseService.listHouseMembersForHousesOfUserId(userId, pageable)
            .map(HashSet::new)
            .map(houseMemberMapper::houseMemberSetToRestApiResponseHouseMemberSet)
            .map(houseMembers -> new ListHouseMembersResponse().members(houseMembers))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Confirms an email address for a user by checking the confirmation token provided,
   * and returns a response entity indicating whether the confirmation was successful
   * or not.
   * 
   * @param userId ID of the user whose email is being confirmed.
   * 
   * @param emailConfirmToken confirmation token provided to the user for email
   * verification, which is used by the `userService.confirmEmail()` method to verify
   * the email address of the user.
   * 
   * @returns an `ResponseEntity` object with a status of either `ok` or `badRequest`,
   * depending on whether the email confirmation was successful.
   */
  @Override
  public ResponseEntity<Void> confirmEmail(String userId, String emailConfirmToken) {
    boolean emailConfirmed = userService.confirmEmail(userId, emailConfirmToken);
    if(emailConfirmed) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Resends an email confirmation request to a user if one was previously sent and
   * failed, returning a `ResponseEntity` with a status code indicating success or failure.
   * 
   * @param userId user for whom the email confirmation status is to be checked and
   * resent if necessary.
   * 
   * @returns an `OK` response entity indicating successful resending of the email
   * confirmation to the user.
   */
  @Override
  public ResponseEntity<Void> resendConfirmEmailMail(String userId) {
    boolean emailConfirmResend = userService.resendEmailConfirm(userId);
    if(emailConfirmResend) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }
}
