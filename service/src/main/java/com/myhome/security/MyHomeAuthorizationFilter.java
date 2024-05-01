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
 * is used to filter incoming HTTP requests based on an authorization token extracted
 * from the request header. The filter decodes the token using a secret key and returns
 * a `UsernamePasswordAuthenticationToken` object representing the authenticated user
 * if the decoded token contains a user ID. If no user ID is found, the filter returns
 * null.
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
   * determines if a request has a valid authentication token and sets the SecurityContextHolder
   * with the appropriate authentication token before passing the request to the next
   * filter in the chain.
   * 
   * @param request HTTP request being processed by the filter.
   * 
   * 	- `authHeaderName`: String representing the name of the HTTP header containing
   * the authentication token.
   * 	- `authHeaderPrefix`: String representing the prefix of the authentication token
   * in the HTTP header.
   * 	- `authHeader`: String representing the actual authentication token found in the
   * HTTP header. If it's null or doesn't start with the prefix, then the authentication
   * token is not present.
   * 	- `request`: HttpServletRequest object containing various attributes related to
   * the HTTP request, such as method, URL, headers, parameters, and more.
   * 
   * @param response HTTP response object that is being filtered by the `doFilterInternal`
   * method.
   * 
   * 1/ `HttpServletRequest request`: This is the original HTTP request that was passed
   * to the filter chain.
   * 2/ `HttpServletResponse response`: This is the response object returned by the
   * filter chain after processing the request. It contains information about the
   * response, such as the status code, headers, and content.
   * 3/ `FilterChain chain`: This is the chain of filters that have been configured for
   * the current HTTP request. The `doFilterInternal` function is called recursively
   * to process each filter in the chain.
   * 4/ `IOException`, `ServletException`: These are the exceptions that can be thrown
   * by the `doFilterInternal` function if an error occurs during processing, such as
   * a missing or invalid authentication token.
   * 5/ `environment`: This is a reference to the environment object that contains
   * configuration properties for the filter. In this case, the `environment` object
   * contains property values for the authorization token header name and prefix.
   * 
   * @param chain FilterChain that will be executed after the authentication check in
   * the function.
   * 
   * 	- `request`: The original HTTP request object that was passed to the filter chain.
   * 	- `response`: The HTTP response object that the filter chain is processing.
   * 	- `FilterChain`: The filter chain that the current filter is a part of, which
   * contains other filters that can be executed in sequence.
   * 	- `environment`: A reference to the Spring Environment that contains configuration
   * properties and other bean instances.
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
   * retrieves an authentication token from a request header, decodes it using the
   * provided secret, and returns a `UsernamePasswordAuthenticationToken` instance
   * representing the user ID and null credentials.
   * 
   * @param request HTTP request object that contains an authentication token in the
   * header, which is used to retrieve and decode the JWT token.
   * 
   * 	- `getHeader()`: Retrieves the value of an HTTP header field.
   * 	- `environment.getProperty()`: Gets a property from the environment map.
   * 	- `appJwtEncoderDecoder.decode()`: Decodes a JSON Web Token (JWT) and retrieves
   * the user ID.
   * 
   * The input `request` has several attributes, including:
   * 
   * 	- `getHeader()`: Retrieves the value of an HTTP header field.
   * 	- `getProperty()`: Gets a property from the request's properties map.
   * 	- `getMethod()`: Returns the HTTP method (e.g., GET, POST, PUT, DELETE) used to
   * make the request.
   * 	- `getRequestURI()`: Returns the requested resource URL.
   * 	- `getProtocol()`: Returns the protocol (e.g., HTTP/1.1) used to make the request.
   * 	- `getRemoteAddr()`: Returns the client's IP address.
   * 	- `getContentLength()`: Returns the size of the request body in bytes.
   * 	- `getHeaderNames()`: Returns an unmodifiable list of the header fields in the request.
   * 	- `getheader()`: Retrieves the value of a specific HTTP header field.
   * 
   * @returns a `UsernamePasswordAuthenticationToken` object containing the user ID and
   * an empty list of roles.
   * 
   * 	- The `String` variable `authHeader` represents the value of the `Authorization`
   * header in the HTTP request.
   * 	- The `String` variable `token` represents the decoded JWT token extracted from
   * the `Authorization` header.
   * 	- The `AppJwt` object `jwt` contains information about the user, including their
   * ID.
   * 	- The `UsernamePasswordAuthenticationToken` object returned by the function
   * consists of a user ID and an empty list of credentials, indicating that no password
   * is required for authentication.
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
