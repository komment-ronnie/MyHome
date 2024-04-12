package com.myhome.services.springdatajpa;

import com.myhome.domain.SecurityTokenType;
import com.myhome.domain.SecurityToken;
import com.myhome.domain.User;
import com.myhome.repositories.SecurityTokenRepository;
import com.myhome.services.SecurityTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

/**
 * is a Java class that implements a SecurityToken service using Spring Data JPA. It
 * provides methods for creating and managing security tokens, including email
 * confirmation and password reset tokens. The class uses Spring Boot configuration
 * to set the expiration time of the tokens.
 */
@Service
@RequiredArgsConstructor
public class SecurityTokenSDJpaService implements SecurityTokenService {

  private final SecurityTokenRepository securityTokenRepository;

  @Value("${tokens.reset.expiration}")
  private Duration passResetTokenTime;
  @Value("${tokens.email.expiration}")
  private Duration emailConfirmTokenTime;

  /**
   * generates a unique security token based on input parameters and saves it to a
   * repository for later use.
   * 
   * @param tokenType type of security token being created, which determines the specific
   * fields and values that are populated in the resulting security token object.
   * 
   * 1/ `SecurityTokenType`: This enumeration value represents the type of security
   * token being created. It can take on the following values: `None`, `Basic`, `KBAuth`,
   * `OAuth2`, and `Jwt`.
   * 2/ `Duration`: This class represents a duration in seconds, which is used to
   * determine the lifetime of the security token.
   * 3/ `LiveTimeSeconds`: This field represents the total number of seconds that the
   * security token will be valid for, starting from the creation date.
   * 4/ `User`: This field represents the user who owns the security token.
   * 
   * The function creates a new security token instance with the specified properties
   * and saves it to the repository.
   * 
   * @param liveTimeSeconds duration of time for which the security token is valid, and
   * it is used to calculate the expiration date of the token.
   * 
   * 	- `LiveTimeSeconds`: This represents the duration for which the security token
   * is valid. It is a `Duration` object, which can be used to represent a time interval
   * in seconds.
   * 	- `getDateAfterDays()`: This is a method that takes a date as input and returns
   * a new date after a specified number of days have passed since the date. It is used
   * to calculate the expiration date of the security token based on the `liveTimeSeconds`
   * parameter.
   * 
   * @param tokenOwner user associated with the generated security token.
   * 
   * 	- `tokenOwner`: The owner of the security token, which can be a user or an organization.
   * 	- `tokenType`: The type of security token created, such as `USER_TOKEN` or `ORGANIZATION_TOKEN`.
   * 	- `liveTimeSeconds`: The number of seconds that the security token is valid for.
   * 	- `creationDate`: The date and time when the security token was created.
   * 	- `expiryDate`: The date and time when the security token expires.
   * 	- `false`: A boolean value indicating whether the security token is revoked or not.
   * 	- `null`: A null value representing the absence of any additional attributes or
   * properties.
   * 
   * @returns a newly created SecurityToken instance with the specified token type,
   * owner, creation date, expiry date, and other properties.
   * 
   * 	- `token`: A unique token string generated using the `UUID.randomUUID()` method.
   * 	- `creationDate`: The current date and time when the token was created, represented
   * as a `LocalDate` object.
   * 	- `expiryDate`: The date and time after which the token will expire, calculated
   * by subtracting the `liveTimeSeconds` from the current date and time using the
   * `getDateAfterDays()` method. Also represented as a `LocalDate` object.
   * 	- `tokenOwner`: The user who owns the token, represented as an instance of the
   * `User` class.
   * 	- `newSecurityToken`: A new `SecurityToken` instance created by calling the
   * `securityTokenRepository.save()` method and passing in the generated token details.
   */
  private SecurityToken createSecurityToken(SecurityTokenType tokenType, Duration liveTimeSeconds, User tokenOwner) {
    String token = UUID.randomUUID().toString();
    LocalDate creationDate = LocalDate.now();
    LocalDate expiryDate = getDateAfterDays(LocalDate.now(), liveTimeSeconds);
    SecurityToken newSecurityToken = new SecurityToken(tokenType, token, creationDate, expiryDate, false, null);
    newSecurityToken.setTokenOwner(tokenOwner);
    newSecurityToken = securityTokenRepository.save(newSecurityToken);
    return newSecurityToken;
  }

  /**
   * creates a security token for an user with the type "EMAIL_CONFIRM". It returns the
   * created security token based on the email confirm token time and the user's identity.
   * 
   * @param tokenOwner user for whom an email confirmation token is being generated.
   * 
   * 	- `tokenOwner`: A `User` object representing the user for whom an email confirmation
   * token is being created. The `User` class has various attributes, including `id`,
   * `username`, and `email`.
   * 
   * @returns a security token with the type `EMAIL_CONFIRM`.
   * 
   * 	- `SecurityTokenType`: This field indicates the type of security token generated,
   * specifically `EMAIL_CONFIRM`.
   * 	- `emailConfirmTokenTime`: The time at which the token was created.
   * 	- `tokenOwner`: The user whose account is being confirmed through this token.
   */
  @Override
  public SecurityToken createEmailConfirmToken(User tokenOwner) {
    return createSecurityToken(SecurityTokenType.EMAIL_CONFIRM, emailConfirmTokenTime, tokenOwner);
  }

  /**
   * creates a security token for password reset with a specified expiration time and
   * owner user.
   * 
   * @param tokenOwner user for whom the password reset token is being generated.
   * 
   * 	- `tokenOwner`: The user whose password is being reset.
   * 	- `passResetTokenTime`: The time when the password reset token was created.
   * 
   * @returns a SecurityToken instance representing a password reset token.
   * 
   * The function returns a SecurityToken object named `createPasswordResetToken`. The
   * SecurityToken object has three attributes: `type`, which is set to `SecurityTokenType.RESET`;
   * `time`, which is set to the current time; and `owner`, which refers to the user
   * for whom the token was created.
   */
  @Override
  public SecurityToken createPasswordResetToken(User tokenOwner) {
    return createSecurityToken(SecurityTokenType.RESET, passResetTokenTime, tokenOwner);
  }

  /**
   * updates a provided SecurityToken by marking it as used and saving it to the
   * repository for future reference.
   * 
   * @param token SecurityToken object that will be used and then saved in the repository
   * after being marked as used.
   * 
   * 	- `setUsed(true)` sets the `used` property to `true`, indicating that the token
   * has been used.
   * 	- The `securityTokenRepository` saves the modified token in its database.
   * 	- The returned token is the updated one with the `used` property set to `true`.
   * 
   * @returns a new SecurityToken object with updated 'used' field set to true and
   * persisted in the repository.
   * 
   * 	- `token`: The SecurityToken instance that has been modified by setting its `used`
   * field to `true`.
   * 	- `save()`: This method is used to save the SecurityToken instance in the repository.
   * 
   * The returned SecurityToken instance represents a token that has been marked as
   * used and is stored in the repository for future reference or further processing.
   */
  @Override
  public SecurityToken useToken(SecurityToken token) {
    token.setUsed(true);
    token = securityTokenRepository.save(token);
    return token;
  }

  /**
   * takes a `LocalDate` and a `Duration` as input, and returns the result of adding
   * the specified number of days to the input date.
   * 
   * @param date initial date that will be extended by the specified `liveTime`.
   * 
   * 	- `LocalDate`: The input date is represented as a `LocalDate`, which means it can
   * be interpreted as a date without time zone information.
   * 	- `date`: This variable holds the initial date value passed to the function.
   * 	- `liveTime`: The `Duration` object represents the number of days that the `date`
   * should be advanced after its original value.
   * 
   * @param liveTime number of days to add to the input `LocalDate` to obtain the desired
   * date after the specified duration has passed.
   * 
   * 	- `Duration liveTime`: A `Duration` object representing the time duration in days
   * to be added to the input `LocalDate`.
   * 	- `toDays()`: A method that returns the number of days represented by the `Duration`
   * value.
   * 
   * @returns a new `LocalDate` instance representing the date that is `liveTime` days
   * after the original `date`.
   * 
   * 	- The output is a `LocalDate` object, which represents a date in the format of "YYYY-MM-DD".
   * 	- The output has been calculated by adding a specified number of days to the input
   * `LocalDate` parameter.
   * 	- The resulting date is always in the future, as the method adds days to the
   * original date.
   */
  private LocalDate getDateAfterDays(LocalDate date, Duration liveTime) {
    return date.plusDays(liveTime.toDays());
  }
}
