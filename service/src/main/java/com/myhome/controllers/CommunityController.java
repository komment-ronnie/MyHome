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
 * handles various operations related to communities, including listing all communities,
 * listing community admins, listing community houses, adding a new community, adding
 * a new house to a community, removing a house from a community, removing an admin
 * from a community, and deleting a community. The controller uses the `CommunityService`
 * class to perform these operations and returns response entities based on the outcome
 * of each operation.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class CommunityController implements CommunitiesApi {
  private final CommunityService communityService;
  private final CommunityApiMapper communityApiMapper;

  /**
   * receives a `CreateCommunityRequest` object, converts it into a `CommunityDto`,
   * creates a new community using the `communityService`, and maps the created community
   * back to a `CreateCommunityResponse`. The response is then returned with a status
   * code of `HttpStatus.CREATED`.
   * 
   * @param request CreateCommunityRequest object that contains the data required to
   * create a new community, which is then used by the function to create the community
   * and return the response.
   * 
   * 	- `@Valid`: This annotation indicates that the `CreateCommunityRequest` object
   * has been validated by the `@Valid` processor.
   * 	- `@RequestBody`: This annotation specifies that the `CreateCommunityRequest`
   * object should be passed in the request body of the API.
   * 	- `CreateCommunityRequest`: This class represents the request to create a community,
   * containing various attributes such as name, description, and tags.
   * 
   * @returns a `CreateCommunityResponse` object containing the newly created community
   * details.
   * 
   * 	- `ResponseEntity`: This is a class that represents a response entity, which is
   * an aggregation of metadata and content. In this case, the content is the `CreateCommunityResponse`.
   * 	- `HttpStatus`: This is an enumeration that indicates the HTTP status code of the
   * response. In this case, it is set to `CREATED`, which means the request was
   * successful and the community was created.
   * 	- `body`: This is a reference to the content of the response entity, which in
   * this case is a `CreateCommunityResponse`.
   * 	- `CreateCommunityResponse`: This class represents the response to the create
   * community request. It contains various properties, including the ID of the newly
   * created community, the name of the community, and the status of the creation (either
   * `CREATED` or `FAILED`).
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
   * retrieves a list of all communities from the community service and maps them to a
   * REST API response object using the `communityApiMapper`. The response is then
   * returned as a `ResponseEntity` with an HTTP status code of `OK` and the mapped
   * community details in the body.
   * 
   * @param pageable page size and sort order for retrieving community details from the
   * service.
   * 
   * 	- `@PageableDefault(size = 200)` - The page size is set to 200 by default.
   * 
   * @returns a list of community details in REST API format.
   * 
   * 	- `GetCommunityDetailsResponse`: This is the class that represents the response
   * entity returned by the function. It has a `getCommunities()` method that adds all
   * the community details returned by the function to a list.
   * 	- `pageable`: This is an optional parameter that represents the pageable request
   * parameters. If present, it defines the size of the page and the Sort order for the
   * results.
   * 	- `communityService`: This is the class that provides the methods for listing all
   * communities.
   * 	- `communityApiMapper`: This is the class that maps the community service results
   * to a rest API response.
   * 
   * In summary, the function receives a `pageable` parameter, lists all communities
   * using the `communityService`, and maps the results to a REST API response using
   * the `communityApiMapper`. The response entity returned by the function has a list
   * of community details as its property.
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
   * retrieves community details based on the given ID and maps them to a REST API
   * response. It returns an `ResponseEntity` with the converted communities list.
   * 
   * @param communityId unique identifier of the community whose details are being requested.
   * 
   * @returns a `ResponseEntity` object with a status of `ok` and a list of communities.
   * 
   * 	- `ResponseEntity<GetCommunityDetailsResponse>` is an entity that wraps the
   * response to the request. It has an `ok` field which is set to `true` if the request
   * was successful and a `body` field that contains the `GetCommunityDetailsResponse`.
   * 	- `GetCommunityDetailsResponse` is a class that represents the response to the
   * request. It has a `communities` field that contains a list of communities.
   * 	- The `communities` list is an array of `Community` objects, each representing a
   * community with its ID, name, and other details.
   * 	- The `map` methods are used to convert the entities returned by the service into
   * the desired response format. For example, `map(communityApiMapper::communityToRestApiResponseCommunity)`
   * maps the `Community` object to the `GetCommunityDetailsResponse.communities` list.
   * 	- The `orElseGet` method is used to provide a default response if the request
   * fails. In this case, it returns a `ResponseEntity.notFound().build()` object with
   * an error message indicating that the community with the provided ID could not be
   * found.
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
   * receives a community ID and pageable parameter, queries the community service to
   * retrieve a list of admins, maps the results to a REST API response format, and
   * returns the response entity.
   * 
   * @param communityId identifier of the community for which the list of admins is requested.
   * 
   * @param pageable page request parameters, such as the page number, size, and sort
   * order, that determine how the list of community admins is retrieved and paginated.
   * 
   * 	- `@PageableDefault(size = 200)` specifies that the pageable request should default
   * to a page size of 200.
   * 	- `Pageable pageable` represents the pageable request, which can be modified or
   * expanded based on the requirements of the function.
   * 
   * @returns a `ResponseEntity` object representing a successful response with a list
   * of community admins.
   * 
   * 	- `ResponseEntity<ListCommunityAdminsResponse>`: This is the generic type of the
   * response entity, which contains a list of `CommunityAdmin` objects.
   * 	- `ListCommunityAdminsResponse`: This class represents the response to the API
   * request, which contains a list of `CommunityAdmin` objects.
   * 	- `admins(List<CommunityAdmin>)`: This method returns the list of `CommunityAdmin`
   * objects contained in the response.
   * 	- `ok()`: This method builds an HTTP 200 OK response entity with the list of
   * `CommunityAdmin` objects.
   * 	- `notFound()`: This method builds an HTTP 404 NOT FOUND response entity.
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
   * receives a community ID and a pageable parameter, and returns a `ResponseEntity`
   * containing a list of houses belonging to that community.
   * 
   * @param communityId ID of the community for which the user wants to list all houses.
   * 
   * @param pageable page number and page size for fetching houses from the community,
   * allowing for pagination of the result set.
   * 
   * 	- `@PageableDefault(size = 200)` specifies the default page size for the list of
   * houses returned in the response. The value `200` indicates that the list will
   * contain a maximum of 200 houses per page.
   * 
   * @returns a `ResponseEntity` object representing a successful response with a list
   * of community houses in a JSON format.
   * 
   * 	- `ResponseEntity<GetHouseDetailsResponse>`: This is the type of the returned
   * entity, which contains a `houses` field that is a list of `CommunityHouseSet` objects.
   * 	- `GetHouseDetailsResponse`: This is the inner type of the returned entity, which
   * represents the response to the list houses API endpoint. It has a `houses` field
   * that is a list of `CommunityHouse` objects.
   * 	- `CommunityHouseSet`: This is the inner type of the `houses` field in the returned
   * entity, which represents a set of `CommunityHouse` objects for a particular community
   * ID.
   * 	- `CommunityHouse`: This is the inner type of the `CommunityHouse` field in the
   * `CommunityHouseSet` object, which represents a single house belonging to a particular
   * community. It has fields for the house ID, community ID, and other house-related
   * details.
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
   * adds admins to a community identified by the provided ID. It first checks if there
   * are any admins to be added, then calls the `addAdminsToCommunity` method to perform
   * the addition. The function returns a response entity with the updated admin set.
   * 
   * @param communityId ID of the community to which admins are being added.
   * 
   * @param request AddCommunityAdminRequest object containing the admin details to be
   * added to the specified community.
   * 
   * 	- `@Valid`: Indicates that the request body is valid and contains the required
   * information for adding admins to a community.
   * 	- `@RequestBody`: Represents the request body as a whole, which contains the
   * `AddCommunityAdminRequest` object.
   * 	- `AddCommunityAdminRequest`: A class that represents the request sent by the
   * client to add admins to a community. It contains several attributes:
   * 	+ `communityId`: The ID of the community to which the admins will be added.
   * 	+ `admins`: A list of user IDs that will be added as admins to the specified community.
   * 
   * The function then processes the request and returns a response entity accordingly.
   * 
   * @returns a `ResponseEntity` object with a status code of `CREATED` and a body
   * containing an `AddCommunityAdminResponse` object with the added admins.
   * 
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response object in the REST API. It has a `status` field that indicates
   * the HTTP status code of the response (e.g., 200 for OK, 404 for Not Found).
   * 	- `body`: This is a reference to the body of the response entity. In this case,
   * it contains an instance of the `AddCommunityAdminResponse` class, which represents
   * the result of adding admins to a community.
   * 	- `admins`: This is a set of strings that represent the IDs of the added admins.
   * 
   * The `addCommunityAdmins` function either returns a response entity with a status
   * code of 201 (Created) and a body containing an `AddCommunityAdminResponse`, or it
   * returns a response entity with a status code of 404 (Not Found) if the community
   * with the given ID does not exist.
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
   * receives a request to add houses to a community, retrieves the list of houses and
   * their corresponding IDs, adds them to the community using the community service,
   * and returns the response with the added houses.
   * 
   * @param communityId identifier of the community to which the houses will be added.
   * 
   * @param request AddCommunityHouseRequest object containing the house names to be
   * added to the specified community, which is passed in from the HTTP request body.
   * 
   * 	- `@Valid`: Indicates that the request body must be valid according to the provided
   * validation rules.
   * 	- `@PathVariable String communityId`: The ID of the community to which the houses
   * will be added.
   * 	- `@RequestBody AddCommunityHouseRequest request`: The request body contains the
   * houses to be added to the community, which are mapped to a `Set` of `CommunityHouseName`
   * objects using the `communityApiMapper`.
   * 
   * @returns a `ResponseEntity` object with a ` HttpStatus` code of `CREATED` and a
   * `AddCommunityHouseResponse` object containing the added houses.
   * 
   * 	- `AddCommunityHouseResponse`: This is the class that represents the response to
   * the API request. It has a single field called `houses`, which is a set of strings
   * representing the IDs of the added houses.
   * 	- `HttpStatus`: The status code of the response, which can be either `CREATED`
   * or `BAD_REQUEST`.
   * 	- `ResponseEntity`: This is the class that represents the response object returned
   * by the API. It has a status code and a body, which in this case is an instance of
   * `AddCommunityHouseResponse`.
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
   * retrieves a community and a house ID from the request parameters, then deletes the
   * house from the community using the `communityService`. If successful, it returns
   * a `ResponseEntity` with a `Void` body.
   * 
   * @param communityId ID of the community to which the house belongs, which is used
   * to retrieve the community details and remove the house from it.
   * 
   * @param houseId ID of the house to be removed from the specified community.
   * 
   * @returns a `ResponseEntity` object representing a successful removal of the house
   * from the specified community.
   * 
   * 	- `ResponseEntity<Void>`: The type of the response entity, which is Void in this
   * case.
   * 	- `noContent()`: This method returns a ResponseEntity with a status code of 204
   * (No Content), indicating that the house has been successfully removed from the
   * community without any content returned in the response body.
   * 	- `<Void>`: The type parameter of the ResponseEntity, which represents Void in
   * this case.
   * 
   * Therefore, the output of the `removeCommunityHouse` function can be destructured
   * as follows:
   * 
   * ResponseEntity<Void> removed = communityOptional.filter(community ->
   * communityService.removeHouseFromCommunityByHouseId(community, houseId))
   *             .map(removed -> ResponseEntity.noContent().<Void>build())
   *             .orElseGet(() -> ResponseEntity.notFound().build());
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
   * removes an admin from a community based on the provided community ID and admin ID,
   * returning a response entity with HTTP status code indicating success or failure.
   * 
   * @param communityId identifier of the community for which an admin is to be removed.
   * 
   * @param adminId ID of the admin to be removed from the community.
   * 
   * @returns a `ResponseEntity` with a status code of either `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the admin was successfully removed from the community.
   * 
   * 	- `HttpStatus.NO_CONTENT`: This status code indicates that the request was
   * successful and resulted in no content being modified or created.
   * 	- `HttpStatus.NOT_FOUND`: This status code indicates that the admin could not be
   * removed from the community, likely because the community or the admin does not exist.
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
   * receives a community ID and deletes it from the service, returning a HTTP status
   * code indicating the result of the operation.
   * 
   * @param communityId ID of the community to be deleted.
   * 
   * @returns a `ResponseEntity` with a status code of either `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the community was successfully deleted.
   * 
   * 	- `HttpStatus.NO_CONTENT`: This indicates that the community was successfully deleted.
   * 	- `HttpStatus.NOT_FOUND`: This indicates that the specified community could not
   * be found.
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
