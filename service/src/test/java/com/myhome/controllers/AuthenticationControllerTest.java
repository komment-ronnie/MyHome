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
 * is a unit test for the AuthenticationController class, which is responsible for
 * handling login requests. The test class sets up mock dependencies and verifies
 * that the authentication controller returns the correct response when given a valid
 * login request.
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
   * initializes Mockito mocking for the current class, enabling mocking of dependencies
   * and behaviors.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * tests the authentication controller's `login` method by providing a valid login
   * request and verifying the response status code, headers, and the call to the
   * authentication service's `login` method.
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
   * creates a new `LoginRequest` instance with email `TEST_EMAIL` and password `TEST_PASSWORD`.
   * 
   * @returns a `LoginRequest` object with predefined email and password values.
   * 
   * 	- The function returns a new `LoginRequest` object.
   * 	- The `email` property of the returned object is set to `TEST_EMAIL`.
   * 	- The `password` property of the returned object is set to `TEST_PASSWORD`.
   */
  private LoginRequest getDefaultLoginRequest() {
    return new LoginRequest().email(TEST_EMAIL).password(TEST_PASSWORD);
  }

  /**
   * returns an instance of `AuthenticationData` with predefined values for `TOKEN` and
   * `TEST_ID`.
   * 
   * @returns an instance of `AuthenticationData` with `TOKEN` and `TEST_ID` properties.
   * 
   * 	- `TOKEN`: A string representing the authentication token.
   * 	- `TEST_ID`: An integer identifier for testing purposes.
   */
  private AuthenticationData getDefaultAuthenticationData() {
    return new AuthenticationData(TOKEN, TEST_ID);
  }
}
