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

package com.prathab.userservice.controllers;

import com.prathab.userservice.controllers.models.mapper.UserApiMapper;
import com.prathab.userservice.controllers.models.request.CreateUserRequest;
import com.prathab.userservice.controllers.models.response.CreateUserResponse;
import com.prathab.userservice.services.UserService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for facilitating user actions.
 */
/**
 * is a RESTful controller that facilitates user actions. It has three main methods:
 * status(), signUp() and CreateUserResponse(). The class uses log4j for logging
 * purposes and depends on the Spring Core, Spring Web, and Spring Data JPA frameworks.
 */
@RestController
@Slf4j
public class UserController {
  private final UserService userService;
  private final UserApiMapper userApiMapper;
  private final Environment environment;

  public UserController(UserService userService,
      UserApiMapper userApiMapper, Environment environment) {
    this.userService = userService;
    this.userApiMapper = userApiMapper;
    this.environment = environment;
  }

  /**
   * logs trace messages and returns the string "Working".
   * 
   * @returns "Working".
   */
  @GetMapping("/users/status")
  public String status() {
    log.trace("Running on port{} with jwt_secret{}",
        environment.getProperty("local.server.port"),
        environment.getProperty("token.secret"));
    return "Working";
  }

  /**
   * receives a `CreateUserRequest` object from the client, creates a new user entity
   * using the provided request details, and returns a `CreateUserResponse` object with
   * the newly created user data.
   * 
   * @param request `CreateUserRequest` object passed from the client, which contains
   * the user's information to be created in the system.
   * 
   * 	- `@Valid`: Indicates that the `request` object must be valid according to the
   * validation rules defined in the Java classes annotated with `@Valid`.
   * 	- `@RequestBody`: Annotates the `request` parameter as a JSON or XML body of the
   * HTTP request.
   * 	- `CreateUserRequest`: The class type of the `request` parameter, which contains
   * the data required to create a new user.
   * 	- `userApiMapper`: A class that maps the `CreateUserResponse` object to a `UserDto`
   * class.
   * 	- `userService`: A class that creates a new user in the system.
   * 	- `createdUserDto`: The transformed `UserDto` object created by the `userService`.
   * 	- `createdUserResponse`: The transformed `CreateUserResponse` object created by
   * the `userApiMapper`.
   * 
   * @returns a `ResponseEntity` with a `HttpStatus.CREATED` status and a `CreateUserResponse`
   * body containing the newly created user details.
   * 
   * 	- `ResponseEntity`: This is an instance of `ResponseEntity`, which represents a
   * response to a HTTP request. It has a status code and a body, which contains the
   * actual response data.
   * 	- `status`: This is the status code of the response, which in this case is `HttpStatus.CREATED`.
   * 	- `body`: This is the response data itself, which is a `CreateUserResponse` object.
   * 	- `CreateUserResponse`: This is a class that contains the data returned by the
   * function, including the user ID, user name, and email address.
   */
  @PostMapping(
      path = "/users",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  public ResponseEntity<CreateUserResponse> signUp(@Valid @RequestBody CreateUserRequest request) {
    log.trace("Received SignUp request");
    var requestUserDto = userApiMapper.createUserRequestToUserDto(request);
    var createdUserDto = userService.createUser(requestUserDto);
    var createdUserResponse = userApiMapper.userDtoToCreateUserResponse(createdUserDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUserResponse);
  }
}
