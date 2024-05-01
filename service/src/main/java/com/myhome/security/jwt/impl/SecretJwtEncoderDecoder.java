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
 * is a component that encodes and decodes JSON Web Tokens (JWTs) by extracting claims
 * such as the user ID and expiration date, signing them with an HMAC-SHA-512 algorithm
 * using a secret key, and returning the encoded JWT. The class also has methods to
 * decode a JWT and build a new `AppJwt` object with updated fields for the user ID
 * and expiration date.
 */
@Component
@Profile("default")
public class SecretJwtEncoderDecoder implements AppJwtEncoderDecoder {

  /**
   * parses an encoded JWT and extracts the subject, expiration time, and builds a new
   * AppJwt instance with the user ID and expiration date.
   * 
   * @param encodedJwt JSON Web Token (JWT) that contains the user's information and
   * expiration time, which is to be decoded and converted into an instance of the
   * `AppJwt` class.
   * 
   * @param secret HSM key used for signing and verifying the JWT token, which is
   * required to extract the claims from the JWT message.
   * 
   * @returns an instance of `AppJwt` with updated `userId` and `expiration` fields.
   * 
   * 	- `userId`: The subject of the JWT claim, representing the user ID.
   * 	- `expiration`: The expiration date and time of the JWT, represented as an Instant
   * in ISO 8601 format, followed by a conversion to a LocalDateTime for ease of use
   * in Java.
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
   * takes a JWT object and a secret key as input, generates a new JWT with updated
   * expiration date based on the original JWT's expiration date, and signs it using
   * HMAC-SHA512 algorithm with the provided secret key. The resulting JWT is returned
   * in a compact form.
   * 
   * @param jwt JSON Web Token (JWT) to be encoded, which contains information about
   * the user and its expiration time.
   * 
   * 	- `jwt`: This is the input parameter to the `encode` function, which represents
   * an app-specific JSON Web Token (JWT) object. It contains information such as the
   * user ID and expiration date.
   * 	- `secret`: This is the secret key used for signing the JWT.
   * 
   * @param secret symmetric encryption key used for signing the JWT.
   * 
   * @returns a compact JWT containing the user ID, expiration date, and HMAC-SHA-512
   * signature.
   */
  @Override public String encode(AppJwt jwt, String secret) {
    Date expiration = Date.from(jwt.getExpiration().atZone(ZoneId.systemDefault()).toInstant());
    return Jwts.builder()
        .setSubject(jwt.getUserId())
        .setExpiration(expiration)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512).compact();
  }
}
