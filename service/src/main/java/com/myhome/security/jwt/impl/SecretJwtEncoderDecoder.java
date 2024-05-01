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
 * is an implementation of the AppJwtEncoderDecoder interface, which provides functions
 * for encoding and decoding JSON Web Tokens (JWTs) using HMAC-SHA-512 algorithm with
 * a secret key. The class takes an encoded JWT and a secret as input, and uses JWT
 * parser to extract claims from the JWT before creating a new AppJwt instance with
 * the extracted information. Additionally, the class provides functions for encoding
 * and decoding JWTs using HMAC-SHA-512 algorithm with a provided secret key.
 */
@Component
@Profile("default")
public class SecretJwtEncoderDecoder implements AppJwtEncoderDecoder {

  /**
   * parses an JSON Web Token (JWT) and extracts its claims, including the user ID and
   * expiration time. It then builds a new `AppJwt` object with the extracted information.
   * 
   * @param encodedJwt JSON Web Token (JWT) that contains the user's information and
   * is to be decoded and converted into an `AppJwt` object.
   * 
   * @param secret secret key used for HMAC-based signature verification, which is
   * required to validate the JWT token's signature and ensure its authenticity.
   * 
   * @returns an instance of `AppJwt` with updated fields for the user ID and expiration
   * date.
   * 
   * 	- The user ID is extracted from the claims in the JWT and is stored in the `userId`
   * field of the `AppJwt` object.
   * 	- The expiration date of the JWT is also extracted from the claims and is stored
   * in the `expiration` field of the `AppJwt` object, which is represented as a `LocalDateTime`.
   * 
   * These properties are essential for decoding the JWT and extracting relevant
   * information about the user and the JWT's validity.
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
   * expiration date and signs it using HMAC-SHA-512 algorithm with the provided secret.
   * 
   * @param jwt JSON Web Token to be encoded, which contains information about the user
   * and the expiration time.
   * 
   * 	- `jwt`: A `AppJwt` object containing information about the JWT token, such as
   * the user ID and expiration time.
   * 	- `secret`: The secret key used for signing the JWT.
   * 
   * @param secret secret key used for HMAC-based signatures in the `encode()` function.
   * 
   * @returns a compact JWT with the user ID, expiration date, and HMAC-SHA signature
   * calculated using the secret.
   */
  @Override public String encode(AppJwt jwt, String secret) {
    Date expiration = Date.from(jwt.getExpiration().atZone(ZoneId.systemDefault()).toInstant());
    return Jwts.builder()
        .setSubject(jwt.getUserId())
        .setExpiration(expiration)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512).compact();
  }
}
