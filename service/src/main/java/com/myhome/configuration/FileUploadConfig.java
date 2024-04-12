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
 * is a configuration class that enables the maximum file size and request size for
 * multipart requests in Spring Boot. The class has one field and one method: the
 * field stores the maximum size of files in kilobytes, and the method creates a
 * MultipartConfigElement instance with the set maximum file size and request size.
 */
@Configuration
public class FileUploadConfig {

  @Value("${files.maxSizeKBytes}")
  private int maxSizeKBytes;

  /**
   * creates a `MultipartConfig` object for use in Spring WebFlux. The created config
   * element sets maximum file size and request size limits in kilobytes, respectively.
   * 
   * @returns a `MultipartConfig` instance with configuration settings for maximum file
   * size and request size.
   * 
   * 	- `MultipartConfigFactory`: This is the class that provides methods for configuring
   * multipart content.
   * 	- `setMaxFileSize()` and `setMaxRequestSize()`: These two methods set the maximum
   * file size and maximum request size for multipart content, respectively. The values
   * are specified in kilobytes (KB).
   * 
   * The output of this function is a `MultipartConfig` object, which represents the
   * configuration for handling multipart content.
   */
  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.ofKilobytes(maxSizeKBytes));
    factory.setMaxRequestSize(DataSize.ofKilobytes(maxSizeKBytes));
    return factory.createMultipartConfig();
  }
}
