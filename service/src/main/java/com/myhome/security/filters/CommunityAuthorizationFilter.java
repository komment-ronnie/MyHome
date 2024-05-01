package com.myhome.security.filters;

import com.myhome.domain.User;
import com.myhome.services.CommunityService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * is a subclass of BasicAuthenticationFilter that performs an internal filter operation
 * based on the request URI and user's role. It checks if the user is an administrator
 * of a particular community by querying the community service and sets a HTTP Status
 * Code accordingly. If not, it calls the superclass' `doFilterInternal` method to
 * continue the filtering process.
 */
public class CommunityAuthorizationFilter extends BasicAuthenticationFilter {
  private final CommunityService communityService;
  private static final String UUID_PATTERN =
      "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
  private static final Pattern ADD_AMENITY_REQUEST_PATTERN =
      Pattern.compile("/communities/" + UUID_PATTERN + "/amenities");

  public CommunityAuthorizationFilter(AuthenticationManager authenticationManager,
      CommunityService communityService) {
    super(authenticationManager);
    this.communityService = communityService;
  }

  /**
   * in a filter class intercepts and processes HTTP requests based on a pattern match
   * and user role validation.
   * 
   * @param request HTTP request received by the filter.
   * 
   * 	- `request.getRequestURI()`: This property returns the URI (Uniform Resource
   * Identifier) of the incoming HTTP request.
   * 	- `matcher`: This is an instance of `Matcher`, which is a class provided by the
   * `java.util.regex` package, and it is used to match the URL pattern of the request.
   * 	- `urlMatcher.find()`: This method checks if the URL pattern in the request matches
   * the pattern defined in the `ADD_AMENITY_REQUEST_PATTERN` variable.
   * 	- `isUserCommunityAdmin(request)`: This is a method that checks whether the current
   * user is an admin of a specific community, based on the incoming request. If the
   * user is not an admin, a `HttpServletResponse.SC_FORBIDDEN` status code is set and
   * the function returns.
   * 	- `super.doFilterInternal(request, response, chain)`: This is a call to the
   * superclass's `doFilterInternal` method, which handles the processing of the request
   * after the URL pattern has been checked.
   * 
   * @param response ServletResponse object that contains information about the HTTP
   * request and allows the filter to send a response to the client.
   * 
   * 1/ `HttpServletResponse`: This is an instance of the `HttpServletResponse` class,
   * which contains information about the current HTTP request and response. It has
   * properties such as `getStatus()`, `getHeader()`, `getMethod()`, and `getProtocol()`.
   * 2/ `status`: The `status` property of `response` represents the status code returned
   * by the server. In this function, it is set to `HttpServletResponse.SC_FORBIDDEN`,
   * indicating that the request was forbidden.
   * 3/ `Header`: The `Header` property of `response` contains a collection of header
   * fields associated with the current HTTP response. It may contain information such
   * as `Content-Type`, `Content-Length`, and `Expires`.
   * 4/ `Method`: The `Method` property of `response` represents the HTTP method (such
   * as GET, POST, PUT, DELETE) used in the current request.
   * 5/ `Protocol`: The `Protocol` property of `response` represents the protocol used
   * for the current HTTP request and response (such as HTTP/1.1 or HTTP/2).
   * 
   * @param chain FilterChain object that contains the next filter to be executed after
   * the current filter is applied.
   * 
   * 	- `HttpServletRequest request`: The current HTTP request received by the filter.
   * 	- `HttpServletResponse response`: The response object to which the filter will
   * write the output.
   * 	- `FilterChain chain`: An instance of `FilterChain`, which is a component of the
   * servlet's filter chain architecture. It represents the sequence of filters that
   * are executed in succession to handle an HTTP request. The current filter is
   * positioned at the end of this chain, and it can modify or remove elements from the
   * chain before passing the request on to the next filter or the servant.
   * 	- `IOException`, `ServletException`: Thrown if any I/O or servlet-specific exception
   * occurs during the filtration process.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    Matcher urlMatcher = ADD_AMENITY_REQUEST_PATTERN.matcher(request.getRequestURI());

    if (urlMatcher.find() && !isUserCommunityAdmin(request)) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    super.doFilterInternal(request, response, chain);
  }

  /**
   * determines if a user is a community admin based on their user ID and the community
   * ID in the request URL. It uses the `SecurityContextHolder` to retrieve the
   * authentication principal, splits the request URI into its components, retrieves
   * the community ID from the second component, and then queries the community service
   * for admins of that community. Finally, it checks if the user ID matches any of the
   * admin IDs in the query result.
   * 
   * @param request HTTP request object containing information about the current user
   * and their activity, which is used to determine if the user is an administrator of
   * a particular community.
   * 
   * 	- `request.getRequestURI()`: This property returns the requested URL of the
   * incoming HTTP request, which can be used to identify the community ID from the URL
   * path.
   * 	- `(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()`:
   * This property retrieves the currently authenticated user ID, which is used to
   * identify the user's role in the community.
   * 
   * @returns a boolean value indicating whether the current user is an admin of a
   * specified community.
   */
  private boolean isUserCommunityAdmin(HttpServletRequest request) {
    String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String communityId = request.getRequestURI().split("/")[2];

    return communityService.findCommunityAdminsById(communityId, null)
        .flatMap(admins -> admins.stream()
            .map(User::getUserId)
            .filter(userId::equals)
            .findFirst()
        )
        .isPresent();
  }
}
