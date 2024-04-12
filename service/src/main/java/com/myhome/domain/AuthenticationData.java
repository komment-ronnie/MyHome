package com.myhome.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * represents a data structure for storing JWT token and user ID for authentication
 * purposes.
 * Fields:
 * 	- jwtToken (String): represents a unique identifier for a user's authentication
 * credentials in the AuthenticationData class.
 * 	- userId (String): in AuthenticationData represents a unique identifier for a user.
 */
@Getter
@RequiredArgsConstructor
public class AuthenticationData {
  private final String jwtToken;
  private final String userId;
}
