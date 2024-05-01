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
 * :
 * 
 * 	- Extends the `FilterChain` class and provides a way to access previous filters
 * in the chain.
 * 	- Uses the `addAmnestyRequestPattern()` method to match requests based on a pattern
 * that includes the community ID in the request URI.
 * 	- Checks if the current user is an admin of a specific community by querying the
 * community service and checking if their ID exists in a list of admins for that community.
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
   * in a Servlet Filter performs an internal filter operation based on the request URI
   * and user's role. If the request URI matches a specific pattern, it checks if the
   * user is an admin for a particular community and sets a HTTP Status Code accordingly.
   * If not, it calls the superclass' `doFilterInternal` method to continue the filtering
   * process.
   * 
   * @param request HTTP request being processed by the filter.
   * 
   * 	- `getRequestURI()` returns the requested resource path, which is matched against
   * a pattern using the `Matcher` object `ADD_AMENITY_REQUEST_PATTERN`.
   * 	- `isUserCommunityAdmin(request)` checks whether the user is an administrator of
   * a community, and if not, responds with a forbidden status code.
   * 	- `super.doFilterInternal(request, response, chain)` calls the parent class's
   * implementation of the `doFilterInternal` method.
   * 
   * @param response HTTP response object that is being filtered by the `doFilterInternal`
   * method.
   * 
   * 	- `HttpServletResponse response`: This is an instance of `HttpServletResponse`,
   * which represents the HTTP response object for the current request. It provides
   * various attributes and methods for handling HTTP requests and responses.
   * 	- `status`: This attribute indicates the status code of the response, which can
   * be one of the values in the `HttpServletResponse.SC_` constant class (e.g.,
   * `HttpServletResponse.SC_OK`, `HttpServletResponse.SC_FORBIDDEN`, etc.).
   * 	- `ServletException`: This attribute represents any exception thrown during the
   * handling of the request, which can be caught and handled by the `doFilterInternal`
   * function or propagated to the caller.
   * 
   * @param chain next filter in the chain that should be applied to the request after
   * the current filter has been executed.
   * 
   * 	- `HttpServletRequest request`: The incoming HTTP request.
   * 	- `HttpServletResponse response`: The outgoing HTTP response.
   * 	- `FilterChain chain`: The filter chain that triggered this function's execution.
   * It can be modified or destroyed within the function.
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
   * verifies if a user is an administrator of a specific community based on the current
   * SecurityContext and the request URI. It queries the community service to find
   * admins for the community ID and checks if the user ID matches any of them.
   * 
   * @param request HTTP request object containing information about the current user
   * and their actions, which is used to determine if the user is a community administrator.
   * 
   * 	- `getRequestURI()` returns the requested URL of the request.
   * 	- `split("/")` splits the URL into its component parts (such as scheme, host,
   * port, and path). The second element in the resulting array is the community ID.
   * 
   * @returns a boolean value indicating whether the current user is an admin of a
   * specific community.
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
