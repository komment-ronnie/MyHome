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
 * is a configuration class that sets maximum file size and request size limits for
 * multipart requests in Spring Boot. The class creates a `MultipartConfig` instance
 * with configuration settings for maximum file size and request size, respectively.
 */
@Configuration
public class FileUploadConfig {

  @Value("${files.maxSizeKBytes}")
  private int maxSizeKBytes;

  /**
   * creates a `MultipartConfig` instance with customized maximum file and request
   * sizes, allowing for efficient handling of multipart requests in a Spring Boot application.
   * 
   * @returns a `MultipartConfig` object, which can be used to configure multipart form
   * data processing in a Spring Boot application.
   * 
   * The MultipartConfigFactory object creates a new instance of MultipartConfig, which
   * is an immutable configuration object that manages file uploads in a web application.
   * The setMaxFileSize() method sets the maximum size of files that can be uploaded
   * in kilobytes (KB), while the setMaxRequestSize() method sets the maximum total
   * size of all files and data in the request in KB.
   */
  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.ofKilobytes(maxSizeKBytes));
    factory.setMaxRequestSize(DataSize.ofKilobytes(maxSizeKBytes));
    return factory.createMultipartConfig();
  }
}
