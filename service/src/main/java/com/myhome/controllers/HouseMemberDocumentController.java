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

package com.myhome.controllers;

import com.myhome.api.DocumentsApi;
import com.myhome.domain.HouseMemberDocument;
import com.myhome.services.HouseMemberDocumentService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller which provides endpoints for managing house member documents
 */
/**
 * is responsible for handling requests related to house member documents, including
 * creating, updating, and deleting them. The controller uses dependency injection
 * to inject the HouseMemberDocumentService interface, which provides methods for
 * creating, updating, and deleting house member documents. The controller also returns
 * response entities with status codes indicating whether the operation was successful
 * or not.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class HouseMemberDocumentController implements DocumentsApi {

  private final HouseMemberDocumentService houseMemberDocumentService;

  /**
   * receives a `memberId` parameter and retrieves the corresponding house member
   * document from the service. It then returns the document content as a byte array
   * with appropriate HTTP headers.
   * 
   * @param memberId identifier of the member for whom the house member document is
   * being retrieved.
   * 
   * @returns a `ResponseEntity` object containing the requested document content as a
   * byte array, with appropriate headers and status code.
   * 
   * 	- `HttpHeaders headers`: This object represents the HTTP headers that will be
   * used to send the response. The `CacheControl` header is set to `noCache()` to
   * indicate that the document should not be cached by the client. The `ContentType`
   * header is set to `MediaType.IMAGE_JPEG` to specify the content type of the document.
   * 	- `byte[] content`: This is the actual document content, which is a byte array
   * representing the image data.
   * 	- `ContentDisposition contentDisposition`: This object represents the content
   * disposition of the response, which specifies the filename and other attributes of
   * the document. The `filename` attribute is set to the name of the document file.
   * 
   * The function returns a `ResponseEntity` object that contains the document content
   * and HTTP headers. The `ResponseEntity` object has three properties: the document
   * content, the HTTP headers, and the response status code. In this case, the status
   * code is set to `HttpStatus.OK`, indicating that the request was successful.
   */
  @Override
  public ResponseEntity<byte[]> getHouseMemberDocument(@PathVariable String memberId) {
    log.trace("Received request to get house member documents");
    Optional<HouseMemberDocument> houseMemberDocumentOptional =
        houseMemberDocumentService.findHouseMemberDocument(memberId);

    return houseMemberDocumentOptional.map(document -> {

      HttpHeaders headers = new HttpHeaders();
      byte[] content = document.getDocumentContent();

      headers.setCacheControl(CacheControl.noCache().getHeaderValue());
      headers.setContentType(MediaType.IMAGE_JPEG);

      ContentDisposition contentDisposition = ContentDisposition
          .builder("inline")
          .filename(document.getDocumentFilename())
          .build();

      headers.setContentDisposition(contentDisposition);

      return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * receives a request to upload a member document and creates a new house member
   * document if successful, otherwise returns a `NOT_FOUND` response.
   * 
   * @param memberId ID of the house member whose document is being uploaded.
   * 
   * @param memberDocument file containing the document to be added as a member of a
   * house, which is being passed through the `@RequestParam` annotation.
   * 
   * 	- `memberId`: The ID of the member whose document is being uploaded.
   * 	- `memberDocument`: A MultipartFile object containing the document to be uploaded
   * for the specified member.
   * 
   * @returns a `ResponseEntity` object with a status code indicating whether the
   * operation was successful or not.
   * 
   * 	- `ResponseEntity.status(HttpStatus.NO_CONTENT)`: This indicates that the operation
   * was successful and no content was returned to the client.
   * 	- `map()`: This method is used to map a single `Optional` value to a `ResponseEntity`.
   * If the `Optional` contains a value, it returns a `ResponseEntity` with a status
   * code of `NO_CONTENT`. If the `Optional` is empty, it returns a `ResponseEntity`
   * with a status code of `NOT_FOUND`.
   * 	- `orElseGet()`: This method is used to provide an alternative value if the `map()`
   * method does not produce a valid response. In this case, it returns a `ResponseEntity`
   * with a status code of `NOT_FOUND` if the `Optional` is empty.
   */
  @Override
  public ResponseEntity uploadHouseMemberDocument(
      @PathVariable String memberId, @RequestParam("memberDocument") MultipartFile memberDocument) {
    log.trace("Received request to add house member documents");

    Optional<HouseMemberDocument> houseMemberDocumentOptional =
        houseMemberDocumentService.createHouseMemberDocument(memberDocument, memberId);
    return houseMemberDocumentOptional
        .map(document -> ResponseEntity.status(HttpStatus.NO_CONTENT).build())
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * updates a house member's document based on the provided ID and multipart file. It
   * returns a response entity with a NO_CONTENT status code if the update is successful,
   * or a NOT_FOUND status code otherwise.
   * 
   * @param memberId unique identifier of the member whose document is being updated.
   * 
   * @param memberDocument file containing the updated house member document to be saved
   * into the database.
   * 
   * 	- `memberId`: A String representing the member's ID.
   * 	- `memberDocument`: A MultipartFile object containing the member's document to
   * be updated.
   * 
   * @returns a `ResponseEntity` object with a status code indicating whether the update
   * was successful or not.
   * 
   * 	- The `ResponseEntity` object represents the response to the update request, with
   * a status code indicating whether the operation was successful or not. In this case,
   * the status code is either `HttpStatus.NO_CONTENT`, indicating that the operation
   * was successful and no additional content was returned, or `HttpStatus.NOT_FOUND`,
   * indicating that the member document could not be found.
   * 	- The `map` method is used to transform the `Optional<HouseMemberDocument>` return
   * value into a `ResponseEntity` object. If the `Optional` is present, it contains
   * the updated house member document, and the response entity has a status code of
   * `HttpStatus.NO_CONTENT`. Otherwise, the response entity has a status code of `HttpStatus.NOT_FOUND`.
   * 	- The `orElseGet` method is used to provide an alternative response entity if the
   * `Optional` is empty. In this case, the alternative response entity has a status
   * code of `HttpStatus.NOT_FOUND`.
   */
  @Override
  public ResponseEntity updateHouseMemberDocument(
      @PathVariable String memberId, @RequestParam("memberDocument") MultipartFile memberDocument) {
    log.trace("Received request to update house member documents");
    Optional<HouseMemberDocument> houseMemberDocumentOptional =
        houseMemberDocumentService.updateHouseMemberDocument(memberDocument, memberId);
    return houseMemberDocumentOptional
        .map(document -> ResponseEntity.status(HttpStatus.NO_CONTENT).build())
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * deletes a house member document based on the provided `memberId`. If successful,
   * it returns a `ResponseEntity` with a status code of `NO_CONTENT`. Otherwise, it
   * returns a `ResponseEntity` with a status code of `NOT_FOUND`.
   * 
   * @param memberId unique identifier of the house member whose document is to be deleted.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the document was successfully deleted.
   * 
   * 	- `HttpStatus.NO_CONTENT`: indicates that the document was successfully deleted
   * 	- `HttpStatus.NOT_FOUND`: indicates that the document could not be found or was
   * not deletable
   */
  @Override
  public ResponseEntity<Void> deleteHouseMemberDocument(@PathVariable String memberId) {
    log.trace("Received request to delete house member documents");
    boolean isDocumentDeleted = houseMemberDocumentService.deleteHouseMemberDocument(memberId);
    if (isDocumentDeleted) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
