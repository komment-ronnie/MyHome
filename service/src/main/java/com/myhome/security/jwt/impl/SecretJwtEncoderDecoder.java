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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link AppJwtEncoderDecoder}.
 */
/**
 * is an implementation of the AppJwtEncoderDecoder interface that provides methods
 * for encoding and decoding JSON Web Tokens (JWTs). The class uses the HmacShaKeyFor
 * method to generate a signing key for the JWT, and the Jwts builder class to parse
 * and generate JWT claims.
 */
@Component
@Profile("default")
public class SecretJwtEncoderDecoder implements AppJwtEncoderDecoder {

  /**
   * takes an encoded JWT and a secret, uses JWT parser to extract claims from the JWT,
   * and creates a new AppJwt instance with the extracted information.
   * 
   * @param encodedJwt JSON Web Token (JWT) that is being decoded and parsed by the
   * `decode()` method.
   * 
   * @param secret secret key used for HMAC-SHA256 signature verification when decoding
   * the JWT.
   * 
   * @returns an instance of `AppJwt` with user ID and expiration date reconstructed
   * from the input JWT.
   * 
   * 	- `userId`: The subject claim in the JWT, representing the user's ID.
   * 	- `expiration`: The expiration date and time of the JWT, represented as an Instant
   * in the function.
   * 
   * The output is constructed by combining these two properties using the `builder`
   * pattern, creating a new `AppJwt` instance with the specified properties.
   */
  @Override public AppJwt decode(String encodedJwt, String secret) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
        .build()
        .parseClaimsJws(encodedJwt)
        .getBody();
    String userId = claims.getSubject();
    Date expiration = claims.getExpiration();
    return AppJwt.builder()
        .userId(userId)
        .expiration(expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
        .build();
  }

  /**
   * takes a `Jwt` object and a secret as input, creates a new JWT with an updated
   * expiration date based on the current time zone, and signs it using HMAC-SHA-512
   * algorithm with the provided secret.
   * 
   * @param jwt JSON Web Token to be encoded, which includes the user ID and expiration
   * date.
   * 
   * 	- `jwt`: A `AppJwt` object containing the JWT claim set and expiration date.
   * 	- `secret`: The secret key used for signing the JWT.
   * 
   * @param secret 30-byte HMAC key used for signing the JWT token.
   * 
   * @returns a compact JWT containing the user ID, expiration date, and HMAC-SHA512
   * signature, all generated using the provided secret.
   */
  @Override public String encode(AppJwt jwt, String secret) {
    Date expiration = Date.from(jwt.getExpiration().atZone(ZoneId.systemDefault()).toInstant());
    return Jwts.builder()
        .setSubject(jwt.getUserId())
        .setExpiration(expiration)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512).compact();
  }
}
