package com.myhome.controllers;

import com.myhome.api.AuthenticationApi;
import com.myhome.domain.AuthenticationData;
import com.myhome.model.LoginRequest;
import com.myhome.services.AuthenticationService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * is responsible for handling login requests and returning an OK response with custom
 * headers based on the authentication data. The class uses the `AuthenticationService`
 * to authenticate the user and create HTTP headers with the user's ID and JWT token.
 * The `createLoginHeaders()` method generates the custom headers by adding user-defined
 * headers to an instance of the `HttpHeaders` class.
 */
@RequiredArgsConstructor
@RestController
public class AuthenticationController implements AuthenticationApi {

  private final AuthenticationService authenticationService;

  /**
   * authenticates a user by calling the `loginService`. It creates headers based on
   * the authentication data and returns an `OK` response.
   * 
   * @param loginRequest authentication request sent by the client, which includes the
   * user credentials and other necessary details for the authentication process.
   * 
   * 	- `@Valid LoginRequest loginRequest`: This indicates that the object passed as
   * an argument to the function is serialized from a JSON request body and has been
   * validated by the `@Validation` annotation.
   * 	- `authenticationService.login(loginRequest)`: This line of code calls the `login`
   * method of the `authenticationService` class, which performs authentication using
   * an unknown implementation. The returned `AuthenticationData` object is then returned
   * as part of the response entity.
   * 
   * @returns a `ResponseEntity` object with an `OK` status and custom headers containing
   * authentication data.
   * 
   * 	- `ResponseEntity`: This is the class that represents the response entity, which
   * contains information about the login request and its outcome.
   * 	- `ok()`: This method returns a ResponseEntity with a status code of 200, indicating
   * that the login was successful.
   * 	- `headers()`: This method allows for the creation of custom HTTP headers based
   * on the authentication data returned by the `authenticationService`. These headers
   * can be used to provide additional information about the login request and its outcome.
   */
  @Override
  public ResponseEntity<Void> login(@Valid LoginRequest loginRequest) {
    final AuthenticationData authenticationData = authenticationService.login(loginRequest);
    return ResponseEntity.ok()
        .headers(createLoginHeaders(authenticationData))
        .build();
  }

  /**
   * creates HTTP headers with user ID and JWT token obtained from `AuthenticationData`.
   * 
   * @param authenticationData user information and JWT token required for authentication.
   * 
   * 	- `userId`: an integer value representing the user ID associated with the
   * authentication data.
   * 	- `token`: a string value representing the JWT token issued to the user for authentication.
   * 
   * @returns an HTTP headers object containing the user ID and JWT token for authentication
   * purposes.
   * 
   * 	- `HttpHeaders`: This is an instance of the `HttpHeaders` class from the Java
   * `HttpClient` package, which contains headers for an HTTP request.
   * 	- `userId`: This is a header added to the HTTP request with the value of `authenticationData.getUserId()`.
   * 	- `token`: This is a header added to the HTTP request with the value of `authenticationData.getJwtToken()`.
   */
  private HttpHeaders createLoginHeaders(AuthenticationData authenticationData) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", authenticationData.getUserId());
    httpHeaders.add("token", authenticationData.getJwtToken());
    return httpHeaders;
  }
}
