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
 * provides functionality for retrieving and manipulating amenities in a system. It
 * exposes several endpoints for getting and updating amenities, as well as deleting
 * them when they are no longer needed.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class AmenityController implements AmenitiesApi {

  private final AmenityService amenitySDJpaService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * retrieves the details of an amenity with a given ID from the database using
   * `amenitySDJpaService`, maps it to a response object using `amenityApiMapper`, and
   * returns it as a `ResponseEntity` with a status code of `OK` or `NOT_FOUND` depending
   * on the result.
   * 
   * @param amenityId identifier of the amenity for which details are requested.
   * 
   * @returns a `ResponseEntity` object containing the amenity details in JSON format.
   * 
   * 	- `ResponseEntity<GetAmenityDetailsResponse>`: This is a response entity that
   * contains the amenity details in the form of `GetAmenityDetailsResponse`.
   * 	- `GetAmenityDetailsResponse`: This class represents the response to the
   * `getAmenityDetails` function, which contains the amenity details fetched from the
   * database. It has several attributes, including `amenityId`, `name`, `description`,
   * `icon`, and `latitude`.
   * 	- `map(Function<T, U> mapper)`: This line uses the `map` method to apply a function
   * to the output of the `getAmenityDetails` function. In this case, the function is
   * `amenityApiMapper::amenityToAmenityDetailsResponse`, which maps the
   * `GetAmenityDetailsResponse` object to a more accessible form.
   * 	- `orElse(T alternative)`: This line provides an alternative output if the original
   * output is not found. In this case, the alternative output is a `ResponseEntity`
   * with a status code of `HttpStatus.NOT_FOUND`.
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
   * retrieves a list of amenities from the database using `amenitySDJpaService`, and
   * maps them to `GetAmenityDetailsResponse` objects using `amenityApiMapper`. It then
   * returns an `ResponseEntity` with the mapped response.
   * 
   * @param communityId identifier of the community whose amenities are to be listed.
   * 
   * @returns a set of `GetAmenityDetailsResponse` objects containing the details of
   * the amenities for a specific community.
   * 
   * 	- `ResponseEntity`: This is the outermost class that represents a response entity
   * in RESTful APIs. It has an `ok` field that indicates whether the response is
   * successful or not.
   * 	- `Set<GetAmenityDetailsResponse>`: This is a set of `GetAmenityDetailsResponse`
   * objects, which are the inner most class representing the amenities with their details.
   * 	- `amenitySDJpaService`: This is an instance of a JPA service that provides methods
   * for interacting with the database.
   * 	- `communityId`: This is the parameter passed to the function, which represents
   * the community ID.
   * 	- `amenityApiMapper`: This is an instance of an API mapper class that maps the
   * JPA entities to the response entity.
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
   * adds amenities to a community through the creation of new amenities in the database
   * and returns a response entity indicating whether the operation was successful.
   * 
   * @param communityId ID of the community to which the amenities will be added.
   * 
   * @param request AddAmenityRequest object containing the amenities to be added to
   * the community, which is used by the `amenitySDJpaService` to create the new amenities
   * in the database.
   * 
   * 	- `communityId`: A string representing the ID of the community to which the
   * amenities will be added.
   * 	- `requestBody`: The AddAmenityRequest object containing the amenities to be added
   * to the community. It has attributes such as `amenities`, which is a list of Amenity
   * objects, and `id`, which is an identifier for the request.
   * 
   * @returns an `AddAmenityResponse` object containing a list of created amenities.
   * 
   * 	- The `ResponseEntity<AddAmenityResponse>` object represents a successful response
   * with an `AddAmenityResponse` object inside it.
   * 	- The `AddAmenityResponse` object contains a list of `Amenity` objects representing
   * the newly created amenities in the community.
   * 	- The `map` method is used to transform the `ResponseEntity<AddAmenityResponse>`
   * object into a `ResponseEntity` object with an `ok` status code.
   * 	- The `orElse` method is used as a fallback to return a `ResponseEntity.notFound().build()`
   * object if the original response cannot be converted to an `ok` status code.
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
   * deletes an amenity based on its ID, returning a response entity indicating whether
   * the operation was successful or not.
   * 
   * @param amenityId id of the amenity to be deleted.
   * 
   * @returns a HTTP status code of `NO_CONTENT` or `NOT_FOUND`, indicating whether the
   * amenity was successfully deleted or not.
   * 
   * 	- `isAmenityDeleted`: A boolean value indicating whether the amenity was successfully
   * deleted or not.
   * 	- `HttpStatus`: The HTTP status code of the response entity, which is either
   * `NO_CONTENT` or `NOT_FOUND`, depending on whether the amenity was found and deleted
   * successfully or not.
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
   * updates an amenity's information in the database based on a request body containing
   * the updated amenity details, and returns a response indicating whether the update
   * was successful or not.
   * 
   * @param amenityId unique identifier of the amenity being updated.
   * 
   * @param request `UpdateAmenityRequest` object that contains the details of the
   * amenity to be updated.
   * 
   * 	- `@Valid`: Indicates that the input request body must be valid according to the
   * schema defined in the `@VALID` annotation.
   * 	- `@RequestBody`: Marks the `request` parameter as a serialized JSON object in
   * the request body.
   * 	- `UpdateAmenityRequest`: Represents the request body schema for updating an
   * amenity, containing fields such as `amenityId`, `name`, `description`, and `location`.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the amenity was updated successfully.
   * 
   * 	- `HttpStatus`: This is an instance of the `HttpStatus` class, which represents
   * the HTTP status code returned by the function. In this case, it can be either
   * `NO_CONTENT` or `NOT_FOUND`.
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response entity that contains the HTTP status code and other information
   * about the response. The `status()` method returns the `HttpStatus` object representing
   * the status code.
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
