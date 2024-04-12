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
 * is an extension of BasicAuthenticationFilter that adds an additional layer of
 * security by requiring a valid JWT token for authentication. The filter decodes the
 * JWT token and checks if the user is authorized to access the requested resource.
 * If the token is invalid or missing, the filter denies access and passes the request
 * to the next filter in the chain.
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
   * validates incoming HTTP requests by checking for an authorization token header and
   * authenticating users using a stored authentication token. If the token is absent
   * or does not match the expected prefix, the function passes the request to the next
   * filter chain stage without further processing.
   * 
   * @param request HTTP request being processed by the filter.
   * 
   * 	- `authHeaderName`: The name of the HTTP header that contains the authentication
   * token.
   * 	- `authHeaderPrefix`: The prefix that is used to start the authentication token
   * in the HTTP header.
   * 	- `authHeader`: The value of the HTTP header containing the authentication token,
   * or null if no such header is present.
   * 	- `request`: The deserialized input object representing the incoming HTTP request.
   * 
   * @param response HttpServletResponse object that is used to write the filtered
   * content to the client.
   * 
   * 	- `request`: The original HTTP request object that triggered the filter chain execution.
   * 	- `chain`: The next filter in the chain to be executed if the authentication fails.
   * 	- `authentication`: A `UsernamePasswordAuthenticationToken` instance obtained
   * from the HTTP header or other means, representing the authenticated user. This is
   * set in the `SecurityContextHolder` using the `setAuthentication()` method.
   * 
   * The `response` object has several properties and attributes, including:
   * 
   * 	- `getWriter()`: Returns a writer for writing response content.
   * 	- `getStatus()`: Returns the HTTP status code of the response.
   * 	- `getHeaders()`: Returns an unmodifiable map of HTTP headers.
   * 	- `getCharacterEncoding()`: Returns the character encoding of the response content.
   * 	- `getContentLength()`: Returns the content length of the response in bytes.
   * 
   * @param chain FilterChain that needs to be executed after the authentication check
   * is performed.
   * 
   * 	- `request`: A `HttpServletRequest` object representing the incoming HTTP request.
   * 	- `response`: A `HttpServletResponse` object representing the outgoing HTTP response.
   * 	- `FilterChain`: An instance of `FilterChain` that represents the chain of filters
   * to be executed in sequence for this request.
   * 	- `IOException`: Thrown if an I/O error occurs during processing, such as a
   * connection reset or file not found.
   * 	- `ServletException`: Thrown if a servlet-specific error occurs during processing,
   * such as a malformed Servlet configuration file or an unknown Servlet API method.
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
   * retrieves an authentication token from a HTTP request header and decodes it to
   * create a `UsernamePasswordAuthenticationToken`. If the token is null, the function
   * returns null.
   * 
   * @param request HTTP request being processed and provides the `Authorization` header
   * value that is used to retrieve the authentication token.
   * 
   * 	- `getHeader`: This method returns the value of the specified HTTP header in the
   * request.
   * 	- `environment.getProperty`: This method retrieves a property from the environment
   * variable.
   * 	- `appJwtEncoderDecoder.decode`: This method decodes the JWT token contained in
   * the `authHeader` and returns the user ID.
   * 
   * Therefore, the input `request` has the following properties:
   * 
   * 	- `authHeader`: A header containing the JWT token.
   * 	- `environment.getProperty("authorization.token.header.name")`: The name of the
   * HTTP header containing the JWT token.
   * 	- `environment.getProperty("authorization.token.header.prefix")`: The prefix of
   * the JWT token in the HTTP header.
   * 	- `environment.getProperty("token.secret")`: The secret key used to decode the
   * JWT token.
   * 
   * @returns a `UsernamePasswordAuthenticationToken` object representing a user
   * authenticated through an authorization token.
   * 
   * 	- `getAuthentication(HttpServletRequest request)`: This is the method signature
   * indicating that it takes an `HttpServletRequest` object as input and returns an
   * `UsernamePasswordAuthenticationToken` object as output.
   * 	- `String authHeader = request.getHeader(environment.getProperty("authorization.token.header.name"))`:
   * This line retrieves the authentication token from the `Authorization` header of
   * the HTTP request. The `environment.getProperty("authorization.token.header.name")`
   * property provides the name of the header where the token is expected to be placed.
   * 	- `if (authHeader == null) { return null; }`: This line checks if the authentication
   * token is present in the `Authorization` header, and if it's null, the method returns
   * a null value.
   * 	- `String token = authHeader.replace(environment.getProperty("authorization.token.header.prefix"),
   * "")`: This line replaces any prefix that may be present in the authentication token
   * with an empty string using the `environment.getProperty("authorization.token.header.prefix")`
   * property as a guide.
   * 	- `AppJwt jwt = appJwtEncoderDecoder.decode(token, environment.getProperty("token.secret"))`:
   * This line decodes the authentication token using the `appJwtEncoderDecoder` class
   * and the `environment.getProperty("token.secret")` property as the secret key.
   * 	- `if (jwt.getUserId() == null) { return null; }`: This line checks if the `userId`
   * property of the decoded JWT is null, and if it is, the method returns a null value.
   * 	- `return new UsernamePasswordAuthenticationToken(jwt.getUserId(), null,
   * Collections.emptyList());`: This line creates a new `UsernamePasswordAuthenticationToken`
   * object with the user ID obtained from the decoded JWT, an empty list of credentials,
   * and a null authentication principal.
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
