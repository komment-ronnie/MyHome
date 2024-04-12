{"name":"FileUploadExceptionAdvice.java","path":"service/src/main/java/com/myhome/controllers/exceptionhandler/FileUploadExceptionAdvice.java","content":{"structured":{"description":"An exception handler for handling various exceptions, particularly MaxUploadSizeExceededException and IOException, in a Spring web application. The exception handler is defined using the @ControllerAdvice annotation and is responsible for generating responses to be returned to the client upon error. Specifically, it returns ResponseEntity objects with customized messages for each type of exception encountered.","items":[{"id":"3fa3ee0c-00bf-71af-1140-c69b9415b72e","ancestors":[],"type":"function","description":"is a custom exception handler for handling exceptions related to file uploads in a Spring Boot application. The class defines two exception handlers, one for MaxUploadSizeExceededException and another for IOException, which are used to handle different types of errors that can occur during file upload. When an exception occurs, the handler returns a ResponseEntity with a custom message for the user.","name":"FileUploadExceptionAdvice","code":"@ControllerAdvice\npublic class FileUploadExceptionAdvice {\n\n  @ExceptionHandler(MaxUploadSizeExceededException.class)\n  public ResponseEntity handleMaxSizeException(MaxUploadSizeExceededException exc) {\n    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new HashMap<String, String>() {{\n      put(\"message\", \"File size exceeds limit!\");\n    }});\n  }\n\n  @ExceptionHandler(IOException.class)\n  public ResponseEntity handleIOException(MaxUploadSizeExceededException exc) {\n    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HashMap<String, String>() {{\n      put(\"message\", \"Something go wrong with document saving!\");\n    }});\n  }\n}","location":{"start":27,"insert":27,"offset":" ","indent":0,"comment":null},"item_type":"class","length":17},{"id":"d4a65c98-a93d-f2bd-914c-7c4c6e9aab87","ancestors":["3fa3ee0c-00bf-71af-1140-c69b9415b72e"],"type":"function","description":"handles the `MaxUploadSizeExceededException` by returning a response entity with an error message.","params":[{"name":"exc","type_name":"MaxUploadSizeExceededException","description":"MaxUploadSizeExceededException thrown by the application.\n\n* `MaxUploadSizeExceededException`: This is the exception type that is being handled. It represents an error where the file size exceeds the allowed limit.\n* `HttpStatus.PAYLOAD_TOO_LARGE`: The HTTP status code returned in the response entity. It indicates that the file size exceeds the allowed limit.","complex_type":true}],"returns":{"type_name":"HttpStatus","description":"a `ResponseEntity` object with a status code of `PAYLOAD_TOO_LARGE` and a body containing a message indicating that the file size exceeds the limit.\n\n* The status code of the response entity is `HttpStatus.PAYLOAD_TOO_LARGE`, indicating that the file size exceeds the limit.\n* The body of the response entity contains a map with a single key-value pair, where the key is \"message\" and the value is a string containing the error message \"File size exceeds limit!\".","complex_type":true},"usage":{"language":"java","code":"@ControllerAdvice\npublic class FileUploadExceptionHandler {\n\n  @ExceptionHandler(MaxUploadSizeExceededException.class)\n  public ResponseEntity<String> handleFileSizeExceeded(MaxUploadSizeExceededException exc) {\n    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(\"File size exceeds limit!\");\n  }\n}\n","description":"\nIn the above example, the method handleFileSizeExceeded is annotated with @ExceptionHandler and it takes a MaxUploadSizeExceededException as its parameter. This means that when this method is called, it will catch any exceptions of type MaxUploadSizeExceededException and execute the code inside the method. The return statement returns an HTTP response with a status code of HttpStatus.PAYLOAD_TOO_LARGE and a body message \"File size exceeds limit!\" indicating that the file size has exceeded the maximum allowed size.\n\nTo use this method, it would be called like so:\n"},"name":"handleMaxSizeException","code":"@ExceptionHandler(MaxUploadSizeExceededException.class)\n  public ResponseEntity handleMaxSizeException(MaxUploadSizeExceededException exc) {\n    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new HashMap<String, String>() {{\n      put(\"message\", \"File size exceeds limit!\");\n    }});\n  }","location":{"start":30,"insert":30,"offset":" ","indent":2,"comment":null},"item_type":"method","length":6},{"id":"271ab5ad-0e45-7cb7-c24f-4af74b48b991","ancestors":["3fa3ee0c-00bf-71af-1140-c69b9415b72e"],"type":"function","description":"handles  an `IOException` exception by returning a response entity with a custom message.","params":[{"name":"exc","type_name":"MaxUploadSizeExceededException","description":"`MaxUploadSizeExceededException` exception that is being handled by the `@ExceptionHandler` annotation.\n\n* `MaxUploadSizeExceededException`: This exception is an extension of the `IOException` class and indicates that the maximum upload size has been exceeded while saving a document.","complex_type":true}],"returns":{"type_name":"HttpStatus","description":"a `ResponseEntity` with a status code of `CONFLICT` and a body containing a map with a single key-value pair, where the key is \"message\" and the value is \"Something went wrong with document saving!\".\n\n* The status code of the response is `HttpStatus.CONFLICT`, indicating an error in document saving.\n* The body of the response contains a map with a single key-value pair, where the key is \"message\" and the value is a string containing the message \"Something went wrong with document saving!\".","complex_type":true},"usage":{"language":"java","code":"@ExceptionHandler(IOException.class)\n  public ResponseEntity handleIOException(MaxUploadSizeExceededException exc) {\n    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HashMap<String, String>() {{\n      put(\"message\", \"Something go wrong with document saving!\");\n    }});\n  }\n","description":""},"name":"handleIOException","code":"@ExceptionHandler(IOException.class)\n  public ResponseEntity handleIOException(MaxUploadSizeExceededException exc) {\n    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HashMap<String, String>() {{\n      put(\"message\", \"Something go wrong with document saving!\");\n    }});\n  }","location":{"start":37,"insert":37,"offset":" ","indent":2,"comment":null},"item_type":"method","length":6}]}}}