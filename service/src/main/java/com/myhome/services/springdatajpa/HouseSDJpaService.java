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
 * provides methods for retrieving lists of `HouseMember` instances associated with
 * a specified `houseId`, as well as paginating the list based on the user ID and
 * pageable parameters. The service uses the `houseMemberRepository` to find the
 * members and returns an optional list of results.
 */
@RequiredArgsConstructor
@Service
public class HouseSDJpaService implements HouseService {
  private final HouseMemberRepository houseMemberRepository;
  private final HouseMemberDocumentRepository houseMemberDocumentRepository;
  private final CommunityHouseRepository communityHouseRepository;

  /**
   * generates a unique identifier using the `UUID` class and returns it as a string.
   * 
   * @returns a unique string representation of a Universally Unique Identifier (UUID)
   * generated using the `UUID.randomUUID()` method.
   */
  private String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  /**
   * retrieves a set of `CommunityHouse` objects from the database using the `findAll()`
   * method of the `communityHouseRepository`. It then adds each retrieved object to a
   * new `Set` instance. The function returns the set of `CommunityHouse` objects.
   * 
   * @returns a set of `CommunityHouse` objects representing all houses in the database.
   * 
   * The Set<CommunityHouse> represents a collection of CommunityHouse objects that
   * represent all the houses in the database. Each element in the set is a CommunityHouse
   * object containing information about a particular house.
   * 
   * The function first creates an empty set using new HashSet<CommunityHouse> and then
   * iterates over the findAll() method of the communityHouseRepository, which returns
   * a list of CommunityHouse objects. For each element in the list, the function adds
   * the corresponding CommunityHouse object to the set using the add() method.
   * 
   * Overall, the returned Set<CommunityHouse> represents all the houses stored in the
   * database, providing a convenient and efficient way to access and manipulate this
   * information.
   */
  @Override
  public Set<CommunityHouse> listAllHouses() {
    Set<CommunityHouse> communityHouses = new HashSet<>();
    communityHouseRepository.findAll().forEach(communityHouses::add);
    return communityHouses;
  }

  /**
   * returns a set of all `CommunityHouse` objects in the repository by calling `findAll`
   * and iterating over the result using `forEach`.
   * 
   * @param pageable page number and page size of the houses to be retrieved from the
   * database, allowing for efficient pagination of the list of community houses.
   * 
   * 	- `Pageable`: This interface defines pagination-related methods for handling large
   * data sets. It provides methods to navigate through a collection of objects.
   * 	- `getNumberOfPages()`: Returns the number of pages that can be retrieved from
   * the data set.
   * 	- `getPageSize()`: Returns the size of each page in the data set.
   * 
   * The function then uses the `findAll` method of the `communityHouseRepository` to
   * retrieve a list of community houses and adds them to the `Set<CommunityHouse>`
   * object `communityHouses`.
   * 
   * @returns a set of `CommunityHouse` objects.
   * 
   * 	- The output is a `Set` of `CommunityHouse` objects. This indicates that the
   * function returns a collection of houses, where each house is represented by an
   * individual object in the set.
   * 	- The set contains the results of the query performed on the `communityHouseRepository`.
   * This suggests that the function queries the repository for all houses and stores
   * them in the set for later use.
   * 	- The `pageable` parameter is used to control the pagination of the houses returned
   * by the function. This allows for efficient retrieval of a subset of houses based
   * on criteria such as page number, size, or sort order.
   */
  @Override
  public Set<CommunityHouse> listAllHouses(Pageable pageable) {
    Set<CommunityHouse> communityHouses = new HashSet<>();
    communityHouseRepository.findAll(pageable).forEach(communityHouses::add);
    return communityHouses;
  }

  /**
   * adds new members to a community house by creating unique member IDs, associating
   * them with the community house, and saving them in the database.
   * 
   * @param houseId unique identifier of a house for which the method is called, and
   * it is used to retrieve the existing house members from the database or to save the
   * new ones after they have been processed.
   * 
   * @param houseMembers set of HouseMember objects to be added to the CommunityHouse,
   * which are processed and saved in the function.
   * 
   * 	- `houseId`: The unique identifier of the house to which the members will be added.
   * 	- `houseMembers`: A set of `HouseMember` objects representing the new members to
   * be added to the house. Each member has a `memberId` and a `CommunityHouse` field
   * that refers to the community house where they reside.
   * 
   * The function first checks if there is already a community house with the specified
   * `houseId`. If such a house exists, it is deserialized using the
   * `communityHouseRepository#findByHouseIdWithHouseMembers` method and stored in the
   * `Optional` variable `communityHouseOptional`. If no community house exists for the
   * given `houseId`, an empty set is returned.
   * 
   * The `Optional` variable `communityHouseOptional` is then mapped using the `map`
   * method to a new `Set<HouseMember>` object that contains the newly created members.
   * Each member has a unique `memberId` generated using the `generateUniqueId()` method,
   * and their `CommunityHouse` field is set to the deserialized community house. The
   * `houseMembers` set is then saved using the `houseMemberRepository#saveAll()` method,
   * and the newly created members are added to the community house's `HouseMembers`
   * list. Finally, the community house is saved using the `communityHouseRepository#save()`
   * method.
   * 
   * @returns a set of `HouseMember` objects, each with a unique ID and a reference to
   * the corresponding `CommunityHouse`.
   * 
   * 	- The output is a `Set` containing the new house members that were added to the
   * community house.
   * 	- The set contains unique `HouseMember` objects, each with a generated `memberId`.
   * 	- Each `HouseMember` object has a `CommunityHouse` reference, indicating its
   * association with the community house.
   * 	- The set also contains any existing `HouseMember` objects that were previously
   * saved in the database but not removed.
   * 	- The `communityHouse` reference is not null, indicating that the function
   * successfully added the members to the community house.
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
   * deletes a member from a house by iterating through the house's members and removing
   * the specified member if found.
   * 
   * @param houseId unique identifier of the community house that the member belongs
   * to, which is used to retrieve the relevant community house object from the repository
   * and modify its membership list.
   * 
   * @param memberId ID of the member to be removed from the community house.
   * 
   * @returns a boolean value indicating whether the specified member was successfully
   * removed from the house.
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
   * retrieves the details of a specific `CommunityHouse` using its unique ID.
   * 
   * @param houseId identifier of the Community House to retrieve details for.
   * 
   * @returns an optional instance of `CommunityHouse`.
   * 
   * 	- `Optional<CommunityHouse>`: The type of the output, which is an optional instance
   * of the `CommunityHouse` class. This means that the function may or may not return
   * a non-null reference to a `CommunityHouse` object, depending on whether a matching
   * record exists in the database.
   * 	- `communityHouseRepository.findByHouseId(houseId)`: The method call used to
   * retrieve the desired record from the database. This method takes a single parameter,
   * `houseId`, which is the ID of the house for which details are being sought. The
   * method returns an instance of `CommunityHouse` if a matching record exists in the
   * database, otherwise it returns `Optional.empty()`.
   */
  @Override
  public Optional<CommunityHouse> getHouseDetailsById(String houseId) {
    return communityHouseRepository.findByHouseId(houseId);
  }

  /**
   * retrieves a paginated list of `HouseMember` objects associated with a specific `houseId`.
   * 
   * @param houseId identifier of the community house for which the list of members is
   * being retrieved.
   * 
   * @param pageable page parameters for retrieving a subset of the `HouseMember` data
   * from the database, allowing for efficient and flexible paging of the results.
   * 
   * 	- The `Pageable` interface represents an object that can be used to page or filter
   * a collection of objects.
   * 	- The `getNumberOfElements` method returns the total number of elements in the collection.
   * 	- The `getPosition` method returns the position of the element within the collection.
   * 	- The `isLast` method returns a boolean indicating whether the element is the
   * last one in the collection.
   * 	- The `isFirst` method returns a boolean indicating whether the element is the
   * first one in the collection.
   * 
   * @returns a list of `HouseMember` objects for the specified house ID.
   * 
   * 	- `Optional<List<HouseMember>>`: This indicates that the function may return an
   * empty list or no list at all, depending on whether any house members exist for the
   * given `houseId`.
   * 	- `getHouseMembersById(String houseId, Pageable pageable)`: This is the input
   * parameter passed to the function. It represents a unique identifier for a community
   * house and a pagination object used to limit the number of results returned.
   * 	- `<List<HouseMember>>`: This type represents a list of `HouseMember` objects.
   * The list may contain multiple elements, each representing a house member associated
   * with the given `houseId`.
   */
  @Override
  public Optional<List<HouseMember>> getHouseMembersById(String houseId, Pageable pageable) {
    return Optional.ofNullable(
        houseMemberRepository.findAllByCommunityHouse_HouseId(houseId, pageable)
    );
  }

  /**
   * retrieves a paginated list of HouseMembers associated with a user's communities
   * from the database.
   * 
   * @param userId user whose house members are to be listed.
   * 
   * @param pageable pagination criteria for the returned list of HouseMembers, allowing
   * for efficient retrieval of a subset of the HouseMembers based on user-defined criteria.
   * 
   * 	- `userId`: A string representing the user ID for which house members are to be
   * retrieved.
   * 	- `pageable`: An instance of `Pageable`, which provides a way to page or filter
   * results from a large dataset. The `pageable` object has several properties, including
   * `getNumberOfElements()` (the number of elements in the page), `getNumberOfPages()`
   * (the total number of pages in the dataset), and `getPage()`: (the current page
   * being retrieved).
   * 
   * @returns a list of `HouseMember` objects for the specified user ID, retrieved from
   * the database.
   * 
   * 	- `Optional<List<HouseMember>>`: The function returns an optional list of house
   * members for the specified user ID, which means that if no house members are found,
   * the list will be empty and the function will return an optional value.
   * 	- `listHouseMembersForHousesOfUserId(String userId, Pageable pageable)`: This
   * function takes two parameters - `userId` (a string representing the user ID) and
   * `pageable` (a pageable object for fetching a subset of the house members).
   * 	- `houseMemberRepository.findAllByCommunityHouse_Community_Admins_UserId(userId,
   * pageable)`: This is the method called by the `listHouseMembersForHousesOfUserId`
   * function to retrieve the list of house members for the specified user ID using a
   * query that filters based on the community house and admin roles.
   */
  @Override
  public Optional<List<HouseMember>> listHouseMembersForHousesOfUserId(String userId,
      Pageable pageable) {
    return Optional.ofNullable(
        houseMemberRepository.findAllByCommunityHouse_Community_Admins_UserId(userId, pageable)
    );
  }
}
