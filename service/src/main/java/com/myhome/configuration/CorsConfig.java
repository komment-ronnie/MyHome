
package com.myhome.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Is used to configure CORS (Cross-Origin Resource Sharing) settings for a web
 * application. It allows requests from specified origins and specifies allowed HTTP
 * methods, headers, and credentials. The class provides a bean that adds CORS mappings
 * to the registry, allowing incoming requests from any origin and specifying allowed
 * methods, headers, and credentials.
 */
@Configuration
public class CorsConfig {

  @Value("${server.cors.allowedOrigins}")
  private String[] allowedOrigins;

  /**
   * configure CORS settings for a web application, allowing requests from specified
   * origins and methods, as well as specifying which headers to expose and whether
   * credentials should be included.
   * 
   * @returns a configuration for CORS (Cross-Origin Resource Sharing) that allows
   * requests from any origin to access resources from the server.
   * 
   * 	- `registry`: The `CorsRegistry` object that is being modified with the CORS mappings.
   * 	- `addCorsMappings()`: The method being called on the `registry` object to add
   * CORS mappings.
   * 	- `allowedOrigins`: An array of strings containing the allowed origins for the
   * CORS mappings.
   * 	- `allowedMethods`: An array of strings containing the allowed HTTP methods for
   * the CORS mappings.
   * 	- `allowedHeaders`: An array of strings containing the allowed headers for the
   * CORS mappings.
   * 	- `exposedHeaders`: An array of strings containing the headers that are exposed
   * to the client.
   * 	- `allowCredentials()`: A boolean value indicating whether credentials (e.g.,
   * cookies, authorization) should be allowed for the CORS mappings.
   */
  /**
   * Allows incoming requests from any origin and specifies allowed methods, headers,
   * and credentials by adding CORS mappings to a registry. It sets all origins as
   * allowed, all HTTP methods, all headers, exposes specific headers, and allows
   * credentials for making requests to the server.
   * 
   * @returns a configuration for CORS mappings.
   * 
   * * It is an instance of `WebMvcConfigurer`, which represents a configuration for a
   * Web application.
   * * The output is used to configure CORS (Cross-Origin Resource Sharing) in the application.
   * * The main attributes are:
   *   - `allowedOrigins`: An array of allowed origins, specifying the domains or
   * subdomains that can make CORS requests to the server.
   *   - `allowedMethods`: An array of allowed HTTP methods, specifying the methods
   * that can be used in CORS requests.
   *   - `allowedHeaders`: An array of allowed headers, specifying the headers that can
   * be included in CORS responses from the server.
   *   - `exposedHeaders`: An array of exposed headers, specifying the headers that can
   * be included in CORS responses and are visible to clients.
   *   - `allowCredentials`: A boolean value indicating whether CORS requests with
   * credentials (such as cookies or authentication tokens) are allowed.
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      /**
       * adds CORS mappings to a registry, allowing requests from any origin and specifying
       * allowed methods, headers, and credentials.
       * 
       * @param registry Cors registry that the method adds mappings to.
       * 
       * 	- `registry`: This is an instance of `CorsRegistry`, which represents the set of
       * CORS mappings for a server.
       * 	- `addMapping`: This method adds a new mapping to the existing set of mappings
       * in `registry`. The mapping includes the URL pattern `"**"`, which means that the
       * mapping applies to all URLs.
       * 	- `allowedOrigins`: An array of allowed origins, which specifies the domains or
       * subdomains that are allowed to make CORS requests to the server.
       * 	- `allowedMethods`: An array of allowed HTTP methods, which specifies the methods
       * that are allowed to be used in CORS requests to the server. The value `"*" means
       * that all methods are allowed.
       * 	- `allowedHeaders`: An array of allowed headers, which specifies the headers that
       * can be included in CORS responses from the server. The value `"*" means that all
       * headers are allowed.
       * 	- `exposedHeaders`: An array of exposed headers, which specifies the headers that
       * can be included in CORS responses from the server and are visible to clients. The
       * values `"token"` and `"userId"` indicate that these two specific headers are exposed.
       * 	- `allowCredentials`: A boolean value that indicates whether CORS requests with
       * credentials (such as cookies or authentication tokens) are allowed.
       */
      /**
       * Enables Cross-Origin Resource Sharing (CORS) for a Spring Boot application, allowing
       * requests from specified origins to access resources. It defines global CORS settings
       * for all URLs by mapping "/**" and specifying allowed origins, methods, headers,
       * and exposed headers.
       * 
       * @param registry configuration object that allows to add and manage CORS (Cross-Origin
       * Resource Sharing) configurations for the Spring application.
       */
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(allowedOrigins)
            .allowedMethods("*")
            .allowedHeaders("*")
            .exposedHeaders("token", "userId")
            .allowCredentials(true);
      }
    };
  }
}
