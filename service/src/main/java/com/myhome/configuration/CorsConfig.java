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

package com.myhome.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * is a Spring Configuration class that enables Cross-Origin Resource Sharing (CORS)
 * for a web application. It defines allowed origins, methods, headers, and credentials
 * for CORS mappings. The class also provides a bean for the WebMvcConfigurer interface
 * to add CORS mappings to the registry.
 */
/**
 * is a configuration class for CORS (Cross-Origin Resource Sharing) that allows
 * requests from any origin to access resources from the server. The class provides
 * methods for adding CORS mappings to a registry, allowing incoming requests from
 * any origin and specifying allowed methods, headers, and credentials.
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
   * adds CORS mappings to a registry, allowing incoming requests from any origin and
   * specifying allowed methods, headers, and credentials.
   * 
   * @returns a configuration for CORS mappings that allows incoming requests from any
   * origin and specifies allowed methods, headers, and credentials.
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
       * adds CORS mappings to a registry, allowing requests from any origin and specifying
       * allowed methods, headers, and credentials.
       * 
       * @param registry Cors registry that the method adds mappings to.
       * 
       * 	- `registry`: A `CorsRegistry` object that represents the configuration for
       * handling Cross-Origin Resource Sharing (CORS) requests.
       * 	- `addMapping`: Adds a mapping to the CORS configuration, allowing certain resources
       * to be accessed from specified origins using specific methods and headers.
       * 	- `allowedOrigins`: An array of allowed origins, which are IP addresses or
       * subdomains that can access the resources.
       * 	- `allowedMethods`: An array of allowed HTTP methods (GET, POST, PUT, DELETE,
       * etc.) that can be used to access the resources.
       * 	- `allowedHeaders`: An array of allowed headers that can be used with the accessed
       * resources.
       * 	- `exposedHeaders`: An array of exposed headers, which are headers that can be
       * accessed by clients in responses.
       * 	- `allowCredentials`: A boolean value indicating whether credentials (such as
       * cookies or Authorization headers) should be allowed for the CORS configuration.
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
