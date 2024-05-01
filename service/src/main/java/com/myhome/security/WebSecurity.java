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
 * is configured to enable CORS, disable CSFR and frames, and add a filter to authorize
 * requests based on the user's role. The configure method takes an HttpSecurity
 * object as input and configures various settings related to authentication management,
 * including disabling CSRF and frames, and adding a filter after the community filter
 * and before the end of the configuration chain.
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
   * configures the HTTP security for a Spring Boot application, disabling CSRF and
   * frame options, and setting session management to STATELESS. It also authorizes
   * requests based on various URL patterns, permitting all access to specific endpoints.
   * Additionally, it adds a filter to handle JWT encoder/decoder tasks.
   * 
   * @param http HTTP security configuration object, which is used to configure various
   * security features such as CORS, session management, and authorization rules for
   * different URL patterns.
   * 
   * 	- `cors()`: enables Cross-Origin Resource Sharing (CORS) functionality.
   * 	- `csrf()`. disable(): disables Cross-Site Request Forgery (CSRF) protection.
   * 	- `headers().frameOptions()`. disable(): disables the ability to set frame options
   * for responses.
   * 	- `sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)`:
   * sets the session creation policy to STATELESS, which means that sessions will not
   * be created automatically.
   * 	- `addFilterAfter(getCommunityFilter(), MyHomeAuthorizationFilter.class)`: adds
   * a filter after the community filter and before the end of the configuration chain.
   * 	- `authorizeRequests()`: defines a set of ant matchers that specify which requests
   * are allowed or denied based on various conditions.
   * 
   * In summary, these properties/attributes configure CORS functionality, disable CSRF
   * protection, disables the ability to set frame options for responses, sets session
   * creation policy to STATELESS, adds a filter after the community filter and before
   * the end of the configuration chain, and defines a set of ant matchers that specify
   * which requests are allowed or denied based on various conditions.
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
   * creates a `CommunityAuthorizationFilter` instance, which combines authentication
   * and community service functionality to filter community content access.
   * 
   * @returns an instance of `CommunityAuthorizationFilter`.
   * 
   * 1/ The input parameters used in the function creation include an instance of
   * `AuthenticationManager` and an instance of `CommunityService`. These parameters
   * represent the authentication manager and community service, respectively.
   * 2/ The return value is a `Filter`, which is an interface that defines the functionality
   * for filtering requests and responses based on criteria specified by the developer.
   * 3/ The `Filter` object returned by the function is an instance of
   * `CommunityAuthorizationFilter`. This class implements the `Filter` interface and
   * provides functionality for authorizing requests based on community-specific rules.
   */
  private Filter getCommunityFilter() throws Exception {
    return new CommunityAuthorizationFilter(authenticationManager(), communityService);
  }

  /**
   * sets up authentication manager builder's user details service and password encoder.
   * 
   * @param auth Authentication Manager Builder, which is used to configure various
   * aspects of the authentication system, including the user details service and
   * password encoder.
   * 
   * 	- `userDetailsService`: It is an instance of `UserDetailsService`, which is used
   * to retrieve user details for authentication purposes.
   * 	- `passwordEncoder`: It is an instance of `PasswordEncoder`, responsible for
   * encrypting passwords securely.
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }
}
