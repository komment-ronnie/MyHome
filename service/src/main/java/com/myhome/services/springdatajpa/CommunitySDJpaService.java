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

import com.myhome.controllers.dto.CommunityDto;
import com.myhome.controllers.dto.mapper.CommunityMapper;
import com.myhome.domain.Community;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.domain.User;
import com.myhome.repositories.CommunityHouseRepository;
import com.myhome.repositories.CommunityRepository;
import com.myhome.repositories.UserRepository;
import com.myhome.services.CommunityService;
import com.myhome.services.HouseService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * provides functionality for managing communities in a Java-based application. It
 * offers several methods for adding, removing, and querying community members and
 * houses, as well as deleting communities. The service uses JPA (Java Persistence
 * API) to interact with the database and provides transactional support for performing
 * multiple operations together in a single database transaction.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CommunitySDJpaService implements CommunityService {
  private final CommunityRepository communityRepository;
  private final UserRepository communityAdminRepository;
  private final CommunityMapper communityMapper;
  private final CommunityHouseRepository communityHouseRepository;
  private final HouseService houseService;

  /**
   * generates a unique ID for a new community, adds an admin user to the community,
   * and saves it to the repository.
   * 
   * @param communityDto CommunityDTO object containing information about the community
   * to be created, which is used to create and save the community instance in the database.
   * 
   * 	- `communityDto.setCommunityId(generateUniqueId());`: This sets the community ID
   * to a generated unique value.
   * 	- `String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();`:
   * This retrieves the authenticated user ID.
   * 	- `Community community = addAdminToCommunity(communityMapper.communityDtoToCommunity(communityDto),
   * userId);`: This method adds an administrator to a community using the community
   * mapper to convert the input `communityDto` to a `Community` object, and then adds
   * the administrator to the community.
   * 	- `Community savedCommunity = communityRepository.save(community);`: This saves
   * the created community to the repository.
   * 
   * @returns a saved community entity in the repository with a unique ID.
   * 
   * 	- `savedCommunity`: This is the saved community object that was created with the
   * provided `communityDto`. It has an `id` attribute that represents the unique
   * identifier of the community.
   * 	- `log.trace()`: This line logs a message at trace level indicating that the
   * community was saved to the repository with its ID.
   */
  @Override
  public Community createCommunity(CommunityDto communityDto) {
    communityDto.setCommunityId(generateUniqueId());
    String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Community community = addAdminToCommunity(communityMapper.communityDtoToCommunity(communityDto),
        userId);
    Community savedCommunity = communityRepository.save(community);
    log.trace("saved community with id[{}] to repository", savedCommunity.getId());
    return savedCommunity;
  }

  /**
   * adds a user as an admin to a Community by updating the Community's `Admins` set
   * with the provided user ID, and then updates the Community's `setAdmins` field with
   * the new list of admins.
   * 
   * @param community Community object that the function modifies by adding a new admin
   * to its `admins` set.
   * 
   * 	- `community`: A Community object representing the community to which an admin
   * is being added.
   * 	- `userId`: The user ID of the admin to be added to the community.
   * 
   * @param userId ID of the user to whom the admin belongs, and it is used to find the
   * existing admin in the `communityAdminRepository` and add the community to that
   * admin's set of communities.
   * 
   * @returns a modified `Community` object with the added admin user.
   * 
   * 	- The community object is updated by adding the specified admin to its list of admins.
   * 	- The `admin` object is updated by adding the specified community to its list of
   * communities.
   * 	- The `communityAdminRepository` is used to find the admin for the specified user
   * ID and retrieve the communities associated with it.
   * 	- The function returns the updated community object.
   */
  private Community addAdminToCommunity(Community community, String userId) {
    communityAdminRepository.findByUserIdWithCommunities(userId).ifPresent(admin -> {
      admin.getCommunities().add(community);
      Set<User> admins = new HashSet<>();
      admins.add(admin);
      community.setAdmins(admins);
    });
    return community;
  }

  /**
   * retrieves a list of `Community` objects from the database using the `findAll`
   * method and stores them in a `Set` object, which is then returned.
   * 
   * @param pageable page number and the number of communities to be retrieved per page,
   * which are used to query the community repository and return a paginated list of communities.
   * 
   * 	- `Pageable pageable`: This represents an object that can be used to navigate and
   * retrieve a page of objects from a data source.
   * 	- `Set<Community> communityListSet`: The method returns a set containing all
   * communities retrieved from the database.
   * 
   * @returns a set of `Community` objects.
   * 
   * 	- The Set<Community> output represents a collection of Community objects that
   * have been retrieved from the database using the findAll method.
   * 	- The Set is a new instance of HashSet, which means that all Community objects
   * will be unique and there will be no duplicates in the list.
   * 	- The forEach method is used to iterate over the Communities in the Set, and each
   * Community is added to the output Set using the add method.
   * 
   * No summary or additional information is provided at the end of this response.
   */
  @Override
  public Set<Community> listAll(Pageable pageable) {
    Set<Community> communityListSet = new HashSet<>();
    communityRepository.findAll(pageable).forEach(communityListSet::add);
    return communityListSet;
  }

  /**
   * retrieves a collection of `Community` objects from the repository and returns a
   * `Set` containing them.
   * 
   * @returns a set of all communities retrieved from the database.
   * 
   * 	- The output is a `Set` of `Community` objects, which represents a collection of
   * all communities in the system.
   * 	- The set is generated by calling the `findAll` method on the `communityRepository`,
   * which returns a list of all community objects in the database.
   * 	- The `forEach` method is then called on the `Set` of `Community` objects, passing
   * in the `communities::add` method to add each community object to the set.
   * 
   * Overall, the `listAll` function provides a convenient and efficient way to access
   * and manipulate all communities in the system.
   */
  @Override public Set<Community> listAll() {
    Set<Community> communities = new HashSet<>();
    communityRepository.findAll().forEach(communities::add);
    return communities;
  }

  /**
   * queries the `communityRepository` to determine if a community exists with the
   * provided `communityId`. If it does, it returns an `Optional` containing the list
   * of `CommunityHouse` objects associated with that community. If it doesn't exist,
   * it returns an empty `Optional`.
   * 
   * @param communityId identifier of the community for which the list of community
   * houses is being retrieved.
   * 
   * @param pageable Pageable object that defines the pagination parameters for retrieving
   * the list of community houses.
   * 
   * 	- `communityId`: The ID of the community to find the corresponding community
   * houses for.
   * 	- `pageable`: A `Pageable` object representing the pagination information for the
   * community house search. It typically contains the page number, page size, sort
   * order, and other parameters used to filter and display the results.
   * 
   * @returns an `Optional` object containing a list of `CommunityHouse` objects, if
   * the community exists and has houses associated with it.
   * 
   * 	- `Optional<List<CommunityHouse>>`: The output is an optional list of community
   * houses, indicating whether there are any community houses found for the provided
   * community ID. If there are no community houses found, the output will be `Optional.empty()`.
   * 	- `List<CommunityHouse>`: The list contains all the community houses associated
   * with the provided community ID, as retrieved from the database.
   * 	- `communityId`: The input parameter representing the community ID for which the
   * community houses are being retrieved.
   */
  @Override
  public Optional<List<CommunityHouse>> findCommunityHousesById(String communityId,
      Pageable pageable) {
    boolean exists = communityRepository.existsByCommunityId(communityId);
    if (exists) {
      return Optional.of(
          communityHouseRepository.findAllByCommunity_CommunityId(communityId, pageable));
    }
    return Optional.empty();
  }

  /**
   * retrieves a list of community admins for a given community ID, using the
   * `communityRepository` and `communityAdminRepository` to filter and page the results.
   * 
   * @param communityId ID of the community whose admins are to be retrieved.
   * 
   * @param pageable page of results that the method will return, allowing for pagination
   * and efficient retrieval of the desired data.
   * 
   * 	- `communityId`: A string representing the unique identifier for a community.
   * 	- `Pageable`: An interface defining the `getNumberOfElements()` and `getTotalElements()`
   * methods, which provide information about the number of elements in the page and
   * the total number of elements in the collection, respectively.
   * 
   * @returns a `Optional` object containing a list of `User` objects if the community
   * exists, otherwise an empty `Optional`.
   * 
   * 	- The function returns an `Optional` object, which contains a value if the operation
   * was successful, and an empty `Optional` object otherwise.
   * 	- If the operation was successful, the `Optional` object contains a list of `User`
   * objects representing the community admins for the given community ID.
   * 	- The list of `User` objects is returned by the `communityAdminRepository.findAllByCommunities_CommunityId`
   * method, which is responsible for retrieving the community admins from the database
   * based on the community ID parameter.
   * 	- The `Pageable` parameter represents a page of results that can be fetched from
   * the database, and it is used to control the pagination of the results returned by
   * the function.
   */
  @Override
  public Optional<List<User>> findCommunityAdminsById(String communityId,
      Pageable pageable) {
    boolean exists = communityRepository.existsByCommunityId(communityId);
    if (exists) {
      return Optional.of(
          communityAdminRepository.findAllByCommunities_CommunityId(communityId, pageable)
      );
    }
    return Optional.empty();
  }

  /**
   * retrieves a `Optional<User>` object containing the community administrator associated
   * with the given `adminId`.
   * 
   * @param adminId ID of the community administrator to be retrieved from the database.
   * 
   * @returns an optional `User` object representing the community administrator with
   * the provided ID.
   * 
   * Optional<User> return value: The function returns an optional object of type `User`,
   * indicating whether a community admin was found or not. If a user is found, the
   * `User` object contains information about the community admin.
   * 
   * `findByUserId`: This method from the `communityAdminRepository` class is used to
   * retrieve a community admin based on their ID. It returns an optional object of
   * type `User`.
   */
  @Override
  public Optional<User> findCommunityAdminById(String adminId) {
    return communityAdminRepository.findByUserId(adminId);
  }

  /**
   * retrieves community details by ID from the repository.
   * 
   * @param communityId identifier of the community to retrieve details for.
   * 
   * @returns an optional instance of `Community`.
   * 
   * 	- The `Optional` type indicates that the function may return `None` if no community
   * with the given ID is found in the repository.
   * 	- The `findByCommunityId` method of the `communityRepository` returns a `List`
   * of `Community` objects that match the given ID, or an empty list if no such community
   * exists.
   * 	- The returned `Optional` contains only one element, which is a `Community` object
   * representing the matching community.
   */
  @Override public Optional<Community> getCommunityDetailsById(String communityId) {
    return communityRepository.findByCommunityId(communityId);
  }

  /**
   * retrieves community details along with its administrators by passing the community
   * ID as an argument.
   * 
   * @param communityId identifier of the community for which details and admins are
   * to be retrieved.
   * 
   * @returns an Optional object containing the details of the specified community and
   * its associated admins.
   * 
   * 	- `Optional<Community>`: This is the type of the output, which is an optional
   * instance of the `Community` class.
   * 	- `communityRepository.findByCommunityIdWithAdmins(communityId)`: This is the
   * method called to retrieve the community details along with its admins. It takes
   * the community ID as an argument and returns a `Stream` of `Community` objects that
   * match the given ID, along with their admin users.
   */
  @Override
  public Optional<Community> getCommunityDetailsByIdWithAdmins(String communityId) {
    return communityRepository.findByCommunityIdWithAdmins(communityId);
  }

  /**
   * adds administrators to a community by searching for the community, adding admins
   * to it, and saving the updated community and admin records.
   * 
   * @param communityId identifier of the community whose admins are being added.
   * 
   * @param adminsIds Set of user IDs that will be added as admins to the community.
   * 
   * 	- `Set<String> adminsIds`: This represents a set of strings that contain the IDs
   * of the administrators to be added to the community.
   * 
   * The function then proceeds to iterate over each ID in the set and performs the
   * following operations:
   * 
   * 1/ Finds the community corresponding to the ID using the
   * `communityRepository.findByCommunityIdWithAdmins` method.
   * 2/ Iterates over each administrator associated with the community using the
   * `communityAdminRepository.findByUserIdWithCommunities` method.
   * 3/ Adds the community to the set of communities associated with each administrator
   * using the `addCommunityToAdmin` method.
   * 4/ Saves the updated administrator entity using the `save` method.
   * 5/ Returns an optional value containing the updated community entity or an empty
   * optional if no updates were made.
   * 
   * @returns an `Optional` containing a `Community` object that has been updated with
   * the provided admins.
   * 
   * 	- `Optional<Community> communitySearch`: This is an optional instance of `Community`,
   * which represents the community that is being searched for based on its ID. If the
   * community is found, this Optional will contain it. Otherwise, it will be empty.
   * 	- `map()` method: This method is called on the `Optional<Community>` instance to
   * map over its contents and perform some operation on each community. In this case,
   * the method takes a function that takes an admin ID as input and maps over the
   * admins of the community. For each admin, it saves the admin to the database and
   * adds them to the community's list of admins. The resulting Optional instance is
   * then returned.
   * 	- `orElseGet()` method: This method is called on the `Optional<Community>` instance
   * to get an alternative value if the original Optional is empty. In this case, it
   * returns an empty Optional instance.
   */
  @Override
  public Optional<Community> addAdminsToCommunity(String communityId, Set<String> adminsIds) {
    Optional<Community> communitySearch =
        communityRepository.findByCommunityIdWithAdmins(communityId);

    return communitySearch.map(community -> {
      adminsIds.forEach(adminId -> {
        communityAdminRepository.findByUserIdWithCommunities(adminId).map(admin -> {
          admin.getCommunities().add(community);
          community.getAdmins().add(communityAdminRepository.save(admin));
          return admin;
        });
      });
      return Optional.of(communityRepository.save(community));
    }).orElseGet(Optional::empty);
  }

  /**
   * takes a community ID and a set of houses, checks if the community exists with the
   * same ID, and if not, creates a new one and adds the houses to it. If the community
   * exists, it updates the existing community with the added houses.
   * 
   * @param communityId unique identifier of the community to which the houses will be
   * added.
   * 
   * @param houses houses to be added to the community, and it is used to update or add
   * new houses to the community based on their existence or lack thereof.
   * 
   * 	- `houses`: This is an instance of `Set`, which represents a collection of
   * `CommunityHouse` objects. Each element in the set is a `CommunityHouse` object
   * with attributes such as `houseId`, `name`, and `community`.
   * 	- `communityId`: This is a string representing the unique identifier of the
   * community to which the houses belong.
   * 	- `community`: This is an instance of `Community`, which represents a collection
   * of houses. The `Community` object has attributes such as `id`, `name`, and `houses`.
   * 	- `houseId`: This is a string representing the unique identifier of each house
   * in the input set.
   * 	- `generateUniqueId()`: This is an optional method that generates a unique
   * identifier for each house if it does not already have one assigned. The identifier
   * is used to avoid duplication of houses within the same community.
   * 
   * @returns a set of unique house IDs that have been added to the specified community.
   * 
   * 	- `Set<String> addedIds`: This represents the set of unique house IDs that were
   * added to the community.
   * 	- `Community community`: This is the community object that was passed as a parameter
   * and on which the houses were added.
   * 	- `Set<House> communityHouses`: This is the set of houses that were already present
   * in the community, before the houses from the input set were added.
   * 	- `CommunityHouseRepository save()`: This method is used to save the updated
   * community object and its houses to the repository.
   * 
   * In summary, the function takes a community ID and a set of houses as input, adds
   * the houses to the community (if they don't already exist), and then saves the
   * updated community object to the repository. The output is a set of unique house
   * IDs that were added to the community.
   */
  @Override
  public Set<String> addHousesToCommunity(String communityId, Set<CommunityHouse> houses) {
    Optional<Community> communitySearch =
        communityRepository.findByCommunityIdWithHouses(communityId);

    return communitySearch.map(community -> {
      Set<String> addedIds = new HashSet<>();

      houses.forEach(house -> {
        if (house != null) {
          boolean houseExists = community.getHouses().stream()
              .noneMatch(communityHouse ->
                  communityHouse.getHouseId().equals(house.getHouseId())
                      && communityHouse.getName().equals(house.getName())
              );
          if (houseExists) {
            house.setHouseId(generateUniqueId());
            house.setCommunity(community);
            addedIds.add(house.getHouseId());
            communityHouseRepository.save(house);
            community.getHouses().add(house);
          }
        }
      });

      communityRepository.save(community);

      return addedIds;
    }).orElse(new HashSet<>());
  }

  /**
   * removes an administrator from a community by searching for the community and its
   * admins, removing the admin from the list of admins, and saving the updated community
   * to the repository. If successful, it returns `true`, otherwise it returns `false`.
   * 
   * @param communityId unique identifier of a community for which an admin is to be removed.
   * 
   * @param adminId ID of an administrator to be removed from a community.
   * 
   * @returns a boolean value indicating whether an admin has been successfully removed
   * from a community.
   */
  @Override
  public boolean removeAdminFromCommunity(String communityId, String adminId) {
    Optional<Community> communitySearch =
        communityRepository.findByCommunityIdWithAdmins(communityId);
    return communitySearch.map(community -> {
      boolean adminRemoved =
          community.getAdmins().removeIf(admin -> admin.getUserId().equals(adminId));
      if (adminRemoved) {
        communityRepository.save(community);
        return true;
      } else {
        return false;
      }
    }).orElse(false);
  }

  /**
   * deletes a community by first identifying all houses associated with it and then
   * deleting them before deleting the community itself.
   * 
   * @param communityId id of the community to be deleted.
   * 
   * @returns a boolean value indicating whether the community with the provided ID was
   * successfully deleted.
   */
  @Override
  @Transactional
  public boolean deleteCommunity(String communityId) {
    return communityRepository.findByCommunityIdWithHouses(communityId)
        .map(community -> {
          Set<String> houseIds = community.getHouses()
              .stream()
              .map(CommunityHouse::getHouseId)
              .collect(Collectors.toSet());

          houseIds.forEach(houseId -> removeHouseFromCommunityByHouseId(community, houseId));
          communityRepository.delete(community);

          return true;
        })
        .orElse(false);
  }

  /**
   * generates a unique identifier using the `UUID.randomUUID()` method and returns it
   * as a string.
   * 
   * @returns a randomly generated unique identifier in the form of a string.
   */
  private String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  /**
   * removes a house from a community by first removing the house members associated
   * with it, and then deleting the house itself. It returns a boolean indicating whether
   * the removal was successful.
   * 
   * @param community Community object that contains information about the community
   * and its houses, and is used to identify the house to be removed and to delete its
   * members from the community.
   * 
   * 	- `community`: The Community entity to be updated. It has various attributes such
   * as `id`, `name`, `description`, `image`, and `members`.
   * 	- `houseId`: The unique identifier of the house to be removed from the community.
   * 	- `houseOptional`: An optional reference to a `CommunityHouse` entity that
   * represents the house to be removed. If present, it contains the house's attributes
   * such as `id`, `communityId`, and `members`.
   * 	- `houses`: A set of `CommunityHouse` entities representing all the houses in the
   * community.
   * 	- `houseMembers`: A stream of `HouseMember` entities representing all the members
   * of the house to be removed.
   * 	- `memberIds`: A set of `String` values representing the unique IDs of the members
   * associated with the house to be removed.
   * 	- `deleteMemberFromHouse`: An API endpoint that deletes a member from a house.
   * 
   * @param houseId ID of the house to be removed from the community, which is used to
   * identify the house and its members to be deleted.
   * 
   * @returns a boolean value indicating whether the house was successfully removed
   * from the community.
   */
  @Transactional
  @Override
  public boolean removeHouseFromCommunityByHouseId(Community community, String houseId) {
    if (community == null) {
      return false;
    } else {
      Optional<CommunityHouse> houseOptional =
          communityHouseRepository.findByHouseIdWithHouseMembers(houseId);
      return houseOptional.map(house -> {
        Set<CommunityHouse> houses = community.getHouses();
        houses.remove(
            house); //remove the house before deleting house members because otherwise the Set relationship would be broken and remove would not work

        Set<String> memberIds = house.getHouseMembers()
            .stream()
            .map(HouseMember::getMemberId)
            .collect(
                Collectors.toSet()); //streams are immutable so need to collect all the member IDs and then delete them from the house

        memberIds.forEach(id -> houseService.deleteMemberFromHouse(houseId, id));

        communityRepository.save(community);
        communityHouseRepository.deleteByHouseId(houseId);
        return true;
      }).orElse(false);
    }
  }
}
