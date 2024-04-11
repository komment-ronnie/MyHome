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

package com.myhome.controllers.exceptionhandler;

import java.io.IOException;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * is a custom exception handler for handling exceptions related to file uploads in
 * a Spring Boot application. The class defines two exception handlers, one for
 * MaxUploadSizeExceededException and another for IOException, which are used to
 * handle different types of errors that can occur during file upload. When an exception
 * occurs, the handler returns a ResponseEntity with a custom message for the user.
 */
@ControllerAdvice
public class FileUploadExceptionAdvice {

  /**
   * handles the `MaxUploadSizeExceededException` by returning a response entity with
   * an error message.
   * 
   * @param exc MaxUploadSizeExceededException thrown by the application.
   * 
   * 	- `MaxUploadSizeExceededException`: This is the exception type that is being
   * handled. It represents an error where the file size exceeds the allowed limit.
   * 	- `HttpStatus.PAYLOAD_TOO_LARGE`: The HTTP status code returned in the response
   * entity. It indicates that the file size exceeds the allowed limit.
   * 
   * @returns a `ResponseEntity` object with a status code of `PAYLOAD_TOO_LARGE` and
   * a body containing a message indicating that the file size exceeds the limit.
   * 
   * 	- The status code of the response entity is `HttpStatus.PAYLOAD_TOO_LARGE`,
   * indicating that the file size exceeds the limit.
   * 	- The body of the response entity contains a map with a single key-value pair,
   * where the key is "message" and the value is a string containing the error message
   * "File size exceeds limit!".
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity handleMaxSizeException(MaxUploadSizeExceededException exc) {
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new HashMap<String, String>() {{
      put("message", "File size exceeds limit!");
    }});
  }

  /**
   * handles  an `IOException` exception by returning a response entity with a custom
   * message.
   * 
   * @param exc `MaxUploadSizeExceededException` exception that is being handled by the
   * `@ExceptionHandler` annotation.
   * 
   * 	- `MaxUploadSizeExceededException`: This exception is an extension of the
   * `IOException` class and indicates that the maximum upload size has been exceeded
   * while saving a document.
   * 
   * @returns a `ResponseEntity` with a status code of `CONFLICT` and a body containing
   * a map with a single key-value pair, where the key is "message" and the value is
   * "Something went wrong with document saving!".
   * 
   * 	- The status code of the response is `HttpStatus.CONFLICT`, indicating an error
   * in document saving.
   * 	- The body of the response contains a map with a single key-value pair, where the
   * key is "message" and the value is a string containing the message "Something went
   * wrong with document saving!".
   */
  @ExceptionHandler(IOException.class)
  public ResponseEntity handleIOException(MaxUploadSizeExceededException exc) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HashMap<String, String>() {{
      put("message", "Something go wrong with document saving!");
    }});
  }
}

