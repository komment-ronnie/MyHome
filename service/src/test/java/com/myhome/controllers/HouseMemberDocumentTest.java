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

import com.myhome.domain.HouseMemberDocument;
import com.myhome.services.HouseMemberDocumentService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * is a test class for the House Member Document controller in a Spring Boot application.
 * The class contains tests for various use cases of the controller, including
 * retrieving a document, uploading a new document, updating an existing document,
 * deleting a document, and handling errors. The tests verifies that the controller
 * returns the expected status code and document content for each use case.
 */
class HouseMemberDocumentTest {

  private static final String MEMBER_ID = "test-member-id";

  private static final MockMultipartFile MULTIPART_FILE =
      new MockMultipartFile("memberDocument", new byte[0]);
  private static final HouseMemberDocument MEMBER_DOCUMENT =
      new HouseMemberDocument(MULTIPART_FILE.getName(), new byte[0]);

  @Mock
  private HouseMemberDocumentService houseMemberDocumentService;

  @InjectMocks
  private HouseMemberDocumentController houseMemberDocumentController;

  /**
   * initializes Mockito mocking utilizing the `MockitoAnnotations.initMocks()` method,
   * enabling the usage of mock objects during testing.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * verifies that the `getHouseMemberDocument` endpoint returns a successful response
   * with the expected document content and content type, and also verifies that the
   * `findHouseMemberDocument` method is called with the correct member ID.
   */
  @Test
  void shouldGetDocumentSuccess() {
    // given
    given(houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID))
        .willReturn(Optional.of(MEMBER_DOCUMENT));
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.getHouseMemberDocument(MEMBER_ID);
    //then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(MEMBER_DOCUMENT.getDocumentContent(), responseEntity.getBody());
    assertEquals(MediaType.IMAGE_JPEG, responseEntity.getHeaders().getContentType());
    verify(houseMemberDocumentService).findHouseMemberDocument(MEMBER_ID);
  }

  /**
   * tests whether the `getHouseMemberDocument` method returns a response with a
   * `HttpStatus.NOT_FOUND` status code when the house member document with the given
   * ID is not found in the database.
   */
  @Test
  void shouldGetDocumentFailure() {
    // given
    given(houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.getHouseMemberDocument(MEMBER_ID);
    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).findHouseMemberDocument(MEMBER_ID);
  }

  /**
   * verifies that uploading a house member document to the controller results in a
   * `NO_CONTENT` status code and the successful creation of the document in the service.
   */
  @Test
  void shouldPostDocumentSuccess() {
    // given
    given(houseMemberDocumentService.createHouseMemberDocument(MULTIPART_FILE, MEMBER_ID))
        .willReturn(Optional.of(MEMBER_DOCUMENT));
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.uploadHouseMemberDocument(MEMBER_ID, MULTIPART_FILE);
    //then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).createHouseMemberDocument(MULTIPART_FILE, MEMBER_ID);
  }

  /**
   * tests whether the `uploadHouseMemberDocument` method returns a `HttpStatus.NOT_FOUND`
   * status code when the document creation fails and no document is created in the database.
   */
  @Test
  void shouldPostDocumentFailureNotFound() {
    // given
    given(houseMemberDocumentService.createHouseMemberDocument(MULTIPART_FILE, MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.uploadHouseMemberDocument(MEMBER_ID, MULTIPART_FILE);
    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).createHouseMemberDocument(MULTIPART_FILE, MEMBER_ID);
  }

  /**
   * tests the `updateHouseMemberDocument` controller method by updating a house member
   * document and verifying that the document is updated successfully and the response
   * status code is `NO_CONTENT`.
   */
  @Test
  void shouldPutDocumentSuccess() {
    // given
    given(houseMemberDocumentService.updateHouseMemberDocument(MULTIPART_FILE, MEMBER_ID))
        .willReturn(Optional.of(MEMBER_DOCUMENT));
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.updateHouseMemberDocument(MEMBER_ID, MULTIPART_FILE);
    //then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).updateHouseMemberDocument(MULTIPART_FILE, MEMBER_ID);
  }

  /**
   * tests whether an update request for a house member document returns a
   * `HttpStatus.NOT_FOUND` status code when the document is not found in the service.
   */
  @Test
  void shouldPutDocumentFailureNotFound() {
    // given
    given(houseMemberDocumentService.updateHouseMemberDocument(MULTIPART_FILE, MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.updateHouseMemberDocument(MEMBER_ID, MULTIPART_FILE);
    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).updateHouseMemberDocument(MULTIPART_FILE, MEMBER_ID);
  }

  /**
   * tests the delete House Member Document controller by calling the delete method and
   * asserting that the response status code is HTTP status NO_CONTENT. Additionally,
   * it verifies that the house member document service was called with the correct
   * member ID.
   */
  @Test
  void shouldDeleteDocumentSuccess() {
    // given
    given(houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID))
        .willReturn(true);
    // when
    ResponseEntity responseEntity =
        houseMemberDocumentController.deleteHouseMemberDocument(MEMBER_ID);
    //then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).deleteHouseMemberDocument(MEMBER_ID);
  }

  /**
   * tests whether the `deleteHouseMemberDocument` method returns a `HttpStatus.NOT_FOUND`
   * status code when the document to be deleted is not found in the database.
   */
  @Test
  void shouldDeleteDocumentFailureNotFound() {
    // given
    given(houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID))
        .willReturn(false);
    // when
    ResponseEntity responseEntity =
        houseMemberDocumentController.deleteHouseMemberDocument(MEMBER_ID);
    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).deleteHouseMemberDocument(MEMBER_ID);
  }
}
