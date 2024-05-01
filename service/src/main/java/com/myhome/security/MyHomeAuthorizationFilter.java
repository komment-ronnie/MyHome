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

import com.myhome.security.jwt.AppJwt;
import com.myhome.security.jwt.AppJwtEncoderDecoder;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * is a subclass of `FilterChain` that checks for an authorization token in the HTTP
 * request's `Authorization` header and decodes it to create a `UsernamePasswordAuthenticationToken`.
 * If the token is null or cannot be decoded, the filter returns a 401 Unauthorized
 * response. Otherwise, it passes the request through the filter chain.
 */
public class MyHomeAuthorizationFilter extends BasicAuthenticationFilter {

  private final Environment environment;
  private final AppJwtEncoderDecoder appJwtEncoderDecoder;

  public MyHomeAuthorizationFilter(
      AuthenticationManager authenticationManager,
      Environment environment,
      AppJwtEncoderDecoder appJwtEncoderDecoder) {
    super(authenticationManager);
    this.environment = environment;
    this.appJwtEncoderDecoder = appJwtEncoderDecoder;
  }

  /**
   * authenticates requests based on a provided header name and prefix. If the header
   * is not present or does not match the prefix, it passes the request to the next
   * chain element. Otherwise, it sets an authentication token using the provided header
   * value and passes the request to the next chain element.
   * 
   * @param request HTTP request being filtered.
   * 
   * 	- `HttpServletRequest request`: This is an instance of the `HttpServletRequest`
   * class, which contains information about the incoming HTTP request.
   * 	- `authHeaderName`: A string property representing the name of the authentication
   * header field in the HTTP request.
   * 	- `authHeaderPrefix`: Another string property representing a prefix to be used
   * when checking the authentication header field.
   * 	- `authHeader`: The value of the authentication header field in the HTTP request,
   * which is either null or starts with the prefix provided by the `authHeaderPrefix`
   * property.
   * 	- `getAuthentication()`: A method that returns an instance of the
   * `UsernamePasswordAuthenticationToken` class, which contains information about the
   * authenticated user.
   * 	- `SecurityContextHolder.getContext().setAuthentication()`: This line sets the
   * authentication token to be used in the current request by calling the `setAuthentication()`
   * method of the `SecurityContextHolder`.
   * 
   * @param response HttpServletResponse object that is the target of the filter's action.
   * 
   * 	- `HttpServletRequest request`: This is the original HTTP request object that
   * triggered the filter chain execution.
   * 	- `HttpServletResponse response`: This is the filtered HTTP response object, which
   * may have been modified by the filter chain execution.
   * 	- `FilterChain chain`: This is the chain of filters that are applied to the request
   * before it reaches the servlet.
   * 	- `IOException`: This is a subclass of runtime exception that indicates an I/O
   * error occurred while processing the request or response.
   * 	- `ServletException`: This is a subclass of runtime exception that indicates a
   * problem occurred while processing the request or response.
   * 
   * @param chain next filter in the filter chain that the current filter is processing,
   * and it is used to pass the request and response objects to the subsequent filter
   * for further processing.
   * 
   * 	- `HttpServletRequest request`: The incoming HTTP request object.
   * 	- `HttpServletResponse response`: The outgoing HTTP response object.
   * 	- `FilterChain chain`: An instance of the `FilterChain` class, which represents
   * the filter chain that this filter is a part of.
   * 	- `IOException`: A subclass of the `Throwable` class that represents an input/output
   * error.
   * 	- `ServletException`: A subclass of the `Throwable` class that represents a
   * servlet-specific error.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    String authHeaderName = environment.getProperty("authorization.token.header.name");
    String authHeaderPrefix = environment.getProperty("authorization.token.header.prefix");

    String authHeader = request.getHeader(authHeaderName);
    if (authHeader == null || !authHeader.startsWith(authHeaderPrefix)) {
      chain.doFilter(request, response);
      return;
    }

    UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(request, response);
  }

  /**
   * retrieves an authentication token from a request header, decodes it, and returns
   * a `UsernamePasswordAuthenticationToken` instance representing the authenticated user.
   * 
   * @param request HTTP request that contains the authentication token in the header.
   * 
   * 	- `request.getHeader()` retrieves the value of an HTTP header field from the
   * incoming request. In this case, it retrieves the authorization token header field
   * named `environment.getProperty("authorization.token.header.name")`.
   * 	- `authHeader.replace()` replaces a prefix string with a new value in the
   * authorization token header field. The prefix is specified by `environment.getProperty("authorization.token.header.prefix")`.
   * 	- `appJwtEncoderDecoder.decode()` deserializes the authorization token into an
   * instance of `AppJwt`. It takes the token string as input and a secret key specified
   * by `environment.getProperty("token.secret")` to validate the token.
   * 	- `jwt.getUserId()` retrieves the user ID associated with the decoded JWT token.
   * 
   * @returns a `UsernamePasswordAuthenticationToken` object representing a user
   * authenticated through an authorization token.
   * 
   * 	- The `String` variable `authHeader` is extracted from the `HttpServletRequest`
   * parameter `request`.
   * 	- The `authHeader` value is then trimmed by removing the prefix specified in the
   * `environment.getProperty("authorization.token.header.prefix")` property.
   * 	- The resulting token is then decoded using the `appJwtEncoderDecoder.decode()`
   * method and passed to the `environment.getProperty("token.secret")` property for decoding.
   * 	- If the decoded token contains a user ID, a new `UsernamePasswordAuthenticationToken`
   * object is created with the user ID as the username and an empty list of credentials.
   * 
   * In summary, the `getAuthentication` function extracts the authentication token
   * from the HTTP request header, decodes it using the provided secret key, and returns
   * a `UsernamePasswordAuthenticationToken` object if the decoded token contains a
   * user ID.
   */
  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    String authHeader =
        request.getHeader(environment.getProperty("authorization.token.header.name"));
    if (authHeader == null) {
      return null;
    }

    String token =
        authHeader.replace(environment.getProperty("authorization.token.header.prefix"), "");
    AppJwt jwt = appJwtEncoderDecoder.decode(token, environment.getProperty("token.secret"));

    if (jwt.getUserId() == null) {
      return null;
    }
    return new UsernamePasswordAuthenticationToken(jwt.getUserId(), null, Collections.emptyList());
  }
}
