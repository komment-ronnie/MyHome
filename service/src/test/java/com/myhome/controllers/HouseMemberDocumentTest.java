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
 * is a unit test for the HouseMemberDocumentController class. It verifies various
 * scenarios related to uploading, downloading, updating, and deleting house member
 * documents, including successful responses, errors, and missing documents. The test
 * class uses JUnit to run the tests and verify the correct behavior of the controller.
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
   * initializes mock objects using MockitoAnnotations, making the class mock-friendly
   * for testing purposes.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * tests the `getHouseMemberDocument` method of the `HouseMemberDocumentController`.
   * It verifies that the method returns a response with the correct status code,
   * document content, and content type when the house member document is found.
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
   * tests whether calling `getHouseMemberDocument()` on a non-existent member ID returns
   * a `HttpStatus.NOT_FOUND` response and verifies the call to `findHouse MemberDocumentService`.
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
   * tests whether uploading a house member document via the controller returns a
   * successful response with no content and verifies that the service creates the
   * document successfully.
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
   * verifies that when a document is uploaded but cannot be created due to an error,
   * the API returns a `HttpStatus.NOT_FOUND` response and simulates the creation of
   * the document using the given `houseMemberDocumentService`.
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
   * tests the updateHouseMemberDocument method by providing a multipart file and
   * verifying that the house member document is updated successfully, with the correct
   * status code and service call executed.
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
   * verifies that if the update House Member Document operation fails due to a document
   * not found exception, then the response status code is set to `HttpStatus.NOT_FOUND`.
   * Additionally, it verifies that the `houseMemberDocumentService.updateHouseMemberDocument()`
   * method is called with the correct parameters.
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
   * tests the delete House Member Document controller's functionality by given a member
   * ID and verifying that when the controller deletes the document, the response status
   * code is NO_CONTENT and the house Member Document Service method is called.
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
   * tests the behavior of the `houseMemberDocumentController` when the document to be
   * deleted cannot be found. It uses mocked dependencies to simulate a `false` return
   * value from the `deleteHouseMemberDocument` method and asserts that the resulting
   * response entity has a status code of `HttpStatus.NOT_FOUND`.
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
