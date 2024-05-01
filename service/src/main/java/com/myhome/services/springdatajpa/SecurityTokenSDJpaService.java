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
 * is an implementation of the SecurityToken Service interface in a Java Persistence
 * API (JPA) application. It provides methods for creating, updating, and using
 * security tokens, as well as checking if a token is valid and verifying its owner.
 * The class also includes a `getDateAfterDays` method that calculates the date after
 * adding a specified number of days to a given `LocalDate`.
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
   * creates a new security token with a random UUID, creation and expiry dates, and
   * sets the owner of the token. It then saves the token to the repository for later
   * retrieval.
   * 
   * @param tokenType type of security token being created, which determines the format
   * and content of the token.
   * 
   * 	- `SecurityTokenType`: This represents the type of security token being created,
   * which can be one of several predefined types (e.g., `Basic`, `Confidential`, `Digest`).
   * 	- `liveTimeSeconds`: The duration for which the security token is valid, represented
   * as a `Duration` object.
   * 
   * @param liveTimeSeconds duration of time, in seconds, that the security token will
   * be valid for.
   * 
   * 	- `LocalDate.now()`: This function generates a current date and time, which
   * represents the moment when the token is created.
   * 	- `getDateAfterDays(LocalDate.now(), liveTimeSeconds)`: This function calculates
   * the expiration date of the token based on the `liveTimeSeconds` parameter. The
   * result is a `LocalDate` object representing the date after `liveTimeSeconds` days
   * have passed from the current date.
   * 
   * @param tokenOwner user associated with the generated security token.
   * 
   * 	- `tokenOwner`: A `User` object representing the owner of the security token.
   * 	+ `tokenOwner.getUsername()`: The username of the token owner.
   * 	+ `tokenOwner.getEmail()`: The email address of the token owner.
   * 
   * @returns a new SecurityToken instance with its properties set and saved in the repository.
   * 
   * 	- `token`: A unique token string generated using the `UUID` class.
   * 	- `creationDate`: The current date and time when the token was created.
   * 	- `expiryDate`: The date and time after which the token will expire, calculated
   * by subtracting the `liveTimeSeconds` from the current date.
   * 	- `tokenOwner`: The user who owns the token.
   * 	- `SecurityToken`: An object of the `SecurityToken` class, representing the
   * security token.
   * 
   * The `securityTokenRepository` is responsible for saving the newly created security
   * token in the database.
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
   * creates a security token for an user, with a specified time and owner.
   * 
   * @param tokenOwner user for whom an email confirmation token is being generated.
   * 
   * 	- `tokenOwner`: A `User` object representing the user for whom an email confirmation
   * token is being generated.
   * 
   * @returns a security token with the specified type and expiration time, created
   * using the provided user's information.
   * 
   * 	- `SecurityTokenType`: The type of token created, which is `EMAIL_CONFIRM`.
   * 	- `emailConfirmTokenTime`: The time when the token was created.
   * 	- `tokenOwner`: The user for whom the token was created.
   */
  @Override
  public SecurityToken createEmailConfirmToken(User tokenOwner) {
    return createSecurityToken(SecurityTokenType.EMAIL_CONFIRM, emailConfirmTokenTime, tokenOwner);
  }

  /**
   * creates a security token for password reset purposes. It generates a unique token
   * based on the user's ID and a time stamp, then returns it to the user.
   * 
   * @param tokenOwner user for whom the password reset token is being created.
   * 
   * 	- `tokenOwner`: This parameter represents a `User` object that contains information
   * about the user who is requesting a password reset. The `User` class has attributes
   * such as `username`, `email`, and `passwordHash`.
   * 
   * @returns a SecurityToken instance representing a password reset token.
   * 
   * 	- The SecurityToken object that is created represents a password reset token.
   * 	- The `SecurityTokenType` field indicates that this is a password reset token.
   * 	- The `passResetTokenTime` field specifies the time when the token was generated.
   * 	- The `tokenOwner` field refers to the user for whom the token was generated.
   */
  @Override
  public SecurityToken createPasswordResetToken(User tokenOwner) {
    return createSecurityToken(SecurityTokenType.RESET, passResetTokenTime, tokenOwner);
  }

  /**
   * updates a provided SecurityToken instance, marks it as used, and saves it to the
   * repository. It returns the updated SecurityToken instance.
   * 
   * @param token SecurityToken object that is being used by the function, and its
   * `used` field is set to `true` before saving it into the repository.
   * 
   * 	- `setUsed(true)` marks the token as used.
   * 	- `securityTokenRepository.save(token)` persists the token in the repository.
   * 
   * @returns a new SecurityToken object with updated `used` field set to true and saved
   * in the repository.
   * 
   * 	- The `setUsed(true)` method sets the `used` field of the `SecurityToken` object
   * to `true`, indicating that the token has been used.
   * 	- The `securityTokenRepository.save(token)` method saves the `SecurityToken`
   * object in the repository, allowing it to be retrieved and reused later.
   * 
   * The output of the `useToken` function is a `SecurityToken` object with its `used`
   * field set to `true`, and saved in the repository for future use.
   */
  @Override
  public SecurityToken useToken(SecurityToken token) {
    token.setUsed(true);
    token = securityTokenRepository.save(token);
    return token;
  }

  /**
   * takes a `LocalDate` and a `Duration` as inputs, and returns a new `LocalDate` that
   * is `liveTime` days after the original date.
   * 
   * @param date initial date to which the `liveTime` is added in days.
   * 
   * 	- `LocalDate date`: Represents a specific date in the format `YYYY-MM-DD`.
   * 	- `Duration liveTime`: Represents the number of days to add to the initial date.
   * 
   * @param liveTime number of days to add to the given `LocalDate`, resulting in the
   * new date after the specified duration has passed.
   * 
   * 	- `toDays()` - Returns the duration of liveTime in days.
   * 
   * @returns a new LocalDate instance representing the date that is `days` later than
   * the original input date.
   * 
   * 	- `LocalDate`: The return type is `LocalDate`, indicating that the method returns
   * a date with the same level of granularity as a calendar date (i.e., no smaller
   * time units like milliseconds).
   * 	- `date`: The input parameter `date` is a `LocalDate` object, which represents a
   * date in the local calendar system.
   * 	- `liveTime`: The input parameter `liveTime` is a `Duration` object, which
   * represents a period of time in the local calendar system.
   * 	- `plusDays`: The method used to calculate the returned output is `plusDays`,
   * which adds the specified number of days to the input date.
   */
  private LocalDate getDateAfterDays(LocalDate date, Duration liveTime) {
    return date.plusDays(liveTime.toDays());
  }
}
