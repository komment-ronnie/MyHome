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
 * is a Java class that provides methods for managing amenities in a database using
 * JPA (Java Persistence API). The class provides the following methods:
 * 
 * 	- `listAllAmenities(String communityId)`: Retrieves a set of all amenities
 * associated with a specific community.
 * 	- `updateAmenity(AmenityDto updatedAmenity)`: Updates an existing amenity in the
 * database based on the input `updatedAmenity` object, which contains the latest
 * values for name, price, community Id, and description.
 */
@Service
@RequiredArgsConstructor
public class AmenitySDJpaService implements AmenityService {

  private final AmenityRepository amenityRepository;
  private final CommunityRepository communityRepository;
  private final CommunityService communityService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * creates a list of amenities for a given community and saves them to the database.
   * 
   * @param amenities set of amenities to be created in the community.
   * 
   * 	- `Set<AmenityDto> amenities`: A set of `AmenityDto` objects, representing the
   * amenities to be created.
   * 	- `String communityId`: The ID of the community to which the amenities belong.
   * 
   * @param communityId identifier of the community to which the amenities belong, which
   * is used to retrieve the community details and associate the amenities with it in
   * the database.
   * 
   * @returns a list of `AmenityDto` objects representing the created amenities.
   * 
   * 	- `Optional<List<AmenityDto>>`: The function returns an optional list of amenities,
   * which means that if no amenities were created successfully, the list will be empty.
   * 	- `createAmenities(Set<AmenityDto> amenities, String communityId)`: This is the
   * input parameter for the function, which represents a set of amenities and the
   * community ID to create them in.
   * 	- `final Optional<Community> community = communityService.getCommunityDetailsById(communityId);`:
   * This line retrieves the community details for the given community ID using the
   * `communityService`. If no community is found, the `Optional` will be empty.
   * 	- `if (!community.isPresent()) {`: This check ensures that if no community is
   * found, the function will return an empty list of amenities.
   * 	- `return Optional.empty();`: This line returns an empty `Optional` list if no
   * community is found.
   * 	- `final List<Amenity> amenitiesWithCommunity = amenities.stream()`: This line
   * streams the input `Set` of amenities and maps each amenity to its corresponding
   * `Amenity` object, including the community ID.
   * 	- `map(amenity -> {`: This line maps each amenity to its updated `Amenity` object
   * with the community ID.
   * 	- `amenity.setCommunity(community.get());`: This line sets the community ID for
   * each amenity.
   * 	- `return amenitiesWithCommunity;` : This line returns the list of updated `Amenity`
   * objects.
   * 	- `final List<AmenityDto> createdAmenities =`: This line streams the list of
   * updated `Amenity` objects and maps each object to its corresponding `AmenityDto`
   * object using the `amenityApiMapper`.
   * 	- `saveAll(createdAmenities).stream()`: This line streams the list of `AmenityDto`
   * objects and maps each object to its corresponding `Amenity` object using the `amenityApiMapper`.
   * 	- `return Optional.of(createdAmenities);` : This line returns an `Optional` list
   * of `AmenityDto` objects representing the created amenities.
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
   * retrieves an Optional<Amenity> object containing the details of an amenity with
   * the provided amenity ID from the amenity repository.
   * 
   * @param amenityId identifier of an amenity for which details are requested.
   * 
   * @returns an Optional object containing the details of the amenity with the provided
   * ID.
   * 
   * Optional<Amenity> represents an optional amenity detail object.
   * The findByAmenityId() method from the amenityRepository is used to retrieve the
   * amenity details for a given amenity ID.
   */
  @Override
  public Optional<Amenity> getAmenityDetails(String amenityId) {
    return amenityRepository.findByAmenityId(amenityId);
  }

  /**
   * deletes an amenity from a community by removing it from the community's amenities
   * list and then deleting the amenity from the repository.
   * 
   * @param amenityId id of an amenity that needs to be deleted.
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
   * retrieves the amenities associated with a community by querying the community
   * repository and mapping the community object to its amenity collection. If no
   * amenities are found, an empty set is returned.
   * 
   * @param communityId identifier of a community for which the amenities are to be listed.
   * 
   * @returns a set of amenities associated with a specific community.
   * 
   * 	- The output is a Set<Amenity>, which means it is an unordered collection of
   * Amenity objects.
   * 	- The Set contains the list of all amenities associated with a particular community,
   * as retrieved from the communityRepository.
   * 	- The function returns an empty Set if no amenities are found for the given communityId.
   * 	- If amenities are found, they are returned as a mapped collection of Amenity
   * objects from the Community object's amenities field.
   */
  @Override
  public Set<Amenity> listAllAmenities(String communityId) {
    return communityRepository.findByCommunityIdWithAmenities(communityId)
        .map(Community::getAmenities)
        .orElse(new HashSet<>());
  }

  /**
   * updates an amenity in the database based on the input `updatedAmenity`. It retrieves
   * the existing amenity with the same `amenityId`, and if found, updates its name,
   * price, and other attributes. If no matching amenity is found, it creates a new
   * one. Finally, it saves the updated amenity to the repository.
   * 
   * @param updatedAmenity updated amenity information that is being updated in the
   * function, including its name, price, ID, amenity ID, and description.
   * 
   * 	- `amenityId`: The ID of the amenity to be updated.
   * 	- `communityId`: The ID of the community associated with the amenity.
   * 	- `name`: The name of the amenity.
   * 	- `price`: The price of the amenity.
   * 	- `description`: A brief description of the amenity.
   * 
   * @returns a boolean value indicating whether the amenity was updated successfully
   * or not.
   * 
   * 	- `map(amenity -> communityRepository.findByCommunityId(updatedAmenity.getCommunityId())`:
   * This line retrieves the community associated with the updated amenity.
   * 	- `map(community -> {...})`: This line creates a new `Amenity` object by updating
   * its name, price, id, amenity id, and description with the values from the input `updatedAmenity`.
   * 	- `orElse(null)`: This line returns the updated `Amenity` object if it exists,
   * otherwise returns `null`.
   * 	- `map(amenityRepository::save)`: This line saves the updated `Amenity` object
   * in the repository.
   * 
   * The output of the `updateAmenity` function is a `Optional` object containing the
   * updated `Amenity` object or `null` if no update was made.
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
