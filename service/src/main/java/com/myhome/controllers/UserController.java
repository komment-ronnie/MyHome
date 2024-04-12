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
   * receives a `CreateUserRequest` and maps it to a `UserDto`, then creates a new user
   * using the `userService`. If successful, it returns a `ResponseEntity` with a
   * `CreateUserResponse`, otherwise it returns a `ResponseEntity` with a `HttpStatus.CONFLICT`.
   * 
   * @param request CreateUserRequest object passed from the client, which contains
   * user information to be created in the system.
   * 
   * 	- `@Valid` is an annotation that indicates the request has been validated by the
   * `@Validated` processor, ensuring it meets certain criteria before entering this method.
   * 	- `CreateUserRequest request` represents a user creation request containing various
   * attributes such as username, email, password, and other relevant information.
   * 
   * @returns a `ResponseEntity` object with a status code of `CREATED` and the created
   * user response as its body.
   * 
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response object that can have a status and a body. The status is set
   * to `HttpStatus.CREATED`, indicating that the user has been created successfully.
   * 	- `body`: This is a reference to the `CreateUserResponse` object that contains
   * the details of the created user. The `body` attribute is not null, indicating that
   * a response was generated.
   * 
   * The `orElseGet` method is used to provide a fallback response if no user is created
   * successfully. In this case, the response status is set to `HttpStatus.CONFLICT`,
   * indicating that there was an error creating the user.
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
   * receives a `Pageable` parameter and retrieves all users from the database using
   * the `userService`. It then maps the retrieved users to a response object using the
   * `userApiMapper`, before returning it as a `ResponseEntity` with a status code of
   * `OK` and the response body.
   * 
   * @param pageable page number and limit for retrieving user details, allowing for
   * efficient pagination of a large dataset.
   * 
   * 	- The `log.trace()` statement indicates that the method has started processing
   * the request.
   * 	- The `Set<User> userDetails` variable holds the list of users returned by the
   * `userService.listAll(pageable)` call.
   * 	- The `userApiMapper.userSetToRestApiResponseUserSet()` method converts the `User`
   * set to a `GetUserDetailsResponseUserSet`.
   * 	- The `GetUserDetailsResponse response` variable holds the final response object,
   * which contains the list of users in its `users` field.
   * 
   * @returns a list of user details in a Rest API response format.
   * 
   * 	- `GetUserDetailsResponse`: This is the class that represents the response from
   * the API. It has a single property called `users`, which is a set of
   * `GetUserDetailsResponseUser` objects.
   * 	- `GetUserDetailsResponseUser`: This is a inner class of `GetUserDetailsResponse`
   * that represents a user object in the response. It has several properties, including
   * `id`, `username`, `email`, and `roles`.
   * 	- `userService`: This is the class that provides the list of users through the
   * `listAll()` method. It is not destructured in this function.
   * 	- `userApiMapper`: This is the class that maps the user list from the service to
   * the API response format. It is not destructured in this function.
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
   * retrieves user details given a user ID, maps them to `GetUserDetailsResponse`, and
   * returns a `ResponseEntity` with the transformed response.
   * 
   * @param userId unique identifier of the user whose details are to be retrieved.
   * 
   * @returns a `ResponseEntity` object with an HTTP status code of OK and a body
   * containing the user details.
   * 
   * 	- The `ResponseEntity` object represents a response with an HTTP status code of
   * OK (HttpStatus.OK) and a body containing the user details in the form of a
   * `GetUserDetailsResponseUser` object.
   * 	- The `getUserDetails` function returns a `Optional` instance of `ResponseEntity`,
   * which means that the function may or may not return a response, depending on whether
   * a user with the given `userId` exists in the database. If no user is found, the
   * function returns a `ResponseEntity` with an HTTP status code of NOT_FOUND (HttpStatus.NOT_FOUND).
   * 	- The `map` method is used to transform the result of the `userService.getUserDetails(userId)`
   * call into a `ResponseEntity` instance. The `map` method takes two lambda functions
   * as arguments: one that maps the `UserDto` object returned by `userService.getUserDetails(userId)`
   * to a `GetUserDetailsResponseUser` object using `userApiMapper.userDtoToGetUserDetailsResponse`,
   * and another that maps the resulting `GetUserDetailsResponseUser` object to a
   * `ResponseEntity` instance with an HTTP status code of OK (HttpStatus.OK) and a
   * body containing the user details.
   * 	- If the `getUserDetails` function does not find a user with the given `userId`
   * in the database, it returns an empty `Optional` instance, which is then mapped to
   * a `ResponseEntity` instance with an HTTP status code of NOT_FOUND (HttpStatus.NOT_FOUND)
   * using the second lambda function.
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
   * handles password reset requests from users. It determines whether the action is
   * FORGOT or RESET, and based on that, it calls the corresponding methods to reset
   * or validate a password. If successful, it returns an `OK` response entity, otherwise
   * it returns a `BAD_REQUEST` response entity.
   * 
   * @param action password action to be performed, with possible values of `FORGOT`
   * or `RESET`, which determine the corresponding action to be taken by the function.
   * 
   * @param forgotPasswordRequest Forgot Password Request object containing the user's
   * email address and other information required for password reset.
   * 
   * 	- `@NotNull`: The `action` parameter must not be null.
   * 	- `@Valid`: The `forgotPasswordRequest` object must be valid according to its schema.
   * 	- `@RequestBody`: The `forgotPasswordRequest` object is passed as a request body
   * in the HTTP request.
   * 	- `ForgotPasswordRequest`: This class represents the request body for resetting
   * or retrieving a password. It contains properties such as `email`, `password`, and
   * `reason`.
   * 
   * @returns a `ResponseEntity` object representing an HTTP 200 OK response.
   * 
   * 	- `ResponseEntity`: This is an object that represents a response to a HTTP request.
   * It has a `statusCode` field that indicates the status of the response (e.g. 200
   * for OK, 404 for Not Found).
   * 	- `build()`: This is a method that creates a new `ResponseEntity` instance with
   * the specified properties.
   * 	- `ok()`: This is an instance of `ResponseEntity` with a status code of 200 (OK).
   * 
   * In the function, the output of the `usersPasswordPost` function depends on the
   * value of the `parsedAction` variable, which is set to one of three values: `FORGOT`,
   * `RESET`, or `UNKNOWN`. If the value of `parsedAction` is `FORGOT`, then the function
   * returns a response with a status code of 200 (OK) indicating that the password
   * reset process has been initiated. If the value of `parsedAction` is `RESET`, then
   * the function returns a response with a status code of 204 (No Content) indicating
   * that the password has been reset successfully. Otherwise, the function returns a
   * response with a status code of 400 (Bad Request) indicating that there is an error
   * in the request.
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
   * receives a request to list all members of all houses of a user, retrieves the
   * members from the houses of the user, maps them to a Rest API response format, and
   * returns it as a ResponseEntity with an OK status or a NOT_FOUND status if there
   * are no members.
   * 
   * @param userId ID of the user for whom the list of house members is being requested.
   * 
   * @param pageable page number and page size required for fetching all house members
   * of a particular user.
   * 
   * 	- `userId`: The user ID for which the houses and members will be listed.
   * 	- `pageable`: A `Pageable` object that contains information about the pagination
   * of results, such as the page number, page size, total pages, and total items.
   * 
   * @returns a `ResponseEntity` object containing a list of `HouseMemberSet` objects
   * representing all housemembers of the specified user.
   * 
   * 	- `ResponseEntity`: This is the top-level class in Spring Web Flux that represents
   * a response entity, which can be either a success or failure response.
   * 	- `ok`: This is a subclass of `ResponseEntity` that indicates a successful response
   * with a 200 status code and a list of `HouseMemberSet` objects as its body.
   * 	- `notFound`: This is a subclass of `ResponseEntity` that indicates a failed
   * response with a 404 status code and a message indicating that the requested resource
   * could not be found.
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
   * verifies whether an email address is confirmed for a given user ID by making a
   * call to the `userService`. If the email is confirmed, it returns an `OK` response
   * entity. Otherwise, it returns a `BAD_REQUEST` response entity.
   * 
   * @param userId user whose email is being confirmed.
   * 
   * @param emailConfirmToken token sent to the user's email for confirmation of their
   * email address.
   * 
   * @returns a `ResponseEntity` object with a status of `ok` or `badRequest`, depending
   * on whether the email confirmation was successful or not.
   * 
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response to a web service request. It contains an `Ok` or `BadRequest`
   * status, as well as other attributes such as headers and body.
   * 	- `ok()`: This is a method that builds an `ResponseEntity` with an `Ok` status.
   * 	- `build()`: This is a method that builds the entire response entity, including
   * the status, headers, and body.
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
   * resends an email confirmation to a user if one was previously sent and failed,
   * returning a response entity with a status of `ok` or `badRequest`.
   * 
   * @param userId ID of the user for whom the email confirmation status is to be resent.
   * 
   * @returns an `ResponseEntity` object with a status of either `ok` or `badRequest`.
   * 
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response to a HTTP request. It has a `statusCode` field that indicates
   * the status of the response (either `OK` or `BAD_REQUEST`).
   * 	- `ok()`: This is a method of the `ResponseEntity` class that returns an instance
   * of the `OkResponse` subclass, which represents a successful response with a
   * `statusCode` of `200`.
   * 	- `build()`: This is a method of the `ResponseEntity` class that returns a new
   * instance of the response object, allowing the caller to add additional attributes
   * or modify the existing ones.
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
