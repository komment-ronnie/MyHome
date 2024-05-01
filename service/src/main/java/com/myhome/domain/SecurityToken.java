package com.myhome.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

/**
 * represents a security token with various attributes and relationships, including
 * token type, token value, creation date, expiry date, and ownership by a user.
 * Fields:
 * 	- tokenType (SecurityTokenType): represents an enumeration of SecurityTokenType
 * enums used to identify the type of security token being represented by the
 * SecurityToken class.
 * 	- token (String): in the SecurityToken class represents the classification of the
 * security token, which is an enumeration of SecurityTokenType enums.
 * 	- creationDate (LocalDate): represents the date when the security token was created.
 * 	- expiryDate (LocalDate): represents the date after which the security token is
 * no longer valid.
 * 	- isUsed (boolean): in the SecurityToken class indicates whether a security token
 * has been used or not.
 * 	- tokenOwner (User): represents a user who owns or has access to a security token
 * in question.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"tokenOwner"})
public class SecurityToken extends BaseEntity {
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SecurityTokenType tokenType;
  @Column(nullable = false, unique = true)
  private String token;
  @Column(nullable = false)
  private LocalDate creationDate;
  @Column(nullable = false)
  private LocalDate expiryDate;
  private boolean isUsed;
  @ManyToOne
  private User tokenOwner;
}
