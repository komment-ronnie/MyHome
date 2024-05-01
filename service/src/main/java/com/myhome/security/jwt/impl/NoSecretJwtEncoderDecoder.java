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
   * decodes a JSON Web Token (JWT) and returns an instance of `AppJwt`. It splits the
   * encoded JWT into two parts using a custom separator, then parses the first part
   * as a string representing the user ID and the second part as a date representing
   * the expiration time.
   * 
   * @param encodedJwt JSON Web Token (JWT) that needs to be decoded and converted into
   * an instance of the `AppJwt` class.
   * 
   * @param secret secret key used to sign the JWT token, which is necessary for decoding
   * and verifying the authenticity of the token.
   * 
   * @returns an instance of `AppJwt` with the user ID and expiration time extracted
   * from the encoded JWT.
   * 
   * 	- `AppJwt`: This is the class that represents an encrypted JWT (Java Token) object.
   * 	- `userId`: The first element in the split `encodedJwt` string represents the
   * user ID.
   * 	- `expiration`: The second element in the split `encodedJwt` string represents
   * the expiration time of the JWT, which is a `LocalDateTime` object.
   */
  @Override public AppJwt decode(String encodedJwt, String secret) {
    String[] strings = encodedJwt.split(SEPARATOR);
    return AppJwt.builder().userId(strings[0]).expiration(LocalDateTime.parse(strings[1])).build();
  }

  /**
   * takes a `AppJwt` object and a secret as input and returns a string representing
   * the user ID and expiration time encoded together with the secret.
   * 
   * @param jwt JSON Web Token to be encoded, containing the user ID and expiration time.
   * 
   * 	- `jwt`: A `AppJwt` object that contains user identification information and an
   * expiration time.
   * 
   * @param secret secret key used to sign the JWT token.
   * 
   * @returns a concatenation of the `jwt.getUserId()` and `jwt.getExpiration()` values,
   * separated by a separator.
   */
  @Override public String encode(AppJwt jwt, String secret) {
    return jwt.getUserId() + SEPARATOR + jwt.getExpiration();
  }
}
