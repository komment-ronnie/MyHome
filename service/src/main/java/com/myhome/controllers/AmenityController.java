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
 * is responsible for handling RESTful API requests related to amenities in a community.
 * It provides endpoints for creating, reading, updating, and deleting amenities. The
 * controller uses the `amenitySDJpaService` class to interact with the database and
 * returns response entities indicating whether the operations were successful or not.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class AmenityController implements AmenitiesApi {

  private final AmenityService amenitySDJpaService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * retrieves the details of an amenity based on its ID, maps the response to a
   * `GetAmenityDetailsResponse` object using a mapper, and returns a `ResponseEntity`
   * with a status code of `OK` or an alternative status code if the amenity is not found.
   * 
   * @param amenityId unique identifier of an amenity for which details are requested.
   * 
   * @returns an `ResponseEntity` object representing a successful response with the
   * requested amenity details.
   * 
   * 	- `ResponseEntity`: This is an instance of `ResponseEntity`, which represents a
   * response to a RESTful API call. It contains the status code and body of the response.
   * 	- `map`: This method is used to map the result of the `amenitySDJpaService.getAmenityDetails()`
   * method to a `GetAmenityDetailsResponse` object using the
   * `amenityApiMapper.amenityToAmenityDetailsResponse()` method.
   * 	- `orElse`: This method is used to return an alternative response if the result
   * of the `amenitySDJpaService.getAmenityDetails()` method is not found. The alternative
   * response is represented by a `ResponseEntity` object with a status code of `HttpStatus.NOT_FOUND`.
   * 	- `ok`: This method is used to indicate that the response is successful, which
   * means that the API call was successful and the requested amenity details were returned.
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
   * receives a community ID and returns a set of amenities, which are then mapped to
   * a set of `GetAmenityDetailsResponse`.
   * 
   * @param communityId unique identifier for a community, which is used to filter the
   * amenities returned in the response.
   * 
   * @returns a set of `GetAmenityDetailsResponse` objects containing the list of
   * amenities for a given community ID.
   * 
   * 	- `ResponseEntity`: This is the type of the output parameter, which represents
   * an entity with an OK status code and a set of amenity details as its body.
   * 	- `Set<GetAmenityDetailsResponse>`: This is the set of amenity details that are
   * returned in the body of the response entity. Each element in the set is an object
   * representing a single amenity, containing its ID, name, description, and other attributes.
   * 	- `amenitySDJpaService`: This is a Java interface used to retrieve the list of
   * amenities from the database using JPA (Java Persistence API).
   * 	- `amenityApiMapper`: This is a Java class used to map the list of amenities
   * returned by the JPA service to a set of `GetAmenityDetailsResponse` objects.
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
   * adds amenities to a community through JPA service, mapping the result to an
   * `AddAmenityResponse` object and returning it as an `OK` response entity or a
   * `NOT_FOUND` response entity if the community does not exist.
   * 
   * @param communityId identifier of the community to which the amenities are being added.
   * 
   * @param request AddAmenityRequest object containing the amenities to be added to
   * the community, which is used by the `amenitySDJpaService` to create the new amenities
   * in the database.
   * 
   * 	- `communityId`: A string representing the ID of the community to which the
   * amenities will be added.
   * 	- `request.getAmenities()`: An array of objects containing information about the
   * amenities to be added. Each object in the array has properties such as `name`,
   * `description`, and `type`.
   * 
   * @returns a `ResponseEntity` object representing a successful addition of amenities
   * to a community.
   * 
   * 	- `ResponseEntity<AddAmenityResponse>`: This is a class that represents an HTTP
   * response entity with a status code and a body. In this case, the body is an instance
   * of `AddAmenityResponse`.
   * 	- `AddAmenityResponse`: This class represents the response to the API call,
   * containing information about the added amenities. It has an `amenities` field,
   * which is a list of `Amenity` objects representing the added amenities.
   * 	- `ok`: This is a method that returns a response entity with a status code of 200
   * (OK).
   * 	- `notFound()`: This is a method that returns a response entity with a status
   * code of 404 (Not Found), indicating that the community with the provided ID could
   * not be found.
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
   * deletes an amenity from the database based on its ID, returning a response entity
   * with HTTP status code indicating the outcome of the operation.
   * 
   * @param amenityId ID of the amenity to be deleted.
   * 
   * @returns a `ResponseEntity` object with a status code indicating whether the amenity
   * was successfully deleted or not.
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
   * updates an amenity using the provided request body and returns a response entity
   * with a status code indicating whether the update was successful or not.
   * 
   * @param amenityId ID of the amenity being updated.
   * 
   * @param request UpdateAmenityRequest object that contains the details of the amenity
   * to be updated.
   * 
   * 	- `@Valid`: Indicates that the input request body must be valid according to the
   * schema provided in the `@Validation` annotation.
   * 	- `@RequestBody`: Marks the input request body as a mandatory parameter.
   * 	- `UpdateAmenityRequest`: The class that defines the structure of the input request
   * body, including its properties and attributes.
   * 
   * @returns a `ResponseEntity` with a status code of `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the update was successful.
   * 
   * 	- `isUpdated`: A boolean value indicating whether the amenity was successfully
   * updated or not. If `true`, the method executed successfully; otherwise, it did not.
   * 	- `HttpStatus`: An instance of the `HttpStatus` class, representing the HTTP
   * status code returned by the method. The status code is either `NO_CONTENT` (204)
   * if the amenity was updated successfully or `NOT_FOUND` (404) otherwise.
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
