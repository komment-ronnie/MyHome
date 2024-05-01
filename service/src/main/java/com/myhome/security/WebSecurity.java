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
 * is used to configure security settings for an Spring Boot application. It allows
 * you to enable CORS functionality, disable CSRF protection, and configure session
 * management policies. Additionally, it sets up authorization rules for various HTTP
 * methods and URL paths using the `authorizeRequests` method. The `getCommunityFilter`
 * method creates a filter that combines the `AuthenticationManager` and `CommunityService`
 * objects to enable community-related authentication and authorization functionality.
 * Finally, the `configure` method configures the `AuthenticationManagerBuilder`
 * instance by specifying the user details service and password encoder used for
 * customizing authentication logic.
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
   * sets up security configurations for an API. It disables CSRF and frames, allows
   * anonymous access to certain endpoints, and adds a filter to authorize requests
   * based on the user's role.
   * 
   * @param http HTTP security configuration object, which is used to configure various
   * settings for securing the application's endpoints.
   * 
   * 	- `cors()`: Enables Cross-Origin Resource Sharing (CORS) functionality.
   * 	- `csrf()`.disable(): Disables Cross-Site Request Forgery (CSRF) protection.
   * 	- `headers().frameOptions()`.disable(): Disables the ability to use frame options
   * in HTTP requests.
   * 	- `sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)`:
   * Configures session creation policy to be stateless.
   * 	- `addFilterAfter(getCommunityFilter(), MyHomeAuthorizationFilter.class)`: Adds
   * a filter after the community filter and before the end of the configuration chain.
   * 
   * The `http` object is deserialized from the input, and its properties are explained
   * above.
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
   * and `CommunityService`. This filter is used to authorize community-related operations
   * based on user authentication.
   * 
   * @returns an instance of the `CommunityAuthorizationFilter` class, which filters
   * community-related endpoints based on user roles and permissions.
   * 
   * The `CommunityAuthorizationFilter` object is created through a combination of two
   * parameters: `authenticationManager()` and `communityService`. The `authenticationManager()`
   * parameter represents an instance of the `AuthenticationManager` interface, which
   * is responsible for managing user authentication in the system. The `communityService`
   * parameter represents an instance of the `CommunityService` interface, which is
   * responsible for handling community-related operations.
   * 
   * The `CommunityAuthorizationFilter` object itself is a subclass of the `AbstractFilter`
   * class, which provides a generic filter implementation that can be used to perform
   * various types of filtering tasks. In this case, the `getCommunityFilter` function
   * returns an instance of the `CommunityAuthorizationFilter` class, which is capable
   * of filtering community-related operations based on user authentication and authorization.
   */
  private Filter getCommunityFilter() throws Exception {
    return new CommunityAuthorizationFilter(authenticationManager(), communityService);
  }

  /**
   * configures authentication-related settings by passing user details service and
   * password encoder instances to the builder.
   * 
   * @param auth AuthenticationManagerBuilder, which is used to configure various aspects
   * of authentication management, including the userDetailsService and passwordEncoder.
   * 
   * 	- `userDetailsService`: A reference to an implementation of `UserDetailsService`,
   * which is responsible for retrieving user details from the database or other data
   * source.
   * 	- `passwordEncoder`: A reference to an implementation of `PasswordEncoder`, which
   * is used to encrypt passwords for the user accounts.
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }
}
