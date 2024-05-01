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

import javax.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

/**
 * is a Spring Boot configuration class that sets maximum file and request size limits
 * for multipart requests. The class creates a `MultipartConfig` instance with
 * customized maximum file and request sizes, allowing efficient handling of multipart
 * requests in a Spring Boot application.
 */
@Configuration
public class FileUploadConfig {

  @Value("${files.maxSizeKBytes}")
  private int maxSizeKBytes;

  /**
   * creates a `MultipartConfig` object with customized settings for maximum file and
   * request sizes, returning the created instance.
   * 
   * @returns a `MultipartConfig` object that configures the maximum file size and
   * request size for multipart requests.
   * 
   * 	- `MultipartConfigFactory`: This is the class that creates and manages multipart
   * requests in Spring WebFlux.
   * 	- `setMaxFileSize()`: This sets the maximum file size allowed in a multipart
   * request, measured in kilobytes (KB).
   * 	- `setMaxRequestSize()`: This sets the maximum request size allowed in a multipart
   * request, also measured in KB.
   * 	- `createMultipartConfig()`: This method creates a new instance of `MultipartConfig`
   * with the specified maximum file and request sizes.
   * 
   * Overall, this function is used to configure the maximum size of multipart requests
   * in Spring WebFlux.
   */
  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.ofKilobytes(maxSizeKBytes));
    factory.setMaxRequestSize(DataSize.ofKilobytes(maxSizeKBytes));
    return factory.createMultipartConfig();
  }
}
