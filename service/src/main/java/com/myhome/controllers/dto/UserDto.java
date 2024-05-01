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

package com.myhome.controllers.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * represents a data transfer object for a user containing their unique identifier,
 * user ID, name, email, password, community IDs, and an email confirmed status.
 * Fields:
 * 	- id (Long): represents a unique identifier for a user in the system.
 * 	- userId (String): represents a unique identifier for a user within the system.
 * 	- name (String): in the UserDto class represents a string value containing the
 * user's name.
 * 	- email (String): in the UserDto class represents a string value containing the
 * user's email address.
 * 	- password (String): in the UserDto class stores a string value representing a
 * user's password.
 * 	- encryptedPassword (String): in the UserDto class contains an encrypted version
 * of the user's password.
 * 	- communityIds (Set<String>): represents a set of strings indicating the user's
 * membership in various communities within the system.
 * 	- emailConfirmed (boolean): in the UserDto class indicates whether a user's email
 * address has been confirmed through a verification process.
 */
@Builder
@Getter
@Setter
public class UserDto {
  private Long id;
  private String userId;
  private String name;
  private String email;
  private String password;
  private String encryptedPassword;
  private Set<String> communityIds;
  private boolean emailConfirmed;
}
