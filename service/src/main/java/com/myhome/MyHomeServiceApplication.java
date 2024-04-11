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

package com.myhome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * is a Spring Boot application that provides a password encoder using BCrypt. The
 * main method starts the application and the @Bean annotation defines a bean for the
 * password encoder.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MyHomeServiceApplication {

  /**
   * runs a Spring application named `MyHomeServiceApplication`.
   * 
   * @param args command-line arguments passed to the `SpringApplication.run()` method
   * when it is called, allowing the application to be launched with specific configuration
   * options or other parameters.
   * 
   * 	- `String[]`: Represents an array of strings, indicating the command-line arguments
   * passed to the program during execution.
   */
  public static void main(String[] args) {
    SpringApplication.run(MyHomeServiceApplication.class, args);
  }

  /**
   * returns a `BCryptPasswordEncoder` instance, which is a cryptographic hash function
   * for password storage and verification.
   * 
   * @returns a BCryptPasswordEncoder instance, which is used to encrypt passwords securely.
   * 
   * The `BCryptPasswordEncoder` object is an implementation of the `PasswordEncoder`
   * interface in Java.
   * It uses the BCrypt hashing algorithm to encrypt passwords securely.
   * This algorithm is considered secure because it takes into account the length and
   * complexity of the password being encrypted, making it more resistant to brute-force
   * attacks.
   */
  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
