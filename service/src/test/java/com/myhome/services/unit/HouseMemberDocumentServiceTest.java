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
 * tests various scenarios related to updating and creating House Member Documents.
 * The tests cover updates with an image file for a member who exists in the repository,
 * updates with an image file for a member who does not exist in the repository,
 * updates with a too-large file, and creates a new document for a member who does
 * not exist in the repository. The tests verify that the service returns the correct
 * documents and modifies the original documents as expected.
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
   * initializes mocks and sets fields for the `HouseMemberDocumentService` class,
   * including the compression border size, maximum file size, and compressed image quality.
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
   * tests the `findHouseMemberDocument` service by providing a valid member ID and
   * verifying that the correct document is returned.
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
   * verifies that no document is present for a given member ID by querying the repository
   * and verifying the result with the service.
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
   * verifies that a House Member Document does not exist for a given member ID by
   * calling the `houseMemberDocumentService.findHouseMemberDocument` method and asserting
   * that the result is an empty Optional.
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
   * deletes a House Member's document given its member ID, and verifies the document's
   * deletion and the updated state of the member object in the repository.
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
   * tests the delete House Member Document method when no document is present for the
   * given member ID. It verifies that the method returns false and sets the document
   * to null, and also verifies the calls to the repository for finding and saving the
   * member.
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
   * verifies that a document associated with a member does not exist when deleting it
   * using the `houseMemberDocumentService`.
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
   * updates a member's document in the database by retrieving the existing member
   * document, replacing it with the new image file, and saving the updated document.
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
   * tests whether the `HouseMemberDocument` service updates a document for a member
   * who does not exist in the repository.
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
   * updates a member's document with a file that is too large, returning an
   * Optional<HouseMemberDocument> indicating whether the update was successful.
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
   * creates a new House Member Document and saves it to the repository while updating
   * the existing member document with a new filename.
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
   * verifies that a HouseMemberDocument is not created when the member does not exist
   * in the repository.
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
   * tests the `houseMemberDocumentService` by creating a mock `MockMultipartFile` with
   * an image file that is too large and checking if the service creates a new document
   * or not.
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
