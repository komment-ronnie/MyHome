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
   * runs the `MyHomeServiceApplication` by using the `SpringApplication.run()` method,
   * passing the class and argument array as arguments.
   * 
   * @param args array of command-line arguments passed to the `SpringApplication.run()`
   * method when invoking the application.
   * 
   * The `SpringApplication.run()` method takes two arguments: `MyHomeServiceApplication.class`
   * and `args`. The first argument is a class that implements the `SpringApplication`
   * interface, which represents the application context. The second argument is an
   * array of strings called `args`, which contains the command-line arguments passed
   * to the application.
   */
  public static void main(String[] args) {
    SpringApplication.run(MyHomeServiceApplication.class, args);
  }

  /**
   * returns a `BCryptPasswordEncoder` instance, which is used to hash and verify
   * passwords in a secure manner.
   * 
   * @returns a `BCryptPasswordEncoder` instance, which is used to encrypt passwords
   * using the bcrypt algorithm.
   * 
   * 	- The `BCryptPasswordEncoder` class is used to generate password hashes using
   * bcrypt hashing algorithm.
   * 	- This class provides methods for generating salt values and computing passwords
   * hashes.
   * 	- The `new BCryptPasswordEncoder()` statement creates an instance of this class,
   * which can then be used to generate password hashes.
   */
  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
