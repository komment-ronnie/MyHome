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
 * is a JPA service for handling amenities data in a Spring Boot application. It
 * provides methods for creating, reading, updating, and deleting amenities, as well
 * as listing all amenities associated with a specific community. The service uses
 * JPA to interact with the database and provides optional objects as output for each
 * method, indicating whether an operation was successful or not.
 */
@Service
@RequiredArgsConstructor
public class AmenitySDJpaService implements AmenityService {

  private final AmenityRepository amenityRepository;
  private final CommunityRepository communityRepository;
  private final CommunityService communityService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * creates a list of amenities for a community by retrieving the community details,
   * mapping amenities to amenity objects, and saving them in the database as amenity
   * DTOs.
   * 
   * @param amenities set of amenities that need to be created for a particular community,
   * which is used to map each amenity to its corresponding AmenityDto and then save
   * them to the database.
   * 
   * 	- `Set<AmenityDto>` - A set containing objects of type `AmenityDto`.
   * 	- `communityId` - A String representing the community ID.
   * 	- `Community` - An optional object of type `Community`, which contains the details
   * of a community.
   * 	- `Amenity` - An object representing an amenity, with properties such as
   * `setCommunity(Community)` and `map(Function<Amenity, AmenityDto>)` for mapping to
   * `AmenityDto`.
   * 	- `List<Amenity>` - A list of objects representing amenities, created by mapping
   * the input `amenities` through a function that converts each `AmenityDto` to an `Amenity`.
   * 
   * @param communityId identifier of the community to which the amenities will be
   * associated with, and is used to retrieve the community details from the service.
   * 
   * @returns a list of `AmenityDto` objects representing created amenities.
   * 
   * 	- The `Optional<List<AmenityDto>>` return type indicates that the function may
   * or may not return a list of amenities, depending on whether the community exists.
   * 	- The `createAmenities` function takes two parameters: `Set<AmenityDto> amenities`
   * and `String communityId`. These parameters represent the amenities to be created
   * and the community ID, respectively.
   * 	- In the body of the function, the `final Optional<Community>` return value is
   * extracted from the `communityService.getCommunityDetailsById(communityId)` method
   * call. If the community does not exist, the `Optional` return value will be empty.
   * 	- The `final List<Amenity>` variable `amenitiesWithCommunity` is created by mapping
   * each amenity in the `amenities` set to its corresponding `Amenity` object using
   * the `map()` method. The `setCommunity()` method is called on each `Amenity` object
   * to set the community ID.
   * 	- The `final List<AmenityDto>` variable `createdAmenities` is created by mapping
   * each `Amenity` object in `amenitiesWithCommunity` to its corresponding `AmenityDto`
   * using the `map()` method. The `saveAll()` method of the `amenityRepository` is
   * used to save all the created amenities.
   * 	- The returned `Optional<List<AmenityDto>>` contains a list of `AmenityDto` objects
   * representing the created amenities.
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
   * retrieves an Optional<Amenity> object representing the details of an amenity based
   * on its ID, by querying the amenityRepository.
   * 
   * @param amenityId identifier of the amenity to retrieve details for.
   * 
   * @returns an Optional containing the details of the amenity with the provided ID,
   * if found in the repository.
   * 
   * 	- The `Optional` object represents the availability of the amenity details for
   * the provided amenity ID. If the amenity is found in the repository, the `Optional`
   * will contain a non-empty value representing the amenity details. Otherwise, the
   * `Optional` will be empty.
   */
  @Override
  public Optional<Amenity> getAmenityDetails(String amenityId) {
    return amenityRepository.findByAmenityId(amenityId);
  }

  /**
   * deletes an amenity from a community by first finding the amenity in the repository,
   * then removing it from the community's list of amenities and finally deleting it
   * from the repository.
   * 
   * @param amenityId ID of an amenity to be deleted.
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
   * queries the community repository for a given community ID and returns a set of
   * amenities associated with that community.
   * 
   * @param communityId ID of the community whose amenities are to be listed.
   * 
   * @returns a set of amenities associated with a specific community.
   * 
   * 	- The output is a Set<Amenity> type, indicating that it contains a collection of
   * Amenity objects.
   * 	- The Set is created using the `map` method, which applies a transformation to
   * the result of the `findByCommunityIdWithAmenities` method. In this case, the
   * transformation is the `getAmenities` method of the `Community` class, which returns
   * a collection of Amenity objects associated with the community.
   * 	- If no amenities are found for the specified community ID, the output is an empty
   * Set<Amenity>.
   */
  @Override
  public Set<Amenity> listAllAmenities(String communityId) {
    return communityRepository.findByCommunityIdWithAmenities(communityId)
        .map(Community::getAmenities)
        .orElse(new HashSet<>());
  }

  /**
   * updates an amenity in the database, given its ID, name, price, description, and
   * community ID. It first retrieves the amenity from the repository, then updates it
   * with the provided values, and finally saves the updated amenity to the database.
   * 
   * @param updatedAmenity updated amenity information that is to be saved into the
   * database by the method.
   * 
   * 	- `amenityId`: A String representing the amenity ID.
   * 	- `communityId`: A String representing the community ID associated with the amenity.
   * 	- `name`: A String representing the name of the amenity.
   * 	- `price`: An integer representing the price of the amenity.
   * 	- `description`: A String representing the description of the amenity.
   * 
   * The function then uses the `amenityRepository` and `communityRepository` to find
   * the corresponding amenity and community records, respectively. The `findByAmenityId`
   * method returns a Stream of `Amenity` objects, while the `findByCommunityId` method
   * returns a Stream of `Community` objects.
   * 
   * The Streams are then combined using the `orElse` method to create a Stream of
   * `Amenity` objects that have been updated with the new values from `updatedAmenity`.
   * The `save` method is then called on the `amenityRepository` to persist the updated
   * amenities.
   * 
   * Finally, the function returns a boolean value indicating whether the update was
   * successful or not.
   * 
   * @returns a boolean value indicating whether the amenity was updated successfully
   * or not.
   * 
   * 	- `map(amenity -> communityRepository.findByCommunityId(updatedAmenity.getCommunityId())`:
   * This line retrieves the community associated with the updated amenity. The community
   * is retrieved from the `communityRepository`.
   * 	- `map(community -> { ... })`: This line maps the community to an updated amenity
   * object, which is created by setting the name, price, ID, amenity ID, and description
   * of the amenity to the corresponding values in the updated amenity object.
   * 	- `orElse(null)`: This line returns the updated amenity object if it exists, or
   * null otherwise.
   * 	- `map(amenityRepository::save)`: This line saves the updated amenity object to
   * the repository.
   * 
   * In summary, the `updateAmenity` function takes an updated amenity object as input
   * and retrieves the associated community from the database. It then creates a new
   * amenity object by mapping the values of the updated amenity and the associated
   * community, and finally saves the updated amenity object to the repository.
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
