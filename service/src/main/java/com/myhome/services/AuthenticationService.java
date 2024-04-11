package com.myhome.services;

import com.myhome.domain.AuthenticationData;
import com.myhome.model.LoginRequest;

/**
 * provides a method for logging in users through the login() method, which returns
 * an AuthenticationData object representing the authenticated user.
 */
public interface AuthenticationService {
  AuthenticationData login(LoginRequest loginRequest);
}
