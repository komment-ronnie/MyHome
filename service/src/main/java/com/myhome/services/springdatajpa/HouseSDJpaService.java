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

import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.repositories.CommunityHouseRepository;
import com.myhome.repositories.HouseMemberDocumentRepository;
import com.myhome.repositories.HouseMemberRepository;
import com.myhome.services.HouseService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * is an implementation of the HouseSDJpa interface provided by Spring Data JPA. It
 * provides several methods for managing house members and their relationships with
 * community houses, including creating, reading, updating, and deleting house members.
 * The service uses the JPA repository interface to interact with the database and
 * provide a layer of abstraction between the application logic and the underlying
 * database technology.
 */
@RequiredArgsConstructor
@Service
public class HouseSDJpaService implements HouseService {
  private final HouseMemberRepository houseMemberRepository;
  private final HouseMemberDocumentRepository houseMemberDocumentRepository;
  private final CommunityHouseRepository communityHouseRepository;

  /**
   * generates a unique identifier based on a UUID created using the `UUID.randomUUID()`
   * method and converted to a string.
   * 
   * @returns a unique, randomly generated string of characters.
   */
  private String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  /**
   * returns a set of `CommunityHouse` objects retrieved from the database using the
   * `findAll()` method of the `communityHouseRepository`.
   * 
   * @returns a set of `CommunityHouse` objects containing all the houses stored in the
   * database.
   * 
   * 	- `Set<CommunityHouse>` is the type of the returned variable, indicating that it
   * is an unordered set of `CommunityHouse` objects.
   * 	- `new HashSet<>` is used to create a new empty set, indicating that no elements
   * have been added to the set yet.
   * 	- `communityHouseRepository.findAll()` is called to retrieve a list of all
   * `CommunityHouse` objects from the database or data source, and each element in the
   * list is added to the set using the `add()` method.
   * 
   * Overall, this function returns a set of all `CommunityHouse` objects that are
   * stored in the database or data source, which can be used for further processing
   * or analysis.
   */
  @Override
  public Set<CommunityHouse> listAllHouses() {
    Set<CommunityHouse> communityHouses = new HashSet<>();
    communityHouseRepository.findAll().forEach(communityHouses::add);
    return communityHouses;
  }

  /**
   * returns a set of `CommunityHouse` objects retrieved from the repository using the
   * `pageable` parameter.
   * 
   * @param pageable page number and the number of houses per page, which are used to
   * retrieve a subset of the CommunityHouse objects from the database.
   * 
   * 	- `Pageable`: This is an interface that provides methods for navigating through
   * a page of data. It has three main attributes: `getPageNumber()`, `getPageSize()`,
   * and `getTotalElements()`.
   * 
   * @returns a set of `CommunityHouse` objects.
   * 
   * 	- `Set<CommunityHouse> communityHouses`: The function returns a set of `CommunityHouse`
   * objects.
   * 	- `pageable`: The `pageable` parameter is used to filter and page the results of
   * the query.
   * 	- `new HashSet<>()`: The function creates an empty set to store the results of
   * the query.
   * 	- `communityHouseRepository.findAll(pageable).forEach(communityHouses::add)`: The
   * function calls the `findAll` method on the `communityHouseRepository` and passes
   * in the `pageable` parameter. The method returns a stream of `CommunityHouse`
   * objects, which are then added to the set using the `add` method.
   * 
   * Therefore, the output of the `listAllHouses` function is a set of all `CommunityHouse`
   * objects that match the query criteria.
   */
  @Override
  public Set<CommunityHouse> listAllHouses(Pageable pageable) {
    Set<CommunityHouse> communityHouses = new HashSet<>();
    communityHouseRepository.findAll(pageable).forEach(communityHouses::add);
    return communityHouses;
  }

  /**
   * adds new members to a community house by generating unique member IDs, associating
   * them with the community house, and saving both the members and the community house
   * to the database.
   * 
   * @param houseId ID of the house for which the members are being added, and is used
   * to retrieve the existing members associated with that house from the database.
   * 
   * @param houseMembers Set of HouseMember objects to be added to the community house,
   * which is then transformed and saved in the database.
   * 
   * 	- `houseId`: The unique identifier of the house for which the members are being
   * added.
   * 	- `houseMembers`: A set of HouseMember objects that represent the existing members
   * of the house.
   * 	- `generateUniqueId()`: A method used to generate a unique identifier for each
   * HouseMember object.
   * 	- `setMemberId()` and `setCommunityHouse()`: Methods used to assign a unique
   * identifier and link each HouseMember object to the corresponding community house.
   * 	- `saveAll()` and `save()`: Methods used to save the updated HouseMembers objects
   * in the database.
   * 
   * The function first checks if there is a matching CommunityHouse object with the
   * provided `houseId`. If such an object exists, it creates a new set of HouseMembers
   * by linking each existing member to the corresponding CommunityHouse object and
   * saving them in the database. Otherwise, it returns an empty set.
   * 
   * @returns a set of `HouseMember` objects, each with a unique identifier and linked
   * to the specified community house.
   * 
   * 	- The output is a `Set` of `HouseMember` objects, which represents the newly added
   * members to the specified house.
   * 	- The set contains unique member IDs generated by the function for each member.
   * 	- Each member is associated with the corresponding community house through its
   * `CommunityHouse` object.
   * 	- The `CommunityHouse` object contains a list of all the members added to it,
   * including the newly added ones.
   * 	- The function returns the set of saved members after updating the community house
   * and saving it in the repository.
   */
  @Override public Set<HouseMember> addHouseMembers(String houseId, Set<HouseMember> houseMembers) {
    Optional<CommunityHouse> communityHouseOptional =
        communityHouseRepository.findByHouseIdWithHouseMembers(houseId);
    return communityHouseOptional.map(communityHouse -> {
      Set<HouseMember> savedMembers = new HashSet<>();
      houseMembers.forEach(member -> member.setMemberId(generateUniqueId()));
      houseMembers.forEach(member -> member.setCommunityHouse(communityHouse));
      houseMemberRepository.saveAll(houseMembers).forEach(savedMembers::add);

      communityHouse.getHouseMembers().addAll(savedMembers);
      communityHouseRepository.save(communityHouse);
      return savedMembers;
    }).orElse(new HashSet<>());
  }

  /**
   * deletes a member from a house based on their ID, by updating the house's members
   * set and saving it to the database.
   * 
   * @param houseId ID of the community house to which the member belongs, which is
   * used to locate the relevant community house record in the database and remove the
   * member from its membership list.
   * 
   * @param memberId member ID to be removed from the community house.
   * 
   * @returns a boolean value indicating whether a member was removed from a house.
   * 
   * 	- `isMemberRemoved`: A boolean value indicating whether the member has been
   * successfully removed from the community house or not.
   * 	- `communityHouseOptional`: An optional instance of `CommunityHouse` representing
   * the community house containing the member to be removed. If present, it means that
   * the function found a matching community house and performed the necessary updates.
   * 	- `houseMembers`: A set of `HouseMember` instances representing all members in
   * the community house. The function iterates over this set to find the member to be
   * removed.
   * 	- `memberId`: The ID of the member to be removed from the community house.
   */
  @Override
  public boolean deleteMemberFromHouse(String houseId, String memberId) {
    Optional<CommunityHouse> communityHouseOptional =
        communityHouseRepository.findByHouseIdWithHouseMembers(houseId);
    return communityHouseOptional.map(communityHouse -> {
      boolean isMemberRemoved = false;
      if (!CollectionUtils.isEmpty(communityHouse.getHouseMembers())) {
        Set<HouseMember> houseMembers = communityHouse.getHouseMembers();
        for (HouseMember member : houseMembers) {
          if (member.getMemberId().equals(memberId)) {
            houseMembers.remove(member);
            communityHouse.setHouseMembers(houseMembers);
            communityHouseRepository.save(communityHouse);
            member.setCommunityHouse(null);
            houseMemberRepository.save(member);
            isMemberRemoved = true;
            break;
          }
        }
      }
      return isMemberRemoved;
    }).orElse(false);
  }

  /**
   * retrieves community house details by ID.
   * 
   * @param houseId identifier of a specific community house to retrieve details for.
   * 
   * @returns an optional object of type `CommunityHouse`.
   * 
   * 	- The `Optional<CommunityHouse>` object represents a potentially null reference
   * to a Community House. If no Community House is found with the provided house ID,
   * the output will be `Optional.empty()`.
   * 	- The `CommunityHouse` field contains details about the Community House, such as
   * its ID, name, and address.
   */
  @Override
  public Optional<CommunityHouse> getHouseDetailsById(String houseId) {
    return communityHouseRepository.findByHouseId(houseId);
  }

  /**
   * retrieves a list of `HouseMember` objects associated with a specific `houseId`.
   * It utilizes the `house MemberRepository` to retrieve the list from the database.
   * 
   * @param houseId unique identifier of the house for which the list of members is
   * being retrieved.
   * 
   * @param pageable request for a specific page of results from the HouseMemberRepository,
   * allowing for pagination and control over the result set.
   * 
   * The `Optional` returned by this function is a container for a list of `HouseMember`.
   * The list itself is not nullable, meaning that if no `HouseMember` objects exist
   * for the given `houseId`, the list will be empty.
   * 
   * @returns a paginated list of `HouseMember` objects associated with the specified
   * `houseId`.
   * 
   * 	- `Optional<List<HouseMember>>`: This is an optional list of house members,
   * represented as a non-nullable reference to a list of HouseMember objects. If no
   * house members exist for the given house ID, the list will be empty.
   * 	- `houseId`: The unique identifier of the house for which the house members are
   * being retrieved.
   * 	- `Pageable`: A pageable interface that enables the retrieval of a subset of house
   * members, typically with pagination.
   */
  @Override
  public Optional<List<HouseMember>> getHouseMembersById(String houseId, Pageable pageable) {
    return Optional.ofNullable(
        houseMemberRepository.findAllByCommunityHouse_HouseId(houseId, pageable)
    );
  }

  /**
   * retrieves a list of `HouseMember` objects from the repository based on the user
   * ID and pageable parameters.
   * 
   * @param userId user for whom the list of house members is being retrieved.
   * 
   * @param pageable pagination information for retrieving the list of house members
   * for the user Id.
   * 
   * 	- `userId`: The unique identifier of the user for whom the house members are being
   * listed.
   * 	- `pageable`: A `Pageable` object that represents the pagination parameters for
   * the query, including the page number, page size, and sorting criteria.
   * 
   * @returns a list of `HouseMember` objects associated with the specified user ID.
   * 
   * 	- `Optional<List<HouseMember>>`: This is an optional list of HouseMembers,
   * indicating that the list may be empty if no HouseMembers exist for the specified
   * user ID.
   * 	- `listHouseMembersForHousesOfUserId`: The function takes in two inputs - `userId`
   * and `pageable`. The output is a list of HouseMembers associated with the specified
   * user ID, retrieved from the `houseMemberRepository`.
   * 	- `findAllByCommunityHouse_Community_Admins_UserId`: This method retrieves all
   * HouseMembers associated with the specified user ID from the database. It takes in
   * three inputs - `communityHouse`, `community`, and `userId`.
   */
  @Override
  public Optional<List<HouseMember>> listHouseMembersForHousesOfUserId(String userId,
      Pageable pageable) {
    return Optional.ofNullable(
        houseMemberRepository.findAllByCommunityHouse_Community_Admins_UserId(userId, pageable)
    );
  }
}
