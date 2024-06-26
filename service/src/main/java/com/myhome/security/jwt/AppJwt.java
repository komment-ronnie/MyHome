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

package com.myhome.security.jwt;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a JWT in the application.
 */
/**
 * represents a JWT in the application with user ID and expiration date.
 * Fields:
 * 	- userId (String): represents a unique identifier for a user in the application.
 * 	- expiration (LocalDateTime): represents the date and time after which the JWT
 * becomes invalid or no longer valid.
 */
@Builder
@ToString
@Getter
public class AppJwt {
  private final String userId;
  private final LocalDateTime expiration;
}