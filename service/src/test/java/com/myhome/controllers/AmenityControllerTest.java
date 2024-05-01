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

import com.myhome.controllers.mapper.AmenityApiMapper;
import com.myhome.domain.Amenity;
import com.myhome.model.AddAmenityRequest;
import com.myhome.model.AddAmenityResponse;
import com.myhome.model.AmenityDto;
import com.myhome.model.GetAmenityDetailsResponse;
import com.myhome.model.UpdateAmenityRequest;
import com.myhome.services.AmenityService;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * tests various endpoints related to amenities in a community. The tests cover adding
 * an amenity, getting an amenity detail, deleting an amenity, updating an amenity,
 * and not updating an amenity if it does not exist. The test cases use mocking and
 * stubbing to verify the functionality of the controllers and the database.
 */
class AmenityControllerTest {

  private static final String TEST_AMENITY_NAME = "test-amenity-name";
  private static final BigDecimal TEST_AMENITY_PRICE = BigDecimal.valueOf(1);
  private final String TEST_AMENITY_ID = "test-amenity-id";
  private final String TEST_AMENITY_DESCRIPTION = "test-amenity-description";
  private final String TEST_COMMUNITY_ID = "1";

  @Mock
  private AmenityService amenitySDJpaService;
  @Mock
  private AmenityApiMapper amenityApiMapper;

  @InjectMocks
  private AmenityController amenityController;

  /**
   * tests whether adding an amenity to a community through the `addAmenityToCommunity`
   * endpoint returns a successful response with a 200 status code.
   */
  @Test
  void shouldAddAmenityToCommunity() {
    // given
    final String communityId = "communityId";
    final AmenityDto amenityDto =
        new AmenityDto().id(1L)
            .amenityId("amenityId")
            .name("name")
            .description("description")
            .price(BigDecimal.ONE)
            .communityId("");
    final HashSet<AmenityDto> amenities = new HashSet<>(singletonList(amenityDto));
    final AddAmenityRequest request = new AddAmenityRequest().amenities(amenities);
    given(amenitySDJpaService.createAmenities(amenities, communityId))
        .willReturn(Optional.of(singletonList(amenityDto)));

    // when
    final ResponseEntity<AddAmenityResponse> response =
        amenityController.addAmenityToCommunity(communityId, request);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  /**
   * tests whether an amenity is added to a community when the community does not exist.
   * It asserts that the response status code is `HttpStatus.NOT_FOUND`.
   */
  @Test
  void shouldNotAddAmenityWhenCommunityNotExists() {
    // given
    final String communityId = "communityId";
    final AmenityDto amenityDto = new AmenityDto();
    final HashSet<AmenityDto> amenities = new HashSet<>(singletonList(amenityDto));
    final AddAmenityRequest request = new AddAmenityRequest().amenities(amenities);
    given(amenitySDJpaService.createAmenities(amenities, communityId))
        .willReturn(Optional.empty());

    // when
    final ResponseEntity<AddAmenityResponse> response =
        amenityController.addAmenityToCommunity(communityId, request);

    // then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  /**
   * initializes mock objects using MockitoAnnotations.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * retrieves an amenity's details by its ID, given to it as a parameter. It uses JPA
   * and API mapper to retrieve the amenity data from the database and map it to the
   * expected response body. The function then returns the response entity with the
   * retrieved amenity details and HTTP status code OK.
   */
  @Test
  void getAmenityDetails() {
    // given
    Amenity testAmenity = getTestAmenity();
    GetAmenityDetailsResponse expectedResponseBody = new GetAmenityDetailsResponse()
        .amenityId(testAmenity.getAmenityId())
        .description(testAmenity.getDescription());

    given(amenitySDJpaService.getAmenityDetails(TEST_AMENITY_ID))
        .willReturn(Optional.of(testAmenity));
    given(amenityApiMapper.amenityToAmenityDetailsResponse(testAmenity))
        .willReturn(expectedResponseBody);

    // when
    ResponseEntity<GetAmenityDetailsResponse> response =
        amenityController.getAmenityDetails(TEST_AMENITY_ID);

    // then
    assertEquals(expectedResponseBody, response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(amenitySDJpaService).getAmenityDetails(TEST_AMENITY_ID);
    verify(amenityApiMapper).amenityToAmenityDetailsResponse(testAmenity);
  }

  /**
   * tests the response of the `getAmenityDetails` method when the amenity with the
   * given ID does not exist in the database. It verifies that the method returns a
   * `ResponseEntity` with a `Body` of `null` and a `StatusCode` of `HttpStatus.NOT_FOUND`.
   */
  @Test
  void getAmenityDetailsNotExists() {
    // given
    given(amenitySDJpaService.getAmenityDetails(TEST_AMENITY_ID))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<GetAmenityDetailsResponse> response =
        amenityController.getAmenityDetails(TEST_AMENITY_ID);

    // then
    assertNull(response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(amenitySDJpaService).getAmenityDetails(TEST_AMENITY_ID);
    verify(amenityApiMapper, never()).amenityToAmenityDetailsResponse(any());
  }

  /**
   * tests the deleteAmenity method of the amenityController class by providing a
   * TEST_AMENITY_ID and verifying that the amenity is deleted from the database and
   * the response entity is null.
   */
  @Test
  void deleteAmenity() {
    // given
    given(amenitySDJpaService.deleteAmenity(TEST_AMENITY_ID))
        .willReturn(true);

    // when
    ResponseEntity response = amenityController.deleteAmenity(TEST_AMENITY_ID);

    // then
    assertNull(response.getBody());
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(amenitySDJpaService).deleteAmenity(TEST_AMENITY_ID);
  }

  /**
   * tests whether deleting an amenity that does not exist returns a `HttpStatus.NOT_FOUND`
   * response and verifies that the amenity is not deleted from the database.
   */
  @Test
  void deleteAmenityNotExists() {
    // given
    given(amenitySDJpaService.deleteAmenity(TEST_AMENITY_ID))
        .willReturn(false);

    // when
    ResponseEntity response = amenityController.deleteAmenity(TEST_AMENITY_ID);

    // then
    assertNull(response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(amenitySDJpaService).deleteAmenity(TEST_AMENITY_ID);
  }

  /**
   * tests the update amenity endpoint by providing a valid request to update an amenity
   * and verifying that the response status code is `HttpStatus.NO_CONTENT` and the
   * amenity is updated in the database.
   */
  @Test
  void shouldUpdateAmenitySuccessfully() {
    // given
    AmenityDto amenityDto = getTestAmenityDto();
    UpdateAmenityRequest request = getUpdateAmenityRequest();

    given(amenityApiMapper.updateAmenityRequestToAmenityDto(request))
        .willReturn(amenityDto);
    given(amenitySDJpaService.updateAmenity(amenityDto))
        .willReturn(true);

    // when
    ResponseEntity<Void> responseEntity =
        amenityController.updateAmenity(TEST_AMENITY_ID, request);

    // then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(amenityApiMapper).updateAmenityRequestToAmenityDto(request);
    verify(amenitySDJpaService).updateAmenity(amenityDto);
  }

  /**
   * tests whether an attempt to update an amenity that does not exist will result in
   * a `HttpStatus.NOT_FOUND` response. It uses mocking to verify the calls to
   * `amenityApiMapper` and `amenitySDJpaService`.
   */
  @Test
  void shouldNotUpdateCommunityAmenityIfAmenityNotExists() {
    // given
    AmenityDto amenityDto = getTestAmenityDto();
    UpdateAmenityRequest request = getUpdateAmenityRequest();

    given(amenityApiMapper.updateAmenityRequestToAmenityDto(request))
        .willReturn(amenityDto);
    given(amenitySDJpaService.updateAmenity(amenityDto))
        .willReturn(false);

    // when
    ResponseEntity<Void> responseEntity =
        amenityController.updateAmenity(TEST_AMENITY_ID, request);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(amenityApiMapper).updateAmenityRequestToAmenityDto(request);
    verify(amenitySDJpaService).updateAmenity(amenityDto);
  }

  /**
   * creates a new instance of the `Amenity` class with predefined ID and description
   * for testing purposes.
   * 
   * @returns a new `Amenity` object with a predefined ID and description.
   * 
   * 	- `amenityId`: A unique identifier for the amenity, set to `TEST_AMENITY_ID`.
   * 	- `description`: A brief description of the amenity, set to `TEST_AMENITY_DESCRIPTION`.
   */
  private Amenity getTestAmenity() {
    return new Amenity()
        .withAmenityId(TEST_AMENITY_ID)
        .withDescription(TEST_AMENITY_DESCRIPTION);
  }

  /**
   * creates a new instance of the `AmenityDto` class with predefined values for an
   * amenity's ID, name, description, price, and community ID for testing purposes.
   * 
   * @returns a `AmenityDto` object containing mock data for testing purposes.
   * 
   * 	- `id`: A long value representing the unique identifier for the amenity.
   * 	- `amenityId`: An integer value representing the amenity ID.
   * 	- `name`: A string value representing the name of the amenity.
   * 	- `description`: A string value representing the description of the amenity.
   * 	- `price`: A double value representing the price of the amenity.
   * 	- `communityId`: A long value representing the community ID associated with the
   * amenity.
   */
  private AmenityDto getTestAmenityDto() {
    return new AmenityDto()
        .id(1L)
        .amenityId(TEST_AMENITY_ID)
        .name(TEST_AMENITY_NAME)
        .description(TEST_AMENITY_DESCRIPTION)
        .price(TEST_AMENITY_PRICE)
        .communityId(TEST_COMMUNITY_ID);
  }

  /**
   * creates a new `UpdateAmenityRequest` object with predefined values for name,
   * description, price, and community ID.
   * 
   * @returns an instance of the `UpdateAmenityRequest` class with set fields for name,
   * description, price, and community ID.
   * 
   * 	- The function returns an `UpdateAmenityRequest` object, which represents a request
   * to update an amenity in the community.
   * 	- The `name` property is set to a specific value, `TEST_AMENITY_NAME`, indicating
   * the name of the amenity to be updated.
   * 	- The `description` property is set to another specific value, `TEST_AMENITY_DESCRIPTION`,
   * representing the description of the amenity.
   * 	- The `price` property is set to a long value, `1L`, indicating the price of the
   * amenity.
   * 	- The `communityId` property is set to a specific value, `TEST_COMMUNITY_ID`,
   * identifying the community in which the amenity is located.
   */
  private UpdateAmenityRequest getUpdateAmenityRequest() {
    return new UpdateAmenityRequest()
        .name(TEST_AMENITY_NAME)
        .description(TEST_AMENITY_DESCRIPTION)
        .price(1L)
        .communityId(TEST_COMMUNITY_ID);
  }
}