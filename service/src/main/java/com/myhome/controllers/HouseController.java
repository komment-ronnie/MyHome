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

import com.myhome.api.HousesApi;
import com.myhome.controllers.dto.mapper.HouseMemberMapper;
import com.myhome.controllers.mapper.HouseApiMapper;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.model.AddHouseMemberRequest;
import com.myhome.model.AddHouseMemberResponse;
import com.myhome.model.GetHouseDetailsResponse;
import com.myhome.model.GetHouseDetailsResponseCommunityHouse;
import com.myhome.model.ListHouseMembersResponse;
import com.myhome.services.HouseService;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * in Spring Boot handles various operations related to houses and their members. The
 * controller provides functions for listing all members of a house, adding new members
 * to a house, and deleting members from a house. The functions take the house ID and
 * member details as input and return the updated member list or a response indicating
 * whether the operation was successful or not.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class HouseController implements HousesApi {
  private final HouseMemberMapper houseMemberMapper;
  private final HouseService houseService;
  private final HouseApiMapper houseApiMapper;

  /**
   * retrieves a list of houses from the service layer and maps them to a response
   * object using API mapper, before returning it as a HTTP OK status response with the
   * list of houses in the body.
   * 
   * @param pageable default page size and sort order for listing all houses, which is
   * used by the `houseService.listAllHouses()` method to retrieve a paginated list of
   * community houses from the database.
   * 
   * 	- `PageableDefault(size = 200)`: This method sets the default page size to 200.
   * The `pageable` object can be used to paginate the result set by calling its
   * `getPageNumber()` and `getPageSize()` methods.
   * 
   * @returns a list of `GetHouseDetailsResponse` objects containing house details.
   * 
   * 	- `response`: an instance of `GetHouseDetailsResponse`, representing the list of
   * houses with their details.
   * 	- `pageable`: an instance of `Pageable`, used to control the pagination of the
   * house list.
   * 	- `houseService`: a service that provides access to the houses data.
   * 	- `houseApiMapper`: a mapper that converts the house data from the local API
   * format to the REST API format.
   * 	- `CommunityHouse`: an entity representing a house with its details.
   * 	- `GetHouseDetailsResponseCommunityHouseSet`: a set of `GetHouseDetailsResponseCommunityHouse`
   * objects, each representing a single house with its details.
   */
  @Override
  public ResponseEntity<GetHouseDetailsResponse> listAllHouses(
      @PageableDefault(size = 200) Pageable pageable) {
    log.trace("Received request to list all houses");

    Set<CommunityHouse> houseDetails =
        houseService.listAllHouses(pageable);
    Set<GetHouseDetailsResponseCommunityHouse> getHouseDetailsResponseSet =
        houseApiMapper.communityHouseSetToRestApiResponseCommunityHouseSet(houseDetails);

    GetHouseDetailsResponse response = new GetHouseDetailsResponse();

    response.setHouses(getHouseDetailsResponseSet);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * receives a `houseId` and retrieves the details of the corresponding house from the
   * `houseService`. It then maps the house details to a `GetHouseDetailsResponse`
   * object and returns it as a `ResponseEntity`.
   * 
   * @param houseId ID of the house for which details are being requested.
   * 
   * @returns a `GetHouseDetailsResponse` object containing a list of houses with their
   * details.
   * 
   * 	- `ResponseEntity<GetHouseDetailsResponse>`: This is a generic type that represents
   * an entity with a response body containing the details of a house.
   * 	- `getHouseDetailsResponseCommunityHouses`: This is a list of community houses,
   * which are the details of individual houses returned in the response body.
   * 	- `houses(getHouseDetailsResponseCommunityHouses)`: This is a method that takes
   * a list of community houses as input and returns a list of houses with their details.
   * 	- `map(Function<GetHouseDetailsResponse, ResponseEntity<GetHouseDetailsResponse>>
   * mapper)`: This line uses the `map` method to apply a mapping function to the output
   * of the previous line. The mapping function takes the `getHouseDetailsResponseCommunityHouses`
   * list and returns a `ResponseEntity<GetHouseDetailsResponse>` entity with the details
   * of each house in the list.
   * 	- `orElse(ResponseEntity.notFound().build());`: This line provides an alternative
   * output if the original mapping function fails. It returns a `ResponseEntity.notFound()`
   * entity, which indicates that the requested house could not be found.
   */
  @Override
  public ResponseEntity<GetHouseDetailsResponse> getHouseDetails(String houseId) {
    log.trace("Received request to get details of a house with id[{}]", houseId);
    return houseService.getHouseDetailsById(houseId)
        .map(houseApiMapper::communityHouseToRestApiResponseCommunityHouse)
        .map(Collections::singleton)
        .map(getHouseDetailsResponseCommunityHouses -> new GetHouseDetailsResponse().houses(getHouseDetailsResponseCommunityHouses))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * retrieves all members associated with a specific house and returns them as a list
   * in a `ListHouseMembersResponse` object.
   * 
   * @param houseId ID of the house for which the members are to be listed.
   * 
   * @param pageable Pageable object that specifies the page size and other pagination
   * settings for retrieving the members of the house.
   * 
   * The `@PageableDefault` annotation specifies the default page size for the response,
   * which is 200 in this case. The `pageable` parameter is a `Pageable` object that
   * represents the pagination settings for the request. It has several properties and
   * attributes, including:
   * 
   * 	- `size`: The number of elements to fetch per page (default: 200)
   * 	- `sort`: A sort specification in the format `{field}: {order}` (e.g., `'name':
   * 'ASC'`)
   * 	- `direction`: The direction of the sort (e.g., `'ASC'` or `'DESC')`
   * 	- `pageable`: A `Pageable` object that represents the current page and can be
   * used to navigate through pages
   * 
   * In summary, the `pageable` parameter is a pagination object that provides information
   * about the number of elements to fetch per page, the sort specification, and the
   * direction of the sort.
   * 
   * @returns a `List<HouseMember>` object containing all members of the specified house.
   * 
   * 	- `ResponseEntity`: This is the top-level entity of the response, representing
   * either a successful response or an error.
   * 	- `ok`: This is a subfield of the `ResponseEntity`, indicating that the request
   * was successful and the response contains the expected data.
   * 	- `members`: This is a list of `HouseMember` objects, which represent the members
   * of the house requested in the function parameters.
   */
  @Override
  public ResponseEntity<ListHouseMembersResponse> listAllMembersOfHouse(
      String houseId,
      @PageableDefault(size = 200) Pageable pageable) {
    log.trace("Received request to list all members of the house with id[{}]", houseId);

    return houseService.getHouseMembersById(houseId, pageable)
        .map(HashSet::new)
        .map(houseMemberMapper::houseMemberSetToRestApiResponseHouseMemberSet)
        .map(houseMembers -> new ListHouseMembersResponse().members(houseMembers))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * receives a request to add members to a house, validates the request, adds the
   * members to the house database, and returns the updated member list in response.
   * 
   * @param houseId ID of the house for which members are being added.
   * 
   * @param request AddHouseMemberRequest object containing the member information to
   * be added to the specified house.
   * 
   * 	- `houseId`: A string representing the ID of the house for which members are being
   * added.
   * 	- `request.getMembers()`: A set of `HouseMemberDTO` objects that contain the
   * details of the members to be added to the house.
   * 	- `houseService.addHouseMembers(houseId, members)`: A call to the `addHouseMembers`
   * method of the `houseService` class, which adds the members provided in the `request`
   * to the house with the specified ID. The method returns a set of `HouseMember`
   * objects representing the newly added members.
   * 
   * @returns a `ResponseEntity` object with a status code and a body containing an
   * `AddHouseMemberResponse` object.
   * 
   * 	- `response`: This is an instance of `AddHouseMemberResponse`, which contains the
   * updated member list for the specified house ID.
   * 	- `savedHouseMembers`: This is a set of `HouseMember` objects that represent the
   * members added to the house.
   * 	- `size()`: This is the number of members added to the house. If no members were
   * added, this will be 0.
   * 
   * In summary, the function returns an instance of `AddHouseMemberResponse` containing
   * the updated member list for the specified house ID, along with information on the
   * number of members added.
   */
  @Override
  public ResponseEntity<AddHouseMemberResponse> addHouseMembers(
      @PathVariable String houseId, @Valid AddHouseMemberRequest request) {

    log.trace("Received request to add member to the house with id[{}]", houseId);
    Set<HouseMember> members =
        houseMemberMapper.houseMemberDtoSetToHouseMemberSet(request.getMembers());
    Set<HouseMember> savedHouseMembers = houseService.addHouseMembers(houseId, members);

    if (savedHouseMembers.size() == 0 && request.getMembers().size() != 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else {
      AddHouseMemberResponse response = new AddHouseMemberResponse();
      response.setMembers(
          houseMemberMapper.houseMemberSetToRestApiResponseAddHouseMemberSet(savedHouseMembers));
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
  }

  /**
   * deletes a member from a house based on the provided house ID and member ID, returning
   * a HTTP status code indicating the result of the operation.
   * 
   * @param houseId identifier of the house for which the member is being deleted.
   * 
   * @param memberId ID of the member to be deleted from the specified house.
   * 
   * @returns a response entity with a HTTP status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the member was successfully deleted.
   * 
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response to a HTTP request. It has a status code and a body, which
   * in this case is an empty `Void` value.
   * 	- `HttpStatus`: This is the HTTP status code associated with the response, which
   * indicates whether the request was successful (NO_CONTENT) or not found (NOT_FOUND).
   * 	- `build()`: This is a method that creates a new instance of the `ResponseEntity`
   * class based on the properties of the output.
   */
  @Override
  public ResponseEntity<Void> deleteHouseMember(String houseId, String memberId) {
    log.trace("Received request to delete a member from house with house id[{}] and member id[{}]",
        houseId, memberId);
    boolean isMemberDeleted = houseService.deleteMemberFromHouse(houseId, memberId);
    if (isMemberDeleted) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}