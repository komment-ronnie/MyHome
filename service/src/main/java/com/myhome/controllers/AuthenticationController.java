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
 * is a Spring REST controller that implements the AuthenticationApi interface. It
 * handles login requests and returns a ResponseEntity with the user ID and JWT token
 * in the HTTP headers.
 */
@RequiredArgsConstructor
@RestController
public class AuthenticationController implements AuthenticationApi {

  private final AuthenticationService authenticationService;

  /**
   * authenticates a user using the `AuthenticationService`, and returns an `ResponseEntity`
   * with an `OK` status code and custom headers containing authentication data.
   * 
   * @param loginRequest authentication request containing the user credentials for
   * validation and authentication by the authentication service.
   * 
   * 	- `@Valid`: This annotation indicates that the `loginRequest` parameter must be
   * valid and contain all required fields.
   * 	- `LoginRequest`: This class represents the request for logging in, with attributes
   * such as `username`, `password`, and `grantType`.
   * 
   * @returns a `ResponseEntity` object containing headers generated based on the
   * authentication data.
   * 
   * 	- `ResponseEntity`: This class represents a response entity that contains information
   * about the status of the request. In this case, it is set to `OK`, indicating that
   * the login was successful.
   * 	- `headers`: This attribute contains a list of headers that are added to the
   * response entity. The headers contain information about the authentication data,
   * such as the user's username and the authentication method used.
   * 	- `build()`: This method creates the response entity by combining the header and
   * body attributes. In this case, it returns a response entity with the `OK` status
   * and the authentication data headers.
   */
  @Override
  public ResponseEntity<Void> login(@Valid LoginRequest loginRequest) {
    final AuthenticationData authenticationData = authenticationService.login(loginRequest);
    return ResponseEntity.ok()
        .headers(createLoginHeaders(authenticationData))
        .build();
  }

  /**
   * creates an HTTP headers object containing user ID and JWT token for authentication
   * purposes based on provided AuthenticationData object.
   * 
   * @param authenticationData user's login information, providing the user ID and JWT
   * token used to authenticate the request.
   * 
   * 	- `getUserId()`: retrieves the user ID associated with the authentication data.
   * 	- `getJwtToken()`: retrieves the JWT token associated with the authentication data.
   * 
   * @returns a set of HTTP headers containing the user ID and JWT token for authentication
   * purposes.
   * 
   * 	- `HttpHeaders`: This is an instance of the `HttpHeaders` class from the Java
   * `javax.net.http` package.
   * 	- `add()` methods: These are methods that allow adding headers to the overall
   * HTTP headers for a request or response. In this case, they add two headers: "userId"
   * and "token".
   */
  private HttpHeaders createLoginHeaders(AuthenticationData authenticationData) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", authenticationData.getUserId());
    httpHeaders.add("token", authenticationData.getJwtToken());
    return httpHeaders;
  }
}
