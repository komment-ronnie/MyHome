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
 * provides various methods for managing communities and their associated houses in
 * a Java application using Spring Data JPA. These methods include adding, updating,
 * removing admins from a community, deleting a community by first identifying and
 * removing all associated houses, generating a unique identifier, removing a house
 * from a community by removing its members, and deleting a house by first removing
 * its members from the community and then deleting the house itself.
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
   * creates a new community and adds an admin user to it, then saves the community to
   * the repository.
   * 
   * @param communityDto Community object that is being created, which contains the
   * necessary data to create a new community in the system.
   * 
   * 	- `communityDto.setCommunityId(generateUniqueId());`: This line sets the `id`
   * property of the community object to a generated unique ID.
   * 	- `String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();`:
   * This line retrieves the user ID of the authenticated user.
   * 	- `Community community = addAdminToCommunity(communityMapper.communityDtoToCommunity(communityDto),
   * userId);`: This line adds an administrator to the community using the `addAdminToCommunity`
   * function, which takes the deserialized community object and the user ID as inputs.
   * 	- `Community savedCommunity = communityRepository.save(community);`: This line
   * saves the created community to the repository using the `save` method of the `CommunityRepository`.
   * 	- `log.trace("saved community with id[{}] to repository", savedCommunity.getId());`:
   * This line logs a trace message indicating that the community was saved to the
   * repository with its ID.
   * 
   * @returns a saved community object in the repository.
   * 
   * 	- `community`: The created community object with its ID generated using `generateUniqueId()`.
   * 	- `userId`: The user ID of the authenticated principal, used to add an admin to
   * the community.
   * 	- `communityMapper`: A mapper object used to convert the `CommunityDto` to a
   * `Community` object.
   * 	- `communityRepository`: A repository object used to save the created community
   * in the database.
   * 	- `log`: A logging object used to log trace messages related to the creation of
   * the community.
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
   * adds a user as an administrator to a community by retrieving their existing
   * communities, adding the specified community to that list, and then updating the
   * community's admin set with the retrieved user.
   * 
   * @param community Community object that the function is modifying by adding an admin
   * to its list of admins.
   * 
   * 	- The `Community` object contains a `setAdmins()` method that sets the list of
   * admins for the community.
   * 	- The `admin` parameter is an instance of `User`, which has a `getCommunities()`
   * method that returns a list of communities associated with the user.
   * 	- The `findByUserIdWithCommunities()` method of the `communityAdminRepository`
   * class is used to retrieve the admin for the given `userId`. If the admin is found,
   * the `add()` method is called on the `admin` object to add the community to its
   * list of communities.
   * 
   * @param userId user ID of the admin to be added to the community.
   * 
   * @returns a modified Community object with the added admin user's information.
   * 
   * 	- The community object that has been updated with the added admin.
   * 	- The admin object that has been added to the community.
   * 	- A set of admins that contains the added admin.
   * 	- The original communities collection of the community, which has not been modified.
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
   * retrieves a list of communities from the repository and returns it as a set.
   * 
   * @param pageable page number and page size for fetching a subset of the communities
   * from the repository.
   * 
   * 	- `Pageable`: This is an interface that provides methods for navigating through
   * large data sets efficiently. It has three methods: `getPageNumber()` to get the
   * current page number, `getPageSize()` to get the number of items per page, and
   * `getTotalPages()` to get the total number of pages.
   * 
   * @returns a set of all `Community` objects stored in the repository.
   * 
   * The Set<Community> variable communityListSet is initialized as new HashSet<>();.
   * This means that it starts empty and can hold any number of Community objects without
   * causing any duplicate entries.
   * 
   * When the forEach() method is called on the findAll(pageable) method, it iterates
   * over each Community object in the repository's collection and adds it to the
   * communityListSet. The pageable argument determines how many Community objects are
   * retrieved from the repository at a time.
   * 
   * The returned Set<Community> variable communityListSet represents all the Communities
   * in the database that match the query specified by the pageable argument.
   */
  @Override
  public Set<Community> listAll(Pageable pageable) {
    Set<Community> communityListSet = new HashSet<>();
    communityRepository.findAll(pageable).forEach(communityListSet::add);
    return communityListSet;
  }

  /**
   * retrieves a set of community objects from the repository and returns it.
   * 
   * @returns a set of all communities stored in the repository.
   * 
   * 	- The `Set<Community>` object represents a collection of all communities in the
   * database.
   * 	- The elements in the set are references to `Community` objects, which contain
   * information about each community.
   * 	- The `HashSet` class is used to ensure that duplicate entries are not included
   * in the set.
   * 	- The function uses the `findAll()` method of the `communityRepository` to retrieve
   * all communities from the database.
   * 	- The `forEach()` method is then called on the retrieved communities, passing in
   * the `communities` set as the iteration target. This adds each community to the set.
   */
  @Override public Set<Community> listAll() {
    Set<Community> communities = new HashSet<>();
    communityRepository.findAll().forEach(communities::add);
    return communities;
  }

  /**
   * searches for community houses based on a given community ID and returns an optional
   * list of community houses if found, otherwise returns an empty list.
   * 
   * @param communityId ID of the community whose houses are to be retrieved.
   * 
   * @param pageable pagination information for the community houses, allowing the
   * function to retrieve a subset of the community houses based on the specified page
   * size and position.
   * 
   * 	- `communityId`: A string representing the community ID for which houses are to
   * be retrieved.
   * 	- `pageable`: An object that defines paging and sorting options for the result
   * set. Its properties include:
   * 	+ `size`: The number of houses to retrieve per page (default: 10).
   * 	+ `sort`: A list of sort criteria in the format `(field name, ascending/descending)`.
   * If no field is specified, the default is to sort by the community ID in ascending
   * order.
   * 	+ `direction`: The sorting direction (`ascending` or `descending`). If not provided,
   * the default is `ascending`.
   * 
   * @returns a `Optional` object containing a list of `CommunityHouse` objects if the
   * community exists, otherwise an empty `Optional`.
   * 
   * 	- `Optional<List<CommunityHouse>>`: The return type is an optional list of community
   * houses, indicating that the function may or may not return a non-empty list depending
   * on whether a community with the given ID exists.
   * 	- `findAllByCommunity_CommunityId(communityId, pageable)`: This method call returns
   * all community houses associated with the given community ID using the `pageable`
   * parameter to specify the pagination criteria.
   * 	- `communityRepository.existsByCommunityId(communityId)`: This method checks
   * whether a community with the given ID exists in the repository. If the community
   * exists, the function proceeds to the next step; otherwise, it returns an empty list.
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
   * retrieves a list of community admins for a given community ID using the
   * `communityRepository` and `communityAdminRepository`. If the community exists, it
   * returns an `Optional` containing a list of community admins. Otherwise, it returns
   * an empty `Optional`.
   * 
   * @param communityId unique identifier of a community whose administrators are to
   * be retrieved.
   * 
   * @param pageable pagination information for retrieving a list of community admins,
   * allowing for efficient and flexible retrieval of a subset of the data.
   * 
   * 	- `communityId`: A string representing the ID of the community for which admins
   * are to be retrieved.
   * 	- `pageable`: An instance of `Pageable`, which enables paging and sorting of the
   * result set based on various attributes, such as `sort`, `order`, `direction`, and
   * `limit`.
   * 
   * @returns a `Optional` of \begin{code}
   * List<User>
   * \end{code} containing the community admins for the given community ID.
   * 
   * 	- `Optional<List<User>>`: This represents an optional list of users who are
   * community admins for the specified community ID. If no users exist with the given
   * community ID, this will be an empty list.
   * 	- `List<User>`: This is a list of user objects representing the community admins
   * for the specified community ID. Each user object contains fields for id, username,
   * email, and other relevant information.
   * 	- `Pageable`: This represents the pageable result set, which allows for paging
   * and fetching of a subset of the total number of users. The pageable result set is
   * used to retrieve a subset of the users who are community admins for the specified
   * community ID.
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
   * retrieves a `Optional<User>` object representing the community admin associated
   * with the given `adminId`.
   * 
   * @param adminId ID of a user who is to be retrieved as a community administrator.
   * 
   * @returns an Optional<User> object containing the community administrator with the
   * specified ID, if found.
   * 
   * Optional<User> represents an optional user object, indicating whether a community
   * administrator exists with the given ID or not.
   * 
   * public indicates that the function is defined outside of any class and is accessible
   * from any package or class.
   * 
   * return is used to indicate the value being returned from the function.
   * 
   * communityAdminRepository refers to a repository of community admins.
   * 
   * findByUserId is a method of the community admin repository that returns an
   * Optional<User> object representing the community administrator with the given user
   * ID, if it exists.
   */
  @Override
  public Optional<User> findCommunityAdminById(String adminId) {
    return communityAdminRepository.findByUserId(adminId);
  }

  /**
   * retrieves the details of a community by its ID from the repository.
   * 
   * @param communityId ID of the Community to retrieve details for.
   * 
   * @returns an optional instance of the `Community` class containing details of the
   * community with the provided ID.
   * 
   * 	- `Optional<Community>` represents a container for holding a community object,
   * which can be present or absent depending on whether a community with the provided
   * id exists in the repository.
   * 	- `Community` is the class representing a community, containing attributes such
   * as id, name, and description.
   */
  @Override public Optional<Community> getCommunityDetailsById(String communityId) {
    return communityRepository.findByCommunityId(communityId);
  }

  /**
   * retrieves community details and admins associated with a given community ID from
   * the repository.
   * 
   * @param communityId ID of the Community for which details and administrators are
   * to be retrieved.
   * 
   * @returns an optional instance of the `Community` class containing details of the
   * specified community and its administrators.
   * 
   * 	- `Optional<Community>` represents an optional community object, which means that
   * if no community is found with the given ID, the function will return an empty Optional.
   * 	- `communityRepository.findByCommunityIdWithAdmins(communityId)` is a query that
   * retrieves the community object with the given ID and includes its admin details.
   * 	- The returned community object contains information such as the community name,
   * description, and admins.
   */
  @Override
  public Optional<Community> getCommunityDetailsByIdWithAdmins(String communityId) {
    return communityRepository.findByCommunityIdWithAdmins(communityId);
  }

  /**
   * takes a community ID and a set of admin IDs, adds the admins to the community, and
   * returns an optional community object representing the updated community with added
   * admins.
   * 
   * @param communityId ID of the community to which admins will be added.
   * 
   * @param adminsIds Set of user IDs of the admins to be added to the community.
   * 
   * 	- `Set<String> adminsIds`: A set of strings representing the IDs of the administrators
   * to be added to the community.
   * 
   * The function first retrieves an optional community object from the repository using
   * the `communityRepository.findByCommunityIdWithAdmins(communityId)` method. If the
   * result is present, it maps each administrator ID in `adminsIds` to its corresponding
   * community admin using the `communityAdminRepository.findByUserIdWithCommunities(adminId)`
   * method. The `map()` method is used to transform the resulting admins into a new
   * set of community admins by adding them to their respective communities, and then
   * saving each admin using the `save()` method. Finally, the function returns an
   * optional community object representing the updated community with the added admins.
   * 
   * @returns an `Optional` of a `Community` object that has been updated with the
   * provided admins.
   * 
   * 	- `Optional<Community>` represents an optional Community object that can be
   * Some(Community) or None.
   * 	- `communitySearch` is an Optional<Community> that contains the found Community
   * object or is empty if no matching Community was found.
   * 	- `adminsIds` is a Set<String> of admin IDs.
   * 	- `communityAdminRepository` is a repository for finding and saving CommunityAdmins.
   * 	- `save()` method saves the provided CommunityAdmin object in the database.
   * 
   * The function first checks if a matching Community object exists with the given
   * community ID using the `findByCommunityIdWithAdmins()` method of the communityRepository.
   * If a match is found, it then loops through each admin ID and finds the corresponding
   * CommunityAdmin objects using the `findByUserIdWithCommunities()` method of the
   * communityAdminRepository. It then adds the found CommunityAdmin object to the
   * Community object's list of admins and saves the modified Community object in the
   * database using the `save()` method. If no matching Community object is found, the
   * function returns an empty Optional<Community>.
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
   * takes a community ID and a set of houses, checks if the community exists, and adds
   * each house to the community if it doesn't exist or if its id is not already present
   * in the community. It also generates unique IDs for new houses.
   * 
   * @param communityId unique identifier of the community for which the houses are
   * being added.
   * 
   * @param houses set of houses to be added to the community.
   * 
   * 	- `houses`: A set of `CommunityHouse` objects, containing the house details for
   * each community member.
   * 	- `CommunityHouse`: A class representing a house in a community, with attributes
   * including `houseId`, `name`, and `community`.
   * 	- `generateUniqueId`: A method generating a unique identifier for the house's ID.
   * 
   * The function first checks if the specified community exists by querying the
   * `communityRepository`. If it does not exist, a new community is created with the
   * existing houses added to it. The `houses` set is then updated with the newly
   * generated IDs and saved in the repository.
   * 
   * @returns a set of unique house IDs that have been added to a community, along with
   * the community's updated house count.
   * 
   * 	- `Set<String>` - The output is a set of unique house IDs that were successfully
   * added to the community.
   * 	- `Optional<Community>` - The `communitySearch` variable represents the result
   * of a query to find the community with the given ID, and it may be empty if no such
   * community exists. If the community is found, the function will modify its houses
   * collection and save it.
   * 	- `Map<CommunityHouse, House>` - This variable is used in the inner `if` statement
   * to check if a house already exists in the community. If it does, the function will
   * generate a new unique ID for the house and add it to the community's houses collection.
   * 	- `CommunityHouseRepository` - This repository is responsible for saving the
   * modified houses to the database.
   * 	- `generateUniqueId()` - This method generates a unique ID for each added house,
   * which helps prevent duplicates in the database.
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
   * removes an admin from a community based on their user ID, returning true if
   * successful and false otherwise.
   * 
   * @param communityId ID of the community whose admin is to be removed.
   * 
   * @param adminId ID of an administrator to be removed from a community.
   * 
   * @returns a boolean value indicating whether an admin was successfully removed from
   * a community.
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
   * deletes a community from the database by finding all houses associated with it,
   * removing them one by one, and then deleting the community itself.
   * 
   * @param communityId ID of the community to be deleted, which is used to locate and
   * remove the community and its associated houses from the database.
   * 
   * @returns a boolean value indicating whether the community was successfully deleted.
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
   * generates a unique identifier using the `UUID` class and returns it as a string.
   * 
   * @returns a unique string of characters generated randomly using the `UUID` class.
   */
  private String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  /**
   * removes a house from a community by first removing the house from the community's
   * houses list, then deleting the house members associated with the house, and finally
   * saving the community and deleting the house.
   * 
   * @param community Community object that the method is called on, which contains
   * information about the community and its houses.
   * 
   * 	- `community`: This is an instance of the `Community` class, which has several
   * attributes, including `id`, `name`, `description`, and a `set` of `House` objects
   * referred to as `houses`.
   * 	- `houseId`: This is the ID of the house to be removed from the community.
   * 	- `houseOptional`: An optional instance of the `CommunityHouse` class, which
   * contains the `id`, `houseMembers`, and `community` attributes. The `houseOptional`
   * variable is created using the `findByHouseIdWithHouseMembers` method of the `communityHouseRepository`.
   * 	- `houses`: This is a `Set` of instances of the `House` class, which contains the
   * ID and other attributes of each house in the community. The `houses` Set is modified
   * within the function to remove the house with the specified `houseId`.
   * 	- `memberIds`: This is a `Set` of strings, containing the IDs of the members
   * associated with the house to be removed. The `memberIds` Set is created using the
   * `stream` method of the `House` class, which maps each house member to its ID.
   * 	- `houseService`: This is an instance of the `HouseService` class, which provides
   * methods for managing houses and their members. The `deleteMemberFromHouse` method
   * is called on the `houseService` instance to remove each member from the house with
   * the specified `houseId`.
   * 	- `communityRepository`: This is an instance of the `CommunityRepository` class,
   * which provides methods for managing communities. The `save` method is called on
   * the `communityRepository` instance after removing the house and its members to
   * update the community state.
   * 	- `communityHouseRepository`: This is an instance of the `CommunityHouseRepository`
   * class, which provides methods for managing houses and their associations with
   * communities. The `deleteByHouseId` method is called on the `communityHouseRepository`
   * instance after removing the house and its members to delete the house from the community.
   * 
   * @param houseId ID of the house to be removed from the community.
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
