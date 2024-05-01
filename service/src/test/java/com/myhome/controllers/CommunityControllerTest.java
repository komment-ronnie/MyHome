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

import com.myhome.controllers.dto.CommunityDto;
import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.mapper.CommunityApiMapper;
import com.myhome.domain.Community;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.User;
import com.myhome.model.AddCommunityAdminRequest;
import com.myhome.model.AddCommunityAdminResponse;
import com.myhome.model.AddCommunityHouseRequest;
import com.myhome.model.AddCommunityHouseResponse;
import com.myhome.model.CommunityHouseName;
import com.myhome.model.CreateCommunityRequest;
import com.myhome.model.CreateCommunityResponse;
import com.myhome.model.GetCommunityDetailsResponse;
import com.myhome.model.GetCommunityDetailsResponseCommunity;
import com.myhome.model.GetHouseDetailsResponse;
import com.myhome.model.GetHouseDetailsResponseCommunityHouse;
import com.myhome.model.ListCommunityAdminsResponse;
import com.myhome.model.ListCommunityAdminsResponseCommunityAdmin;
import com.myhome.services.CommunityService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * tests various endpoints related to communities in the API, including adding and
 * removing houses, admins, and communities, as well as deleting communities. The
 * tests verify that the correct HTTP status codes are returned and that the community
 * service is properly updated when the controller receives requests.
 */
class CommunityControllerTest {
  private static final String COMMUNITY_ADMIN_ID = "1";
  private static final String COMMUNITY_ADMIN_NAME = "Test Name";
  private static final String COMMUNITY_ADMIN_EMAIL = "testadmin@myhome.com";
  private static final String COMMUNITY_ADMIN_PASSWORD = "testpassword@myhome.com";
  private static final String COMMUNITY_HOUSE_ID = "2";
  private static final String COMMUNITY_HOUSE_NAME = "Test House";
  private static final String COMMUNITY_NAME = "Test Community";
  private static final String COMMUNITY_ID = "3";
  private static final String COMMUNITY_DISTRICT = "Wonderland";

  @Mock
  private CommunityService communityService;

  @Mock
  private CommunityApiMapper communityApiMapper;

  @InjectMocks
  private CommunityController communityController;

  /**
   * initializes Mockito mocks for the class under test.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * creates a new `CommunityDto` object with the specified community ID, name, district,
   * and admin user Dtos.
   * 
   * @returns a `CommunityDto` object with a single admin user.
   * 
   * 1/ `communityAdminDtos`: A set of `UserDto` objects representing the community
   * administrators for the created community. Each `UserDto` object has a `userId`,
   * `name`, `email`, `password`, and `communityIds` field.
   * 2/ `communityId`: The ID of the created community.
   * 3/ `name`: The name of the created community.
   * 4/ `district`: The district of the created community.
   * 5/ `admins`: A set of `UserDto` objects representing the community administrators
   * for the created community.
   */
  private CommunityDto createTestCommunityDto() {
    Set<UserDto> communityAdminDtos = new HashSet<>();
    UserDto userDto = UserDto.builder()
        .userId(COMMUNITY_ADMIN_ID)
        .name(COMMUNITY_ADMIN_NAME)
        .email(COMMUNITY_ADMIN_NAME)
        .password(COMMUNITY_ADMIN_PASSWORD)
        .communityIds(new HashSet<>(singletonList(COMMUNITY_ID)))
        .build();

    communityAdminDtos.add(userDto);
    CommunityDto communityDto = new CommunityDto();
    communityDto.setCommunityId(COMMUNITY_ID);
    communityDto.setName(COMMUNITY_NAME);
    communityDto.setDistrict(COMMUNITY_DISTRICT);
    communityDto.setAdmins(communityAdminDtos);

    return communityDto;
  }

  /**
   * Creates a new instance of the `CommunityHouse` class with the provided community,
   * name, ID, and initial member and group sets.
   * 
   * @param community Community object that provides the context for the creation of a
   * new CommunityHouse instance.
   * 
   * @returns a `CommunityHouse` object representing the test community house with a
   * unique ID and name.
   */
  private CommunityHouse createTestCommunityHouse(Community community) {
    return new CommunityHouse(community, COMMUNITY_HOUSE_NAME, COMMUNITY_HOUSE_ID, new HashSet<>(),
        new HashSet<>());
  }

  /**
   * creates a new Community object with a default set of users and houses, and sets
   * the admin user's email and password.
   * 
   * @returns a new Community object containing an admin user and a test House.
   * 
   * 	- The `Community` object represents a test community with a set of houses and an
   * admin user.
   * 	- The `HashSet` objects in the `Community` constructor represent the initial
   * members and districts of the community.
   * 	- The `COMMUNITY_NAME` and `COMMUNITY_ID` fields represent the name and ID of the
   * community, respectively.
   * 	- The `COMMUNITY_DISTRICT` field represents the district of the community.
   * 	- The `User` object representing the admin user is added to the list of admins
   * in the community.
   * 	- The `HashSet` object representing the houses in the community is added to the
   * list of houses in the community.
   * 	- The `admin.getCommunities().add(community)` method adds the community to the
   * list of communities managed by the admin user.
   */
  private Community createTestCommunity() {
    Community community =
        new Community(new HashSet<>(), new HashSet<>(), COMMUNITY_NAME, COMMUNITY_ID,
            COMMUNITY_DISTRICT, new HashSet<>());
    User admin = new User(COMMUNITY_ADMIN_NAME, COMMUNITY_ADMIN_ID, COMMUNITY_ADMIN_EMAIL, true,
        COMMUNITY_ADMIN_PASSWORD, new HashSet<>(), null);
    community.getAdmins().add(admin);
    community.getHouses().add(createTestCommunityHouse(community));
    admin.getCommunities().add(community);

    return community;
  }

  /**
   * tests the create community API by creating a new community with a given name and
   * district, mapping the response to a CreateCommunityResponse object, and verifying
   * the status code and contents of the response.
   */
  @Test
  void shouldCreateCommunitySuccessfully() {
    // given
    CreateCommunityRequest request =
        new CreateCommunityRequest()
            .name(COMMUNITY_NAME)
            .district(COMMUNITY_DISTRICT);
    CommunityDto communityDto = createTestCommunityDto();
    CreateCommunityResponse response =
        new CreateCommunityResponse()
            .communityId(COMMUNITY_ID);
    Community community = createTestCommunity();

    given(communityApiMapper.createCommunityRequestToCommunityDto(request))
        .willReturn(communityDto);
    given(communityService.createCommunity(communityDto))
        .willReturn(community);
    given(communityApiMapper.communityToCreateCommunityResponse(community))
        .willReturn(response);

    // when
    ResponseEntity<CreateCommunityResponse> responseEntity =
        communityController.createCommunity(request);

    // then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityApiMapper).createCommunityRequestToCommunityDto(request);
    verify(communityApiMapper).communityToCreateCommunityResponse(community);
    verify(communityService).createCommunity(communityDto);
  }

  /**
   * tests the `listAllCommunity` method of a class by providing a set of communities
   * to be retrieved, and then verifying that the correct communities are returned in
   * the response.
   */
  @Test
  void shouldListAllCommunitiesSuccessfully() {
    // given
    Set<Community> communities = new HashSet<>();
    Community community = createTestCommunity();
    communities.add(community);

    Set<GetCommunityDetailsResponseCommunity> communityDetailsResponse
        = new HashSet<>();
    communityDetailsResponse.add(
        new GetCommunityDetailsResponseCommunity()
            .communityId(COMMUNITY_ID)
            .name(COMMUNITY_NAME)
            .district(COMMUNITY_DISTRICT)
    );

    GetCommunityDetailsResponse response = new GetCommunityDetailsResponse();
    response.getCommunities().addAll(communityDetailsResponse);

    Pageable pageable = PageRequest.of(0, 1);
    given(communityService.listAll(pageable))
        .willReturn(communities);
    given(communityApiMapper.communitySetToRestApiResponseCommunitySet(communities))
        .willReturn(communityDetailsResponse);

    // when
    ResponseEntity<GetCommunityDetailsResponse> responseEntity =
        communityController.listAllCommunity(pageable);

    // then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityApiMapper).communitySetToRestApiResponseCommunitySet(communities);
    verify(communityService).listAll(pageable);
  }

  /**
   * tests the listCommunityDetails method of a class, CommunityController. It verifies
   * that the method returns a response entity with the correct status code and the
   * community details object in the body of the response.
   */
  @Test
  void shouldGetCommunityDetailsSuccessfully() {
    // given
    Optional<Community> communityOptional = Optional.of(createTestCommunity());
    Community community = communityOptional.get();
    GetCommunityDetailsResponseCommunity communityDetails =
        new GetCommunityDetailsResponseCommunity()
            .communityId(COMMUNITY_ID)
            .name(COMMUNITY_NAME)
            .district(COMMUNITY_DISTRICT);

    Set<GetCommunityDetailsResponseCommunity> communityDetailsResponse
        = new HashSet<>();
    communityDetailsResponse.add(communityDetails);

    GetCommunityDetailsResponse response =
        new GetCommunityDetailsResponse().communities(communityDetailsResponse);

    given(communityService.getCommunityDetailsById(COMMUNITY_ID))
        .willReturn(communityOptional);
    given(communityApiMapper.communityToRestApiResponseCommunity(community))
        .willReturn(communityDetails);

    // when
    ResponseEntity<GetCommunityDetailsResponse> responseEntity =
        communityController.listCommunityDetails(COMMUNITY_ID);

    // then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityService).getCommunityDetailsById(COMMUNITY_ID);
    verify(communityApiMapper).communityToRestApiResponseCommunity(community);
  }

  /**
   * verifies that when the community ID is invalid, the listCommunityDetails method
   * returns a ResponseEntity with a HTTP status code of NOT_FOUND and an empty body.
   */
  @Test
  void shouldGetNotFoundListCommunityDetailsSuccess() {
    // given
    given(communityService.getCommunityDetailsById(COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<GetCommunityDetailsResponse> responseEntity =
        communityController.listCommunityDetails(COMMUNITY_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(communityService).getCommunityDetailsById(COMMUNITY_ID);
    verifyNoInteractions(communityApiMapper);
  }

  /**
   * tests the `listCommunityAdmins` endpoint by providing a community ID and a pageable
   * request object, then verifying that the response status code is 200 OK and that
   * the response body contains a list of admins in the expected format.
   */
  @Test
  void shouldListCommunityAdminsSuccess() {
    // given
    Community community = createTestCommunity();
    List<User> admins = new ArrayList<>(community.getAdmins());
    Optional<List<User>> communityAdminsOptional = Optional.of(admins);

    Pageable pageable = PageRequest.of(0, 1);

    given(communityService.findCommunityAdminsById(COMMUNITY_ID, pageable))
        .willReturn(communityAdminsOptional);

    Set<User> adminsSet = new HashSet<>(admins);

    Set<ListCommunityAdminsResponseCommunityAdmin> listAdminsResponses = new HashSet<>();
    listAdminsResponses.add(
        new ListCommunityAdminsResponseCommunityAdmin()
            .adminId(COMMUNITY_ADMIN_ID)
    );

    given(communityApiMapper.communityAdminSetToRestApiResponseCommunityAdminSet(adminsSet))
        .willReturn(listAdminsResponses);

    ListCommunityAdminsResponse response =
        new ListCommunityAdminsResponse().admins(listAdminsResponses);

    // when
    ResponseEntity<ListCommunityAdminsResponse> responseEntity =
        communityController.listCommunityAdmins(COMMUNITY_ID, pageable);

    // then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityApiMapper).communityAdminSetToRestApiResponseCommunityAdminSet(adminsSet);
    verify(communityService).findCommunityAdminsById(COMMUNITY_ID, pageable);
  }

  /**
   * tests the listCommunityAdmins endpoint by providing a non-existent community ID
   * and verifying that the response status code is NOT_FOUND and the body is null,
   * while also verifying that the findCommunityAdminsById method of the CommunityService
   * class is called with the non-existent community ID.
   */
  @Test
  void shouldReturnNoAdminDetailsNotFoundSuccess() {
    // given
    Pageable pageable = PageRequest.of(0, 1);

    given(communityService.findCommunityAdminsById(COMMUNITY_ID, pageable))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<ListCommunityAdminsResponse> responseEntity =
        communityController.listCommunityAdmins(COMMUNITY_ID, pageable);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(communityService).findCommunityAdminsById(COMMUNITY_ID, pageable);
    verifyNoInteractions(communityApiMapper);
  }

  /**
   * tests the addCommunityAdmins endpoint by adding admins to a community and verifying
   * that the response is a CREATED status code with the expected AddCommunityAdminResponse
   * body.
   */
  @Test
  void shouldAddCommunityAdminSuccess() {
    // given
    AddCommunityAdminRequest addRequest = new AddCommunityAdminRequest();
    Community community = createTestCommunity();
    Set<User> communityAdmins = community.getAdmins();
    for (User admin : communityAdmins) {
      addRequest.getAdmins().add(admin.getUserId());
    }

    Set<String> adminIds = addRequest.getAdmins();
    AddCommunityAdminResponse response = new AddCommunityAdminResponse().admins(adminIds);

    given(communityService.addAdminsToCommunity(COMMUNITY_ID, adminIds))
        .willReturn(Optional.of(community));

    // when
    ResponseEntity<AddCommunityAdminResponse> responseEntity =
        communityController.addCommunityAdmins(COMMUNITY_ID, addRequest);

    // then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityService).addAdminsToCommunity(COMMUNITY_ID, adminIds);
  }

  /**
   * tests whether an attempt to add admins to a community that does not exist results
   * in a `NOT_FOUND` status code and null response body.
   */
  @Test
  void shouldNotAddAdminToCommunityNotFoundSuccessfully() {
    // given
    AddCommunityAdminRequest addRequest = new AddCommunityAdminRequest();
    Community community = createTestCommunity();
    Set<User> communityAdmins = community.getAdmins();
    for (User admin : communityAdmins) {
      addRequest.getAdmins().add(admin.getUserId());
    }

    Set<String> adminIds = addRequest.getAdmins();

    given(communityService.addAdminsToCommunity(COMMUNITY_ID, adminIds))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<AddCommunityAdminResponse> responseEntity =
        communityController.addCommunityAdmins(COMMUNITY_ID, addRequest);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(communityService).addAdminsToCommunity(COMMUNITY_ID, adminIds);
  }

  /**
   * verifies that the `listCommunityHouses` endpoint returns a list of community houses,
   * including the expected house details in the response.
   */
  @Test
  void shouldListCommunityHousesSuccess() {
    Community community = createTestCommunity();
    List<CommunityHouse> houses = new ArrayList<>(community.getHouses());
    Set<CommunityHouse> housesSet = new HashSet<>(houses);
    Set<GetHouseDetailsResponseCommunityHouse> getHouseDetailsSet = new HashSet<>();
    getHouseDetailsSet.add(new GetHouseDetailsResponseCommunityHouse()
        .houseId(COMMUNITY_HOUSE_ID)
        .name(COMMUNITY_NAME)
    );

    GetHouseDetailsResponse response = new GetHouseDetailsResponse().houses(getHouseDetailsSet);
    Pageable pageable = PageRequest.of(0, 1);

    given(communityService.findCommunityHousesById(COMMUNITY_ID, pageable))
        .willReturn(Optional.of(houses));
    given(communityApiMapper.communityHouseSetToRestApiResponseCommunityHouseSet(housesSet))
        .willReturn(getHouseDetailsSet);

    // when
    ResponseEntity<GetHouseDetailsResponse> responseEntity =
        communityController.listCommunityHouses(COMMUNITY_ID, pageable);

    //then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityService).findCommunityHousesById(COMMUNITY_ID, pageable);
    verify(communityApiMapper).communityHouseSetToRestApiResponseCommunityHouseSet(housesSet);
  }

  /**
   * tests the `listCommunityHouses` endpoint by providing a non-existent community ID
   * and verifying a `NOT_FOUND` status code and an empty response body.
   */
  @Test
  void testListCommunityHousesCommunityNotExistSuccess() {
    // given
    Pageable pageable = PageRequest.of(0, 1);
    given(communityService.findCommunityHousesById(COMMUNITY_ID, pageable))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<GetHouseDetailsResponse> responseEntity =
        communityController.listCommunityHouses(COMMUNITY_ID, pageable);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(communityService).findCommunityHousesById(COMMUNITY_ID, pageable);
    verifyNoInteractions(communityApiMapper);
  }

  /**
   * tests the `AddCommunityHouse` endpoint by providing a valid request and verifying
   * that it returns a `HttpStatus.CREATED` response and the expected response body.
   */
  @Test
  void shouldAddCommunityHouseSuccessfully() {
    // given
    AddCommunityHouseRequest addCommunityHouseRequest = new AddCommunityHouseRequest();
    Community community = createTestCommunity();
    Set<CommunityHouse> communityHouses = community.getHouses();
    Set<CommunityHouseName> communityHouseNames = new HashSet<>();
    communityHouseNames.add(new CommunityHouseName().name(COMMUNITY_HOUSE_NAME));

    Set<String> houseIds = new HashSet<>();
    for (CommunityHouse house : communityHouses) {
      houseIds.add(house.getHouseId());
    }

    addCommunityHouseRequest.getHouses().addAll(communityHouseNames);

    AddCommunityHouseResponse response = new AddCommunityHouseResponse().houses(houseIds);

    given(communityApiMapper.communityHouseNamesSetToCommunityHouseSet(communityHouseNames))
        .willReturn(communityHouses);
    given(communityService.addHousesToCommunity(COMMUNITY_ID, communityHouses))
        .willReturn(houseIds);

    // when
    ResponseEntity<AddCommunityHouseResponse> responseEntity =
        communityController.addCommunityHouses(COMMUNITY_ID, addCommunityHouseRequest);

    // then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityApiMapper).communityHouseNamesSetToCommunityHouseSet(communityHouseNames);
    verify(communityService).addHousesToCommunity(COMMUNITY_ID, communityHouses);
  }

  /**
   * verifies that if an empty `AddCommunityHouseRequest` is passed to the `addCommunityHouses`
   * method, it should return a `HttpStatus.BAD_REQUEST` response and be silent on the
   * call to the `communityApiMapper` and `communityService`.
   */
  @Test
  void shouldThrowBadRequestWithEmptyAddHouseRequest() {
    // given
    AddCommunityHouseRequest emptyRequest = new AddCommunityHouseRequest();

    given(communityApiMapper.communityHouseNamesSetToCommunityHouseSet(emptyRequest.getHouses()))
        .willReturn(new HashSet<>());
    given(communityService.addHousesToCommunity(COMMUNITY_ID, new HashSet<>()))
        .willReturn(new HashSet<>());

    // when
    ResponseEntity<AddCommunityHouseResponse> responseEntity =
        communityController.addCommunityHouses(COMMUNITY_ID, emptyRequest);

    // then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(communityApiMapper).communityHouseNamesSetToCommunityHouseSet(new HashSet<>());
    verify(communityService).addHousesToCommunity(COMMUNITY_ID, new HashSet<>());
  }

  /**
   * tests the remove community house endpoint by creating a test community, getting
   * its details, removing the house from it, and verifying the response status code
   * and service calls.
   */
  @Test
  void shouldRemoveCommunityHouseSuccessfully() {
    // given
    Community community = createTestCommunity();

    given(communityService.getCommunityDetailsById(COMMUNITY_ID))
        .willReturn(Optional.of(community));
    given(communityService.removeHouseFromCommunityByHouseId(createTestCommunity(),
        COMMUNITY_HOUSE_ID))
        .willReturn(true);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.removeCommunityHouse(COMMUNITY_ID, COMMUNITY_HOUSE_ID);

    // then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(communityService).removeHouseFromCommunityByHouseId(community, COMMUNITY_HOUSE_ID);
    verify(communityService).getCommunityDetailsById(COMMUNITY_ID);
  }

  /**
   * verifies that removing a community house with a non-existent ID returns a NOT_FOUND
   * status code and calls the `removeHouseFromCommunityByHouseId` service method.
   */
  @Test
  void shouldNotRemoveCommunityHouseIfNotFoundSuccessfully() {
    // given
    Community community = createTestCommunity();

    given(communityService.getCommunityDetailsById(COMMUNITY_ID))
        .willReturn(Optional.of(community));
    given(communityService.removeHouseFromCommunityByHouseId(community, COMMUNITY_HOUSE_ID))
        .willReturn(false);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.removeCommunityHouse(COMMUNITY_ID, COMMUNITY_HOUSE_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(communityService).removeHouseFromCommunityByHouseId(community, COMMUNITY_HOUSE_ID);
  }

  /**
   * verifies that removing a community house with a given ID fails when the community
   * with that ID is not found in the database.
   */
  @Test
  void shouldNotRemoveCommunityHouseIfCommunityNotFound() {
    //given
    Community community = createTestCommunity();

    given(communityService.getCommunityDetailsById(COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<Void> responseEntity =
        communityController.removeCommunityHouse(COMMUNITY_ID, COMMUNITY_HOUSE_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(communityService).getCommunityDetailsById(COMMUNITY_ID);
    verify(communityService, never()).removeHouseFromCommunityByHouseId(community,
        COMMUNITY_HOUSE_ID);
  }

  /**
   * tests whether removing an admin from a community using the
   * `communityController.removeAdminFromCommunity()` method results in a successful
   * response with a `HttpStatus.NO_CONTENT` status code and verifies that the
   * `communityService.removeAdminFromCommunity()` method was called with the correct
   * parameters.
   */
  @Test
  void shouldRemoveAdminFromCommunitySuccessfully() {
    // given
    given(communityService.removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID))
        .willReturn(true);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID);

    // then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(communityService).removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID);
  }

  /**
   * verifies that removing an admin from a community returns a `HttpStatus.NOT_FOUND`
   * response if the admin is not found in the community's admins list.
   */
  @Test
  void shouldNotRemoveAdminIfNotFoundSuccessfully() {
    // given
    given(communityService.removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID))
        .willReturn(false);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(communityService).removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID);
  }

  /**
   * verifies that the `deleteCommunity` method of the `CommunityController` class can
   * successfully delete a community with the given ID.
   */
  @Test
  void shouldDeleteCommunitySuccessfully() {
    // given
    given(communityService.deleteCommunity(COMMUNITY_ID))
        .willReturn(true);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.deleteCommunity(COMMUNITY_ID);

    // then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(communityService).deleteCommunity(COMMUNITY_ID);
  }

  /**
   * verifies that deleting a community with a non-existent ID returns a `HttpStatus.NOT_FOUND`
   * response and invokes the `deleteCommunity` method of the `communityService`.
   */
  @Test
  void shouldNotDeleteCommunityNotFoundSuccessfully() {
    // given
    given(communityService.deleteCommunity(COMMUNITY_ID))
        .willReturn(false);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.deleteCommunity(COMMUNITY_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(communityService).deleteCommunity(COMMUNITY_ID);
  }

  /**
   * creates a new instance of `CommunityHouse`, setting its name, ID, and initial
   * members to their default values, and returns it.
   * 
   * @returns a mock `CommunityHouse` object.
   * 
   * 	- `CommunityHouse communityHouse`: This is an instance of the `CommunityHouse`
   * class, which represents a mock community house.
   * 	- `name`: The name of the community house, set to `COMMUNITY_HOUSE_NAME`.
   * 	- `houseId`: The ID of the community house, set to `COMMUNITY_HOUSE_ID`.
   * 	- `houseMembers`: A set of members of the community house, which is empty by default.
   */
  private CommunityHouse getMockCommunityHouse() {
    CommunityHouse communityHouse = new CommunityHouse();
    communityHouse.setName(COMMUNITY_HOUSE_NAME);
    communityHouse.setHouseId(COMMUNITY_HOUSE_ID);
    communityHouse.setHouseMembers(new HashSet<>());

    return communityHouse;
  }

  /**
   * Creates a mock Community object with pre-defined admins, houses, and district. It
   * returns the created Community object.
   * 
   * @param admins set of users who will be admins for the generated mock community.
   * 
   * @returns a mock Community object with admins and a House.
   */
  private Community getMockCommunity(Set<User> admins) {
    Community community =
        new Community(admins, new HashSet<>(), COMMUNITY_NAME, COMMUNITY_ID,
            COMMUNITY_DISTRICT, new HashSet<>());
    User admin = new User(COMMUNITY_ADMIN_NAME, COMMUNITY_ADMIN_ID, COMMUNITY_ADMIN_EMAIL, true,
        COMMUNITY_ADMIN_PASSWORD, new HashSet<>(), new HashSet<>());
    community.getAdmins().add(admin);
    admin.getCommunities().add(community);

    CommunityHouse communityHouse = getMockCommunityHouse();
    communityHouse.setCommunity(community);
    community.getHouses().add(communityHouse);

    return community;
  }
}
