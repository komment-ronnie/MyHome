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

package com.myhome.controllers.mapper;

import com.myhome.controllers.dto.PaymentDto;
import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.request.EnrichedSchedulePaymentRequest;
import com.myhome.domain.Community;
import com.myhome.domain.HouseMember;
import com.myhome.domain.Payment;
import com.myhome.domain.User;
import com.myhome.model.AdminPayment;
import com.myhome.model.HouseMemberDto;
import com.myhome.model.MemberPayment;
import com.myhome.model.SchedulePaymentRequest;
import com.myhome.model.SchedulePaymentResponse;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

/**
 * provides methods to transform and map between different entities and DTOs in a
 * payment schedule management system, including schedules, payments, members, and admins.
 */
@Mapper
public interface SchedulePaymentApiMapper {

  /**
   * converts a given `adminId` string to a `UserDto` object representing an admin user
   * with the provided ID.
   * 
   * @param adminId unique identifier of an administrator, which is used to build a
   * `UserDto` object containing information about the administrator.
   * 
   * @returns a `UserDto` object representing an admin user with the specified `adminId`.
   * 
   * 1/ `userId`: This attribute holds the value of the `adminId` parameter passed to
   * the function as an argument. It is used to construct a `UserDto` object with a
   * unique user ID.
   * 2/ `build()`: This method is called on the `UserDto.builder()` object to create a
   * new instance of the `UserDto` class with the specified properties. The resulting
   * object contains all the necessary attributes for an admin entity, such as the user
   * ID and other relevant information.
   */
  @Named("adminIdToAdmin")
  static UserDto adminIdToAdminDto(String adminId) {
    return UserDto.builder()
        .userId(adminId)
        .build();
  }

  /**
   * maps a `memberId` string to an instance of `HouseMemberDto`, which contains the
   * `memberId` field populated with the input value.
   * 
   * @param memberId unique identifier of a member in the `HouseMemberDto` object
   * constructed by the function.
   * 
   * @returns a `HouseMemberDto` object containing the input `memberId`.
   * 
   * 	- memberId: A string attribute representing the Member ID.
   */
  @Named("memberIdToMember")
  static HouseMemberDto memberIdToMemberDto(String memberId) {
    return new HouseMemberDto()
        .memberId(memberId);
  }

  /**
   * maps a `UserDto` object to its corresponding `UserId`.
   * 
   * @param userDto User object containing information about a user, which is used to
   * retrieve the user's ID.
   * 
   * 	- `getUserId()` returns the user ID of the admin.
   * 
   * @returns a string representing the user ID of the specified `UserDto` object.
   */
  @Named("adminToAdminId")
  static String adminToAdminId(UserDto userDto) {
    return userDto.getUserId();
  }

  /**
   * maps a `HouseMemberDto` object to its corresponding `MemberId`.
   * 
   * @param houseMemberDto House Member object containing information about a member
   * of a house, which is passed to the `memberToMemberId()` function to retrieve the
   * member's ID.
   * 
   * 	- `getMemberId()`: returns the member ID of the House Member entity.
   * 
   * @returns a string representing the member ID of the input `HouseMemberDto` object.
   */
  @Named("memberToMemberId")
  static String memberToMemberId(HouseMemberDto houseMemberDto) {
    return houseMemberDto.getMemberId();
  }

  @Mappings({
      @Mapping(source = "adminId", target = "admin", qualifiedByName = "adminIdToAdmin"),
      @Mapping(source = "memberId", target = "member", qualifiedByName = "memberIdToMember")
  })
  PaymentDto schedulePaymentRequestToPaymentDto(SchedulePaymentRequest schedulePaymentRequest);

  PaymentDto enrichedSchedulePaymentRequestToPaymentDto(
      EnrichedSchedulePaymentRequest enrichedSchedulePaymentRequest);

  /**
   * maps the user details of a payment request to an admin and another member, using
   * the `getEnrichedRequestMember` and `getEnrichedRequestAdmin` methods.
   * 
   * @param paymentDto PaymentDto object, which is being built using the `@MappingTarget`
   * annotation and is used to map the user details of the payment request to administrator
   * and member.
   * 
   * 	- `PaymentDto.PaymentDtoBuilder`: This is an instance of a class annotated with
   * `@Builder`, which provides a way to build instances of the `PaymentDto` class using
   * a fluent interface.
   * 	- `EnrichedSchedulePaymentRequest`: This is the input parameter for the function,
   * which contains enriched user details of the payment request. It has various
   * attributes, including `member` and `admin`, which are used to populate the
   * corresponding fields in the `paymentDto`.
   * 
   * @param enrichedSchedulePaymentRequest payment request with user details enriched
   * and mapped to admin and house member fields in the `PaymentDto` object.
   * 
   * 	- `PaymentDto.PaymentDtoBuilder`: This is an instance of a builder class for the
   * `PaymentDto` type, which is used to create instances of the `PaymentDto` class.
   * 	- `EnrichedSchedulePaymentRequest`: This is the input object that contains various
   * properties and attributes related to payment schedules. These properties may include
   * information about the member or administrator associated with the payment request.
   */
  @AfterMapping
  default void setUserFields(@MappingTarget PaymentDto.PaymentDtoBuilder paymentDto, EnrichedSchedulePaymentRequest enrichedSchedulePaymentRequest) {
    // MapStruct and Lombok requires you to pass in the Builder instance of the class if that class is annotated with @Builder, or else the AfterMapping method is not used.
    // required to use AfterMapping to convert the user details of the payment request to admin, and same with house member
    paymentDto.member(getEnrichedRequestMember(enrichedSchedulePaymentRequest));
    paymentDto.admin(getEnrichedRequestAdmin(enrichedSchedulePaymentRequest));
  }

  Set<MemberPayment> memberPaymentSetToRestApiResponseMemberPaymentSet(
      Set<Payment> memberPaymentSet);

  @Mapping(target = "memberId", expression = "java(payment.getMember().getMemberId())")
  MemberPayment paymentToMemberPayment(Payment payment);

  Set<AdminPayment> adminPaymentSetToRestApiResponseAdminPaymentSet(
      Set<Payment> memberPaymentSet);

  @Mapping(target = "adminId", expression = "java(payment.getAdmin().getUserId())")
  AdminPayment paymentToAdminPayment(Payment payment);

  @Mappings({
      @Mapping(source = "admin", target = "adminId", qualifiedByName = "adminToAdminId"),
      @Mapping(source = "member", target = "memberId", qualifiedByName = "memberToMemberId")
  })
  SchedulePaymentResponse paymentToSchedulePaymentResponse(PaymentDto payment);

  /**
   * enriches a `SchedulePaymentRequest` object with additional information from the
   * user and community, including their IDs, names, emails, and encrypted passwords,
   * as well as the community ID.
   * 
   * @param request Schedule Payment Request to be enriched, providing its type,
   * description, recurring status, charge amount, due date, and other relevant details.
   * 
   * 	- `type`: The type of payment request (e.g., "Rent", "Utilities", etc.)
   * 	- `description`: A brief description of the payment request
   * 	- `isRecurring`: Whether the payment request is recurring or not
   * 	- `charge`: The amount of the payment request
   * 	- `dueDate`: The due date of the payment request
   * 	- `adminId`: The ID of the admin who created/modified the payment request
   * 	- `adminName`: The name of the admin who created/modified the payment request
   * 	- `adminEmail`: The email address of the admin who created/modified the payment
   * request
   * 	- `encryptedPassword`: The encrypted password of the admin who created/modified
   * the payment request
   * 	- `communityIds`: A set of community IDs associated with the payment request
   * 	- `memberId`: The ID of the member for whom the payment request is made
   * 	- `houseMemberDocument`: An optional document filename containing additional
   * information about the member's household (if any)
   * 	- `name`: The name of the member
   * 	- `communityHouse`: An optional reference to the member's community house (if any)
   * 
   * @param admin user who is performing the action of creating the schedule payment
   * request, and provides information about their role and identity.
   * 
   * 	- `getCommunities()`: Returns a stream of `Community` objects representing the
   * communities that the admin is a part of.
   * 	- `map()`: Maps each `Community` object to its community ID using the `map()` method.
   * 	- `collect()`: Collects the mapped community IDs into a set using the `collect()`
   * method.
   * 	- `getId()`, `getName()`, `getEmail()`, and `getEncryptedPassword()`: Returns the
   * admin's ID, name, email, and encrypted password, respectively.
   * 	- `getAdminId()` and `admin.getId()`: Returns the admin's ID.
   * 	- `getDueDate()` and `request.getDueDate()`: Returns the due date of the payment
   * request.
   * 	- `getCharge()` and `request.getCharge()`: Returns the charge amount for the
   * payment request.
   * 	- `getType()` and `request.getType()`: Returns the type of the payment request
   * (e.g., "monthly", "one-time").
   * 	- `isRecurring()` and `request.isRecurring()`: Returns a boolean indicating whether
   * the payment request is recurring or not.
   * 	- `getHouseMemberDocument()` and `member.getHouseMemberDocument()`: Returns the
   * House Member document of the member, if it exists.
   * 
   * @param member HouseMember object that provides additional information about the
   * member whose schedule payment request is being enriched, including their member
   * ID, house ID, and community ID.
   * 
   * 	- `getMemberId()`: Returns the ID of the member.
   * 	- `getId()`: Returns the ID of the member.
   * 	- `getHouseMemberDocument()`: Returns the House Member Document of the member,
   * or null if not available.
   * 	- `getName()`: Returns the name of the member.
   * 	- `getCommunityHouse()`: Returns the Community House of the member, or null if
   * not available.
   * 
   * These properties are used in the construction of the enriched `SchedulePaymentRequest`
   * object.
   * 
   * @returns an enriched `SchedulePaymentRequest` object containing additional fields.
   * 
   * 	- `type`: The type of payment request (e.g. "rent", "utility")
   * 	- `description`: A brief description of the payment request
   * 	- `recurring`: Whether the payment request is recurring or not
   * 	- `charge`: The amount of the payment request
   * 	- `dueDate`: The due date of the payment request
   * 	- `adminId`: The ID of the admin who created the payment request
   * 	- `adminName`: The name of the admin who created the payment request
   * 	- `adminEmail`: The email address of the admin who created the payment request
   * 	- `encryptedPassword`: The encrypted password of the admin who created the payment
   * request
   * 	- `communityIds`: A set of community IDs associated with the payment request
   * 	- `memberId`: The ID of the member for whom the payment request was made
   * 	- `houseMemberDocumentFilename`: The filename of the House Member document
   * associated with the payment request (if available)
   * 	- `memberName`: The name of the member for whom the payment request was made
   * 	- `communityHouseId`: The ID of the community house associated with the payment
   * request (if available)
   */
  default EnrichedSchedulePaymentRequest enrichSchedulePaymentRequest(
      SchedulePaymentRequest request, User admin, HouseMember member) {
    Set<String> communityIds = admin.getCommunities()
        .stream()
        .map(Community::getCommunityId)
        .collect(Collectors.toSet());
    return new EnrichedSchedulePaymentRequest(request.getType(),
        request.getDescription(),
        request.isRecurring(),
        request.getCharge(),
        request.getDueDate(),
        request.getAdminId(),
        admin.getId(),
        admin.getName(),
        admin.getEmail(),
        admin.getEncryptedPassword(),
        communityIds,
        member.getMemberId(),
        member.getId(),
        member.getHouseMemberDocument() != null ? member.getHouseMemberDocument()
            .getDocumentFilename() : "",
        member.getName(),
        member.getCommunityHouse() != null ? member.getCommunityHouse().getHouseId() : "");
  }

  /**
   * creates a `UserDto` object representing the administrator associated with an
   * enriched schedule payment request. It populates the user details using the provided
   * enriched schedule payment request.
   * 
   * @param enrichedSchedulePaymentRequest administrative entity to which the request
   * belongs, providing the admin ID, name, email, and encrypted password for further
   * processing.
   * 
   * 	- `userId`: The user ID of the administrator who requested the enriched payment
   * schedule.
   * 	- `id`: The ID of the administrator's entity in the system.
   * 	- `name`: The name of the administrator.
   * 	- `email`: The email address of the administrator.
   * 	- `encryptedPassword`: The encrypted password for the administrator.
   * 
   * @returns a `UserDto` object containing the administrator's details.
   * 
   * 	- `userId`: The ID of the admin user associated with the enriched schedule payment
   * request.
   * 	- `id`: The ID of the enriched schedule payment request.
   * 	- `name`: The name of the admin user associated with the enriched schedule payment
   * request.
   * 	- `email`: The email address of the admin user associated with the enriched
   * schedule payment request.
   * 	- `encryptedPassword`: The encrypted password of the admin user associated with
   * the enriched schedule payment request.
   */
  default UserDto getEnrichedRequestAdmin(EnrichedSchedulePaymentRequest enrichedSchedulePaymentRequest) {
    return UserDto.builder()
        .userId(enrichedSchedulePaymentRequest.getAdminId())
        .id(enrichedSchedulePaymentRequest.getAdminEntityId())
        .name(enrichedSchedulePaymentRequest.getAdminName())
        .email(enrichedSchedulePaymentRequest.getAdminEmail())
        .encryptedPassword(enrichedSchedulePaymentRequest.getAdminEncryptedPassword())
        .build();
  }

  /**
   * transforms an `EnrichedSchedulePaymentRequest` object into a `HouseMemberDto`
   * object, including the member's ID, name, and entity ID.
   * 
   * @param enrichedSchedulePaymentRequest House Member's payment request, which provides
   * additional information about the member and their payment, such as their entity
   * ID and member ID, as well as their name.
   * 
   * 	- `getMemberEntityId`: an integer representing the entity ID of the house member
   * associated with the payment request.
   * 	- `getMemberId`: a string representing the ID of the member in the system.
   * 	- `getHouseMemberName`: a string representing the name of the house member
   * associated with the payment request.
   * 
   * @returns a `HouseMemberDto` object containing the member's ID, name, and member
   * ID from the input `EnrichedSchedulePaymentRequest`.
   * 
   * 	- `id`: This attribute represents the unique identifier for the member entity
   * associated with the enriched schedule payment request.
   * 	- `memberId`: This attribute contains the ID of the member associated with the
   * enriched schedule payment request.
   * 	- `name`: This attribute holds the name of the house member associated with the
   * enriched schedule payment request.
   */
  default HouseMemberDto getEnrichedRequestMember(EnrichedSchedulePaymentRequest enrichedSchedulePaymentRequest) {
    return new HouseMemberDto()
        .id(enrichedSchedulePaymentRequest.getMemberEntityId())
        .memberId(enrichedSchedulePaymentRequest.getMemberId())
        .name(enrichedSchedulePaymentRequest.getHouseMemberName());
  }
}
