package com.myhome.services;

import com.myhome.domain.AuthenticationData;
import com.myhome.model.LoginRequest;

/**
 * provides a method to log in users and returns an AuthenticationData object
 * representing the authenticated user.
 */
public interface AuthenticationService {
  AuthenticationData login(LoginRequest loginRequest);
}
