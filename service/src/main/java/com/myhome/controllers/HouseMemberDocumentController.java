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
 * is responsible for handling requests related to house member documents. The
 * controller receives requests to get, upload, update, and delete house member
 * documents. It uses the `HouseMemberDocumentService` class to perform these operations
 * and returns response entities with a status code indicating whether the operation
 * was successful or not.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class HouseMemberDocumentController implements DocumentsApi {

  private final HouseMemberDocumentService houseMemberDocumentService;

  /**
   * receives a member ID as path variable and retrieves the corresponding house member
   * document from the service. It then returns the document content as byte array with
   * appropriate headers.
   * 
   * @param memberId ID of the house member for whom the document is being retrieved.
   * 
   * @returns a response entity with the requested house member document content.
   * 
   * 	- The input parameter `memberId` represents the unique identifier of a house member.
   * 	- The `Optional<HouseMemberDocument>` object returned by the
   * `houseMemberDocumentService.findHouseMemberDocument()` method contains the document
   * associated with the provided `memberId`, if found.
   * 	- The `HttpHeaders` object `headers` is set with various properties:
   * 	+ `CacheControl`: sets the cache control to `no-cache`.
   * 	+ `ContentType`: sets the content type to `image/jpeg`.
   * 	+ `ContentDisposition`: sets the content disposition to `inline`, with a filename
   * of the document's original name.
   * 	- The `ResponseEntity` object returned by the `orElseGet()` method contains the
   * following properties:
   * 	+ `content`: contains the actual document content as a byte array.
   * 	+ `headers`: contains the set of headers described above.
   * 	+ `statusCode`: contains the HTTP status code `OK`.
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
   * receives a request to add a house member document, creates a new document using
   * the provided multipart file and member ID, and returns a response entity indicating
   * whether the operation was successful or not.
   * 
   * @param memberId unique identifier of the member whose document is being uploaded.
   * 
   * @param memberDocument document to be added or updated for the specified house member.
   * 
   * 	- `memberId`: The identifier of the house member whose document is being uploaded.
   * 	- `memberDocument`: A `MultipartFile` object representing the document to be uploaded.
   * 
   * @returns a response entity with a HTTP status code of NO_CONTENT or NOT_FOUND,
   * depending on whether a house member document was successfully created.
   * 
   * 	- The first part of the output is `ResponseEntity`, which represents an HTTP
   * response entity. This entity has a status code that indicates whether the request
   * was successful or not. In this case, if the house member document is created
   * successfully, the status code will be `HttpStatus.NO_CONTENT`. Otherwise, it will
   * be `HttpStatus.NOT_FOUND`.
   * 	- The `map` method is used to transform the `Optional<HouseMemberDocument>` into
   * a `ResponseEntity` object. This method takes a function that returns a `ResponseEntity`
   * object, and applies it to the `Optional` object if it is present. If the `Optional`
   * object is empty, the method returns an empty `ResponseEntity` object with a status
   * code of `HttpStatus.NOT_FOUND`.
   * 	- The `orElseGet` method is used as a fallback in case the `map` method does not
   * produce a valid response entity. It takes a function that returns a `ResponseEntity`
   * object, and applies it to an empty `Optional` object. This means that if there is
   * no house member document to return, the function will return an empty `ResponseEntity`
   * object with a status code of `HttpStatus.NOT_FOUND`.
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
   * first checks if the update was successful using the `houseMemberDocumentService`,
   * and then returns a response entity indicating whether the update was successful
   * or not.
   * 
   * @param memberId identifier of the house member whose document is being updated.
   * 
   * @param memberDocument file containing the updated house member document to be saved
   * into the database by the `houseMemberDocumentService`.
   * 
   * 	- `memberId`: A String representing the unique identifier of the member whose
   * document is being updated.
   * 	- `memberDocument`: A MultipartFile object containing the document to be updated
   * for the specified member.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the update was successful or not.
   * 
   * 	- `map`: This method is used to map the `Optional<HouseMemberDocument>` result
   * to a `ResponseEntity`. If the `Optional` is present, it contains the updated House
   * Member Document, and the response entity is created with a status code of `NO_CONTENT`.
   * If the `Optional` is absent, the response entity is created with a status code of
   * `NOT_FOUND`.
   * 	- `orElseGet`: This method is used to provide an alternative implementation of
   * the `map` method if the `Optional` is empty. In this case, the alternative
   * implementation creates a `ResponseEntity` with a status code of `NOT_FOUND`.
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
   * it returns a `ResponseEntity` with a status code of `NO_CONTENT`. If unsuccessful,
   * it returns a `ResponseEntity` with a status code of `NOT_FOUND`.
   * 
   * @param memberId ID of a house member whose document will be deleted.
   * 
   * @returns a `ResponseEntity` with a `NO_CONTENT` status code if the document was
   * successfully deleted, or a `NOT_FOUND` status code otherwise.
   * 
   * 	- `isDocumentDeleted`: This boolean variable indicates whether the house member
   * document has been deleted successfully or not. If it is true, the document has
   * been deleted. Otherwise, it means that the document could not be deleted for some
   * reason.
   * 	- `HttpStatus`: The status code of the response entity, which can be either
   * NO_CONTENT (204) if the document was deleted successfully or NOT_FOUND (404) if
   * the document could not be found.
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
