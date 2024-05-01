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

import com.myhome.api.CommunitiesApi;
import com.myhome.controllers.dto.CommunityDto;
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
import com.myhome.model.ListCommunityAdminsResponse;
import com.myhome.services.CommunityService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller which provides endpoints for managing community
 */
/**
 * in the provided code handles various operations related to communities, including
 * creating, reading, updating, and deleting communities. The controller also provides
 * methods for removing houses from communities and admins from communities. The
 * methods return response entities with HTTP status codes indicating the result of
 * the operation, such as NO_CONTENT or NOT_FOUND.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class CommunityController implements CommunitiesApi {
  private final CommunityService communityService;
  private final CommunityApiMapper communityApiMapper;

  /**
   * Takes a `CreateCommunityRequest` object as input, maps it to a `CommunityDto`,
   * creates a new community using the `communityService`, and then maps the created
   * community back to a `CreateCommunityResponse`. The response is then returned with
   * a status code of `HttpStatus.CREATED`.
   * 
   * @param request CreateCommunityRequest object containing the details of the community
   * to be created, which is used by the function to create the corresponding Community
   * entity and return the response.
   * 
   * @returns a `CreateCommunityResponse` object containing the created community details.
   */
  @Override
  public ResponseEntity<CreateCommunityResponse> createCommunity(@Valid @RequestBody
      CreateCommunityRequest request) {
    log.trace("Received create community request");
    CommunityDto requestCommunityDto =
        communityApiMapper.createCommunityRequestToCommunityDto(request);
    Community createdCommunity = communityService.createCommunity(requestCommunityDto);
    CreateCommunityResponse createdCommunityResponse =
        communityApiMapper.communityToCreateCommunityResponse(createdCommunity);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCommunityResponse);
  }

  /**
   * Receives a `Pageable` object and lists all communities from the database using the
   * `communityService`. It then maps the community details to a REST API response
   * format using `communityApiMapper`, and returns a `ResponseEntity` with the list
   * of communities in the response body.
   * 
   * @param pageable page number and the page size for fetching community details from
   * the service.
   * 
   * @returns a list of community details in REST API format.
   */
  @Override
  public ResponseEntity<GetCommunityDetailsResponse> listAllCommunity(
      @PageableDefault(size = 200) Pageable pageable) {
    log.trace("Received request to list all community");

    Set<Community> communityDetails = communityService.listAll(pageable);
    Set<GetCommunityDetailsResponseCommunity> communityDetailsResponse =
        communityApiMapper.communitySetToRestApiResponseCommunitySet(communityDetails);

    GetCommunityDetailsResponse response = new GetCommunityDetailsResponse();
    response.getCommunities().addAll(communityDetailsResponse);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Receives a community ID from the path variable and retrieves the details of that
   * community from the service layer using the `communityService`. It then maps the
   * result to a `GetCommunityDetailsResponse` object and returns it as a response entity.
   * 
   * @param communityId ID of the community to retrieve details for.
   * 
   * @returns a `ResponseEntity` object representing a successful response with the
   * list of community details.
   */
  @Override
  public ResponseEntity<GetCommunityDetailsResponse> listCommunityDetails(
      @PathVariable String communityId) {
    log.trace("Received request to get details about community with id[{}]", communityId);

    return communityService.getCommunityDetailsById(communityId)
        .map(communityApiMapper::communityToRestApiResponseCommunity)
        .map(Arrays::asList)
        .map(HashSet::new)
        .map(communities -> new GetCommunityDetailsResponse().communities(communities))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Receives a community ID and a pageable request parameter, queries the community
   * service to retrieve a list of admins for that community, maps the result to a
   * `HashSet`, converts it to a REST API response, and returns an `ResponseEntity`
   * with the list of admins.
   * 
   * @param communityId ID of the community for which the admins are to be listed.
   * 
   * @param pageable page number and the number of admins per page to be retrieved from
   * the database for listing all admins of a community.
   * 
   * @returns a `ResponseEntity` object of type `ListCommunityAdminsResponse`, containing
   * a list of community admins.
   */
  @Override
  public ResponseEntity<ListCommunityAdminsResponse> listCommunityAdmins(
      @PathVariable String communityId,
      @PageableDefault(size = 200) Pageable pageable) {
    log.trace("Received request to list all admins of community with id[{}]", communityId);

    return communityService.findCommunityAdminsById(communityId, pageable)
        .map(HashSet::new)
        .map(communityApiMapper::communityAdminSetToRestApiResponseCommunityAdminSet)
        .map(admins -> new ListCommunityAdminsResponse().admins(admins))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Receives a community ID and a pageable parameter, retrieves the list of houses
   * associated with that community from the community service, maps the results to a
   * hash set, converts the hash set to a REST API response, and returns it as a `GetHouseDetailsResponse`.
   * 
   * @param communityId unique identifier of the community for which the user wants to
   * retrieve all houses.
   * 
   * @param pageable page number and page size for fetching the community houses,
   * allowing for pagination of the results.
   * 
   * @returns a `ResponseEntity` object containing a list of houses belonging to a
   * specific community.
   */
  @Override
  public ResponseEntity<GetHouseDetailsResponse> listCommunityHouses(
      @PathVariable String communityId,
      @PageableDefault(size = 200) Pageable pageable) {
    log.trace("Received request to list all houses of community with id[{}]", communityId);

    return communityService.findCommunityHousesById(communityId, pageable)
        .map(HashSet::new)
        .map(communityApiMapper::communityHouseSetToRestApiResponseCommunityHouseSet)
        .map(houses -> new GetHouseDetailsResponse().houses(houses))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Adds administrators to a community based on a request. It first checks if the
   * community exists, then it adds the admins to the community and returns the updated
   * community with the added admins.
   * 
   * @param communityId ID of the community to which admins are being added.
   * 
   * @param request AddCommunityAdminRequest object that contains the information about
   * the admins to be added to the community.
   * 
   * @returns a `ResponseEntity` with a status of `CREATED` and a body containing an
   * `AddCommunityAdminResponse` object with the added admins' user IDs.
   */
  @Override
  public ResponseEntity<AddCommunityAdminResponse> addCommunityAdmins(
      @PathVariable String communityId, @Valid @RequestBody
      AddCommunityAdminRequest request) {
    log.trace("Received request to add admin to community with id[{}]", communityId);
    Optional<Community> communityOptional =
        communityService.addAdminsToCommunity(communityId, request.getAdmins());
    return communityOptional.map(community -> {
      Set<String> adminsSet = community.getAdmins()
          .stream()
          .map(User::getUserId)
          .collect(Collectors.toSet());
      AddCommunityAdminResponse response = new AddCommunityAdminResponse().admins(adminsSet);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * Adds houses to a community by converting house names to CommunityHouse objects,
   * adding them to the community, and returning the updated list of houses for the community.
   * 
   * @param communityId id of the community to which the houses will be added.
   * 
   * @param request AddCommunityHouseRequest object containing the house names to be
   * added to the specified community.
   * 
   * @returns a `ResponseEntity` object with a `HttpStatus` code of `CREATED` and a
   * `AddCommunityHouseResponse` object as its body.
   */
  @Override
  public ResponseEntity<AddCommunityHouseResponse> addCommunityHouses(
      @PathVariable String communityId, @Valid @RequestBody
      AddCommunityHouseRequest request) {
    log.trace("Received request to add house to community with id[{}]", communityId);
    Set<CommunityHouseName> houseNames = request.getHouses();
    Set<CommunityHouse> communityHouses =
        communityApiMapper.communityHouseNamesSetToCommunityHouseSet(houseNames);
    Set<String> houseIds = communityService.addHousesToCommunity(communityId, communityHouses);
    if (houseIds.size() != 0 && houseNames.size() != 0) {
      AddCommunityHouseResponse response = new AddCommunityHouseResponse();
      response.setHouses(houseIds);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Receives a request to delete a house from a community, retrieves the community
   * details and houses associated with the given community ID, and returns a response
   * entity indicating whether the house was successfully removed.
   * 
   * @param communityId unique identifier of a community that the house belongs to.
   * 
   * @param houseId 12-digit unique identifier of the house to be removed from the
   * specified community.
   * 
   * @returns a `ResponseEntity` object with a `statusCode` of `NO_CONTENT`, indicating
   * that the house has been successfully removed from the community.
   */
  @Override
  public ResponseEntity<Void> removeCommunityHouse(
      @PathVariable String communityId, @PathVariable String houseId
  ) {
    log.trace(
        "Received request to delete house with id[{}] from community with id[{}]",
        houseId, communityId);

    Optional<Community> communityOptional = communityService.getCommunityDetailsById(communityId);

    return communityOptional.filter(
        community -> communityService.removeHouseFromCommunityByHouseId(community, houseId))
        .map(removed -> ResponseEntity.noContent().<Void>build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Deletes an admin from a community based on their ID, returning a status code
   * indicating success or failure.
   * 
   * @param communityId identifier of the community to which the admin belongs.
   * 
   * @param adminId ID of the admin to be removed from the community.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the admin was successfully removed from the community.
   */
  @Override
  public ResponseEntity<Void> removeAdminFromCommunity(
      @PathVariable String communityId, @PathVariable String adminId) {
    log.trace(
        "Received request to delete an admin from community with community id[{}] and admin id[{}]",
        communityId, adminId);
    boolean adminRemoved = communityService.removeAdminFromCommunity(communityId, adminId);
    if (adminRemoved) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Deletes a community with the given ID using the `communityService`. If the delete
   * is successful, it returns a `ResponseEntity` with status code `NO_CONTENT`. If the
   * delete fails, it returns a `ResponseEntity` with status code `NOT_FOUND`.
   * 
   * @param communityId ID of the community to be deleted.
   * 
   * @returns a `ResponseEntity` with a status code of either `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the community was successfully deleted.
   */
  @Override
  public ResponseEntity<Void> deleteCommunity(@PathVariable String communityId) {
    log.trace("Received delete community request");
    boolean isDeleted = communityService.deleteCommunity(communityId);
    if (isDeleted) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
