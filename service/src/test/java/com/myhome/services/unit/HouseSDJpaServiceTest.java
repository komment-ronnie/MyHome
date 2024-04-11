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
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.repositories.CommunityHouseRepository;
import com.myhome.repositories.HouseMemberDocumentRepository;
import com.myhome.repositories.HouseMemberRepository;
import com.myhome.services.springdatajpa.HouseSDJpaService;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * is a JUnit test class that verifies the behavior of the HouseSDJpaService class.
 * The tests cover various scenarios such as listing all houses, adding and deleting
 * members from a house, and deleting a member from a house that does not exist. The
 * tests use mock objects to verify the interactions with the underlying repositories
 * and ensure that the service behaves correctly in different situations.
 */
class HouseSDJpaServiceTest {

  private final int TEST_HOUSES_COUNT = 10;
  private final int TEST_HOUSE_MEMBERS_COUNT = 10;
  private final String HOUSE_ID = "test-house-id";
  private final String MEMBER_ID = "test-member-id";

  @Mock
  private HouseMemberRepository houseMemberRepository;
  @Mock
  private HouseMemberDocumentRepository houseMemberDocumentRepository;
  @Mock
  private CommunityHouseRepository communityHouseRepository;
  @InjectMocks
  private HouseSDJpaService houseSDJpaService;

  /**
   * initializes mock objects using MockitoAnnotations, allowing for effective testing
   * of code under various conditions.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * retrieves a set of community houses from the database using `houseSDJpaService`,
   * compares it with the expected result, and verifies that the repository method
   * `findAll()` was called once to retrieve the houses.
   */
  @Test
  void listAllHousesDefault() {
    // given
    Set<CommunityHouse> housesInDatabase = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);
    
    given(communityHouseRepository.findAll())
        .willReturn(housesInDatabase);

    // when
    Set<CommunityHouse> resultHouses = houseSDJpaService.listAllHouses();

    // then
    assertEquals(housesInDatabase, resultHouses);
    verify(communityHouseRepository).findAll();
  }

  /**
   * lists all houses from a database using a page request and compares the result with
   * the expected list of houses to verify its correctness.
   */
  @Test
  void listAllHousesCustomPageable() {
    // given
    Set<CommunityHouse> housesInDatabase = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);
    Pageable pageRequest = PageRequest.of(0, TEST_HOUSES_COUNT);
    Page<CommunityHouse> housesPage = new PageImpl<>(
        new ArrayList<>(housesInDatabase),
        pageRequest,
        TEST_HOUSES_COUNT
    );
    given(communityHouseRepository.findAll(pageRequest))
        .willReturn(housesPage);

    // when
    Set<CommunityHouse> resultHouses = houseSDJpaService.listAllHouses(pageRequest);

    // then
    assertEquals(housesInDatabase, resultHouses);
    verify(communityHouseRepository).findAll(pageRequest);
  }

  /**
   * adds a set of HouseMembers to an existing CommunityHouse, updates the CommunityHouse's
   * `houseMembers` list, and saves both the CommunityHouse and the added HouseMembers
   * in the database.
   */
  @Test
  void addHouseMembers() {
    // given
    Set<HouseMember> membersToAdd = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);
    int membersToAddSize = membersToAdd.size();
    CommunityHouse communityHouse = TestUtils.CommunityHouseHelpers.getTestCommunityHouse();

    given(communityHouseRepository.findByHouseIdWithHouseMembers(HOUSE_ID))
        .willReturn(Optional.of(communityHouse));
    given(houseMemberRepository.saveAll(membersToAdd))
        .willReturn(membersToAdd);

    // when
    Set<HouseMember> resultMembers = houseSDJpaService.addHouseMembers(HOUSE_ID, membersToAdd);

    // then
    assertEquals(membersToAddSize, resultMembers.size());
    assertEquals(membersToAddSize, communityHouse.getHouseMembers().size());
    verify(communityHouseRepository).save(communityHouse);
    verify(houseMemberRepository).saveAll(membersToAdd);
    verify(communityHouseRepository).findByHouseIdWithHouseMembers(HOUSE_ID);
  }

  /**
   * tests the addHouseMembers method when the house with the given ID does not exist
   * in the repository. It verifies that no members are added and interacts with the
   * repository to simulate the expected behavior.
   */
  @Test
  void addHouseMembersHouseNotExists() {
    // given
    Set<HouseMember> membersToAdd = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);

    given(communityHouseRepository.findByHouseIdWithHouseMembers(HOUSE_ID))
        .willReturn(Optional.empty());

    // when
    Set<HouseMember> resultMembers = houseSDJpaService.addHouseMembers(HOUSE_ID, membersToAdd);

    // then
    assertTrue(resultMembers.isEmpty());
    verify(communityHouseRepository).findByHouseIdWithHouseMembers(HOUSE_ID);
    verify(communityHouseRepository, never()).save(any());
    verifyNoInteractions(houseMemberRepository);
  }

  /**
   * deletes a specified member from a community house. It first retrieves the community
   * house and its members, then deletes the member from the house members list, saves
   * the community house, and finally verifies the delete operation.
   */
  @Test
  void deleteMemberFromHouse() {
    // given
    Set<HouseMember> houseMembers = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);
    CommunityHouse communityHouse = TestUtils.CommunityHouseHelpers.getTestCommunityHouse();

    HouseMember memberToDelete = new HouseMember().withMemberId(MEMBER_ID);
    memberToDelete.setCommunityHouse(communityHouse);

    houseMembers.add(memberToDelete);
    communityHouse.setHouseMembers(houseMembers);

    given(communityHouseRepository.findByHouseIdWithHouseMembers(HOUSE_ID))
        .willReturn(Optional.of(communityHouse));

    // when
    boolean isMemberDeleted = houseSDJpaService.deleteMemberFromHouse(HOUSE_ID, MEMBER_ID);

    // then
    assertTrue(isMemberDeleted);
    assertNull(memberToDelete.getCommunityHouse());
    assertFalse(communityHouse.getHouseMembers().contains(memberToDelete));
    verify(communityHouseRepository).findByHouseIdWithHouseMembers(HOUSE_ID);
    verify(communityHouseRepository).save(communityHouse);
    verify(houseMemberRepository).save(memberToDelete);
  }

  /**
   * verifies that a member is not deleted from a house when the member does not exist
   * in the house's membership list.
   */
  @Test
  void deleteMemberFromHouseNotExists() {
    // given
    given(communityHouseRepository.findByHouseIdWithHouseMembers(HOUSE_ID))
        .willReturn(Optional.empty());

    // when
    boolean isMemberDeleted = houseSDJpaService.deleteMemberFromHouse(HOUSE_ID, MEMBER_ID);

    // then
    assertFalse(isMemberDeleted);
    verify(communityHouseRepository).findByHouseIdWithHouseMembers(HOUSE_ID);
    verify(communityHouseRepository, never()).save(any());
    verifyNoInteractions(houseMemberRepository);
  }

  /**
   * tests whether a member can be deleted from a house when the member is not present
   * in the house's member list. It does this by deleting a member from the house and
   * verifying that the member is not found in the house's member list after deletion.
   */
  @Test
  void deleteMemberFromHouseMemberNotPresent() {
    // given
    Set<HouseMember> houseMembers = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);
    CommunityHouse communityHouse = TestUtils.CommunityHouseHelpers.getTestCommunityHouse();

    communityHouse.setHouseMembers(houseMembers);

    given(communityHouseRepository.findByHouseIdWithHouseMembers(HOUSE_ID))
        .willReturn(Optional.of(communityHouse));

    // when
    boolean isMemberDeleted = houseSDJpaService.deleteMemberFromHouse(HOUSE_ID, MEMBER_ID);

    // then
    assertFalse(isMemberDeleted);
    verify(communityHouseRepository).findByHouseIdWithHouseMembers(HOUSE_ID);
    verify(communityHouseRepository, never()).save(communityHouse);
    verifyNoInteractions(houseMemberRepository);
  }
}