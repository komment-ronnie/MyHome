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
   * receives a `CreateCommunityRequest` from the client, maps it to a `CommunityDto`,
   * creates a new community instance using the `communityService`, and returns a
   * `CreateCommunityResponse` with the created community details.
   * 
   * @param request CreateCommunityRequest object passed from the client, containing
   * the necessary data to create a new community.
   * 
   * 	- `CreateCommunityRequest request`: This is a class that contains fields for
   * community name, description, and image.
   * 	- `log.trace("Received create community request")`: This line logs a message
   * indicating that the method has received a create community request.
   * 	- `CommunityDto requestCommunityDto = communityApiMapper.createCommunityRequestToCommunityDto(request)`:
   * This line maps the input request to a `CommunityDto` object, which contains fields
   * for the community name and description.
   * 	- `Community createdCommunity = communityService.createCommunity(requestCommunityDto)`:
   * This line creates a new instance of the `Community` class using the `CommunityDto`
   * object as input.
   * 	- `CreateCommunityResponse createdCommunityResponse =
   * communityApiMapper.communityToCreateCommunityResponse(createdCommunity)`: This
   * line maps the newly created `Community` object to a `CreateCommunityResponse`
   * object, which contains fields for the community ID and URL.
   * 
   * @returns a `CreateCommunityResponse` object containing the newly created community
   * details.
   * 
   * 	- `ResponseEntity`: This is a class that represents an HTTP response entity with
   * a status code and a body. In this case, the status code is `HttpStatus.CREATED`,
   * which indicates that the request was successful and the community was created.
   * 	- `body`: This is the main content of the response entity, which in this case is
   * the `CreateCommunityResponse` object.
   * 	- `CreateCommunityResponse`: This class represents the response to a create
   * community request. It contains information about the created community, such as
   * its ID, name, and description.
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
   * receives a `Pageable` parameter and returns a `GetCommunityDetailsResponse` object
   * containing a list of communities retrieved from the service.
   * 
   * @param pageable page request parameters, such as the number of results per page
   * and the current page number, which are used to paginate the list of communities
   * returned by the `communityService.listAll()` method.
   * 
   * 	- `@PageableDefault(size = 200)`: This annotation sets the default page size to
   * 200 for the list of community details.
   * 
   * @returns a list of community details responses, containing the communities retrieved
   * from the service and mapped to the REST API response format.
   * 
   * 	- `GetCommunityDetailsResponse`: This class represents the response to the list
   * community request. It contains a list of `GetCommunityDetailsResponseCommunity`
   * objects, which represent the communities returned by the API.
   * 	- `getCommunities()`: This is a List<GetCommunityDetailsResponseCommunity> that
   * contains all the communities returned by the API.
   * 	- `HttpStatus.OK`: The HTTP status code of the response, indicating that the
   * request was successful.
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
   * receives a community ID and retrieves the details of the corresponding community
   * from the service. It then maps the response to a `GetCommunityDetailsResponse`
   * object and returns it as an `ResponseEntity`.
   * 
   * @param communityId id of the community whose details are requested.
   * 
   * @returns a `ResponseEntity` object containing a list of community details.
   * 
   * 	- `ResponseEntity<GetCommunityDetailsResponse>`: This is the type of the output
   * returned by the function, which is a wrapper class around the actual response. It
   * contains an instance of `GetCommunityDetailsResponse` along with any additional
   * metadata that may be present in the response.
   * 	- `GetCommunityDetailsResponse`: This is the inner class contained within
   * `ResponseEntity`, which represents the actual response returned by the function.
   * It contains a list of communities, represented as a `List<Community>` object.
   * 	- `communities(communities)`: This method is called on the `GetCommunityDetailsResponse`
   * instance and returns a `List<Community>` object containing the communities found
   * in the response.
   * 	- `map(Function<? super T, R> mappingFunction)`: This line uses the `map()` method
   * to apply a functional transformation to the output of the function. In this case,
   * the transformation is defined as a lambda expression that takes an instance of
   * `GetCommunityDetailsResponse` and returns a `ResponseEntity<GetCommunityDetailsResponse>`
   * instance with an updated response body containing the list of communities.
   * 	- `orElseGet(() -> ResponseEntity.notFound().build());`: This line provides an
   * alternative to the previous mapping function in case the original response is not
   * found. It creates a new `ResponseEntity` instance with a status code of 404 (Not
   * Found) and builds it using the `build()` method.
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
   * receives a community ID and page number, retrieves the list of admins for that
   * community from the database using the `communityService`, maps them to a REST API
   * response format using `communityApiMapper`, and returns a `ResponseEntity` with
   * the list of admins.
   * 
   * @param communityId ID of the community whose admins are to be listed.
   * 
   * @param pageable page parameters that control how the list of community admins is
   * fetched and displayed, such as the number of items per page and the current page
   * number.
   * 
   * 	- `size`: The number of results to be returned per page.
   * 	- `sort`: The field by which the list should be sorted.
   * 	- `direction`: The direction of the sort (ascending or descending).
   * 
   * @returns a `ResponseEntity` object containing a list of community admins.
   * 
   * 	- `ResponseEntity`: This is the base class for all response entities in Spring
   * WebFlux. It represents a response entity with an HTTP status code and headers.
   * 	- `ok`: This is a method that creates a ResponseEntity with an HTTP status code
   * of 200 (OK).
   * 	- `notFound`: This is a method that creates a ResponseEntity with an HTTP status
   * code of 404 (Not Found).
   * 	- `map`: This is a method that applies a function to the input data and returns
   * the result as a new ResponseEntity. In this case, it maps the `HashSet` of community
   * admins to a `ListCommunityAdminsResponse` object.
   * 	- `admins`: This is a variable that contains the list of community admins returned
   * by the `findCommunityAdminsById` method. It is of type `List<CommunityAdmin>`.
   * 	- `communityApiMapper`: This is an interface that defines the mapping between the
   * `CommunityAdmin` object and the `RestApiResponseCommunityAdminSet` object.
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
   * receives a community ID and pageable parameters, queries the community service to
   * retrieve a list of houses, maps them to a HashSet, converts them into a REST API
   * response, and returns it as an `ResponseEntity`.
   * 
   * @param communityId unique identifier of the community for which the user is
   * requesting to list all houses.
   * 
   * @param pageable page size and sort information for listing community houses.
   * 
   * 	- `PageableDefault`: This is an annotation that indicates the default page size
   * for the function. The value of 200 indicates that the function will return a page
   * of 200 houses by default.
   * 	- `size`: This is the page size specified in the `PageableDefault` annotation.
   * It can be overridden by passing a different value as an argument to the function.
   * 
   * @returns a `ResponseEntity` object representing a successful response with a list
   * of community houses.
   * 
   * 	- `ResponseEntity<GetHouseDetailsResponse>`: This is the type of the output
   * returned by the function, which is an entity representing a response to the list
   * community houses request.
   * 	- `GetHouseDetailsResponse`: This is the inner class of the `ResponseEntity`,
   * which contains the details of the houses listed in the community.
   * 	- `houses`: This is a `List` of `CommunityHouseSet` objects, which represent the
   * houses in the community.
   * 	- `map`: This is a method that maps the result of the `findCommunityHousesById`
   * method to a `GetHouseDetailsResponse` object.
   * 	- `orElseGet`: This is a method that returns an alternative response entity if
   * the `findCommunityHousesById` method does not return a valid result.
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
   * receives a request to add admins to a community, retrieves the community optional,
   * adds the admins to the community, and returns a response entity with the added admins.
   * 
   * @param communityId ID of the community to which admins are being added.
   * 
   * @param request AddCommunityAdminRequest object containing the details of the admins
   * to be added to the community.
   * 
   * 	- `@Valid`: This annotation indicates that the input request body must be valid
   * according to the AddCommunityAdminRequest schema defined in the application's configuration.
   * 	- `@PathVariable String communityId`: This specifies the ID of the community for
   * which admins are being added.
   * 	- `@RequestBody AddCommunityAdminRequest request`: This indicates that the input
   * request body contains the `AddCommunityAdminRequest` data structure, which includes
   * information about the admins to be added to the community.
   * 
   * @returns a `ResponseEntity` object with a status code of `CREATED` and a body
   * containing an `AddCommunityAdminResponse` object with the added admins.
   * 
   * 	- `ResponseEntity`: This is the type of the response entity, which is an instance
   * of `ResponseEntity`.
   * 	- `status`: This property holds the HTTP status code of the response entity. The
   * possible values for this property are `CREATED`, `OK`, and others.
   * 	- `body`: This property holds the body of the response entity, which is an instance
   * of `AddCommunityAdminResponse`.
   * 	- `admins`: This property holds a set of strings representing the IDs of the
   * admins added to the community.
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
   * receives a request to add one or more houses to a community, maps the house names
   * to community houses, and then adds the houses to the community. If the addition
   * is successful, it returns a response with the added houses, otherwise it returns
   * a bad request status.
   * 
   * @param communityId ID of the community to which the houses will be added.
   * 
   * @param request AddCommunityHouseRequest object that contains the houses to be added
   * to the community.
   * 
   * 	- `@Valid`: Indicates that the request body must be valid according to the specified
   * validation rules.
   * 	- `@RequestBody`: Represents the request body as a whole, indicating that it
   * should be serialized and sent in the request message.
   * 	- `AddCommunityHouseRequest`: The class representing the request body, which
   * contains the houses to be added to the community.
   * 	+ `getHouses()`: A set of community house names.
   * 	+ `setHouses()`: No-argument constructor for setting the houses field.
   * 
   * The function performs the following operations:
   * 
   * 1/ Logs a message with the community ID and the received request.
   * 2/ Maps the community house names to a set of community houses using the `communityApiMapper`.
   * 3/ Calls the `addHousesToCommunity` method of the `communityService` to add the
   * community houses to the community.
   * 4/ If the addition was successful, creates an `AddCommunityHouseResponse` object
   * and returns it in the response entity with a status code of `CREATED`.
   * 5/ Otherwise, returns a response entity with a status code of `BAD_REQUEST`.
   * 
   * @returns a `ResponseEntity` with a `AddCommunityHouseResponse` object containing
   * the IDs of the added houses.
   * 
   * 	- The response entity is an instance of `ResponseEntity`, which contains the
   * status code and body of the response.
   * 	- The status code is set to `HttpStatus.CREATED`, indicating that the request was
   * successful and the result is a created resource.
   * 	- The body of the response is an instance of `AddCommunityHouseResponse`, which
   * contains the IDs of the added houses.
   * 
   * The `AddCommunityHouseResponse` object has the following properties:
   * 
   * 	- `houses`: a set containing the IDs of the added houses.
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
   * deletes a house from a community, logging the request and checking if the house
   * exists before returning an HTTP response.
   * 
   * @param communityId ID of the community that the house to be deleted belongs to.
   * 
   * @param houseId ID of the house to be removed from the specified community.
   * 
   * @returns a `ResponseEntity` object representing a successful response with a status
   * code of `NO_CONTENT`.
   * 
   * 	- `ResponseEntity`: This is the base class for all response entities in Spring
   * WebFlux. It contains the status code and headers of the response.
   * 	- `noContent()`: This method builds a response entity with a status code of 204
   * (No Content), indicating that the operation was successful but there is no content
   * to return.
   * 	- `<Void>`: This is the type parameter for the response entity, indicating that
   * it will contain no data.
   * 
   * The various attributes of the returned output are:
   * 
   * 	- `statusCode`: The status code of the response, which in this case is 204 (No Content).
   * 	- `headers`: The headers of the response, which may include information such as
   * the Content-Type header indicating that the response contains no data.
   * 
   * In summary, the returned output of the `removeCommunityHouse` function is a response
   * entity with a status code of 204 (No Content) and an empty body.
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
   * removes an admin from a community based on their ID, returning a HTTP status code
   * indicating the result of the operation.
   * 
   * @param communityId identifier of the community whose admin is to be removed.
   * 
   * @param adminId ID of an administrator to be removed from a community.
   * 
   * @returns a HTTP `NO_CONTENT` status code indicating the admin was successfully removed.
   * 
   * 	- `ResponseEntity`: This is the class that represents the HTTP response entity,
   * which contains information about the status code and headers.
   * 	- `status`: This is a field of type `HttpStatus` that indicates the status code
   * of the response, which can be either `NO_CONTENT` or `NOT_FOUND`.
   * 	- `build()`: This is a method that builds the HTTP response entity based on the
   * status code and headers.
   * 
   * In summary, the output of the `removeAdminFromCommunity` function is an HTTP
   * response entity with a status code indicating whether the admin was successfully
   * removed from the community or not.
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
   * deletes a community identified by the `communityId` parameter, returning a
   * `ResponseEntity` with a status code indicating the result of the operation.
   * 
   * @param communityId ID of the community to be deleted.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the community was successfully deleted.
   * 
   * 	- `HttpStatus.NO_CONTENT`: This status code indicates that the requested resource
   * has been successfully deleted and no further content is available.
   * 	- `HttpStatus.NOT_FOUND`: This status code indicates that the requested community
   * with the provided ID could not be found, which may indicate an invalid or missing
   * ID.
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
