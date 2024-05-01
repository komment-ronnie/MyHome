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
 * is responsible for handling user-related operations in an application. It provides
 * endpoints for signing up, listing all users, getting details of a specific user,
 * resetting a password, and confirming an email address. The controller uses dependency
 * injection to inject the `userService` and `houseService`, which are used to perform
 * actions related to users and houses, respectively.
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
   * receives a `CreateUserRequest` object, creates a corresponding `UserDto` object
   * using the provided request data, and then attempts to create a new user in the
   * system. If successful, it returns a `CreateUserResponse` object containing details
   * of the newly created user.
   * 
   * @param request `CreateUserRequest` object containing information about the user
   * to be created.
   * 
   * 	- `@Valid`: The `request` object is validated before processing.
   * 	- `CreateUserRequest request`: This is the incoming request object containing the
   * details of a new user to be created.
   * 	- `log.trace("Received SignUp request")`: This line logs a message indicating
   * that the `signUp` function has received a sign-up request.
   * 	- `UserDto requestUserDto = userApiMapper.createUserRequestToUserDto(request)`:
   * The incoming `CreateUserRequest` object is converted into a `UserDto` object using
   * the `userApiMapper`. This step allows for easier processing of the request within
   * the function.
   * 	- `Optional<UserDto> createdUserDto = userService.createUser(requestUserDto)`:
   * The `createdUserDto` variable is created to store the resulting user entity after
   * creation, using the `userService`. If the creation was successful, the resulting
   * user entity will be stored in this variable; otherwise, it will be empty.
   * 	- `return createdUserDto.map(userDto -> { ... })`: The `createdUserDto` variable
   * is used to return a response object based on the result of the creation operation.
   * If the creation was successful, the response object will contain the newly created
   * user entity; otherwise, it will contain an error message.
   * 	- `orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build())`: This
   * line provides a fallback response in case the creation operation fails, indicating
   * a conflict with the requested creation.
   * 
   * @returns a `ResponseEntity` object with a status code of `CREATED` and a body
   * containing the created user's response.
   * 
   * 	- `ResponseEntity`: This is an instance of `ResponseEntity`, which represents a
   * response to a web request. It has two methods: `status()` and `body()`. The
   * `status()` method returns an HTTP status code, while the `body()` method returns
   * the response body as a specific type (in this case, `CreateUserResponse`).
   * 	- `CreateUserResponse`: This is a class that represents the response to the sign-up
   * request. It has several properties, including `userId`, `username`, `email`, and
   * `password`. These properties represent the user's ID, username, email address, and
   * password, respectively.
   * 	- `map()`: This method is used to map the `Optional<UserDto>` returned by the
   * `createUser` method to a `CreateUserResponse`. If the `Optional` contains a value,
   * the method returns a `ResponseEntity` with the corresponding `CreateUserResponse`.
   * Otherwise, it returns a `ResponseEntity` with an HTTP status code of `CONFLICT`.
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
   * receives a pageable request from the user and retrieves all users from the user
   * service, mapping them to a response object containing the user details in REST API
   * format. It then returns a response entity with the list of users.
   * 
   * @param pageable page number and the number of users per page to be listed by the
   * user service.
   * 
   * 	- `log.trace("Received request to list all users")` - This line logs a trace
   * message indicating that the method has received a request to list all users.
   * 	- `Set<User> userDetails = userService.listAll(pageable)` - This line calls the
   * `listAll` method of the `userService` class, passing in `pageable` as a parameter.
   * The method returns a set of `User` objects representing the list of users.
   * 	- `Set<GetUserDetailsResponseUser> userDetailsResponse =
   * userApiMapper.userSetToRestApiResponseUserSet(userDetails)` - This line calls the
   * `userApiMapper` class's `userSetToRestApiResponseUserSet` method, passing in the
   * set of `User` objects returned by the `listAll` method. The method maps each `User`
   * object to a corresponding `GetUserDetailsResponseUser` object and returns a set
   * of these mapped objects.
   * 
   * @returns a list of `GetUserDetailsResponse` objects containing the user details.
   * 
   * 	- `setUsers`: A Set of `GetUserDetailsResponseUser` objects, representing the
   * list of all users returned by the function. Each object in the set contains the
   * details of a single user, including their username, email, and roles.
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
   * receives a user ID and returns a `ResponseEntity` object containing the details
   * of the user with that ID, after mapping the user data to a `GetUserDetailsResponse`
   * object using the `userApiMapper`.
   * 
   * @param userId identifier of the user whose details are being requested.
   * 
   * @returns a `ResponseEntity` object with an HTTP status code of `OK` and a body
   * containing the detailed user information.
   * 
   * 	- The function returns an `Optional` instance of `ResponseEntity`, which represents
   * a response entity with a status code and a body.
   * 	- The status code is either `OK` (200) or `NOT_FOUND` (404), indicating whether
   * the user details were successfully retrieved or not.
   * 	- The body of the response entity contains a `GetUserDetailsResponse` object,
   * which represents the user details returned by the function. This object has several
   * attributes, including the user ID, name, email, and other details.
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
   * takes in a request object containing a forgot password request and determines
   * whether to reset the user's password based on the action provided in the request.
   * If successful, it returns an `ResponseEntity` with a status of `OK`. Otherwise,
   * it returns an `ResponseEntity` with a status of `BAD_REQUEST`.
   * 
   * @param action password action to be performed, with possible values of `FORGOT`
   * or `RESET`, which determine the corresponding logic to be executed within the function.
   * 
   * @param forgotPasswordRequest Forgot Password Request object that contains the
   * user's email address and other details required for password resetting.
   * 
   * 	- `@NotNull`: The `action` parameter must not be null.
   * 	- `@Valid`: The `forgotPasswordRequest` object must be valid according to the
   * provided validation rules.
   * 	- `@RequestBody`: The `forgotPasswordRequest` object is passed in as a request
   * body, indicating that it was sent from the client-side in the HTTP request.
   * 	- `ForgotPasswordRequest`: This class represents the request for resetting a
   * user's password. It contains properties such as:
   * 	+ `username`: The username of the user requesting the password reset.
   * 	+ `email`: The email address of the user requesting the password reset.
   * 	+ `password`: The current password of the user, which will be reset.
   * 	+ `newPassword`: The new password that the user wants to set as their password.
   * 	+ `confirmNewPassword`: A confirmation of the new password entered by the user.
   * 
   * @returns an `ResponseEntity` object representing a successful response with a
   * status code of `OK`.
   * 
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response message in a web application. It contains information about
   * the status of the request and any data that was sent or received during the execution
   * of the function.
   * 	- `ok`: This is a boolean value indicating whether the request was successful.
   * If `result` is `true`, then the request was successful, and if it is `false`, then
   * the request failed.
   * 	- `build`: This is a method that returns the constructed response entity. It takes
   * no arguments and simply returns the instance of the `ResponseEntity` class.
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
   * receives a request to list all members of all houses associated with a user, and
   * returns a ResponseEntity containing a ListHouseMembersResponse with the member information.
   * 
   * @param userId user for whom the member list is being retrieved.
   * 
   * @param pageable request to list all members of all houses of a specific user,
   * allowing for pagination and filtering of the results.
   * 
   * The `pageable` parameter is of type `Pageable`, which represents a page of data
   * that can be retrieved from a database or API. The `Pageable` class defines several
   * attributes that can be used to customize the pagination process, including:
   * 
   * 	- `pageNumber`: The current page number being processed.
   * 	- `pageSize`: The number of elements that will be returned in each page.
   * 	- `totalElements`: The total number of elements in the dataset.
   * 	- `totalPages`: The total number of pages that can be retrieved from the dataset.
   * 
   * In this function, we are using the `map` method to transform the deserialized
   * `pageable` parameter into a `ResponseEntity` object. Specifically, we are calling
   * the `map` method on the `houseService.listHouseMembersForHousesOfUserId` function,
   * which returns a `List<HouseMember>` object. We then use the `map` method again to
   * transform the `List<HouseMember>` object into a `ResponseEntity` object using the
   * `map` method.
   * 
   * @returns a `ResponseEntity` containing a list of house members for the specified
   * user.
   * 
   * 	- `ResponseEntity`: This is an object that represents a response to a web request.
   * It can have several properties, including `body`, `headers`, and `statusCode`.
   * 	- `map`: This is a method that takes a function as an argument and applies it to
   * the original output. In this case, the function maps the `houseMembers` list to a
   * new response object.
   * 	- `new ListHouseMembersResponse()`: This is a constructor for the `ListHouseMembersResponse`
   * class. It creates a new instance of this class with an empty `members` field.
   * 	- `map(houseMembers -> new ListHouseMembersResponse().members(houseMembers))`:
   * This line maps the `houseMembers` list to a new response object by calling the
   * `ListHouseMembersResponse` constructor and passing it the `houseMembers` list as
   * an argument.
   * 	- `map(ResponseEntity::ok)`: This line maps the original output to a new response
   * object with a status code of 200 (OK).
   * 	- `orElse(ResponseEntity.notFound().build())`: This line provides an alternative
   * output if the original call fails. It returns a response object with a status code
   * of 404 (Not Found) and an empty body.
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
   * verifies an email address for a user by checking if it has been confirmed using
   * the provided confirmation token. If the email is confirmed, a `ResponseEntity`
   * with an `ok` status code is returned. Otherwise, a `ResponseEntity` with a
   * `badRequest` status code is returned.
   * 
   * @param userId unique identifier of the user whose email is being confirmed.
   * 
   * @param emailConfirmToken token sent to the user's email address for email confirmation.
   * 
   * @returns a `ResponseEntity` object with a status code of either `ok` or `badRequest`,
   * indicating whether the email confirmation was successful or not.
   * 
   * 	- `ResponseEntity`: This is an object that represents a HTTP response entity,
   * which contains information about the response status and body.
   * 	- `ok`: This is a boolean property that indicates whether the email confirmation
   * was successful or not. If it's true, then the email confirmation was successful,
   * otherwise it failed.
   * 	- `build()`: This is a method that creates a new `ResponseEntity` object with the
   * given properties.
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
   * resends an email confirmation to a user's registered email address if the confirmation
   * was previously unsuccessful.
   * 
   * @param userId ID of a user for whom an email confirmation link needs to be resent.
   * 
   * @returns an `ResponseEntity` object representing a successful response with a
   * status of `OK`.
   * 
   * 	- `ResponseEntity`: This is the generic type of the returned response entity,
   * which represents an HTTP response with a status code and a body.
   * 	- `Void`: The type parameter of `ResponseEntity`, indicating that the response
   * entity has no content.
   * 	- `boolean emailConfirmResend`: This variable represents the result of the
   * `resendEmailConfirm` method called within the function, which indicates whether
   * the email confirmation was resent successfully or not.
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
