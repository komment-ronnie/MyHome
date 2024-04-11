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
 * is responsible for handling requests related to houses and their members. It
 * provides endpoints for listing all houses, getting details of a specific house,
 * adding members to a house, and deleting members from a house. The controller uses
 * dependencies such as the HouseMemberMapper and the HouseService to perform these
 * operations.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class HouseController implements HousesApi {
  private final HouseMemberMapper houseMemberMapper;
  private final HouseService houseService;
  private final HouseApiMapper houseApiMapper;

  /**
   * receives a pageable request from the client and list all houses from the service,
   * then maps them to the REST API response format using the provided mapper, and
   * returns the response to the client.
   * 
   * @param pageable page size and sort order for listing all houses.
   * 
   * 	- `@PageableDefault(size = 200)` - This annotation sets the default page size for
   * listings to 200.
   * 
   * @returns a list of `GetHouseDetailsResponseCommunityHouseSet`.
   * 
   * 	- `response`: This is the main output of the function, which is a `GetHouseDetailsResponse`
   * object.
   * 	- `setHouses`: This is a set of `CommunityHouse` objects, which are the details
   * of each house listed in the response.
   * 	- `pageable`: This is an optional parameter that represents the page size and
   * sort order for the list of houses.
   * 	- `houseService`: This is the service used to retrieve the list of houses.
   * 	- `houseApiMapper`: This is the mapper used to transform the list of `CommunityHouse`
   * objects into a set of `GetHouseDetailsResponseCommunityHouse` objects.
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
   * receives a house ID and returns a `GetHouseDetailsResponse` object with a list of
   * houses matching the provided ID. It uses service-level methods to retrieve the
   * details and map them to a rest API response.
   * 
   * @param houseId unique identifier of the house for which details are requested, and
   * it is used to retrieve the corresponding house details from the service.
   * 
   * @returns a `GetHouseDetailsResponse` object containing a list of houses with their
   * details.
   * 
   * 	- `ResponseEntity<GetHouseDetailsResponse>`: This is a generic type that represents
   * an entity with a response body containing a `GetHouseDetailsResponse` object.
   * 	- `GetHouseDetailsResponse`: This class represents the response body of the entity,
   * which contains a list of `CommunityHouse` objects.
   * 	- `CommunityHouse`: This class represents a single house in the community, with
   * attributes such as id, name, and location.
   * 	- `map(Function<T, R> mapper)`: This method applies a mapping function to the
   * output of the `getHouseDetails` method, which transforms the response body into a
   * new form. In this case, the function maps each `CommunityHouse` object to a
   * `GetHouseDetailsResponse` object.
   * 	- `map(Supplier<T> supplier)`: This method returns a stream of `T` objects, where
   * `T` is the type of the output of the `getHouseDetails` method. In this case, the
   * supplier returns an empty stream, which means that the output of the method will
   * be an empty list.
   * 	- `orElse(T otherValue)`: This method returns a new response entity if the result
   * of the previous mapping operation is not present, or the specified `otherValue`
   * otherwise. In this case, if the `getHouseDetails` method does not return a response
   * body, the resulting entity will be an `ResponseEntity.notFound().build()`.
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
   * `HouseMember` objects in a `ListHouseMembersResponse` message.
   * 
   * @param houseId unique identifier of the house for which members are to be listed.
   * 
   * @param pageable page request parameters, such as the page number and size of the
   * result set, which are used to filter and limit the response from the
   * `houseService.getHouseMembersById()` method.
   * 
   * 	- `size`: The number of elements to be returned in each page of results.
   * 	- `sort`: The field by which the results should be sorted.
   * 	- `direction`: The direction of sorting (ascending or descending).
   * 
   * @returns a `ListHouseMembersResponse` object containing the list of members of the
   * specified house.
   * 
   * 	- `ResponseEntity<ListHouseMembersResponse>`: This is the type of the output
   * returned by the function, which represents a response entity containing a list of
   * members of a house.
   * 	- `ListHouseMembersResponse`: This is a class that contains properties related
   * to the list of members of a house. The properties include:
   * 	+ `members`: A list of `HouseMember` objects, representing the members of the house.
   * 	- `ok`: This is a boolean property indicating whether the response was successful
   * or not. If the response was not successful, the value of this property will be `false`.
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
   * takes a house ID and a request with member details, adds the members to the house,
   * and returns the updated member list in the response.
   * 
   * @param houseId identifier of the house to which the members will be added.
   * 
   * @param request AddHouseMemberRequest object that contains the member details to
   * be added to the house.
   * 
   * 	- `houseId`: A string representing the unique identifier of the house to which
   * members will be added.
   * 	- `request.getMembers()`: An array of `AddHouseMemberRequest.Members` objects
   * containing the details of the members to be added to the house. Each `Members`
   * object has the following properties:
   * 	+ `member`: A string representing the unique identifier of the member to be added.
   * 	+ `email`: A string representing the email address of the member.
   * 	+ `firstName`: A string representing the first name of the member.
   * 	+ `lastName`: A string representing the last name of the member.
   * 	+ `phoneNumber`: A string representing the phone number of the member.
   * 
   * In summary, the `addHouseMembers` function takes a house ID and a list of members
   * to be added to that house, processes them, and returns a response indicating whether
   * the operation was successful or not.
   * 
   * @returns a `ResponseEntity` object containing the response to the request, which
   * includes the added house members in a JSON format.
   * 
   * 	- `response`: This is an instance of the `AddHouseMemberResponse` class, which
   * contains information about the added members.
   * 	- `members`: This is a set of `HouseMember` objects, which represent the added
   * members to the house.
   * 	- `size`: The size of the `members` set, indicating the number of added members.
   * 
   * The output is structured in the following way:
   * 
   * {
   * response: {
   * members: [...],
   * size: 3
   * }
   * }
   * 
   * Where `[...]` represents the contents of the `members` set. The `size` property
   * indicates the number of added members.
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
   * deletes a member from a house based on the house ID and member ID provided in the
   * request. If the member is successfully deleted, a `NO_CONTENT` status code is
   * returned. If the member cannot be found, a `NOT_FOUND` status code is returned.
   * 
   * @param houseId ID of the house for which a member is being deleted.
   * 
   * @param memberId ID of the member to be deleted from the specified house.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the member was successfully deleted or not.
   * 
   * 	- `ResponseEntity`: This is an object that represents the response to the delete
   * request. It has a `status` field that indicates the HTTP status code of the response,
   * and a `body` field that contains the response entity itself.
   * 	- `HttpStatus`: This is an enum that defines the possible HTTP status codes that
   * can be returned by the function. The function returns `NO_CONTENT` if the member
   * was successfully deleted, and `NOT_FOUND` otherwise.
   * 	- ` Void`: This is a type parameter of the `ResponseEntity` class, which represents
   * the void value returned by the function.
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