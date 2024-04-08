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
 * is a Spring Boot application that includes a password encoder using BCrypt. The
 * main method starts the application and runs it with the given arguments. The
 * getPasswordEncoder() method returns a BCryptPasswordEncoder object, which is used
 * to encrypt passwords in the application.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MyHomeServiceApplication {

  /**
   * starts a Spring application using the `SpringApplication.run()` method, passing
   * the class `MyHomeServiceApplication` as an argument.
   * 
   * @param args command-line arguments passed to the `SpringApplication.run()` method
   * when invoking the application.
   * 
   * 	- `SpringApplication.run()` method is called with the `MyHomeServiceApplication.class`
   * and `args` as arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(MyHomeServiceApplication.class, args);
  }

  /**
   * returns a `BCryptPasswordEncoder` instance, which is a widely-used password hashing
   * algorithm that provides strong security for password storage.
   * 
   * @returns a `BCryptPasswordEncoder` instance, which is used to encrypt passwords securely.
   * 
   * 	- The function returns an instance of the `BCryptPasswordEncoder` class, which
   * is a popular password hashing algorithm used for encrypting passwords securely.
   * 	- The `BCryptPasswordEncoder` class provides several methods for encrypting and
   * verifying passwords using the bcrypt algorithm.
   * 	- The encoder uses a salt value to generate a unique hash for each password,
   * making it difficult for attackers to use precomputed tables or rainbow tables to
   * crack the password.
   */
  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
