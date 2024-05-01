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

package com.myhome.security.jwt.impl;

import com.myhome.security.jwt.AppJwt;
import com.myhome.security.jwt.AppJwtEncoderDecoder;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link AppJwtEncoderDecoder}. Use this only in testing.
 */
/**
 * is an implementation of the AppJwtEncoderDecoder interface that handles JSON Web
 * Token (JWT) encoding and decoding without using secrets. It provides methods for
 * encoding and decoding JWTs, as well as creating new `AppJwt` objects from decoded
 * JWTs.
 */
@Profile("test")
@Component
public class NoSecretJwtEncoderDecoder implements AppJwtEncoderDecoder {
  private static final String SEPARATOR = "\\+";

  /**
   * decodes a JSON Web Token (JWT) string and returns an instance of the `AppJwt` class
   * with extracted user ID and expiration date.
   * 
   * @param encodedJwt JSON Web Token (JWT) that is to be decoded and converted into
   * an instance of the `AppJwt` class.
   * 
   * @param secret decryption secret used to extract the user ID and expiration time
   * from the encoded JWT.
   * 
   * @returns an instance of the `AppJwt` class with a user ID and expiration time
   * extracted from the encoded JWT.
   * 
   * 	- The `AppJwt` object is constructed using the `builder()` method, which allows
   * for flexible configuration and customization of the resulting object.
   * 	- The `userId` attribute represents the user ID associated with the JWT token.
   * 	- The `expiration` attribute contains the expiration time of the JWT token in the
   * form of a `LocalDateTime` object, representing the point in time when the token
   * will become invalid.
   */
  @Override public AppJwt decode(String encodedJwt, String secret) {
    String[] strings = encodedJwt.split(SEPARATOR);
    return AppJwt.builder().userId(strings[0]).expiration(LocalDateTime.parse(strings[1])).build();
  }

  /**
   * takes a `AppJwt` object and a secret as input and returns a encoded string consisting
   * of the user ID and expiration time.
   * 
   * @param jwt JWT (JSON Web Token) object containing information about the user and
   * its expiration date, which is used to generate a unique identifier for the user.
   * 
   * 	- `jwt`: This is an instance of the `AppJwt` class, which contains properties
   * such as `getUserId()` and `getExpiration()`.
   * 
   * @param secret symmetric key used for signing the JWT token.
   * 
   * @returns a string consisting of the `userId` and `expiration` values concatenated
   * with a separator.
   */
  @Override public String encode(AppJwt jwt, String secret) {
    return jwt.getUserId() + SEPARATOR + jwt.getExpiration();
  }
}
