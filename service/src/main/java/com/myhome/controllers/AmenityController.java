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

import com.myhome.api.AmenitiesApi;
import com.myhome.controllers.mapper.AmenityApiMapper;
import com.myhome.domain.Amenity;
import com.myhome.model.AddAmenityRequest;
import com.myhome.model.AddAmenityResponse;
import com.myhome.model.AmenityDto;
import com.myhome.model.GetAmenityDetailsResponse;
import com.myhome.model.UpdateAmenityRequest;
import com.myhome.services.AmenityService;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * in the provided codebase handles API endpoints related to amenities in a community.
 * The controller provides methods for adding, deleting, and updating amenities, as
 * well as returning response entities with HTTP status codes indicating the outcome
 * of the operations. The methods use the `amenitySDJpaService` class to interact
 * with the database.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class AmenityController implements AmenitiesApi {

  private final AmenityService amenitySDJpaService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * retrieves details for an amenity with the given ID from the database using
   * `amenitySDJpaService`, maps the result to a `AmenityDetailsResponse` object, and
   * returns a `ResponseEntity` with a status of `OK` or an alternative response if the
   * amenity is not found.
   * 
   * @param amenityId ID of an amenity for which details are requested.
   * 
   * @returns a `ResponseEntity` object with an `ok` status and the requested amenity
   * details.
   * 
   * 	- `ResponseEntity<GetAmenityDetailsResponse>`: This is a generic response entity
   * that represents a response to a request for amenity details. It has an `ok` field
   * that indicates whether the response was successful or not. If the response was
   * successful, the `ok` field will be set to `true`, otherwise it will be set to `false`.
   * 	- `GetAmenityDetailsResponse`: This is a class that represents the response to a
   * request for amenity details. It has fields for the amenity ID, name, and description,
   * as well as an `amenities` field that contains a list of amenities associated with
   * the specified amenity ID.
   * 	- `amenitySDJpaService`: This is a JPA service that provides methods for interacting
   * with the amenity data storage. It is used to retrieve the amenity details in the
   * function.
   * 	- `amenityApiMapper`: This is a mapper class that maps the response from the
   * amenity data storage to the `GetAmenityDetailsResponse` class. It is used to convert
   * the raw data returned by the amenity data storage into the desired response format.
   */
  @Override
  public ResponseEntity<GetAmenityDetailsResponse> getAmenityDetails(
      @PathVariable String amenityId) {
    return amenitySDJpaService.getAmenityDetails(amenityId)
        .map(amenityApiMapper::amenityToAmenityDetailsResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * retrieves a set of amenities from the database using `JPA`, and then maps them to
   * a set of `GetAmenityDetailsResponse` objects using `ApiMapper`. Finally, it returns
   * an `ResponseEntity` with the mapped response.
   * 
   * @param communityId unique identifier of the community for which the list of amenities
   * is being retrieved.
   * 
   * @returns a set of `GetAmenityDetailsResponse` objects containing the details of
   * all amenities for a given community.
   * 
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response object that can contain a body and a status code.
   * 	- `ok`: This is a tag indicating that the response is successful (i.e., the status
   * code is 200).
   * 	- `Set<GetAmenityDetailsResponse>`: This is a set of `GetAmenityDetailsResponse`
   * objects, which represent the list of amenities for the given community ID.
   * 	- `amenitySDJpaService`: This is a Java class that provides methods for listing
   * all amenities, likely implementing some sort of database access layer.
   * 	- `amenityApiMapper`: This is a Java class that maps the list of amenities to a
   * set of `GetAmenityDetailsResponse` objects, likely implementing some sort of data
   * transformation or mapping layer.
   */
  @Override
  public ResponseEntity<Set<GetAmenityDetailsResponse>> listAllAmenities(
      @PathVariable String communityId) {
    Set<Amenity> amenities = amenitySDJpaService.listAllAmenities(communityId);
    Set<GetAmenityDetailsResponse> response =
        amenityApiMapper.amenitiesSetToAmenityDetailsResponseSet(amenities);
    return ResponseEntity.ok(response);
  }

  /**
   * adds amenities to a community by creating them through the `amenitySDJpaService`
   * and returning a `ResponseEntity` object indicating whether the operation was
   * successful or not.
   * 
   * @param communityId ID of the community to which the amenities will be added.
   * 
   * @param request AddAmenityRequest object containing the amenities to be added to
   * the community.
   * 
   * 	- `communityId`: A String representing the ID of the community to which the
   * amenities will be added.
   * 	- `request`: An instance of the `AddAmenityRequest` class, containing a list of
   * `Amenity` objects that will be added to the community.
   * 
   * @returns a `ResponseEntity` object with an `ok` status and a list of created amenities.
   * 
   * 	- `ResponseEntity`: This is the generic type of the output, which is an instance
   * of `ResponseEntity`.
   * 	- `ok`: This is a method that returns a `ResponseEntity` instance with a status
   * code of 200 (OK).
   * 	- `notFound`: This is another method that returns a `ResponseEntity` instance
   * with a status code of 404 (Not Found) if the communityId provided in the request
   * does not exist.
   */
  @Override
  public ResponseEntity<AddAmenityResponse> addAmenityToCommunity(
      @PathVariable String communityId,
      @RequestBody AddAmenityRequest request) {
    return amenitySDJpaService.createAmenities(request.getAmenities(), communityId)
        .map(amenityList -> new AddAmenityResponse().amenities(amenityList))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * deletes an amenity from the database based on its ID, returning a HTTP status code
   * indicating whether the operation was successful or not.
   * 
   * @param amenityId identifier of an amenity to be deleted.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the amenity was successfully deleted or not.
   * 
   * 	- `HttpStatus.NO_CONTENT`: This status code indicates that the amenity was
   * successfully deleted and no content was returned.
   * 	- `HttpStatus.NOT_FOUND`: This status code indicates that the amenity could not
   * be found or was not present in the database, which means it was not deleted.
   */
  @Override
  public ResponseEntity deleteAmenity(@PathVariable String amenityId) {
    boolean isAmenityDeleted = amenitySDJpaService.deleteAmenity(amenityId);
    if (isAmenityDeleted) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * updates an amenity's details using the `amenitySDJpaService`. It returns a response
   * entity indicating whether the update was successful or not.
   * 
   * @param amenityId ID of the amenity being updated.
   * 
   * @param request UpdateAmenityRequest object containing the updated amenity details,
   * which is converted to an AmenityDto object through the amenityApiMapper before
   * being passed to the amenitySDJpaService for updating the amenity in the database.
   * 
   * 	- `@Valid`: Indicates that the input request body must be validated according to
   * the specified validation rules.
   * 	- `@RequestBody`: Marks the `request` parameter as a request body, indicating
   * that it should be deserialized from the request body.
   * 	- `UpdateAmenityRequest`: The class of the request body, which contains fields
   * for updating an amenity.
   * 
   * @returns a response entity with a status code of either `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the amenity was successfully updated or not.
   * 
   * 	- `ResponseEntity`: This is an entity object that represents the HTTP response
   * status and body. In this case, it has a status code of `HttpStatus.NO_CONTENT`,
   * indicating that the amenity was successfully updated.
   * 	- `build()`: This is a method that creates a new `ResponseEntity` object with the
   * specified status code and body.
   * 	- `isUpdated`: This is a boolean flag that indicates whether the amenity was
   * successfully updated or not. If it's `true`, the amenity was updated, otherwise
   * it was not found.
   */
  @Override
  public ResponseEntity<Void> updateAmenity(@PathVariable String amenityId,
      @Valid @RequestBody UpdateAmenityRequest request) {
    AmenityDto amenityDto = amenityApiMapper.updateAmenityRequestToAmenityDto(request);
    amenityDto.setAmenityId(amenityId);
    boolean isUpdated = amenitySDJpaService.updateAmenity(amenityDto);
    if (isUpdated) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
