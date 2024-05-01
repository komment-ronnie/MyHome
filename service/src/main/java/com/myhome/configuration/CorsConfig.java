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
 * is a configuration class for handling Cross-Origin Resource Sharing (CORS) requests
 * in a web application. It allows incoming requests from any origin and specifies
 * allowed methods, headers, and credentials. The class provides a method to add CORS
 * mappings to a registry, allowing requests from any origin and specifying allowed
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
   * adds CORS mappings to a registry, allowing requests from any origin and specifying
   * allowed methods, headers, and credentials.
   * 
   * @returns a set of CORS mappings that allow requests from any origin and specify
   * allowed methods, headers, and credentials.
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
       * which methods, headers, and credentials are allowed for all resources.
       * 
       * @param registry Cors registry that the method adds mappings to.
       * 
       * 	- `registry`: This is an instance of the `CorsRegistry` class, which contains
       * metadata about CORS (Cross-Origin Resource Sharing) settings for a particular resource.
       * 	- `addMapping`: This method adds a mapping to the registry, specifying the URL
       * pattern that the mapping applies to. In this case, the mapping is applied to all
       * URLs (`"/**"`).
       * 	- `allowedOrigins`: An array of strings representing the origins (domain names
       * or IP addresses) from which the requested resource can be accessed.
       * 	- `allowedMethods`: An array of strings representing the HTTP methods (such as
       * GET, POST, PUT, DELETE, etc.) that are allowed to access the resource. In this
       * case, all methods are allowed.
       * 	- `allowedHeaders`: An array of strings representing the headers that are allowed
       * to be exposed in responses from the resource. In this case, two headers (`token`
       * and `userId`) are allowed.
       * 	- `allowCredentials`: A boolean value indicating whether the resource supports
       * CORS credentials (such as cookies, tokens, or HTTP authorization). In this case,
       * the value is `true`.
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
