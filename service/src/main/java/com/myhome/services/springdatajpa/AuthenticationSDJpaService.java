package com.myhome.services.springdatajpa;

import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.exceptions.CredentialsIncorrectException;
import com.myhome.controllers.exceptions.UserNotFoundException;
import com.myhome.domain.AuthenticationData;
import com.myhome.model.LoginRequest;
import com.myhome.security.jwt.AppJwt;
import com.myhome.security.jwt.AppJwtEncoderDecoder;
import com.myhome.services.AuthenticationService;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * is responsible for authenticating users by verifying their passwords and generating
 * an JWT token for authorization. It takes in a login request containing the user's
 * email address and password, and uses methods from the UserSDJpaService,
 * AppJwtEncoderDecoder, and PasswordEncoder classes to compare the provided password
 * with the encrypted password stored for the user, create a new JWT token based on
 * the user's data, and return an AuthenticationData object containing the encoded
 * JWT token and the user ID.
 */
@Slf4j
@Service
public class AuthenticationSDJpaService implements AuthenticationService {

  private final Duration tokenExpirationTime;
  private final String tokenSecret;

  private final UserSDJpaService userSDJpaService;
  private final AppJwtEncoderDecoder appJwtEncoderDecoder;
  private final PasswordEncoder passwordEncoder;

  public AuthenticationSDJpaService(@Value("${token.expiration_time}") Duration tokenExpirationTime,
      @Value("${token.secret}") String tokenSecret,
      UserSDJpaService userSDJpaService,
      AppJwtEncoderDecoder appJwtEncoderDecoder,
      PasswordEncoder passwordEncoder) {
    this.tokenExpirationTime = tokenExpirationTime;
    this.tokenSecret = tokenSecret;
    this.userSDJpaService = userSDJpaService;
    this.appJwtEncoderDecoder = appJwtEncoderDecoder;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * authenticates a user by checking their password against an encrypted version stored
   * in the database. If the passwords match, it creates a JWT token and returns an
   * `AuthenticationData` object containing the encoded token and user ID.
   * 
   * @param loginRequest login request containing the email address and password of the
   * user to be authenticated.
   * 
   * 	- `getEmail()`: Returns the email address of the user attempting to log in.
   * 	- `getPassword()`: Returns the password entered by the user for authentication.
   * 	- `orElseThrow()`: Throws an exception if the user is not found based on the email
   * address provided.
   * 	- `isPasswordMatching()`: Compares the entered password with the encrypted password
   * stored for the user, and throws an exception if they do not match.
   * 	- `createJwt()`: Generates a JWT token containing the user ID and other relevant
   * information using the deserialized user data.
   * 	- `encode()`: Encodes the JWT token using the provided secret key.
   * 
   * @returns an `AuthenticationData` object containing an encoded JWT token and the
   * user ID.
   * 
   * 	- The AuthenticationData object contains an encoded token in the form of a string,
   * which represents the user's identity and is generated using the JWT algorithm.
   * 	- The UserId property of the AuthenticationData object refers to the unique
   * identifier of the user whose credentials were validated.
   * 	- The AppJwtEncoderDecoder class is used to encode the JWT token with a secret
   * key, which adds an additional layer of security to prevent unauthorized access to
   * the token.
   */
  @Override
  public AuthenticationData login(LoginRequest loginRequest) {
    log.trace("Received login request");
    final UserDto userDto = userSDJpaService.findUserByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new UserNotFoundException(loginRequest.getEmail()));
    if (!isPasswordMatching(loginRequest.getPassword(), userDto.getEncryptedPassword())) {
      throw new CredentialsIncorrectException(userDto.getUserId());
    }
    final AppJwt jwtToken = createJwt(userDto);
    final String encodedToken = appJwtEncoderDecoder.encode(jwtToken, tokenSecret);
    return new AuthenticationData(encodedToken, userDto.getUserId());
  }

  /**
   * compares a provided `requestPassword` with the corresponding password stored in
   * the `databasePassword`, returning `true` if they match and `false` otherwise.
   * 
   * @param requestPassword password entered by the user for authentication purposes.
   * 
   * @param databasePassword password stored in the database that the function compares
   * with the `requestPassword`.
   * 
   * @returns a boolean value indicating whether the provided request password matches
   * the corresponding database password.
   */
  private boolean isPasswordMatching(String requestPassword, String databasePassword) {
    return passwordEncoder.matches(requestPassword, databasePassword);
  }

  /**
   * creates a new AppJwt instance with the user's ID, expiration time, and expiration
   * date.
   * 
   * @param userDto user details used to create the JWT token.
   * 
   * 	- `userId`: The user ID of the JWT token's holder (represented by an integer value).
   * 	- `expiration`: The LocalDateTime object representing the token's expiration time,
   * calculated by adding the `tokenExpirationTime` to the current date and time.
   * 
   * @returns an AppJwt object containing user ID and expiration time.
   * 
   * 	- `userId`: The user ID of the user for whom the JWT is being created.
   * 	- `expiration`: The expiration time of the JWT, which is the LocalDateTime object
   * representing the current date and time plus the tokenExpirationTime.
   * 	- `build()`: This method is used to build the JWT instance using the provided properties.
   */
  private AppJwt createJwt(UserDto userDto) {
    final LocalDateTime expirationTime = LocalDateTime.now().plus(tokenExpirationTime);
    return AppJwt.builder()
        .userId(userDto.getUserId())
        .expiration(expirationTime)
        .build();
  }
}
