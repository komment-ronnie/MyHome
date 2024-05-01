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

package com.myhome.controllers.response;

import com.myhome.model.CreateUserRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response model for create user request.
 *
 * @see CreateUserRequest
 */
/**
 * is a response model for creating a user, with three fields representing an identifier,
 * name, and email address associated with the user created in response to a create
 * user request.
 * Fields:
 * 	- userId (String): represents an identifier for a created user in response to a
 * create user request.
 * 	- name (String): in the CreateUserResponse class represents a string value
 * containing the user's name.
 * 	- email (String): represents an email address associated with the user created
 * in response to the create user request.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateUserResponse {
  private String userId;
  private String name;
  private String email;
}
