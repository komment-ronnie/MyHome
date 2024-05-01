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

package com.myhome.services.unit;

import helpers.TestUtils;
import com.myhome.domain.HouseMember;
import com.myhome.domain.HouseMemberDocument;
import com.myhome.repositories.HouseMemberDocumentRepository;
import com.myhome.repositories.HouseMemberRepository;
import com.myhome.services.springdatajpa.HouseMemberDocumentSDJpaService;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * tests the behavior of the HouseMemberDocumentService class by providing various
 * input parameters and verifying the resulting output. The test cases cover scenarios
 * such as updating an existing member document, creating a new document for a
 * non-existent member, and saving a document that is too large to be saved. The tests
 * verify the correctness of the service's behavior and ensure that it preserves the
 * original member document in case of failure.
 */
public class HouseMemberDocumentServiceTest {

  private static final String MEMBER_ID = "test-member-id";
  private static final String MEMBER_NAME = "test-member-name";
  private static final HouseMemberDocument MEMBER_DOCUMENT =
      new HouseMemberDocument("test-file-name", new byte[0]);
  private static final int COMPRESSION_BORDER_SIZE_KB = 99;
  private static final int MAX_FILE_SIZE_KB = 1;
  private static final long COMPRESSED_IMAGE_QUALITY = (long) 0.99;

  @Mock
  private HouseMemberRepository houseMemberRepository;

  @Mock
  private HouseMemberDocumentRepository houseMemberDocumentRepository;

  @InjectMocks
  private HouseMemberDocumentSDJpaService houseMemberDocumentService;

  /**
   * initializes the House Member Document Service by setting instance fields for
   * compression border size, maximum file size, and compressed image quality.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(houseMemberDocumentService, "compressionBorderSizeKBytes",
        COMPRESSION_BORDER_SIZE_KB);
    ReflectionTestUtils.setField(houseMemberDocumentService, "maxFileSizeKBytes", MAX_FILE_SIZE_KB);
    ReflectionTestUtils.setField(houseMemberDocumentService, "compressedImageQuality",
        COMPRESSED_IMAGE_QUALITY);
  }

  /**
   * tests the `houseMemberDocumentService.findHouseMemberDocument()` method by providing
   * a valid `MEMBER_ID` and asserting that the resulting `Optional<HouseMemberDocument>`
   * is present and contains the expected `MEMBER_DOCUMENT`.
   */
  @Test
  void findMemberDocumentSuccess() {
    // given
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID);

    // then
    assertTrue(houseMemberDocument.isPresent());
    assertEquals(MEMBER_DOCUMENT, houseMemberDocument.get());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
  }

  /**
   * verifies that a House Member Document is not present for a given member ID by
   * asserting its absence and invoking the `houseMemberRepository` to retrieve the
   * member object.
   */
  @Test
  void findMemberDocumentNoDocumentPresent() {
    // given
    HouseMember testMember = new HouseMember(MEMBER_ID, null, MEMBER_NAME, null);
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
  }

  /**
   * tests whether a house member document does not exist for a given member ID. It
   * uses mocking to verify that the repository was called with the correct member ID
   * and returns an empty Optional.
   */
  @Test
  void findMemberDocumentMemberNotExists() {
    // given
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
  }

  /**
   * tests the deletion of a HouseMember document. It verifies that the document is
   * deleted and that the member object's document field is set to null after the
   * deletion is successful.
   */
  @Test
  void deleteMemberDocumentSuccess() {
    // given
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    // when
    boolean isDocumentDeleted = houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID);

    // then
    assertTrue(isDocumentDeleted);
    assertNull(testMember.getHouseMemberDocument());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberRepository).save(testMember);
  }

  /**
   * tests whether deleting a member document that does not exist returns false and
   * sets the member document to null, while also verifying the repository call and
   * never saving the updated member object.
   */
  @Test
  void deleteMemberDocumentNoDocumentPresent() {
    // given
    HouseMember testMember = new HouseMember(MEMBER_ID, null, MEMBER_NAME, null);
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    // when
    boolean isDocumentDeleted = houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID);

    // then
    assertFalse(isDocumentDeleted);
    assertNull(testMember.getHouseMemberDocument());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberRepository, never()).save(testMember);
  }

  /**
   * verifies that a house member document is not deleted if the member does not exist
   * in the repository.
   */
  @Test
  void deleteMemberDocumentMemberNotExists() {
    // given
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    boolean isDocumentDeleted = houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID);

    // then
    assertFalse(isDocumentDeleted);
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberRepository, never()).save(any());
  }

  /**
   * updates a house member's document in the database. It takes a new document file
   * as input, retrieves the member from the repository using the member ID, saves the
   * updated document to the database, and returns the updated document.
   */
  @Test
  void updateHouseMemberDocumentSuccess() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);
    MockMultipartFile newDocumentFile = new MockMultipartFile("new-test-file-name", imageBytes);
    HouseMemberDocument savedDocument =
        new HouseMemberDocument(String.format("member_%s_document.jpg", MEMBER_ID), imageBytes);
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    given(houseMemberDocumentRepository.save(savedDocument))
        .willReturn(savedDocument);
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.updateHouseMemberDocument(newDocumentFile, MEMBER_ID);

    // then
    assertTrue(houseMemberDocument.isPresent());
    assertEquals(testMember.getHouseMemberDocument(), houseMemberDocument.get());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository).save(savedDocument);
    verify(houseMemberRepository).save(testMember);
  }

  /**
   * updates a house member document with an image file for a member who does not exist
   * in the repository.
   */
  @Test
  void updateHouseMemberDocumentMemberNotExists() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);
    MockMultipartFile newDocumentFile = new MockMultipartFile("new-test-file-name", imageBytes);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.empty());

    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.updateHouseMemberDocument(newDocumentFile, MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository, never()).save(any());
    verify(houseMemberRepository, never()).save(any());
  }

  /**
   * updates an existing House Member Document with a too-large file, returns whether
   * the update was successful, and verifies certain method calls were made.
   */
  @Test
  void updateHouseMemberDocumentTooLargeFile() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(1000, 1000);
    MockMultipartFile tooLargeDocumentFile =
        new MockMultipartFile("new-test-file-name", imageBytes);
    HouseMemberDocument savedDocument =
        new HouseMemberDocument(String.format("member_%s_document.jpg", MEMBER_ID), imageBytes);
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    given(houseMemberDocumentRepository.save(savedDocument))
        .willReturn(savedDocument);
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.updateHouseMemberDocument(tooLargeDocumentFile, MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    assertEquals(testMember.getHouseMemberDocument(), MEMBER_DOCUMENT);
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository, never()).save(any());
    verify(houseMemberRepository, never()).save(any());
  }

  /**
   * creates a new House Member Document and saves it to the database while updating
   * the member's document filename if necessary.
   */
  @Test
  void createHouseMemberDocumentSuccess() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);
    HouseMemberDocument savedDocument =
        new HouseMemberDocument(String.format("member_%s_document.jpg", MEMBER_ID), imageBytes);
    MockMultipartFile newDocumentFile = new MockMultipartFile("new-test-file-name", imageBytes);
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    given(houseMemberDocumentRepository.save(savedDocument))
        .willReturn(savedDocument);
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.createHouseMemberDocument(newDocumentFile, MEMBER_ID);

    // then
    assertTrue(houseMemberDocument.isPresent());
    assertNotEquals(testMember.getHouseMemberDocument().getDocumentFilename(),
        MEMBER_DOCUMENT.getDocumentFilename());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository).save(savedDocument);
    verify(houseMemberRepository).save(testMember);
  }

  /**
   * tests the createHouseMemberDocument service by providing a file for a member who
   * does not exist in the House Member repository.
   */
  @Test
  void createHouseMemberDocumentMemberNotExists() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);
    MockMultipartFile newDocumentFile = new MockMultipartFile("new-test-file-name", imageBytes);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.createHouseMemberDocument(newDocumentFile, MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository, never()).save(any());
    verify(houseMemberRepository, never()).save(any());
  }

  /**
   * tests the create House Member Document service by providing a file that is too
   * large to be saved, and verifying that it returns an empty Optional and the original
   * document is not modified.
   */
  @Test
  void createHouseMemberDocumentTooLargeFile() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(1000, 1000);
    MockMultipartFile tooLargeDocumentFile =
        new MockMultipartFile("new-test-file-name", imageBytes);
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.createHouseMemberDocument(tooLargeDocumentFile, MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    assertEquals(testMember.getHouseMemberDocument(), MEMBER_DOCUMENT);
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository, never()).save(any());
    verify(houseMemberRepository, never()).save(any());
  }
}
