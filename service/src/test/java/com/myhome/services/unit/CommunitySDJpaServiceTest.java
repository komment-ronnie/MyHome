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
import com.myhome.controllers.dto.CommunityDto;
import com.myhome.controllers.dto.mapper.CommunityMapper;
import com.myhome.domain.Community;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.domain.User;
import com.myhome.repositories.CommunityHouseRepository;
import com.myhome.repositories.CommunityRepository;
import com.myhome.repositories.UserRepository;
import com.myhome.services.HouseService;
import com.myhome.services.springdatajpa.CommunitySDJpaService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * tests the removeHouseFromCommunityByHouseId method of the CommunitySDJpaService
 * class. The test cases cover various scenarios such as community and house existence,
 * community not exists, house not exists, and house not in community. The test cases
 * verify the correctness of the method through interactions with the repository and
 * service layers, and also verify that the method calls are made correctly using the
 * provided mock dependencies.
 */
public class CommunitySDJpaServiceTest {

  private final String TEST_COMMUNITY_ID = "test-community-id";
  private final String TEST_COMMUNITY_NAME = "test-community-name";
  private final String TEST_COMMUNITY_DISTRICT = "test-community-name";

  private final int TEST_ADMINS_COUNT = 2;
  private final int TEST_HOUSES_COUNT = 2;
  private final int TEST_HOUSE_MEMBERS_COUNT = 2;
  private final int TEST_COMMUNITIES_COUNT = 2;

  private final String TEST_ADMIN_ID = "test-admin-id";
  private final String TEST_ADMIN_NAME = "test-user-name";
  private final String TEST_ADMIN_EMAIL = "test-user-email";
  private final String TEST_ADMIN_PASSWORD = "test-user-password";
  private final String TEST_HOUSE_ID = "test-house-id";

  @Mock
  private CommunityRepository communityRepository;
  @Mock
  private UserRepository communityAdminRepository;
  @Mock
  private CommunityMapper communityMapper;
  @Mock
  private CommunityHouseRepository communityHouseRepository;
  @Mock
  private HouseService houseService;

  @InjectMocks
  private CommunitySDJpaService communitySDJpaService;

  /**
   * initializes mock objects using MockitoAnnotations.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * creates a new `User` object with predefined values for the administrator user,
   * including name, ID, email, and password.
   * 
   * @returns a `User` object containing the specified attributes.
   * 
   * 	- `TEST_ADMIN_NAME`: The name of the test admin user.
   * 	- `TEST_ADMIN_ID`: The ID of the test admin user.
   * 	- `TEST_ADMIN_EMAIL`: The email address of the test admin user.
   * 	- `false`: A boolean value indicating whether the user is an administrator or not.
   * 	- `TEST_ADMIN_PASSWORD`: The password of the test admin user.
   * 	- `HashSet<>`: An empty set of hash codes for the user's groups and permissions.
   * 	- `HashSet<>`: An empty set of hash codes for the user's roles.
   */
  private User getTestAdmin() {
    return new User(
        TEST_ADMIN_NAME,
        TEST_ADMIN_ID,
        TEST_ADMIN_EMAIL,
        false,
        TEST_ADMIN_PASSWORD,
        new HashSet<>(),
        new HashSet<>());
  }

  /**
   * queries the community repository to retrieve a set of communities and compares it
   * with the expected result obtained through TestUtils.
   */
  @Test
  void listAllCommunities() {
    // given
    Set<Community> communities = TestUtils.CommunityHelpers.getTestCommunities(TEST_COMMUNITIES_COUNT);
    given(communityRepository.findAll())
        .willReturn(communities);

    // when
    Set<Community> resultCommunities = communitySDJpaService.listAll();

    // then
    assertEquals(communities, resultCommunities);
    verify(communityRepository).findAll();
  }

  /**
   * creates a new community object and saves it to the database, using a test community
   * DTO as input.
   */
  @Test
  void createCommunity() {
    // given
    CommunityDto testCommunityDto = getTestCommunityDto();
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity(TEST_COMMUNITY_ID, TEST_COMMUNITY_NAME, TEST_COMMUNITY_DISTRICT, 0, 0);
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(TEST_ADMIN_ID,
            null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    given(communityMapper.communityDtoToCommunity(testCommunityDto))
        .willReturn(testCommunity);
    given(communityAdminRepository.findByUserIdWithCommunities(TEST_ADMIN_ID))
            .willReturn(Optional.of(getTestAdmin()));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);

    // when
    Community createdCommunity = communitySDJpaService.createCommunity(testCommunityDto);

    // then
    assertNotNull(createdCommunity);
    assertEquals(testCommunityDto.getName(), createdCommunity.getName());
    assertEquals(testCommunityDto.getDistrict(), createdCommunity.getDistrict());
    verify(communityMapper).communityDtoToCommunity(testCommunityDto);
    verify(communityAdminRepository).findByUserIdWithCommunities(TEST_ADMIN_ID);
    verify(communityRepository).save(testCommunity);
  }

  /**
   * retrieves a list of CommunityHouse instances associated with a given community ID
   * using repository calls and returns an Optional<List<CommunityHouse>> containing
   * the retrieved list.
   */
  @Test
  void findCommunityHousesById() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    List<CommunityHouse> testCommunityHouses = new ArrayList<>(testCommunity.getHouses());
    given(communityRepository.existsByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(true);
    given(communityHouseRepository.findAllByCommunity_CommunityId(TEST_COMMUNITY_ID, null))
        .willReturn(testCommunityHouses);

    // when
    Optional<List<CommunityHouse>> resultCommunityHousesOptional =
        communitySDJpaService.findCommunityHousesById(TEST_COMMUNITY_ID, null);

    // then
    assertTrue(resultCommunityHousesOptional.isPresent());
    List<CommunityHouse> resultCommunityHouses = resultCommunityHousesOptional.get();
    assertEquals(testCommunityHouses, resultCommunityHouses);
    verify(communityRepository).existsByCommunityId(TEST_COMMUNITY_ID);
    verify(communityHouseRepository).findAllByCommunity_CommunityId(TEST_COMMUNITY_ID, null);
  }

  /**
   * verifies that no community houses exist for a given community ID by first checking
   * if the repository exists and then calling the `findAllByCommunity_CommunityId`
   * method on the community house repository without any parameters.
   */
  @Test
  void findCommunityHousesByIdNotExist() {
    // given
    given(communityRepository.existsByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(false);

    // when
    Optional<List<CommunityHouse>> resultCommunityHousesOptional =
        communitySDJpaService.findCommunityHousesById(TEST_COMMUNITY_ID, null);

    // then
    assertFalse(resultCommunityHousesOptional.isPresent());
    verify(communityRepository).existsByCommunityId(TEST_COMMUNITY_ID);
    verify(communityHouseRepository, never()).findAllByCommunity_CommunityId(TEST_COMMUNITY_ID,
        null);
  }

  /**
   * retrieves a list of admins for a given community ID by querying the community and
   * community admin repositories.
   */
  @Test
  void findCommunityAdminsById() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    List<User> testCommunityAdmins = new ArrayList<>(testCommunity.getAdmins());
    given(communityRepository.existsByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(true);
    given(communityAdminRepository.findAllByCommunities_CommunityId(TEST_COMMUNITY_ID, null))
        .willReturn(testCommunityAdmins);

    // when
    Optional<List<User>> resultAdminsOptional =
        communitySDJpaService.findCommunityAdminsById(TEST_COMMUNITY_ID, null);

    // then
    assertTrue((resultAdminsOptional.isPresent()));
    List<User> resultAdmins = resultAdminsOptional.get();
    assertEquals(testCommunityAdmins, resultAdmins);
    verify(communityRepository).existsByCommunityId(TEST_COMMUNITY_ID);
    verify(communityAdminRepository).findAllByCommunities_CommunityId(TEST_COMMUNITY_ID, null);
  }

  /**
   * verifies that a list of community admins cannot be retrieved for a non-existent
   * community ID.
   */
  @Test
  void findCommunityAdminsByIdNotExists() {
    // given
    given(communityRepository.existsByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(false);

    // when
    Optional<List<User>> resultAdminsOptional =
        communitySDJpaService.findCommunityAdminsById(TEST_COMMUNITY_ID, null);

    // then
    assertFalse((resultAdminsOptional.isPresent()));
    verify(communityRepository).existsByCommunityId(TEST_COMMUNITY_ID);
  }

  /**
   * adds a set of users as admins to a community. It first retrieves the community
   * with the given ID and then adds each user to the community using the `communityAdminRepository`.
   */
  @Test
  void addAdminsToCommunity() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    Set<User> adminToAdd = TestUtils.UserHelpers.getTestUsers(TEST_ADMINS_COUNT);
    Set<String> adminToAddIds = adminToAdd.stream()
        .map(admin -> admin.getUserId())
        .collect(Collectors.toSet());

    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);
    adminToAdd.forEach(admin -> {
      given(communityAdminRepository.findByUserIdWithCommunities(admin.getUserId()))
          .willReturn(Optional.of(admin));
    });
    adminToAdd.forEach(admin -> {
      given(communityAdminRepository.save(admin))
          .willReturn(admin);
    });
    // when
    Optional<Community> updatedCommunityOptional =
        communitySDJpaService.addAdminsToCommunity(TEST_COMMUNITY_ID, adminToAddIds);

    // then
    assertTrue(updatedCommunityOptional.isPresent());
    adminToAdd.forEach(admin -> assertTrue(admin.getCommunities().contains(testCommunity)));
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
    adminToAdd.forEach(
        admin -> verify(communityAdminRepository).findByUserIdWithCommunities(admin.getUserId()));
  }

  /**
   * adds admins to a community that does not exist in the repository.
   */
  @Test
  void addAdminsToCommunityNotExist() {
    // given
    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    Optional<Community> updatedCommunityOptional =
        communitySDJpaService.addAdminsToCommunity(TEST_COMMUNITY_ID, any());

    // then
    assertFalse(updatedCommunityOptional.isPresent());
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
  }

  /**
   * retrieves Community details by its ID, given a test Community object and mocked
   * repository calls to retrieve the Community record from the database.
   */
  @Test
  void communityDetailsById() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    given(communityRepository.findByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));

    // when
    Optional<Community> communityOptional =
        communitySDJpaService.getCommunityDetailsById(TEST_COMMUNITY_ID);

    // then
    assertTrue(communityOptional.isPresent());
    assertEquals(testCommunity, communityOptional.get());
    verify(communityRepository).findByCommunityId(TEST_COMMUNITY_ID);
  }

  /**
   * retrieves community details for a given ID and admins, checks if the result is
   * present and equals the expected community, and verifies the call to the repository
   * to find the community by ID with admins.
   */
  @Test
  void communityDetailsByIdWithAdmins() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));

    // when
    Optional<Community> communityOptional =
        communitySDJpaService.getCommunityDetailsByIdWithAdmins(TEST_COMMUNITY_ID);

    // then
    assertTrue(communityOptional.isPresent());
    assertEquals(testCommunity, communityOptional.get());
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
  }

  /**
   * adds a set of houses to an existing community in the database, verifying that the
   * correct houses were added and the community was updated accordingly.
   */
  @Test
  void addHousesToCommunity() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    Set<CommunityHouse> housesToAdd = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);
    housesToAdd.forEach(house -> {
      given(communityHouseRepository.save(house))
          .willReturn(house);
    });

    // when
    Set<String> addedHousesIds =
        communitySDJpaService.addHousesToCommunity(TEST_COMMUNITY_ID, housesToAdd);

    // then
    assertEquals(housesToAdd.size(), addedHousesIds.size());
    housesToAdd.forEach(house -> {
      assertEquals(house.getCommunity(), testCommunity);
    });
    verify(communityRepository).findByCommunityIdWithHouses(TEST_COMMUNITY_ID);
    housesToAdd.forEach(house -> {
      verify(communityHouseRepository).save(house);
    });
  }

  /**
   * adds a set of houses to a community repository when no houses exist for that community.
   */
  @Test
  void addHousesToCommunityNotExist() {
    // given
    Set<CommunityHouse> housesToAdd = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    Set<String> addedHousesIds =
        communitySDJpaService.addHousesToCommunity(TEST_COMMUNITY_ID, housesToAdd);

    // then
    assertTrue(addedHousesIds.isEmpty());
    verify(communityRepository).findByCommunityIdWithHouses(TEST_COMMUNITY_ID);
    verify(communityRepository, never()).save(any());
    verify(communityHouseRepository, never()).save(any());
  }

  /**
   * adds a set of houses to an existing community in the database, verifying that the
   * community exists and the houses are added successfully.
   */
  @Test
  void addHousesToCommunityHouseExists() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    Set<CommunityHouse> houses = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);
    testCommunity.setHouses(houses);

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);
    houses.forEach(house -> given(communityHouseRepository.save(house)).willReturn(house));

    // when
    Set<String> addedHousesIds =
        communitySDJpaService.addHousesToCommunity(TEST_COMMUNITY_ID, houses);

    // then
    assertTrue(addedHousesIds.isEmpty());
    verify(communityRepository).findByCommunityIdWithHouses(TEST_COMMUNITY_ID);
    verify(communityRepository).save(testCommunity);
    verify(communityHouseRepository, never()).save(any());
  }

  /**
   * removes an admin from a community by finding the community with the given ID and
   * removing the admin with the given ID, saving the updated community in the repository.
   */
  @Test
  void removeAdminFromCommunity() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    User testAdmin = getTestAdmin();
    testCommunity.getAdmins().add(testAdmin);

    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);

    // when
    boolean adminRemoved =
        communitySDJpaService.removeAdminFromCommunity(TEST_COMMUNITY_ID, TEST_ADMIN_ID);

    // then
    assertTrue(adminRemoved);
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
    verify(communityRepository).save(testCommunity);
  }

  /**
   * attempts to remove an admin from a community that does not exist in the repository.
   * It verifies the result and calls the necessary methods on the `communityRepository`.
   */
  @Test
  void removeAdminFromCommunityNotExists() {
    // given
    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean adminRemoved =
        communitySDJpaService.removeAdminFromCommunity(TEST_COMMUNITY_ID, TEST_ADMIN_ID);

    // then
    assertFalse(adminRemoved);
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
    verify(communityRepository, never()).save(any());
  }

  /**
   * verifies that an admin is not removed from a community if they do not exist in the
   * community's admin list.
   */
  @Test
  void removeAdminFromCommunityAdminNotExists() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();

    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);

    // when
    boolean adminRemoved =
        communitySDJpaService.removeAdminFromCommunity(TEST_COMMUNITY_ID, TEST_ADMIN_ID);

    // then
    assertFalse(adminRemoved);
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
    verify(communityRepository, never()).save(testCommunity);
  }

  /**
   * deletes a community from the database based on its ID, retrieving and deleting
   * associated houses using JPA repository interactions.
   */
  @Test
  void deleteCommunity() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    Set<CommunityHouse> testCommunityHouses = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);
    testCommunity.setHouses(testCommunityHouses);

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    testCommunityHouses.forEach(house -> {
      given(communityHouseRepository.findByHouseId(house.getHouseId()))
          .willReturn(Optional.of(house));
    });

    testCommunityHouses.forEach(house -> {
      given(communityHouseRepository.findByHouseId(house.getHouseId()))
          .willReturn(Optional.of(house));
    });

    // when
    boolean communityDeleted = communitySDJpaService.deleteCommunity(TEST_COMMUNITY_ID);

    // then
    assertTrue(communityDeleted);
    verify(communityRepository).findByCommunityIdWithHouses(TEST_COMMUNITY_ID);
    verify(communityRepository).delete(testCommunity);
  }

  /**
   * tests the delete community method by attempting to delete a community that does
   * not exist in the repository. It verifies that the method returns false and calls
   * the appropriate repository methods to verify the community's existence before
   * deleting it.
   */
  @Test
  void deleteCommunityNotExists() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean communityDeleted = communitySDJpaService.deleteCommunity(TEST_COMMUNITY_ID);

    // then
    assertFalse(communityDeleted);
    verify(communityRepository).findByCommunityIdWithHouses(TEST_COMMUNITY_ID);
    verify(communityHouseRepository, never()).deleteByHouseId(any());
    verify(communityRepository, never()).delete(testCommunity);
  }

  /**
   * removes a specified house from a community by its ID, updating the community's
   * house list and deleting the house's members associations. It also updates the
   * community repository and house service calls.
   */
  @Test
  void removeHouseFromCommunityByHouseId() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    CommunityHouse testHouse = TestUtils.CommunityHouseHelpers.getTestCommunityHouse(TEST_HOUSE_ID);
    Set<HouseMember> testHouseMembers = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);
    testHouse.setHouseMembers(testHouseMembers);
    testCommunity.getHouses().add(testHouse);

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityHouseRepository.findByHouseIdWithHouseMembers(TEST_HOUSE_ID))
        .willReturn(Optional.of(testHouse));

    // when
    boolean houseDeleted =
        communitySDJpaService.removeHouseFromCommunityByHouseId(testCommunity, TEST_HOUSE_ID);

    // then
    assertTrue(houseDeleted);
    assertFalse(testCommunity.getHouses().contains(testHouse));
    verify(communityRepository).save(testCommunity);
    testHouse.getHouseMembers()
        .forEach(houseMember -> verify(houseService).deleteMemberFromHouse(TEST_HOUSE_ID,
            houseMember.getMemberId()));
    verify(communityHouseRepository).findByHouseIdWithHouseMembers(TEST_HOUSE_ID);
    verify(communityHouseRepository).deleteByHouseId(TEST_HOUSE_ID);
  }

  /**
   * checks if a house can be removed from a community that does not exist. It verifies
   * the absence of a community with the given ID and saves the test community without
   * any modifications.
   */
  @Test
  void removeHouseFromCommunityByHouseIdCommunityNotExists() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean houseDeleted =
        communitySDJpaService.removeHouseFromCommunityByHouseId(null, TEST_HOUSE_ID);

    // then
    assertFalse(houseDeleted);
    verify(communityHouseRepository, never()).findByHouseId(TEST_HOUSE_ID);
    verifyNoInteractions(houseService);
    verify(communityRepository, never()).save(testCommunity);
  }

  /**
   * checks if a house exists in a community before removing it. It returns `false` if
   * the house does not exist and verifies interactions with the repository and service
   * layers.
   */
  @Test
  void removeHouseFromCommunityByHouseIdHouseNotExists() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();

    given(communityHouseRepository.findByHouseIdWithHouseMembers(TEST_HOUSE_ID))
        .willReturn(Optional.empty());

    // when
    boolean houseDeleted =
        communitySDJpaService.removeHouseFromCommunityByHouseId(testCommunity, TEST_HOUSE_ID);

    // then
    assertFalse(houseDeleted);
    verify(communityHouseRepository).findByHouseIdWithHouseMembers(TEST_HOUSE_ID);
    verifyNoInteractions(houseService);
    verify(communityRepository, never()).save(testCommunity);
  }

  /**
   * removes a house from a community if the house is not already a member of the community.
   */
  @Test
  void removeHouseFromCommunityByHouseIdHouseNotInCommunity() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();

    given(communityHouseRepository.findByHouseIdWithHouseMembers(TEST_HOUSE_ID))
        .willReturn(Optional.empty());

    // when
    boolean houseDeleted =
        communitySDJpaService.removeHouseFromCommunityByHouseId(testCommunity, TEST_HOUSE_ID);

    // then
    assertFalse(houseDeleted);
    verify(communityHouseRepository).findByHouseIdWithHouseMembers(TEST_HOUSE_ID);
    verifyNoInteractions(houseService);
    verify(communityRepository, never()).save(testCommunity);
  }

  /**
   * creates a new instance of the `CommunityDto` class with predefined values for
   * community ID, district, and name.
   * 
   * @returns a `CommunityDto` object containing pre-defined values for testing purposes.
   * 
   * 	- `testCommunityDto`: A `CommunityDto` object representing a test community with
   * a unique `communityId`, `district`, and `name`.
   */
  private CommunityDto getTestCommunityDto() {
    CommunityDto testCommunityDto = new CommunityDto();
    testCommunityDto.setCommunityId(TEST_COMMUNITY_ID);
    testCommunityDto.setDistrict(TEST_COMMUNITY_DISTRICT);
    testCommunityDto.setName(TEST_COMMUNITY_NAME);
    return testCommunityDto;
  }

}
