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

package com.myhome.services.springdatajpa;

import com.myhome.controllers.mapper.AmenityApiMapper;
import com.myhome.domain.Amenity;
import com.myhome.domain.Community;
import com.myhome.model.AmenityDto;
import com.myhome.repositories.AmenityRepository;
import com.myhome.repositories.CommunityRepository;
import com.myhome.services.AmenityService;
import com.myhome.services.CommunityService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * is a Java class that provides amenity-related operations such as creating, updating,
 * and deleting amenities in a database using JPA. The class provides methods for
 * creating new amenities, retrieving amenity details, deleting amenities, and listing
 * all amenities for a specific community.
 */
@Service
@RequiredArgsConstructor
public class AmenitySDJpaService implements AmenityService {

  private final AmenityRepository amenityRepository;
  private final CommunityRepository communityRepository;
  private final CommunityService communityService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * creates a list of `AmenityDto` objects from a set of `Amenity` objects, and then
   * saves them to the database. It returns an `Optional` containing the created
   * `AmenityDto` objects.
   * 
   * @param amenities set of amenities to be created in the community, which are
   * transformed into a list of `AmenityDto` objects and then saved in the database.
   * 
   * 	- `Set<AmenityDto> amenities`: A set containing the amenities to be created in
   * the community. Each amenity is represented as an `AmenityDto`.
   * 	- `String communityId`: The ID of the community where the amenities will be created.
   * 
   * The function first retrieves the community details using
   * `communityService.getCommunityDetailsById(communityId)`, and then maps each
   * `AmenityDto` to an `Amenity` object using `amenityApiMapper.amenityDtoToAmenity`.
   * The resulting list of `Amenity` objects is then transformed into a list of
   * `AmenityDto` objects using `amenityApiMapper.amenityToAmenityDto`. Finally, the
   * list of created `AmenityDto` objects is returned as an `Optional`.
   * 
   * @param communityId ID of the community to which the amenities will be added or updated.
   * 
   * @returns a list of `AmenityDto` objects representing the created amenities.
   * 
   * 	- `Optional<List<AmenityDto>>`: This indicates that the function returns an
   * optional list of amenities, which means that the list may be empty if no amenities
   * were created successfully.
   * 	- `createAmenities(Set<AmenityDto> amenities, String communityId)`: This parameter
   * represents the input set of amenities and the community ID to create amenities for.
   * 	- `final Optional<Community> community = communityService.getCommunityDetailsById(communityId);`:
   * This line retrieves the community details for the specified community ID using the
   * `communityService`. If the community is not found, the `Optional` object will be
   * empty.
   * 	- `if (!community.isPresent()) {`: This line checks if the community was found
   * or not. If it wasn't, the function returns an empty list.
   * 	- `final List<Amenity> amenitiesWithCommunity = amenities.stream()`: This line
   * streams the input set of amenities and transforms each amenity into a new `Amenity`
   * object that has a reference to the retrieved community.
   * 	- `map(amenity -> {`: This line maps each amenity to a new `Amenity` object with
   * the retrieved community reference.
   * 	- `amenity.setCommunity(community.get());`: This line sets the community reference
   * of each transformed amenity to the retrieved community.
   * 	- `return amenitiesWithCommunity.stream()`: This line streams the list of transformed
   * amenities and saves them in the database using the `amenityRepository`.
   * 	- `final List<AmenityDto> createdAmenities =`: This line retrieves the list of
   * newly created amenities from the database using the `amenityRepository`.
   * 	- `return Optional.of(createdAmenities);`: This line returns an optional list of
   * newly created amenities.
   */
  @Override
  public Optional<List<AmenityDto>> createAmenities(Set<AmenityDto> amenities, String communityId) {
    final Optional<Community> community = communityService.getCommunityDetailsById(communityId);
    if (!community.isPresent()) {
      return Optional.empty();
    }
    final List<Amenity> amenitiesWithCommunity = amenities.stream()
        .map(amenityApiMapper::amenityDtoToAmenity)
        .map(amenity -> {
          amenity.setCommunity(community.get());
          return amenity;
        })
        .collect(Collectors.toList());
    final List<AmenityDto> createdAmenities =
        amenityRepository.saveAll(amenitiesWithCommunity).stream()
            .map(amenityApiMapper::amenityToAmenityDto)
            .collect(Collectors.toList());
    return Optional.of(createdAmenities);
  }

  /**
   * retrieves an Optional<Amenity> object representing the details of an amenity with
   * the specified `amenityId`.
   * 
   * @param amenityId identifier of an amenity for which details are being retrieved.
   * 
   * @returns an Optional object containing the details of the amenity with the provided
   * ID.
   * 
   * 	- `Optional<Amenity>` represents an optional amenity details object, which means
   * that if no amenity is found with the given `amenityId`, the function will return
   * an empty Optional.
   * 	- `amenityRepository.findByAmenityId(amenityId)` is a method call that retrieves
   * an amenity details object from the repository based on the `amenityId` parameter.
   */
  @Override
  public Optional<Amenity> getAmenityDetails(String amenityId) {
    return amenityRepository.findByAmenityId(amenityId);
  }

  /**
   * deletes an amenity from a community by removing it from the community's amenities
   * list and then deleting the amenity from the repository.
   * 
   * @param amenityId ID of an amenity that needs to be deleted.
   * 
   * @returns a boolean value indicating whether the amenity was successfully deleted.
   */
  @Override
  public boolean deleteAmenity(String amenityId) {
    return amenityRepository.findByAmenityIdWithCommunity(amenityId)
        .map(amenity -> {
          Community community = amenity.getCommunity();
          community.getAmenities().remove(amenity);
          amenityRepository.delete(amenity);
          return true;
        })
        .orElse(false);
  }

  /**
   * retrieves a set of amenities associated with a given community ID using a combination
   * of repository and method calls, and returns the result in a set object.
   * 
   * @param communityId community ID that is used to retrieve the amenities associated
   * with it.
   * 
   * @returns a set of amenities associated with a specific community.
   * 
   * 	- The output is a `Set<Amenity>` containing all the amenities associated with a
   * specific community.
   * 	- The `Community` objects in the input are used to retrieve the amenities for
   * each community.
   * 	- If there are no amenities associated with a particular community, the output
   * will be an empty set (`{}`).
   * 	- The `map()` method is used to transform the `Community` objects into `Amenity`
   * objects, and the resulting `Set<Amenity>` is returned as the output.
   */
  @Override
  public Set<Amenity> listAllAmenities(String communityId) {
    return communityRepository.findByCommunityIdWithAmenities(communityId)
        .map(Community::getAmenities)
        .orElse(new HashSet<>());
  }

  /**
   * updates an amenity in the database based on the input `updatedAmenity`. It first
   * retrieves the existing amenity with the matching `amenityId`, then updates its
   * name, price, and other properties using the `findByCommunityId` method. Finally,
   * it saves the updated amenity to the repository.
   * 
   * @param updatedAmenity updated amenity object containing the latest values for name,
   * price, community Id and description.
   * 
   * 	- `amenityId`: A string representing the amenity ID.
   * 	- `communityId`: A string representing the community ID associated with the amenity.
   * 	- `name`: A string representing the name of the amenity.
   * 	- `price`: An integer representing the price of the amenity.
   * 	- `description`: A string representing the description of the amenity.
   * 
   * The function then queries the `amenityRepository` to find an existing amenity with
   * the same `amenityId`, and if found, it updates the `communityId` associated with
   * that amenity using the `communityRepository`. If no matching amenity is found, the
   * function creates a new `Amenity` object with the provided values and saves it to
   * the repository.
   * 
   * @returns a boolean value indicating whether the amenity was updated successfully
   * or not.
   * 
   * 	- `map(amenity -> communityRepository.findByCommunityId(updatedAmenity.getCommunityId())`:
   * This line returns a stream of `Community` objects that are associated with the
   * given `Amenity` object. The `findByCommunityId` method is called on the
   * `communityRepository` to retrieve the communities associated with the `Amenity`.
   * 	- `map(community -> { ... })`: This line returns a stream of `Amenity` objects
   * that are updated with the latest values from the `updatedAmenity` object. The
   * `setName`, `setPrice`, `setId`, `setAmenityId`, and `setDescription` methods are
   * called on each `Amenity` object to update its properties.
   * 	- `orElse(null)`: This line returns a stream of `Amenity` objects that are either
   * updated or returned as `null` if there is no community associated with the given
   * `Amenity`.
   * 	- `map(amenityRepository::save)`: This line saves the updated `Amenity` objects
   * in the database using the `amenityRepository`.
   * 
   * The output of the `updateAmenity` function is a stream of `Amenity` objects that
   * are either updated or returned as `null`, depending on whether there is a community
   * associated with the given `Amenity`.
   */
  @Override
  public boolean updateAmenity(AmenityDto updatedAmenity) {
    String amenityId = updatedAmenity.getAmenityId();
    return amenityRepository.findByAmenityId(amenityId)
        .map(amenity -> communityRepository.findByCommunityId(updatedAmenity.getCommunityId())
            .map(community -> {
              Amenity updated = new Amenity();
              updated.setName(updatedAmenity.getName());
              updated.setPrice(updatedAmenity.getPrice());
              updated.setId(amenity.getId());
              updated.setAmenityId(amenityId);
              updated.setDescription(updatedAmenity.getDescription());
              return updated;
            })
            .orElse(null))
        .map(amenityRepository::save).isPresent();
  }
}
