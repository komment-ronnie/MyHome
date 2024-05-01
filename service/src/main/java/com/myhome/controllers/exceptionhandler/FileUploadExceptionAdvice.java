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
 * processes requests that exceed maximum file size limits by returning custom error
 * responses with descriptive messages. It also handles IOExceptions by returning
 * response entities with custom messages indicating something went wrong with document
 * saving.
 */
@ControllerAdvice
public class FileUploadExceptionAdvice {

  /**
   * generates a response entity with an error message when a file size exceeds the limit.
   * 
   * @param exc MaxUploadSizeExceededException object passed to the function as a parameter.
   * 
   * 	- `exc`: A `MaxUploadSizeExceededException` object containing information about
   * the exceeding file size limit.
   * 
   * The response entity is created with a HTTP status code `PAYLOAD_TOO_LARGE`, and a
   * body containing a map with a single entry: `message`. The value of this entry is
   * set to "File size exceeds limit!".
   * 
   * @returns a response entity with a status code of `PAYLOAD_TOO_LARGE` and a body
   * containing a message indicating that the file size exceeds the limit.
   * 
   * 	- `HttpStatus`: The status code of the response entity, which is set to `PAYLOAD_TOO_LARGE`.
   * 	- `body`: A map containing a single key-value pair, where the key is `"message"`
   * and the value is a string representing a message indicating that the file size
   * exceeds the limit.
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity handleMaxSizeException(MaxUploadSizeExceededException exc) {
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new HashMap<String, String>() {{
      put("message", "File size exceeds limit!");
    }});
  }

  /**
   * handles  an IOException exception by returning a ResponseEntity with a CONFLICT
   * status code and a message in a HashMap.
   * 
   * @param exc `MaxUploadSizeExceededException` that is to be handled by the
   * `@ExceptionHandler` annotation.
   * 
   * 	- `MaxUploadSizeExceededException`: The exception class representing an issue
   * with document saving due to exceeding the maximum upload size limit.
   * 
   * @returns a `ResponseEntity` object with a status code of `HttpStatus.CONFLICT` and
   * a body containing a map with a single entry containing a message.
   * 
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response object that can be used to handle HTTP responses.
   * 	- `status`: This property is an instance of the `HttpStatus` class, which represents
   * the HTTP status code of the response. In this case, the status code is `CONFLICT`.
   * 	- `body`: This property is a map of key-value pairs, where each key is a string
   * and each value is also a string. In this case, the map contains one entry with the
   * key "message" and the value "Something went wrong with document saving!".
   */
  @ExceptionHandler(IOException.class)
  public ResponseEntity handleIOException(MaxUploadSizeExceededException exc) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HashMap<String, String>() {{
      put("message", "Something go wrong with document saving!");
    }});
  }
}

