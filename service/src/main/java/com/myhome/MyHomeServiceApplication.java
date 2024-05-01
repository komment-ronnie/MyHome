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
 * main method starts the application and the `@Bean` annotation defines a bean for
 * the password encoder.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MyHomeServiceApplication {

  /**
   * runs a SpringApplication, which launches a Spring Boot application with the given
   * class as its configuration class.
   * 
   * @param args command-line arguments passed to the `SpringApplication.run()` method
   * when invoking the application.
   * 
   * 	- The `String[]` type indicates an array of string objects.
   * 	- The name `args` is used as the variable name for the input parameter.
   * 	- The `SpringApplication.run()` method is called to initiate the Spring application
   * framework.
   * 	- The `MyHomeServiceApplication.class` is the fully qualified class name of the
   * application being run, which is passed as an argument to `SpringApplication.run()`.
   */
  public static void main(String[] args) {
    SpringApplication.run(MyHomeServiceApplication.class, args);
  }

  /**
   * returns a `BCryptPasswordEncoder` instance, which is a popular and secure password
   * hashing algorithm used to protect user passwords.
   * 
   * @returns a `BCryptPasswordEncoder` object, which is used to encrypt passwords securely.
   * 
   * The function returns an instance of the `BCryptPasswordEncoder` class, which is a
   * third-party password encryption library.
   * This encoder uses a salted hashing algorithm to encrypt passwords, providing a
   * high level of security against brute-force attacks.
   * The `BCryptPasswordEncoder` class offers several configuration options for tweaking
   * the encryption process, such as the cost parameter that controls the number of
   * iterations in the hashing process.
   */
  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
