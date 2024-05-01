package com.myhome.security;

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
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * is a subclass of Servlet Filter that checks if the current user has administrative
 * privileges for a community based on the request URL. If not authorized, it sets
 * the status code to HTTP 401 Unauthorized and returns. Otherwise, it delegates to
 * the superclass's `doFilterInternal` method.
 */
public class CommunityAuthorizationFilter extends BasicAuthenticationFilter {
    private final CommunityService communityService;
    private final String uuidPattern = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private final Pattern addAdminRequestPattern = Pattern.compile("/communities/" + uuidPattern + "/admins");


    public CommunityAuthorizationFilter(AuthenticationManager authenticationManager,
                                        CommunityService communityService) {
        super(authenticationManager);
        this.communityService = communityService;
    }

    /**
     * in the given Java code checks if the request URI matches a pattern for administrative
     * requests and whether the user is an administrator or not. If the user is not an
     * administrator, it sets the response status to `SC_UNAUTHORIZED`. If the user is
     * an administrator, it delegates the processing to the parent `doFilterInternal`
     * method of the filter chain.
     * 
     * @param request HTTP request being processed by the filter.
     * 
     * 	- `request.getRequestURI()` returns the request URL, which is matched against the
     * pattern `addAdminRequestPattern`.
     * 	- `urlMatcher.find()` checks if the URL matches the pattern, and returns `true`
     * if it does.
     * 	- `!isUserCommunityAdmin(request)` checks whether the user has administrative
     * privileges for the community, and returns `true` if they do not have such privileges.
     * 
     * The remaining portion of the function is executed after these checks are performed.
     * 
     * @param response HttpServletResponse object that will be used to handle the filtered
     * request.
     * 
     * 	- `request`: The incoming HTTP request from the client, which is passed as an
     * argument to this method.
     * 	- `chain`: The next Servlet filter in the chain that should be executed.
     * 	- `isUserCommunityAdmin`: A boolean value indicating whether the user is a community
     * admin or not.
     * 	- `response`: The output response object, which may be modified by this method.
     * Its properties are:
     * 	+ `setStatus()`: Sets the status code of the response, which can be an HTTP status
     * code (e.g., 200, 404, etc.).
     * 	+ `getStatus()`: Returns the current status code of the response.
     * 
     * @param chain next filter in the filtering chain to be executed after the current
     * filter has completed its operation.
     * 
     * 	- `HttpServletRequest request`: The current HTTP request.
     * 	- `HttpServletResponse response`: The current HTTP response.
     * 	- `FilterChain chain`: A filter chain object that represents the sequence of
     * filters in the filter chain.
     * 
     * The `chain` object has various attributes and methods, including:
     * 
     * 	- `doFilter`(): Performs the actual filtering of the request.
     * 	- `getNext()`: Returns the next filter in the chain or `null` if there are no
     * more filters.
     * 	- `setNext()`: Sets the next filter in the chain.
     * 	- `add`(): Adds a new filter to the end of the chain.
     * 	- `remove`(): Removes a filter from the chain.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        Matcher urlMatcher = addAdminRequestPattern.matcher(request.getRequestURI());

        if (urlMatcher.find() && !isUserCommunityAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        super.doFilterInternal(request, response, chain);
    }

    /**
     * determines if a user is an administrator of a community based on their user ID and
     * the community ID in the request URL. It retrieves the list of community admins
     * from the database and filters them to find the user's admin status. If the user
     * is an admin, the function returns `true`, otherwise it returns `false`.
     * 
     * @param request HTTP request being processed and provides the community ID from the
     * request URI.
     * 
     * 	- `request`: A `HttpServletRequest` object representing an HTTP request.
     * 	- `getRequestURI()`: Returns the URI of the current request, without any path information.
     * 	- `split('/')`": Splits the URI into a array of sub-strings using the '/' character
     * as the delimiter.
     * 	- `findCommunityAdminsById(communityId, null)`: A method of the `communityService`
     * class that retrieves a list of community admins for a given community ID. The
     * `null` parameter represents the absence of any filters or sorting criteria.
     * 	- `optional`: An optional object representing the result of the method call, which
     * can be either `Optional<List<User>>` or `Optional<User>`.
     * 	- `get()`: Extracts the list of community admins from the optional object, if it
     * is not empty.
     * 	- `stream()`: Streams the list of community admins to filter out any admin whose
     * user ID does not match the authenticated user's ID.
     * 	- `findFirst()`: Finds the first admin in the streamed list that matches the
     * condition, or returns `null` if no such admin is found.
     * 	- `orElse(null)`: Returns the admin if a match is found, or `null` otherwise.
     * 
     * @returns a boolean value indicating whether the current user is an administrator
     * of a community based on their ID.
     */
    private boolean isUserCommunityAdmin(HttpServletRequest request) {
        String userId = (String) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String communityId = request
                .getRequestURI().split("/")[2];
        Optional<List<User>> optional = communityService
                .findCommunityAdminsById(communityId, null);

        if (optional.isPresent()) {
            List<User> communityAdmins = optional.get();
            User admin = communityAdmins
                    .stream()
                    .filter(communityAdmin -> communityAdmin.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            return admin != null;
        }

        return false;
    }
}