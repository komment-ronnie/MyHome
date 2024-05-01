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
 * provides methods for creating and managing security tokens used in the application's
 * authentication process. It offers functionality for generating new security tokens,
 * confirming email addresses, resetting passwords, and marking tokens as used and
 * saving them to a repository for future reference. The class also includes utility
 * methods for calculating the number of days between two dates.
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
   * creates a new security token with a unique identifier, creation date, expiry date,
   * and owner. It saves the token to a repository for later use.
   * 
   * @param tokenType type of security token being created, which determines the format
   * and content of the token.
   * 
   * 	- `SecurityTokenType`: This represents the type of security token being created,
   * such as `Active` or `Inactive`.
   * 	- `Duration liveTimeSeconds`: This is the duration in seconds for which the
   * security token is valid.
   * 	- `User tokenOwner`: This is the user who owns the security token.
   * 
   * The function then creates a new `SecurityToken` instance with the specified
   * properties and saves it to the repository using the `save()` method.
   * 
   * @param liveTimeSeconds duration of time that the generated security token will be
   * valid for, in seconds.
   * 
   * 	- `liveTimeSeconds`: This is an instance of the `Duration` class, representing
   * the lifetime of the security token in seconds. The `Duration` class has several
   * attributes, including the number of seconds, nanoseconds, and microseconds.
   * 	- `LocalDate creationDate`: This represents the date and time when the security
   * token was created. It is an instance of the `LocalDate` class, which represents a
   * date in the Java world. The `LocalDate` class has several attributes, including
   * the year, month, day of the week, day of the month, and hour of the day.
   * 	- `LocalDate expiryDate`: This represents the date and time when the security
   * token will expire. It is also an instance of the `LocalDate` class. The `expiryDate`
   * property has the same attributes as the `creationDate`.
   * 
   * @param tokenOwner user who owns the newly created security token.
   * 
   * 	- `tokenOwner`: A `User` object that represents the owner of the security token.
   * It contains attributes such as `id`, `username`, `password`, and any other relevant
   * information for user authentication and authorization purposes.
   * 
   * @returns a newly created security token instance containing the specified information.
   * 
   * 	- `token`: A unique token identifier generated using the `UUID` class.
   * 	- `creationDate`: The date and time when the security token was created, represented
   * as a `LocalDate`.
   * 	- `expiryDate`: The date and time after which the security token will expire,
   * calculated by subtracting the `liveTimeSeconds` from the current date using the
   * `getDateAfterDays` method. Also represented as a `LocalDate`.
   * 	- `tokenOwner`: The user who owns the security token, represented as an instance
   * of the `User` class.
   * 	- `SecurityToken`: An object representing the security token, containing all its
   * properties and attributes.
   * 
   * Note: The `securityTokenRepository` is not explained as it is not part of the
   * function's output.
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
   * creates a security token for an email confirmation process with a specific time
   * stamp and reference to the token owner.
   * 
   * @param tokenOwner User object that owns the token being created.
   * 
   * 	- `tokenOwner`: The user object for which an email confirmation token is being
   * generated. This object contains various attributes, such as `username`, `email`,
   * and `password`.
   * 
   * @returns a security token of type `EMAIL_CONFIRM`.
   * 
   * 	- `SecurityTokenType`: This is an enumeration value indicating that the token is
   * for email confirmation.
   * 	- `emailConfirmTokenTime`: This is a long value representing the time at which
   * the token was created.
   * 	- `tokenOwner`: This is the user whose security token is being generated.
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
   * 	- `passResetTokenTime`: The time when the password reset token was generated.
   * 
   * The function creates a security token using the `createSecurityToken` method and
   * passes it as an argument to the constructor of the `SecurityToken` class, along
   * with the `SecurityTokenType.RESET`, `passResetTokenTime`, and `tokenOwner`.
   * 
   * @returns a SecurityToken instance representing a password reset token with a
   * specified expiration time and owner user.
   * 
   * 	- The SecurityToken object `createPasswordResetToken` returns is of type `SecurityTokenType.RESET`.
   * 	- The `passResetTokenTime` parameter represents the time when the password reset
   * token was generated, which is included in the SecurityToken's attributes.
   * 	- The `tokenOwner` parameter represents the user for whom the password reset token
   * is being created, and is also included in the SecurityToken's attributes.
   */
  @Override
  public SecurityToken createPasswordResetToken(User tokenOwner) {
    return createSecurityToken(SecurityTokenType.RESET, passResetTokenTime, tokenOwner);
  }

  /**
   * updates a security token by marking it as used and saving it to the repository,
   * returning the updated token.
   * 
   * @param token SecurityToken object that is being processed and updated by the
   * `useToken` method.
   * 
   * 	- `setUsed(true)` sets the `used` attribute of the token to `true`.
   * 	- `securityTokenRepository.save(token)` saves the token in the security token
   * repository after updating its `used` attribute.
   * 
   * @returns a modified SecurityToken object, with the `used` field set to `true` and
   * persisted in the repository.
   * 
   * 	- The `setUsed(true)` method sets the `used` field of the token to `true`. This
   * indicates that the token has been used and cannot be reused.
   * 	- The `save()` method saves the modified token in the security token repository.
   * 	- The returned token is a persisted instance of the `SecurityToken` class, which
   * contains its own unique identifier and other attributes specific to the application.
   */
  @Override
  public SecurityToken useToken(SecurityToken token) {
    token.setUsed(true);
    token = securityTokenRepository.save(token);
    return token;
  }

  /**
   * takes a `LocalDate` and a `Duration` as inputs and returns the date that is
   * `liveTime` days after the original date.
   * 
   * @param date LocalDate object to be adjusted by adding the specified number of days.
   * 
   * LocalDate represents a date with no time part. It is a type-safe representation
   * of a date with millisecond precision. Date and Time classes are combined into one
   * class in Java to form LocalDate. The day of the month, month of the year, and year
   * are among the properties that make up this date.
   * Additionally, `liveTime` represents a duration of time measured in days, which is
   * multiplied by the `date` object's days attribute to produce the output date.
   * 
   * @param liveTime duration of time that the `date` should be after it is modified
   * by the function, and it is used to calculate the number of days to add to the `date`.
   * 
   * 	- `toDays()`: This method returns the duration of `liveTime` in days.
   * 
   * @returns a new LocalDate that represents the date after adding the specified number
   * of days to the input date.
   * 
   * The returned output is a `LocalDate` object, indicating that it represents a date
   * in the local calendar system.
   * The output is created by adding the specified `Duration` (in days) to the input
   * `LocalDate`. This operation transforms the original date by the specified number
   * of days.
   * Therefore, the output represents the date that is `liveTime` days after the original
   * input date.
   */
  private LocalDate getDateAfterDays(LocalDate date, Duration liveTime) {
    return date.plusDays(liveTime.toDays());
  }
}
