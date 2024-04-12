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
 * provides endpoints for managing house member documents. The class has methods for
 * getting, updating, and deleting house member documents, along with handling HTTP
 * responses.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class HouseMemberDocumentController implements DocumentsApi {

  private final HouseMemberDocumentService houseMemberDocumentService;

  /**
   * retrieves a house member document from the service and returns it as a byte array
   * in the response entity with appropriate headers for caching and content type.
   * 
   * @param memberId ID of the member for whom the corresponding house member document
   * is being retrieved.
   * 
   * @returns a `ResponseEntity` object containing the requested document content as a
   * byte array and HTTP headers.
   * 
   * 	- `HttpHeaders headers`: This contains metadata about the response, such as caching
   * directives and content type.
   * 	- `byte[] content`: The actual document content in byte form.
   * 	- `ContentDisposition contentDisposition`: Contains information about how to
   * display or handle the response, such as filename and inline/attachment status.
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
   * receives a request to upload a house member document, creates a new document using
   * the provided file and member ID, and returns a response entity indicating whether
   * the operation was successful or not.
   * 
   * @param memberId ID of the house member whose document is being uploaded.
   * 
   * @param memberDocument file containing the member's document to be uploaded.
   * 
   * 	- `@PathVariable String memberId`: The unique identifier for the house member
   * whose document is being uploaded.
   * 	- `@RequestParam("memberDocument") MultipartFile memberDocument`: The file
   * containing the house member's document, which can be either a PDF or JPEG image.
   * 
   * @returns a `ResponseEntity` object with a status code indicating whether the
   * document was successfully uploaded or not.
   * 
   * 	- `ResponseEntity.status(HttpStatus.NO_CONTENT)`: This indicates that the operation
   * was successful and no content was returned to the client.
   * 	- `ResponseEntity.status(HttpStatus.NOT_FOUND)`: This indicates that the house
   * member document could not be found, likely because it does not exist or has been
   * deleted.
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
   * receives a request to update a house member's document and updates the corresponding
   * document in the database using the provided document and member ID. If the document
   * is successfully updated, a NO_CONTENT status code is returned. If the document
   * cannot be found or the request fails, a NOT_FOUND status code is returned.
   * 
   * @param memberId unique identifier of the member whose document is being updated.
   * 
   * @param memberDocument document to be updated for the corresponding member ID.
   * 
   * 	- `memberId`: The ID of the house member whose document is being updated.
   * 	- `memberDocument`: A MultipartFile containing the updated document for the house
   * member.
   * 
   * @returns a response entity with a status code of NO_CONTENT or NOT_FOUND, depending
   * on whether the update was successful.
   * 
   * 	- `ResponseEntity.status(HttpStatus.NO_CONTENT).build()`: This is a response
   * entity with a status code of NO_CONTENT, indicating that the update was successful
   * and no additional content was returned.
   * 	- `ResponseEntity.status(HttpStatus.NOT_FOUND).build()`: This is a response entity
   * with a status code of NOT_FOUND, indicating that the requested member document
   * could not be found.
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
   * deletes a house member document based on the provided member ID, returning a HTTP
   * status code indicating whether the operation was successful or not.
   * 
   * @param memberId ID of a house member whose document is to be deleted.
   * 
   * @returns a `ResponseEntity` with a status code of either `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the document was successfully deleted or not.
   * 
   * 	- `HttpStatus.NO_CONTENT`: This status code indicates that the requested resource
   * has been successfully deleted and no content was returned.
   * 	- `HttpStatus.NOT_FOUND`: This status code indicates that the requested house
   * member document could not be found, which means it may have been deleted or never
   * existed in the first place.
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
