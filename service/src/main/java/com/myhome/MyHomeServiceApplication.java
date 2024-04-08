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
 * is a Spring Boot application that provides a password encoder using the BCrypt
 * PasswordEncoder class. The main method starts the application and the getPasswordEncoder()
 * method returns a BCryptPasswordEncoder instance.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MyHomeServiceApplication {

  /**
   * starts the Spring application by running the `MyHomeServiceApplication` class.
   * 
   * @param args 1 or more command-line arguments passed to the `SpringApplication.run()`
   * method when executing the application.
   * 
   * 	- The argument array has zero or more elements, which are the command-line arguments
   * passed to the application.
   * 	- The `args` object provides access to the individual arguments in the array
   * through its `get` methods, such as `get[0]` for the first argument and `get[1]`
   * for the second argument, and so on.
   */
  public static void main(String[] args) {
    SpringApplication.run(MyHomeServiceApplication.class, args);
  }

  /**
   * returns a `BCryptPasswordEncoder` instance, which is used to encrypt passwords
   * using the BCrypt hashing algorithm.
   * 
   * @returns a BCryptPasswordEncoder instance, which is a widely-used password hashing
   * algorithm.
   * 
   * 1/ Type: The `BCryptPasswordEncoder` class is a specific implementation of the
   * `PasswordEncoder` interface in Java.
   * 2/ Parameters: The constructor for `BCryptPasswordEncoder` takes no parameters,
   * indicating that it does not have any mandatory initialization values.
   * 3/ Methods: The `BCryptPasswordEncoder` class provides several methods for password
   * encryption and decryption, including `encode()` and `decode()`.
   */
  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
