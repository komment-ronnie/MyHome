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
 * is responsible for handling requests related to houses and their members. The class
 * provides methods for listing all members of a house, adding members to a house,
 * and deleting members from a house. These methods use the `houseService` class to
 * interact with the database and return responses in the form of `ResponseEntity` objects.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class HouseController implements HousesApi {
  private final HouseMemberMapper houseMemberMapper;
  private final HouseService houseService;
  private final HouseApiMapper houseApiMapper;

  /**
   * receives a pageable parameter and returns a response entity containing a list of
   * houses, which have been converted from a set of community house objects to a list
   * of REST API response community house objects using the `houseApiMapper`.
   * 
   * @param pageable page request parameters, such as the number of houses to display
   * per page and the total number of pages, which are used to paginate the list of
   * houses returned by the method.
   * 
   * 	- `@PageableDefault(size = 200)`: This annotation specifies the default page size
   * for the list of houses returned in the response. The value `200` indicates that
   * the default page size is 200 houses.
   * 	- `Pageable`: This is an interface that provides methods for pagination, such as
   * `getTotalElements()` (returning the total number of houses), `getNumberOfElements()`
   * (returning the number of houses in the current page), and `getPageNumber()/setPageNumber()`
   * (allowing the client to navigate through the pages).
   * 	- `size`: This property is a double value that represents the number of houses
   * per page. It is used by the `Pageable` interface to determine the total number of
   * pages required to display all the houses.
   * 
   * @returns a list of house details in a REST API response format.
   * 
   * 	- `response`: An instance of `GetHouseDetailsResponse`, which contains a list of
   * `CommunityHouse` objects in its `houses` field.
   * 	- `pageable`: A `Pageable` object that represents the pagination settings for the
   * list of houses.
   * 	- `houseService`: An instance of a service class that provides methods for
   * interacting with the house data.
   * 	- `houseApiMapper`: An instance of a mapping class that converts the `CommunityHouse`
   * objects returned by the `houseService` to the corresponding REST API response format.
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
   * retrieves the details of a house based on its ID and maps them to a `GetHouseDetailsResponse`
   * object, which is then returned as an `OK` response entity or an empty response
   * entity if the house is not found.
   * 
   * @param houseId unique identifier of the house for which details are requested.
   * 
   * @returns a `ResponseEntity` object containing a list of houses with their details.
   * 
   * 	- `ResponseEntity<GetHouseDetailsResponse>` represents an entity that contains a
   * response object for the `getHouseDetails` method.
   * 	- `getHouseDetailsResponseCommunityHouses` is a list of community houses associated
   * with the given house ID.
   * 	- `houses(getHouseDetailsResponseCommunityHouses)` returns a list of houses in
   * the response.
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
   * retrieves the members of a house with a given ID and returns them as a list of
   * `HouseMember` objects in a `ResponseEntity`.
   * 
   * @param houseId identifier of the house for which the members are to be listed.
   * 
   * @param pageable default page size and sort order for the list of house members
   * returned in the response, allowing for pagination of the result set.
   * 
   * 	- `size`: The number of members to be returned per page (default is 200)
   * 
   * The function processes the input parameters and returns a response entity with the
   * list of members of the specified house.
   * 
   * @returns a `ListHouseMembersResponse` object containing a list of house members.
   * 
   * 	- `ResponseEntity`: This is the type of response entity that is returned, which
   * indicates whether the request was successful or not. In this case, it is `ok`,
   * indicating a successful response.
   * 	- `ListHouseMembersResponse`: This is the type of response object that contains
   * a list of members of the house.
   * 	- `members`: This is a list of `HouseMember` objects, which contain information
   * about each member of the house.
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
   * adds members to a house identified by the provided `houseId`. It takes in the
   * request containing the members to add, processes it, and returns a response
   * indicating whether the addition was successful or not.
   * 
   * @param houseId identifier of the house for which members are being added.
   * 
   * @param request AddHouseMemberRequest object containing the member details to be
   * added to the specified house.
   * 
   * 	- `houseId`: A string representing the unique identifier of the house to which
   * members will be added.
   * 	- `request.getMembers()`: A set of HouseMember objects containing the new members
   * to be added to the house.
   * 
   * The function performs several operations, including logging a trace message and
   * adding new members to the house using the `houseService`. If the addition is
   * successful, the function returns a `ResponseEntity` with a status code of `CREATED`
   * and the updated member list in the response body. Otherwise, it returns a
   * `ResponseEntity` with a status code of `NOT_FOUND`.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NOT_FOUND` or
   * `CREATED`, depending on whether any members were added successfully.
   * 
   * 	- `response`: A `AddHouseMemberResponse` object that contains the added members
   * in a list format.
   * 	- `savedHouseMembers`: A set of `HouseMember` objects that represent the successfully
   * saved members in the database.
   * 
   * The function first logs a trace message to indicate the receipt of the request and
   * then processes the request by converting the `AddHouseMemberRequest` object into
   * a list of `HouseMember` objects using the `houseMemberMapper`. It then adds the
   * members to the house using the `houseService`, and if any errors occur, it returns
   * a `ResponseEntity` with a status code of `NOT_FOUND`. If the addition is successful,
   * it creates a new `AddHouseMemberResponse` object and populates it with the saved
   * members in a list format. Finally, it returns the response entity with a status
   * code of `CREATED`.
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
   * deletes a member from a house based on the provided house ID and member ID. It
   * first logs the request, then checks if the member was successfully deleted using
   * the `houseService`. If successful, it returns a `ResponseEntity` with a
   * `HttpStatus.NO_CONTENT`. Otherwise, it returns a `ResponseEntity` with a `HttpStatus.NOT_FOUND`.
   * 
   * @param houseId unique identifier of the house for which a member is being deleted.
   * 
   * @param memberId ID of the member to be deleted from the specified house.
   * 
   * @returns a `ResponseEntity` with a status code of either `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the member was successfully deleted or not.
   * 
   * 	- `HttpStatus.NO_CONTENT`: This status code indicates that the member was
   * successfully deleted and no additional content is provided in the response.
   * 	- `HttpStatus.NOT_FOUND`: This status code indicates that the member could not
   * be found in the house, possibly because it does not exist or has already been deleted.
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