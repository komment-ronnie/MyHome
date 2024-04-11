package com.myhome.repositories;

import com.myhome.domain.SecurityToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * extends JpaRepository and is responsible for managing SecurityTokens in a database
 * using Spring Data JPA.
 */
public interface SecurityTokenRepository extends JpaRepository<SecurityToken, Long> {
}
