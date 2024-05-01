package com.myhome.repositories;

import com.myhome.domain.SecurityToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * extends JpaRepository and manages SecurityTokens in a database using Spring Data
 * JPA for querying and persisting to the database.
 */
public interface SecurityTokenRepository extends JpaRepository<SecurityToken, Long> {
}
