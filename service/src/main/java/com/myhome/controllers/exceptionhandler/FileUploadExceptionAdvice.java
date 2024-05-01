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
 * is a controller advice that handles exceptions related to file uploads, specifically
 * the `MaxUploadSizeExceededException` and `IOException`. The class returns a response
 * entity with an error message for the `MaxUploadSizeExceededException`, and a custom
 * message for the `IOException`.
 */
@ControllerAdvice
public class FileUploadExceptionAdvice {

  /**
   * processes a request that exceeds the maximum file size limit by returning a custom
   * error response with a descriptive message.
   * 
   * @param exc MaxUploadSizeExceededException object passed to the function.
   * 
   * 	- `MaxUploadSizeExceededException`: This is the class that `exc` instance belongs
   * to.
   * 	- `HttpStatus`: The HTTP status code associated with the exception, which is `PAYLOAD_TOO_LARGE`.
   * 	- `body`: A map containing a single key-value pair, where the key is "message"
   * and the value is a string representing the error message for the user.
   * 
   * @returns a `ResponseEntity` with a status code of `PAYLOAD_TOO_LARGE` and a body
   * containing a message indicating that the file size exceeds the limit.
   * 
   * 	- `HttpStatus`: The HTTP status code of the response entity, which is set to `PAYLOAD_TOO_LARGE`.
   * 	- `body`: A map containing a single key-value pair, where the key is `"message"`
   * and the value is a string containing the error message `"File size exceeds limit!"`.
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity handleMaxSizeException(MaxUploadSizeExceededException exc) {
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new HashMap<String, String>() {{
      put("message", "File size exceeds limit!");
    }});
  }

  /**
   * handles  an `IOException` exception by returning a response entity with a CONFLICT
   * status code and a custom message in the body.
   * 
   * @param exc `MaxUploadSizeExceededException` exception that is being handled by the
   * `@ExceptionHandler` annotation.
   * 
   * The `MaxUploadSizeExceededException` object provided to the function has several
   * attributes, including:
   * 
   * 	- `exc`: The original exception object that was caught and transformed into a
   * response entity.
   * 	- `HttpStatus`: The HTTP status code associated with the response entity. In this
   * case, it is set to `CONFLICT`.
   * 	- `body`: A map containing a single key-value pair: `message`, which holds the
   * error message to be returned in the response body.
   * 
   * @returns a `ResponseEntity` object with a status code of `CONFLICT` and a body
   * containing a map with a single key-value pair, where the key is "message" and the
   * value is a custom message indicating that something went wrong with document saving.
   * 
   * 	- The HTTP status code in the response entity is `HttpStatus.CONFLICT`, indicating
   * that an error occurred during document saving.
   * 	- The body of the response entity contains a map with a single key-value pair,
   * where the key is "message" and the value is a string containing the error message
   * ("Something went wrong with document saving!").
   */
  @ExceptionHandler(IOException.class)
  public ResponseEntity handleIOException(MaxUploadSizeExceededException exc) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HashMap<String, String>() {{
      put("message", "Something go wrong with document saving!");
    }});
  }
}

