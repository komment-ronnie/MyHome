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
 * is an implementation of the AuthenticationService interface, providing authentication
 * functionality using Spring Data JPA and Spring Security's JWT. The class takes in
 * various parameters such as token expiration time, secret, and user service, and
 * uses them to create a JWT and validate credentials.
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
   * authenticates a user by verifying their password and generating an JWT token for
   * authorization.
   * 
   * @param loginRequest login request received by the method, providing the email
   * address and password of the user attempting to log in.
   * 
   * 	- `LoginRequest`: This class represents the login request, containing email and
   * password for authentication.
   * 	- `email`: A string property representing the user's email address for login.
   * 	- `password`: A string property representing the user's password for login.
   * 	- `orElseThrow()`: An method that throws a `UserNotFoundException` if the user
   * with the provided email is not found in the database.
   * 	- `isPasswordMatching()`: A method that compares the provided password with the
   * encrypted password stored for the user, and returns `true` if they match, otherwise
   * `false`.
   * 	- `createJwt()`: An method that creates an JWT token based on the user's data,
   * using the `appJwtEncoderDecoder`.
   * 	- `encode()`: An method of the `appJwtEncoderDecoder` class that encodes the JWT
   * token into a string.
   * 
   * @returns an `AuthenticationData` object containing an encoded JWT token and the
   * user ID.
   * 
   * 	- `AuthenticationData`: This is the class that represents the authentication data,
   * which consists of an encoded token and a user ID.
   * 	- `encodedToken`: This is a string representing the encoded JWT token issued by
   * the login function.
   * 	- `userID`: This is the ID of the user who has successfully logged in.
   * 
   * In addition to these properties, the output also includes information about the
   * login request, such as the email address and password provided by the user.
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
   * compares a provided password with an equivalent password stored in a database,
   * returning `true` if they match and `false` otherwise.
   * 
   * @param requestPassword password provided by the user for authentication purposes.
   * 
   * @param databasePassword password stored in the database that the function compares
   * to the `requestPassword`.
   * 
   * @returns a boolean value indicating whether the provided request password matches
   * the password stored in the database.
   * 
   * 	- The function returns a boolean value indicating whether the request password
   * matches the database password.
   * 	- The `passwordEncoder` is used to perform the comparison between the request
   * password and the database password.
   * 	- The comparison is performed using the `matches()` method, which takes two
   * parameters - the request password and the database password - and returns a boolean
   * value indicating whether they match.
   */
  private boolean isPasswordMatching(String requestPassword, String databasePassword) {
    return passwordEncoder.matches(requestPassword, databasePassword);
  }

  /**
   * creates a JWT token with an expiration time based on the provided `tokenExpirationTime`.
   * It sets the user ID and expiration date in the JWT builder and builds the final JWT.
   * 
   * @param userDto user's details, including their ID, which are used to generate an
   * expiration time for the JWT token.
   * 
   * 	- `userId`: The user ID of the user for whom the JWT token is being created.
   * 	- `expirationTime`: The expiration time of the JWT token in LocalDateTime format,
   * calculated by adding the `tokenExpirationTime` to the current date and time.
   * 
   * @returns a `AppJwt` object containing user ID and expiration time.
   * 
   * 1/ `userId`: The user ID of the user to whom the JWT is being created for.
   * 2/ `expiration`: The expiration time of the JWT in LocalDateTime format, which
   * represents the time when the JWT will expire.
   * 3/ `build()`: This method creates a new AppJwt instance with the specified properties
   * and returns it.
   */
  private AppJwt createJwt(UserDto userDto) {
    final LocalDateTime expirationTime = LocalDateTime.now().plus(tokenExpirationTime);
    return AppJwt.builder()
        .userId(userDto.getUserId())
        .expiration(expirationTime)
        .build();
  }
}
