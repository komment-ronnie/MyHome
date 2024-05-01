package com.myhome.controllers;

import com.myhome.domain.AuthenticationData;
import com.myhome.model.LoginRequest;
import com.myhome.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * is a unit test for the AuthenticationController class, which handles login requests.
 * The test class sets up mock dependencies and verifies that the authentication
 * controller returns the correct response when given a valid login request.
 */
public class AuthenticationControllerTest {

  private static final String TEST_ID = "1";
  private static final String TEST_EMAIL = "email@mail.com";
  private static final String TEST_PASSWORD = "password";
  private static final String TOKEN = "token";

  @Mock
  private AuthenticationService authenticationService;
  @InjectMocks
  private AuthenticationController authenticationController;

  /**
   * is used to initialize mock objects using MockitoAnnotations.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * tests the login functionality of the authentication controller by providing a valid
   * login request and verifying the response status code, headers, and the execution
   * of the `login` method of the authentication service.
   */
  @Test
  void loginSuccess() {
    // given
    LoginRequest loginRequest = getDefaultLoginRequest();
    AuthenticationData authenticationData = getDefaultAuthenticationData();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", authenticationData.getUserId());
    httpHeaders.add("token", authenticationData.getJwtToken());
    given(authenticationService.login(loginRequest))
        .willReturn(authenticationData);

    // when
    ResponseEntity<Void> response = authenticationController.login(loginRequest);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(response.getHeaders().size(), 2);
    assertEquals(response.getHeaders(), httpHeaders);
    verify(authenticationService).login(loginRequest);
  }

  /**
   * creates a new `LoginRequest` instance with predefined email and password for testing
   * purposes.
   * 
   * @returns a `LoginRequest` object with predefined email and password values.
   * 
   * 	- The function returns a new instance of the `LoginRequest` class.
   * 	- The `email` field is set to `TEST_EMAIL`, which represents an email address for
   * the login request.
   * 	- The `password` field is set to `TEST_PASSWORD`, which represents the password
   * for the login request.
   */
  private LoginRequest getDefaultLoginRequest() {
    return new LoginRequest().email(TEST_EMAIL).password(TEST_PASSWORD);
  }

  /**
   * creates a default instance of the `AuthenticationData` class with a token and test
   * ID.
   * 
   * @returns an instance of `AuthenticationData` containing the token and test ID.
   * 
   * 	- `TOKEN`: This is a string value representing an authentication token.
   * 	- `TEST_ID`: This is an integer value used to identify a specific test.
   */
  private AuthenticationData getDefaultAuthenticationData() {
    return new AuthenticationData(TOKEN, TEST_ID);
  }
}
