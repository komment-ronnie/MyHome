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
 * provides a set of methods for transforming and enriching Schedule Payment Requests,
 * as well as retrieving Administrator details based on an enriched schedule payment
 * request.
 */
@Mapper
public interface SchedulePaymentApiMapper {

  /**
   * converts a given admin ID into a `UserDto` object representing an administrator
   * with the same ID.
   * 
   * @param adminId user ID of an administrator for which an `AdminDto` object is to
   * be constructed.
   * 
   * @returns a `UserDto` object with the specified `adminId`.
   * 
   * 	- `userId`: A String that represents the admin ID used to build the `UserDto`.
   */
  @Named("adminIdToAdmin")
  static UserDto adminIdToAdminDto(String adminId) {
    return UserDto.builder()
        .userId(adminId)
        .build();
  }

  /**
   * converts a given member ID string into a corresponding `HouseMemberDto` object,
   * with the member ID field already populated.
   * 
   * @param memberId ID of a member to be converted into a `HouseMemberDto` object.
   * 
   * @returns a `HouseMemberDto` object containing the `memberId`.
   * 
   * 	- `memberId`: This is a String attribute that contains the member ID passed in
   * as input.
   * 	- No other attributes or properties have been defined for this object.
   */
  @Named("memberIdToMember")
  static HouseMemberDto memberIdToMemberDto(String memberId) {
    return new HouseMemberDto()
        .memberId(memberId);
  }

  /**
   * transforms a `UserDto` object into a string representing the user ID.
   * 
   * @param userDto user object containing information about the user for which the
   * `adminToAdminId` function is being called.
   * 
   * 	- `getUserId()`: returns the user ID of the `UserDto`.
   * 
   * @returns a string representing the user ID of the admin.
   */
  @Named("adminToAdminId")
  static String adminToAdminId(UserDto userDto) {
    return userDto.getUserId();
  }

  /**
   * returns the `MemberId` field from the `HouseMemberDto` object passed as an argument.
   * 
   * @param houseMemberDto House Member object that contains the member ID to be converted
   * into a string.
   * 
   * 	- `getMemberId()`: Returns the member ID of the House Member object.
   * 
   * @returns a string representing the ` MemberId` of the input `HouseMemberDto`.
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
   * maps the user fields of a payment request to their administrative and house member
   * counterparts using MapStruct and Lombok's `@Builder` annotation.
   * 
   * @param paymentDto PaymentDto object to be modified with user details.
   * 
   * 	- `paymentDto`: The PaymentDto class is annotated with `@MappingTarget`, indicating
   * that it is the target of a mapping operation.
   * 	- `PaymentDtoBuilder`: The `PaymentDtoBuilder` instance is passed as an argument
   * to the `setUserFields` function, which suggests that this class is used for building
   * instances of the `PaymentDto` class.
   * 	- `enrichedSchedulePaymentRequest`: This parameter represents the enriched schedule
   * payment request, which contains additional information beyond what is provided in
   * the original payment request.
   * 	- `getEnrichedRequestMember()` and `getEnrichedRequestAdmin()`: These methods are
   * used to extract specific fields or attributes from the enriched schedule payment
   * request, specifically the member and admin details.
   * 
   * @param enrichedSchedulePaymentRequest enriched payment request with additional
   * user details, which are then mapped to administrative and member fields in the
   * resulting `PaymentDto`.
   * 
   * 	- `getEnrichedRequestMember`: This method extracts the member details from the
   * enriched payment request.
   * 	- `getEnrichedRequestAdmin`: This method extracts the admin details from the
   * enriched payment request.
   * 
   * The `paymentDto` object is updated with the member and admin details using the
   * `member()` and `admin()` methods, respectively.
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
   * enriches a `SchedulePaymentRequest` object by adding additional information such
   * as community IDs, admin and member details, and house membership documents.
   * 
   * @param request Schedule Payment Request to be enriched, providing its type,
   * description, recurring status, charge amount, due date, and other relevant details.
   * 
   * 	- `type`: The type of schedule payment request (e.g., "one-time" or "recurring").
   * 	- `description`: A brief description of the payment request.
   * 	- `isRecurring`: Indicates whether the payment request is recurring.
   * 	- `charge`: The amount of the payment request.
   * 	- `dueDate`: The due date of the payment request.
   * 	- `adminId`: The ID of the admin who created or modified the payment request.
   * 	- `adminName`: The name of the admin who created or modified the payment request.
   * 	- `adminEmail`: The email address of the admin who created or modified the payment
   * request.
   * 	- `encryptedPassword`: The encrypted password of the admin who created or modified
   * the payment request (only included if the admin has an encrypted password).
   * 	- `communityIds`: A set of community IDs associated with the payment request.
   * 	- `memberId`: The ID of the member for whom the payment request is being made.
   * 	- `memberName`: The name of the member for whom the payment request is being made.
   * 	- `memberCommunityHouseId`: The ID of the community house associated with the
   * member (only included if the member has a community house).
   * 
   * @param admin user who is authorizing the payment request, providing their ID, name,
   * email, and encrypted password to enrich the request.
   * 
   * 	- `getCommunities()` returns a stream of community IDs associated with the admin.
   * 	- `map()` transforms the stream into a set of community IDs.
   * 	- `getAdminId()` and `getId()` return the ID of the admin.
   * 	- `getName()` and `getEmail()` return the name and email address of the admin, respectively.
   * 	- `getEncryptedPassword()` returns the encrypted password of the admin.
   * 	- `communityIds` is a set of community IDs associated with the admin.
   * 	- `getHouseMemberDocument()` returns a document filename associated with the member.
   * 	- `getName()`, `getId()`, and `getCommunityHouse()` return information about the
   * member.
   * 
   * @param member HouseMember object that provides additional information about the
   * payment request, including the member's ID, name, community house ID, and document
   * filename.
   * 
   * 	- `member.getMemberId()` - The member's ID.
   * 	- `member.getHouseMemberDocument() != null ? member.getHouseMemberDocument().getDocumentFilename()
   * : ""` - The filename of the member's House Member document, or an empty string if
   * none is present.
   * 
   * @returns an enriched `SchedulePaymentRequest` object containing additional community
   * and member information.
   * 
   * 	- `type`: The type of schedule payment request, which could be either "one-time"
   * or "recurring".
   * 	- `description`: A brief description of the schedule payment request.
   * 	- `isRecurring`: A boolean indicating whether the schedule payment request is
   * recurring or not.
   * 	- `charge`: The charge amount for the schedule payment request.
   * 	- `dueDate`: The due date of the schedule payment request.
   * 	- `adminId`: The ID of the admin who created the schedule payment request.
   * 	- `adminName`: The name of the admin who created the schedule payment request.
   * 	- `adminEmail`: The email address of the admin who created the schedule payment
   * request.
   * 	- `encryptedPassword`: The encrypted password of the admin who created the schedule
   * payment request.
   * 	- `communityIds`: A set of community IDs associated with the schedule payment request.
   * 	- `memberId`: The ID of the member whose schedule payment request is being enriched.
   * 	- `houseMemberDocumentFilename`: The filename of the House Member document
   * associated with the member, or an empty string if no document exists.
   * 	- `memberName`: The name of the member whose schedule payment request is being enriched.
   * 	- `communityHouseId`: The ID of the community house associated with the member,
   * or an empty string if no community house exists.
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
   * creates a `UserDto` object representing an admin associated with a schedule payment
   * request, using the provided `EnrichedSchedulePaymentRequest`.
   * 
   * @param enrichedSchedulePaymentRequest administrative user for whom the request is
   * being enriched, providing their user ID, entity ID, name, email, and encrypted password.
   * 
   * 	- `adminId`: The ID of the admin user associated with the schedule payment request.
   * 	- `adminEntityId`: The entity ID of the admin user associated with the schedule
   * payment request.
   * 	- `adminName`: The name of the admin user associated with the schedule payment request.
   * 	- `adminEmail`: The email address of the admin user associated with the schedule
   * payment request.
   * 	- `adminEncryptedPassword`: The encrypted password of the admin user associated
   * with the schedule payment request.
   * 
   * @returns a `UserDto` object with enriched administrative details.
   * 
   * 	- `userId`: The ID of the admin user associated with the enriched schedule payment
   * request.
   * 	- `id`: The entity ID of the admin in the system.
   * 	- `name`: The name of the admin user.
   * 	- `email`: The email address of the admin user.
   * 	- `encryptedPassword`: The encrypted password of the admin user.
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
   * object, including member ID, name, and entity ID.
   * 
   * @param enrichedSchedulePaymentRequest House Member entity details, which are used
   * to populate the `HouseMemberDto` output object with the member's ID, name, and
   * member ID.
   * 
   * 	- `getMemberEntityId`: an integer representing the member entity ID.
   * 	- `getMemberId`: a string representing the member ID.
   * 	- `getHouseMemberName`: a string representing the house member name.
   * 
   * @returns a `HouseMemberDto` object containing the member's ID, name, and membership
   * ID.
   * 
   * 	- `id`: A string representing the ID of the house member entity.
   * 	- `memberId`: An integer representing the member ID of the house member.
   * 	- `name`: A string representing the name of the house member.
   */
  default HouseMemberDto getEnrichedRequestMember(EnrichedSchedulePaymentRequest enrichedSchedulePaymentRequest) {
    return new HouseMemberDto()
        .id(enrichedSchedulePaymentRequest.getMemberEntityId())
        .memberId(enrichedSchedulePaymentRequest.getMemberId())
        .name(enrichedSchedulePaymentRequest.getHouseMemberName());
  }
}
