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
 * is a unit test class for the AmenityController class. It provides tests for updating
 * an amenity in the community, checking whether the update is successful and the
 * amenity exists before update, and verifying the calls to the amenity API mapper
 * and amenity SDJpaService.
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
   * tests the `addAmenityToCommunity` endpoint by providing an amenity to be added to
   * a community and verifying that the response status code is OK.
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
   * verifies that an attempt to add an amenity to a community that does not exist
   * returns a `HttpStatus.NOT_FOUND`.
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
   * retrieves an amenity's details by its ID, returns the response body in the form
   * of `GetAmenityDetailsResponse`, and verifies that the status code is `HttpStatus.OK`.
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
   * verifies that when an amenity with the given ID does not exist, it returns a
   * `HttpStatus.NOT_FOUND` response and calls `amenitySDJpaService.getAmenityDetails(TEST_AMENITY_ID)`
   * to verify its execution.
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
   * tests the delete amenity method of the amenity controller by providing a mocked
   * response from the amenity SDJpaService, verifying the response of the method and
   * asserting that the body of the response is null and the status code is HttpStatus.NO_CONTENT.
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
   * tests the scenario where the amenity to be deleted does not exist in the database,
   * and verifies the response of the controller, the status code, and the call to the
   * service method.
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
   * tests the updateAmenity method of the AmenityController by providing a test amenity
   * dto, creating a request object, and then verifying that the method returns a
   * response entity with a NO_CONTENT status code and updates the amenity in the
   * database using the amenityApiMapper and amenitySDJpaService.
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
   * tests whether an amenity is updated successfully when it does not exist in the database.
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
   * creates a new `Amenity` object with a predefined ID and description for testing purposes.
   * 
   * @returns a new `Amenity` object with predefined ID and description.
   * 
   * 	- `amenityId`: An integer value representing the unique identification of the amenity.
   * 	- `description`: A string value providing a brief description of the amenity.
   */
  private Amenity getTestAmenity() {
    return new Amenity()
        .withAmenityId(TEST_AMENITY_ID)
        .withDescription(TEST_AMENITY_DESCRIPTION);
  }

  /**
   * creates a new instance of the `AmenityDto` class with predefined values for an
   * amenity, including ID, amenity ID, name, description, price, and community ID.
   * 
   * @returns an instance of `AmenityDto` with predefined values.
   * 
   * 1/ `id`: A long value representing the unique identifier for the amenity.
   * 2/ `amenityId`: An integer value representing the amenity ID.
   * 3/ `name`: A string value representing the name of the amenity.
   * 4/ `description`: A string value representing the description of the amenity.
   * 5/ `price`: A double value representing the price of the amenity.
   * 6/ `communityId`: An integer value representing the community ID associated with
   * the amenity.
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
   * creates a new instance of `UpdateAmenityRequest`, setting the name, description,
   * price, and community ID to predefined values.
   * 
   * @returns an `UpdateAmenityRequest` object containing the specified name, description,
   * price, and community ID.
   * 
   * 	- `name`: A string variable representing the name of the amenity.
   * 	- `description`: A string variable representing the description of the amenity.
   * 	- `price`: An long integer variable representing the price of the amenity.
   * 	- `communityId`: An integer variable representing the ID of the community to which
   * the amenity belongs.
   */
  private UpdateAmenityRequest getUpdateAmenityRequest() {
    return new UpdateAmenityRequest()
        .name(TEST_AMENITY_NAME)
        .description(TEST_AMENITY_DESCRIPTION)
        .price(1L)
        .communityId(TEST_COMMUNITY_ID);
  }
}