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
 * is an implementation of the AppJwtEncoderDecoder interface, designed to work only
 * in testing environments. It splits the encoded JWT into two parts using a separator,
 * and then parses the user ID and expiration time from the resulting array. Finally,
 * it returns a new AppJwt object with the extracted values.
 */
@Profile("test")
@Component
public class NoSecretJwtEncoderDecoder implements AppJwtEncoderDecoder {
  private static final String SEPARATOR = "\\+";

  /**
   * takes an encoded JWT and a secret, splits the encoded JWT into a array of strings
   * using the specified separator, and then creates a new `AppJwt` object with the
   * user ID and expiration time extracted from the array.
   * 
   * @param encodedJwt JSON Web Token (JWT) that needs to be decoded and returned as
   * an instance of the `AppJwt` class.
   * 
   * @param secret secret key used to decode the JWT.
   * 
   * @returns an instance of `AppJwt` with user ID and expiration time extracted from
   * the encoded JWT.
   * 
   * 	- `AppJwt`: This is the class that represents an JSON Web Token (JWT), which is
   * the type of token being decoded. It has fields for the user ID and expiration time.
   * 	- `userId(strings[0])`: This field represents the user ID extracted from the split
   * `encodedJwt` string.
   * 	- `expiration(LocalDateTime.parse(strings[1]))`: This field represents the
   * expiration time of the JWT, which is also extracted from the split `encodedJwt` string.
   * 
   * The output of the `decode` function is an instance of `AppJwt`, which contains the
   * user ID and expiration time of the decoded JWT.
   */
  @Override public AppJwt decode(String encodedJwt, String secret) {
    String[] strings = encodedJwt.split(SEPARATOR);
    return AppJwt.builder().userId(strings[0]).expiration(LocalDateTime.parse(strings[1])).build();
  }

  /**
   * takes a JWT object `jwt` and a secret as input, and returns an encoded string
   * consisting of the user ID and expiration date.
   * 
   * @param jwt Java Transcrypting Worry Token containing information about the user
   * and expiration date, which is used to generate the encoded token returned by the
   * function.
   * 
   * 	- `jwt`: The input parameter is an instance of `AppJwt`, which contains user-related
   * information such as `getUserId()` and `getExpiration()`.
   * 	- `secret`: The secret key used for signing the JWT.
   * 
   * @param secret secret key used for signing the JWT.
   * 
   * @returns a base64-encoded string representing the user ID and expiration date of
   * the JWT.
   */
  @Override public String encode(AppJwt jwt, String secret) {
    return jwt.getUserId() + SEPARATOR + jwt.getExpiration();
  }
}
