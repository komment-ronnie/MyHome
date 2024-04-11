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
 * provides a set of mappings between different entities and DTOs used in a payment
 * API, including mapping between Payment, User, HouseMember, and Community entities
 * to corresponding DTOs, as well as providing additional methods for enriching the
 * schedule payment request with user and house member details.
 */
@Mapper
public interface SchedulePaymentApiMapper {

  /**
   * converts a `String` representing an administrator ID into a `UserDto` object
   * containing the ID and other relevant information.
   * 
   * @param adminId user ID of an admin to be converted into a `UserDto` object.
   * 
   * @returns a `UserDto` object representing the admin with the specified `adminId`.
   * 
   * 1/ `userId`: A string representing the user ID of the admin.
   * 2/ `build()`: Creates a new instance of `UserDto` with the specified `userId`.
   */
  @Named("adminIdToAdmin")
  static UserDto adminIdToAdminDto(String adminId) {
    return UserDto.builder()
        .userId(adminId)
        .build();
  }

  /**
   * converts a `memberId` string into a `HouseMemberDto` object, which contains the
   * original member ID as its sole property.
   * 
   * @param memberId 12-digit unique identifier of a member in the House, which is used
   * to retrieve the corresponding member details in the `HouseMemberDto` object created
   * by the function.
   * 
   * @returns a `HouseMemberDto` object containing the input `memberId`.
   * 
   * 	- `memberId`: This is a String attribute that represents the member ID passed as
   * an input to the function.
   * 	- Other attributes not mentioned in the function signature or documentation are
   * not included in the output.
   */
  @Named("memberIdToMember")
  static HouseMemberDto memberIdToMemberDto(String memberId) {
    return new HouseMemberDto()
        .memberId(memberId);
  }

  /**
   * maps a `UserDto` object to its corresponding `userId`.
   * 
   * @param userDto User object that contains information about an administrator, and
   * it is used to extract the user's ID from the object.
   * 
   * 	- `getUserId()` returns the user ID of the admin.
   * 
   * @returns a `String` representing the user ID of the admin.
   */
  @Named("adminToAdminId")
  static String adminToAdminId(UserDto userDto) {
    return userDto.getUserId();
  }

  /**
   * converts a `HouseMemberDto` object into its corresponding member ID.
   * 
   * @param houseMemberDto House Member object containing information about a member
   * of a household, which is used to retrieve the member's ID in the `memberToMemberId`
   * function.
   * 
   * 	- `getMemberId()`: Returns the `MemberId` field of `houseMemberDto`.
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
   * maps the user details from the enriched schedule payment request to the admin and
   * member fields of the PaymentDto object using the `@MappingTarget` annotation.
   * 
   * @param paymentDto PaymentDto object, which is being modified to include user details
   * from the enriched schedule payment request.
   * 
   * 	- `PaymentDto.PaymentDtoBuilder`: This is an instance of a class annotated with
   * `@Builder`, which provides a way to construct instances of the `PaymentDto` class.
   * 	- `EnrichedSchedulePaymentRequest`: This is the input parameter to the function,
   * which contains the user details of the payment request.
   * 	- `getEnrichedRequestMember()` and `getEnrichedRequestAdmin()`: These are methods
   * that extract the member and admin details from the input `EnrichedSchedulePaymentRequest`
   * object, respectively.
   * 
   * Therefore, the `setUserFields` function takes a `PaymentDto.PaymentDtoBuilder`
   * instance and an `EnrichedSchedulePaymentRequest` object as inputs, and updates the
   * `member` and `admin` properties of the deserialized `paymentDto` instance using
   * the extracted member and admin details.
   * 
   * @param enrichedSchedulePaymentRequest payment request with user details enriched
   * for further processing and mapping, providing the necessary data for the
   * `setUserFields()` method to operate effectively.
   * 
   * 	- `PaymentDto.PaymentDtoBuilder`: This is an instance of a builder class for the
   * `PaymentDto` type, which is annotated with `@Builder`. The builder is required to
   * pass in the instance of the class when using the `AfterMapping` method.
   * 	- `EnrichedSchedulePaymentRequest`: This is the deserialized input object containing
   * information about a payment request, including member and admin details.
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
   * takes a `SchedulePaymentRequest` object and enhances it with additional information
   * from the user and community, such as the admin's name, email, encrypted password,
   * and community IDs, as well as the member's ID and house document filename.
   * 
   * @param request SchedulePaymentRequest object that contains information about the
   * payment request to be enriched, including its type, description, recurrence status,
   * charge amount, due date, and administrator and member details.
   * 
   * 	- `getType()`: The type of schedule payment request (e.g., "Monthly")
   * 	- `getDescription()`: A brief description of the payment request
   * 	- `isRecurring()`: Whether the payment request is recurring or not
   * 	- `getCharge()`: The charge amount for the payment request
   * 	- `getDueDate()`: The due date of the payment request
   * 	- `getAdminId()`: The ID of the admin who created the payment request
   * 	- `admin.getId()`: The ID of the admin who is associated with the payment request
   * 	- `admin.getName()`: The name of the admin who is associated with the payment request
   * 	- `admin.getEmail()`: The email address of the admin who is associated with the
   * payment request
   * 	- `admin.getEncryptedPassword()`: The encrypted password of the admin who is
   * associated with the payment request
   * 	- `communityIds`: A set of community IDs that the payment request is related to
   * 	- `member.getMemberId()`: The ID of the member who made the payment request
   * 	- `member.getId()`: The ID of the member who made the payment request
   * 	- `member.getHouseMemberDocument() != null ? member.getHouseMemberDocument().getDocumentFilename()
   * : ""`: The filename of the House Member document associated with the member (if
   * it exists)
   * 	- `member.getName()`: The name of the member who made the payment request
   * 	- `member.getCommunityHouse() != null ? member.getCommunityHouse().getHouseId()
   * : """`: The ID of the community house associated with the member (if it exists)
   * 
   * @param admin User object containing information about the admin user who made the
   * request, and provides the admin's ID, name, email, encrypted password, and communities.
   * 
   * 	- `getCommunities()`: Returns a stream of `Community` objects representing the
   * communities that the admin is a member of.
   * 	- `map()`: Maps each `Community` object to its community ID using the `map()` method.
   * 	- `collect()`: Collects the mapped community IDs into a set using the `collect()`
   * method.
   * 	- `getAdminId()`: Returns the admin's ID.
   * 	- `getId()`: Returns the admin's ID.
   * 	- `getName()`: Returns the admin's name.
   * 	- `getEmail()`: Returns the admin's email address.
   * 	- `getEncryptedPassword()`: Returns the admin's encrypted password.
   * 	- `communityIds`: Returns a set of community IDs associated with the admin.
   * 
   * @param member HouseMember object that provides additional information about the
   * member for whom the payment request is being enriched, including their community
   * ID and document filename.
   * 
   * 	- `member.getMemberId()` - The unique identifier for the member in the system.
   * 	- `member.getId()` - The ID of the member in the database.
   * 	- `member.getHouseMemberDocument()` - If not null, it contains information about
   * the member's house membership, including the document filename.
   * 	- `member.getName()` - The member's name.
   * 	- `member.getCommunityHouse()` - If not null, it references the community house
   * associated with the member.
   * 
   * @returns an enriched `SchedulePaymentRequest` object with additional fields.
   * 
   * 1/ `type`: The type of payment request, which could be "one-time" or "recurring".
   * 2/ `description`: A brief description of the payment request.
   * 3/ `isRecurring`: Indicates whether the payment request is recurring or not.
   * 4/ `charge`: The amount to be charged for the payment request.
   * 5/ `dueDate`: The date by which the payment must be made.
   * 6/ `adminId`: The ID of the admin who created the payment request.
   * 7/ `adminName`: The name of the admin who created the payment request.
   * 8/ `adminEmail`: The email address of the admin who created the payment request.
   * 9/ `encryptedPassword`: The encrypted password of the admin who created the payment
   * request.
   * 10/ `communityIds`: A set of community IDs associated with the payment request.
   * 11/ `memberId`: The ID of the member to whom the payment request is addressed.
   * 12/ `houseMemberDocumentFilename`: The filename of the House Member document, if
   * available.
   * 13/ `memberName`: The name of the member to whom the payment request is addressed.
   * 14/ `communityHouseId`: The ID of the community house associated with the member
   * to whom the payment request is addressed.
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
   * builds a `UserDto` object representing an administrator for a payment request,
   * using the provided `EnrichedSchedulePaymentRequest` object as input.
   * 
   * @param enrichedSchedulePaymentRequest administrative user for whom the request is
   * being enriched, providing their user ID, entity ID, name, email, and encrypted password.
   * 
   * 	- `adminId`: The ID of the administrator associated with the schedule payment request.
   * 	- `adminEntityId`: The entity ID of the administrator associated with the schedule
   * payment request.
   * 	- `adminName`: The name of the administrator associated with the schedule payment
   * request.
   * 	- `adminEmail`: The email address of the administrator associated with the schedule
   * payment request.
   * 	- `adminEncryptedPassword`: An encrypted password for the administrator associated
   * with the schedule payment request.
   * 
   * @returns a `UserDto` object containing the administrator's details.
   * 
   * 	- `userId`: The ID of the administrator associated with the enriched schedule
   * payment request.
   * 	- `id`: The entity ID of the administrator associated with the enriched schedule
   * payment request.
   * 	- `name`: The name of the administrator associated with the enriched schedule
   * payment request.
   * 	- `email`: The email address of the administrator associated with the enriched
   * schedule payment request.
   * 	- `encryptedPassword`: The encrypted password of the administrator associated
   * with the enriched schedule payment request.
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
   * object, containing the member's ID, name, and entity ID.
   * 
   * @param enrichedSchedulePaymentRequest payment request with additional data, such
   * as the member entity ID and name, which are used to create a new `HouseMemberDto`.
   * 
   * 	- `memberEntityId`: A unique identifier for the member associated with the schedule
   * payment request.
   * 	- `memberId`: The ID of the member associated with the schedule payment request.
   * 	- `houseMemberName`: The name of the member associated with the schedule payment
   * request.
   * 
   * @returns a `HouseMemberDto` object containing the member's ID, name, and member
   * ID from the input `EnrichedSchedulePaymentRequest`.
   * 
   * 	- `id`: The ID of the member entity associated with the enriched schedule payment
   * request.
   * 	- `memberId`: The ID of the member associated with the enriched schedule payment
   * request.
   * 	- `name`: The name of the house member associated with the enriched schedule
   * payment request.
   */
  default HouseMemberDto getEnrichedRequestMember(EnrichedSchedulePaymentRequest enrichedSchedulePaymentRequest) {
    return new HouseMemberDto()
        .id(enrichedSchedulePaymentRequest.getMemberEntityId())
        .memberId(enrichedSchedulePaymentRequest.getMemberId())
        .name(enrichedSchedulePaymentRequest.getHouseMemberName());
  }
}
