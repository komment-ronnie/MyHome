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

package com.myhome.security;

import com.myhome.security.filters.CommunityAuthorizationFilter;
import com.myhome.security.jwt.AppJwtEncoderDecoder;
import com.myhome.services.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.Filter;

/**
 * is configured to enable web security for the application, allowing only authenticated
 * requests and filtering them based on the community authorization filter. The
 * configure() method sets up the HTTP security, disabling CORS, frame options, and
 * session creation policy. Additionally, it adds a filter after the community filter
 * using the MyHomeAuthorizationFilter class.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {
  private final Environment environment;
  private final UserDetailsService userDetailsService;
  private final CommunityService communityService;
  private final PasswordEncoder passwordEncoder;
  private final AppJwtEncoderDecoder appJwtEncoderDecoder;

  /**
   * defines security settings for an API, disabling CORS and CSFR, and allowing all
   * methods on specific URLs. It also adds a filter to authenticate and authorize
   * requests, and another filter to handle JWT encoding and decoding.
   * 
   * @param http security configuration for the application, allowing the code to
   * configure various security features such as CORS, CSFR, session management, and
   * authorization policies for specific URLs and HTTP methods.
   * 
   * 	- `cors`: Enables CORS (Cross-Origin Resource Sharing) functionality.
   * 	- `csrf`: Disables CSRF (Cross-Site Request Forgery) protection.
   * 	- `frameOptions`: Disables frame options for security reasons.
   * 	- `sessionManagement`: Configures the session creation policy to be stateful.
   * 	- `addFilterAfter`: Adds a filter after the `MyHomeAuthorizationFilter`.
   * 	- `authorizeRequests`: Configures authorization rules for various HTTP methods
   * and URL paths.
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable();
    http.headers().frameOptions().disable();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.addFilterAfter(getCommunityFilter(), MyHomeAuthorizationFilter.class);

    http.authorizeRequests()
        .antMatchers(environment.getProperty("api.public.h2console.url.path"))
        .permitAll()
        .antMatchers(environment.getProperty("api.public.actuator.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.POST, environment.getProperty("api.public.registration.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.POST, environment.getProperty("api.public.login.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS, environment.getProperty("api.public.cors.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.GET, environment.getProperty("api.public.confirm-email.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.GET, environment.getProperty("api.public.resend-confirmation-email.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.POST, environment.getProperty("api.public.confirm-email.url.path"))
        .permitAll()
        .antMatchers("/swagger/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .addFilter(new MyHomeAuthorizationFilter(authenticationManager(), environment,
            appJwtEncoderDecoder))
        .addFilterAfter(getCommunityFilter(), MyHomeAuthorizationFilter.class);
  }

  /**
   * creates a `CommunityAuthorizationFilter` instance by combining the `AuthenticationManager`
   * and `CommunityService` objects, enabling the filter to authenticate and authorize
   * community-related requests.
   * 
   * @returns a `Filter` object implementing community authorization functionality
   * through a combination of authentication and service calls.
   * 
   * 	- The function returns an instance of the `Filter` class.
   * 	- The filter is created by combining two objects: the `AuthenticationManager` and
   * the `CommunityService`.
   * 	- The `AuthenticationManager` is used to authenticate requests, while the
   * `CommunityService` provides functionality related to communities.
   */
  private Filter getCommunityFilter() throws Exception {
    return new CommunityAuthorizationFilter(authenticationManager(), communityService);
  }

  /**
   * sets up authentication manager configuration by providing a user details service
   * and password encoder for customizing authentication logic.
   * 
   * @param auth AuthenticationManagerBuilder instance, which is being configured by
   * specifying the user details service and password encoder used for authentication.
   * 
   * 	- `userDetailsService`: This property is an instance of `UserDetailsService`. It
   * represents the user details service responsible for storing and retrieving user information.
   * 	- `passwordEncoder`: This property is an instance of `PasswordEncoder`. It encodes
   * passwords before saving them to the database or when retrieving them.
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }
}
