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
 * Is a Spring Boot application that provides a password encoder using BCrypt. It
 * runs a Spring application and defines a bean for the password encoder. The class
 * implements a secure password encryption mechanism.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MyHomeServiceApplication {

  /**
   * Runs the `SpringApplication` instance with a given class and arguments. The specified
   * class is assumed to be an application configuration class, which is used to create
   * a Spring application context. This process initializes the application and starts
   * it.
   *
   * @param args command-line arguments passed to the Java program when it is launched,
   * which are then used by the `SpringApplication.run()` method.
   */
  public static void main(String[] args) {
    SpringApplication.run(MyHomeServiceApplication.class, args);
  }

  /**
   * Returns an instance of a `BCryptPasswordEncoder`, which is used to securely store
   * and verify passwords using the bcrypt algorithm. This encoder can be used to hash
   * password strings and compare them with user-inputted credentials for authentication
   * purposes.
   *
   * @returns an instance of a `BCryptPasswordEncoder` class.
   */
  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
