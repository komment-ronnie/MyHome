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
   * initializes Mockito Annotations for testing purposes by calling `MockitoAnnotations.initMocks(this)`.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * verifies that the `AuthenticationController#login` method logs in a user successfully,
   * returns a `HttpStatus.OK` response with the correct headers and calls the
   * `AuthenticationService#login` method.
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
   * creates a default `LoginRequest` object with email `TEST_EMAIL` and password `TEST_PASSWORD`.
   * 
   * @returns a `LoginRequest` object with pre-defined email and password values.
   * 
   * 	- `email`: The email address associated with the login request.
   * 	- `password`: The password associated with the login request.
   */
  private LoginRequest getDefaultLoginRequest() {
    return new LoginRequest().email(TEST_EMAIL).password(TEST_PASSWORD);
  }

  /**
   * creates a new `AuthenticationData` object with a token and test ID.
   * 
   * @returns an `AuthenticationData` object containing the token and test ID.
   * 
   * 	- `TOKEN`: This is an integer value that represents a token for authentication purposes.
   * 	- `TEST_ID`: This is a unique identifier assigned to the authentication data.
   */
  private AuthenticationData getDefaultAuthenticationData() {
    return new AuthenticationData(TOKEN, TEST_ID);
  }
}
