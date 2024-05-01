package com.myhome.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * represents a data structure for storing JWT token and user ID for authentication
 * purposes with private fields for jwtToken and userId.
 * Fields:
 * 	- jwtToken (String): in the AuthenticationData class represents a unique identifier
 * for a user's JWT token.
 * 	- userId (String): in the AuthenticationData class represents a unique identifier
 * for a user's authentication credentials.
 */
@Getter
@RequiredArgsConstructor
public class AuthenticationData {
  private final String jwtToken;
  private final String userId;
}
