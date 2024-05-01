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

import com.myhome.controllers.mapper.AmenityApiMapper;
import com.myhome.domain.Amenity;
import com.myhome.domain.Community;
import com.myhome.model.AmenityDto;
import com.myhome.repositories.AmenityRepository;
import com.myhome.repositories.CommunityRepository;
import com.myhome.services.CommunityService;
import com.myhome.services.springdatajpa.AmenitySDJpaService;
import helpers.TestUtils;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * tests the update method of the AmenitySDJpaService class, which updates an amenity's
 * details in a database based on a test amenity DTO and returns the updated amenity
 * object with a reference to the test community. The test class generates a test
 * amenity DTO with predefined attributes and sets them to the corresponding fields
 * of the updated amenity object, as well as setting a reference to a test community.
 */
class AmenitySDJpaServiceTest {

  private static final String TEST_AMENITY_NAME = "test-amenity-name";
  private static final BigDecimal TEST_AMENITY_PRICE = BigDecimal.valueOf(1);
  private final String TEST_AMENITY_ID = "test-amenity-id";
  private final String TEST_AMENITY_DESCRIPTION = "test-amenity-description";
  private final String TEST_COMMUNITY_ID = "test-community-id";
  private final int TEST_AMENITIES_COUNT = 2;
  @Mock
  private AmenityRepository amenityRepository;
  @Mock
  private CommunityRepository communityRepository;
  @Mock
  private CommunityService communityService;
  @Mock
  private AmenityApiMapper amenityApiMapper;

  @InjectMocks
  private AmenitySDJpaService amenitySDJpaService;

  /**
   * initializes Mockito Annotations for the test class by calling `MockitoAnnotations.initMocks(this)`.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * deletes an amenity from the repository and verifies its deletion by checking if
   * it is no longer present in the repository and if the delete method was called on
   * the repository.
   */
  @Test
  void deleteAmenity() {
    // given
    Amenity testAmenity =
        TestUtils.AmenityHelpers.getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION);

    given(amenityRepository.findByAmenityIdWithCommunity(TEST_AMENITY_ID))
        .willReturn(Optional.of(testAmenity));

    // when
    boolean amenityDeleted = amenitySDJpaService.deleteAmenity(TEST_AMENITY_ID);

    // then
    assertTrue(amenityDeleted);
    verify(amenityRepository).findByAmenityIdWithCommunity(TEST_AMENITY_ID);
    verify(amenityRepository).delete(testAmenity);
  }

  /**
   * tests whether an amenity with the given ID exists in the repository before attempting
   * to delete it. If the amenity does not exist, it does not get deleted and the
   * function fails.
   */
  @Test
  void deleteAmenityNotExists() {
    // given
    given(amenityRepository.findByAmenityIdWithCommunity(TEST_AMENITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean amenityDeleted = amenitySDJpaService.deleteAmenity(TEST_AMENITY_ID);

    // then
    assertFalse(amenityDeleted);
    verify(amenityRepository).findByAmenityIdWithCommunity(TEST_AMENITY_ID);
    verify(amenityRepository, never()).delete(any());
  }

  /**
   * retrieves a set of amenities associated with a community, compares them to a set
   * of test amenities, and asserts that they are equal.
   */
  @Test
  void listAllAmenities() {
    // given
    Set<Amenity> testAmenities = TestUtils.AmenityHelpers.getTestAmenities(TEST_AMENITIES_COUNT);
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    testCommunity.setAmenities(testAmenities);

    given(communityRepository.findByCommunityIdWithAmenities(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));

    // when
    Set<Amenity> resultAmenities = amenitySDJpaService.listAllAmenities(TEST_COMMUNITY_ID);

    // then
    assertEquals(testAmenities, resultAmenities);
    verify(communityRepository).findByCommunityIdWithAmenities(TEST_COMMUNITY_ID);
  }

  /**
   * verifies that no amenity exists for a given community by retrieving the list of
   * amenities from the database using the `amenitySDJpaService`, and then comparing
   * it to an empty set.
   */
  @Test
  void listAllAmenitiesNotExists() {
    // given
    given(communityRepository.findByCommunityIdWithAmenities(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    Set<Amenity> resultAmenities = amenitySDJpaService.listAllAmenities(TEST_COMMUNITY_ID);

    // then
    assertEquals(new HashSet<>(), resultAmenities);
    verify(communityRepository).findByCommunityIdWithAmenities(TEST_COMMUNITY_ID);
  }

  /**
   * tests whether adding an amenity to an existing community is successful. It verifies
   * that the amenity is added to the community's amenities list, and its details are
   * updated in the database.
   */
  @Test
  void shouldAddAmenityToExistingCommunity() {
    // given
    final String communityId = "communityId";
    final Community community = new Community().withCommunityId(communityId);
    final AmenityDto baseAmenityDto = new AmenityDto()
        .id(1L)
        .amenityId("amenityId")
        .name("name")
        .description("description")
        .price(BigDecimal.valueOf(12));
    final AmenityDto amenityDtoWithCommunity = baseAmenityDto.communityId(communityId);
    final Amenity baseAmenity = new Amenity();
    final Amenity amenityWithCommunity = new Amenity().withCommunity(community);
    final List<Amenity> amenitiesWithCommunity = singletonList(amenityWithCommunity);
    final HashSet<AmenityDto> requestAmenitiesDto = new HashSet<>(singletonList(baseAmenityDto));
    given(communityService.getCommunityDetailsById(communityId))
        .willReturn(Optional.of(community));
    given(amenityApiMapper.amenityDtoToAmenity(baseAmenityDto))
        .willReturn(baseAmenity);
    given(amenityRepository.saveAll(amenitiesWithCommunity))
        .willReturn(amenitiesWithCommunity);
    given(amenityApiMapper.amenityToAmenityDto(amenityWithCommunity))
        .willReturn(amenityDtoWithCommunity);

    // when
    final Optional<List<AmenityDto>> actualResult =
        amenitySDJpaService.createAmenities(requestAmenitiesDto, communityId);

    // then
    assertTrue(actualResult.isPresent());
    final List<AmenityDto> actualResultAmenitiesDtos = actualResult.get();
    assertEquals(singletonList(amenityDtoWithCommunity), actualResultAmenitiesDtos);
    verify(communityService).getCommunityDetailsById(communityId);
    verify(amenityApiMapper).amenityDtoToAmenity(baseAmenityDto);
    verify(amenityRepository).saveAll(amenitiesWithCommunity);
    verify(amenityApiMapper).amenityToAmenityDto(amenityWithCommunity);
  }

  /**
   * tests whether creating amenities for a community that does not exist will return
   * an empty list. It uses mock interactions with the `communityService`, `amenityApiMapper`,
   * and `amenityRepository` to verify the behavior of the `amenitySDJpaService`.
   */
  @Test
  void shouldFailOnAddAmenityToNotExistingCommunity() {
    // given
    final String communityId = "communityId";
    final AmenityDto baseAmenityDto = new AmenityDto()
        .id(1L)
        .amenityId("amenityId")
        .name("name")
        .description("description")
        .price(BigDecimal.valueOf(12));
    final HashSet<AmenityDto> requestAmenitiesDto = new HashSet<>(singletonList(baseAmenityDto));
    given(communityService.getCommunityDetailsById(communityId))
        .willReturn(Optional.empty());

    // when
    final Optional<List<AmenityDto>> actualResult =
        amenitySDJpaService.createAmenities(requestAmenitiesDto, communityId);

    // then
    assertFalse(actualResult.isPresent());
    verify(communityService).getCommunityDetailsById(communityId);
    verifyNoInteractions(amenityApiMapper);
    verifyNoInteractions(amenityRepository);
  }

  /**
   * tests whether updating an amenity in a community database successfully updates the
   * amenity.
   */
  @Test
  void shouldUpdateCommunityAmenitySuccessfully() {
    // given
    Amenity communityAmenity =
        TestUtils.AmenityHelpers.getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION);
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    AmenityDto updated = getTestAmenityDto();
    Amenity updatedAmenity = getUpdatedCommunityAmenity();

    given(amenityRepository.findByAmenityId(TEST_AMENITY_ID))
        .willReturn(Optional.of(communityAmenity));
    given(communityRepository.findByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(amenityRepository.save(updatedAmenity))
        .willReturn(updatedAmenity);

    // when
    boolean result = amenitySDJpaService.updateAmenity(updated);

    // then
    assertTrue(result);
    verify(amenityRepository).findByAmenityId(TEST_AMENITY_ID);
    verify(communityRepository).findByCommunityId(TEST_COMMUNITY_ID);
    verify(amenityRepository).save(updatedAmenity);
  }

  /**
   * verifies that updating a community amenity with an non-existent amenity ID does
   * not save the updated amenity to the database and does not interact with the `communityRepository`.
   */
  @Test
  void shouldNotUpdateCommunityAmenitySuccessfullyIfAmenityNotExists() {
    // given
    given(amenityRepository.findByAmenityId(TEST_AMENITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean result = amenitySDJpaService.updateAmenity(getTestAmenityDto());

    // then
    assertFalse(result);
    verify(amenityRepository, times(0)).save(getUpdatedCommunityAmenity());
    verifyNoInteractions(communityRepository);
  }

  /**
   * tests whether an attempt to update a community amenity fails due to saving the
   * updated amenity in the database.
   */
  @Test
  void shouldNotUpdateCommunityAmenitySuccessfullyIfSavingFails() {
    // given
    Amenity testAmenity =
        TestUtils.AmenityHelpers.getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION);
    Amenity updatedAmenity = getUpdatedCommunityAmenity();
    AmenityDto updatedDto = getTestAmenityDto();
    Community community = TestUtils.CommunityHelpers.getTestCommunity();

    given(amenityRepository.findByAmenityId(TEST_AMENITY_ID))
        .willReturn(Optional.of(testAmenity));
    given(communityRepository.findByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(community));
    given(amenityRepository.save(updatedAmenity))
        .willReturn(null);

    // when
    boolean result = amenitySDJpaService.updateAmenity(updatedDto);

    // then
    assertFalse(result);
    verify(amenityRepository).findByAmenityId(TEST_AMENITY_ID);
    verify(communityRepository).findByCommunityId(TEST_COMMUNITY_ID);
    verify(amenityRepository).save(updatedAmenity);
  }

  /**
   * tests whether updating an amenity with a non-existent community will return false
   * and verify interactions with the amenity and community repositories.
   */
  @Test
  void shouldNotUpdateAmenityIfCommunityDoesNotExist() {
    // given
    Amenity communityAmenity =
        TestUtils.AmenityHelpers.getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION);
    AmenityDto updatedDto = getTestAmenityDto();

    given(amenityRepository.findByAmenityId(TEST_AMENITY_ID))
        .willReturn(Optional.of(communityAmenity));
    given(communityRepository.findByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean result = amenitySDJpaService.updateAmenity(updatedDto);

    // then
    assertFalse(result);
    verify(amenityRepository).findByAmenityId(TEST_AMENITY_ID);
    verify(communityRepository).findByCommunityId(TEST_COMMUNITY_ID);
    verifyNoMoreInteractions(amenityRepository);
  }

  /**
   * creates a mock `AmenityDto` object with pre-defined values for an amenity entity,
   * including its ID, amenity ID, name, description, price, and community ID.
   * 
   * @returns a `AmenityDto` object containing mock data for testing purposes.
   * 
   * 	- `id`: A long value representing the unique identifier for the amenity entity.
   * 	- `amenityId`: An integer value representing the amenity ID.
   * 	- `name`: A string value representing the name of the amenity.
   * 	- `description`: A string value representing the description of the amenity.
   * 	- `price`: A double value representing the price of the amenity.
   * 	- `communityId`: A long value representing the ID of the community associated
   * with the amenity.
   */
  private AmenityDto getTestAmenityDto() {
    Long TEST_AMENITY_ENTITY_ID = 1L;

    return new AmenityDto()
        .id(TEST_AMENITY_ENTITY_ID)
        .amenityId(TEST_AMENITY_ID)
        .name(TEST_AMENITY_NAME)
        .description(TEST_AMENITY_DESCRIPTION)
        .price(TEST_AMENITY_PRICE)
        .communityId(TEST_COMMUNITY_ID);
  }

  /**
   * returns an updated `Amenity` object based on a test amenity DTO, including ID,
   * name, price, description, and community.
   * 
   * @returns an updated `Amenity` object with the latest values from the input `AmenityDto`.
   * 
   * 1/ `amenityId`: The ID of the amenity.
   * 2/ `name`: The name of the amenity.
   * 3/ `price`: The price of the amenity.
   * 4/ `description`: The description of the amenity.
   * 5/ `community`: A reference to the community where the amenity is located.
   */
  private Amenity getUpdatedCommunityAmenity() {
    AmenityDto communityAmenityDto = getTestAmenityDto();
    return new Amenity()
        .withAmenityId(communityAmenityDto.getAmenityId())
        .withName(communityAmenityDto.getName())
        .withPrice(communityAmenityDto.getPrice())
        .withDescription(communityAmenityDto.getDescription())
        .withCommunity(TestUtils.CommunityHelpers.getTestCommunity());
  }
}