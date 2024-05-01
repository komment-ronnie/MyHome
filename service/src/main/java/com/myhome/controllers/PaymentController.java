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

package com.myhome.controllers;

import com.myhome.api.PaymentsApi;
import com.myhome.controllers.dto.PaymentDto;
import com.myhome.controllers.mapper.SchedulePaymentApiMapper;
import com.myhome.controllers.request.EnrichedSchedulePaymentRequest;
import com.myhome.domain.Community;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.domain.Payment;
import com.myhome.domain.User;
import com.myhome.model.AdminPayment;
import com.myhome.model.ListAdminPaymentsResponse;
import com.myhome.model.ListMemberPaymentsResponse;
import com.myhome.model.SchedulePaymentRequest;
import com.myhome.model.SchedulePaymentResponse;
import com.myhome.services.CommunityService;
import com.myhome.services.PaymentService;
import com.myhome.utils.PageInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller which provides endpoints for managing payments
 */
/**
 * in Spring Boot handles payments-related functionality, including listing all member
 * payments for a given house member ID, listing all payments scheduled by an admin
 * with a given ID, and determining if an admin is present in a specified community.
 * The controller utilizes the `schedulePaymentApiMapper` to map between the REST API
 * response and the required Java classes.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentsApi {
  private final PaymentService paymentService;
  private final CommunityService communityService;
  private final SchedulePaymentApiMapper schedulePaymentApiMapper;

  /**
   * handles schedule payment requests from admins for a community house. It retrieves
   * the member and admin information, checks if the admin is an admin of the community
   * house, enriches the request with additional data, schedules the payment, and returns
   * the response to the client.
   * 
   * @param request Schedule Payment Request sent by the user, which contains information
   * about the payment and the member to be paid.
   * 
   * 	- `request.getMemberId()`: The ID of a house member to be paid.
   * 	- `request.getAdminId()`: The ID of an admin responsible for scheduling the payment.
   * 	- `paymentService.getHouseMember(request.getMemberId())`: Returns a `HouseMember`
   * object representing the house member with the given ID, or throws an exception if
   * not found.
   * 	- `communityService.findCommunityAdminById(request.getAdminId())`: Returns a
   * `User` object representing the admin with the given ID, or throws an exception if
   * not found.
   * 	- `isUserAdminOfCommunityHouse(houseMember.getCommunityHouse(), admin)`: A boolean
   * value indicating whether the admin is an admin of the community house associated
   * with the house member.
   * 
   * @returns a `SchedulePaymentResponse` object containing the scheduled payment details.
   * 
   * 	- `ResponseEntity`: This is the generic type of the response entity, which is an
   * instance of the class `ResponseEntity`.
   * 	- `status`: This is a field of type `HttpStatus`, which indicates the HTTP status
   * code of the response. In this case, it is set to `CREATED`.
   * 	- `body`: This is a field of type `SchedulePaymentResponse`, which contains the
   * detailed response data.
   * 
   * The `SchedulePaymentResponse` object has several attributes, including:
   * 
   * 	- `paymentId`: This is a field of type `UUID`, which represents the unique
   * identifier of the scheduled payment.
   * 	- `memberId`: This is a field of type `UUID`, which represents the ID of the
   * member whose payment is being scheduled.
   * 	- `adminId`: This is a field of type `UUID`, which represents the ID of the
   * community admin who scheduled the payment.
   * 	- `paymentDate`: This is a field of type `Instant`, which represents the date and
   * time when the payment will be processed.
   * 	- `paymentAmount`: This is a field of type `BigDecimal`, which represents the
   * amount of the scheduled payment.
   */
  @Override
  public ResponseEntity<SchedulePaymentResponse> schedulePayment(@Valid
      SchedulePaymentRequest request) {
    log.trace("Received schedule payment request");

    HouseMember houseMember = paymentService.getHouseMember(request.getMemberId())
        .orElseThrow(() -> new RuntimeException(
            "House member with given id not exists: " + request.getMemberId()));
    User admin = communityService.findCommunityAdminById(request.getAdminId())
        .orElseThrow(
            () -> new RuntimeException("Admin with given id not exists: " + request.getAdminId()));

    if (isUserAdminOfCommunityHouse(houseMember.getCommunityHouse(), admin)) {
      final EnrichedSchedulePaymentRequest paymentRequest =
          schedulePaymentApiMapper.enrichSchedulePaymentRequest(request, admin, houseMember);
      final PaymentDto paymentDto =
          schedulePaymentApiMapper.enrichedSchedulePaymentRequestToPaymentDto(paymentRequest);
      final PaymentDto processedPayment = paymentService.schedulePayment(paymentDto);
      final SchedulePaymentResponse paymentResponse =
          schedulePaymentApiMapper.paymentToSchedulePaymentResponse(processedPayment);
      return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
    }

    return ResponseEntity.notFound().build();
  }

  /**
   * determines if a given user is an administrator of a community house by checking
   * if their username is present in the community house's admin list.
   * 
   * @param communityHouse CommunityHouse object that is being checked for admin status.
   * 
   * 	- `communityHouse`: A `CommunityHouse` object representing the community house
   * being checked for administration rights.
   * 	- `getCommunity()`: A method on the `CommunityHouse` class that returns a `Community`
   * object, which is the main entity of interest in the system.
   * 	- `getAdmins()`: A method on the `Community` object that returns a list of `User`
   * objects representing the admins of the community house.
   * 
   * @param admin User object to be checked for admin status in relation to the `communityHouse`.
   * 
   * 	- `admin`: A `User` object representing a user in the community house.
   * 	- `CommunityHouse`: An object representing the community house where the user is
   * being checked for admin status.
   * 
   * @returns a boolean value indicating whether the specified user is an admin of the
   * community house.
   */
  private boolean isUserAdminOfCommunityHouse(CommunityHouse communityHouse, User admin) {
    return communityHouse.getCommunity()
        .getAdmins()
        .contains(admin);
  }

  /**
   * retrieves payment details with the given ID from the payment service and maps them
   * to a `SchedulePaymentResponse`. If no payment details are found, it returns a
   * `ResponseEntity` with a status code of `404`.
   * 
   * @param paymentId identifier of the payment for which details are requested.
   * 
   * @returns a `ResponseEntity` object representing a payment details response.
   * 
   * 	- `paymentId`: The unique identifier for the payment being retrieved.
   * 	- `paymentService`: A service that provides payment-related functionality.
   * 	- `schedulePaymentApiMapper`: A mapper used to transform payment data into a
   * Schedule Payment response object.
   * 	- `ResponseEntity`: An entity that represents a response, indicating whether the
   * request was successful or not. In this case, it is either `ok` or `notFound`.
   */
  @Override
  public ResponseEntity<SchedulePaymentResponse> listPaymentDetails(String paymentId) {
    log.trace("Received request to get details about a payment with id[{}]", paymentId);

    return paymentService.getPaymentDetails(paymentId)
        .map(schedulePaymentApiMapper::paymentToSchedulePaymentResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * receives a member ID and returns a response entity containing a list of payments
   * associated with that member.
   * 
   * @param memberId house member for whom all payments are to be listed.
   * 
   * @returns a `ResponseEntity` object containing a list of `MemberPayment` objects.
   * 
   * 	- `ResponseEntity<ListMemberPaymentsResponse>`: This is the type of the returned
   * response entity, which contains a list of member payments.
   * 	- `listAllMemberPayments()`: This is the method that returns the response entity.
   * 	- `log.trace()`: This line logs a trace message indicating that the function has
   * received a request to list all the payments for a specific house member.
   * 	- `paymentService.getHouseMember(memberId)`: This line retrieves the member object
   * associated with the given `memberId`.
   * 	- `paymentService.getPaymentsByMember(memberId)`: This line retrieves the list
   * of payments associated with the retrieved member object.
   * 	- `schedulePaymentApiMapper.memberPaymentSetToRestApiResponseMemberPaymentSet()`:
   * This line maps the list of payments to a response entity containing the list of
   * member payments.
   * 	- `new List Member Payments Response().payments(memberPayments)`: This line creates
   * a new instance of the `ListMemberPaymentsResponse` class and sets its `payments`
   * field to the list of payments retrieved from the API.
   * 	- `map(ResponseEntity::ok) ||ElseGet(() -> ResponseEntity.notFound().build())`:
   * This line maps the response entity to either `OK` or `NOT FOUND`, depending on
   * whether a valid response was received from the API.
   */
  @Override
  public ResponseEntity<ListMemberPaymentsResponse> listAllMemberPayments(String memberId) {
    log.trace("Received request to list all the payments for the house member with id[{}]",
        memberId);

    return paymentService.getHouseMember(memberId)
        .map(payments -> paymentService.getPaymentsByMember(memberId))
        .map(schedulePaymentApiMapper::memberPaymentSetToRestApiResponseMemberPaymentSet)
        .map(memberPayments -> new ListMemberPaymentsResponse().payments(memberPayments))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * retrieves all scheduled payments for a given admin in a community, checks if the
   * admin is present in the community, and returns the list of payments in a response
   * entity.
   * 
   * @param communityId community that the admin belongs to, which is used to filter
   * the payments listed in the response.
   * 
   * @param adminId ID of the admin for whom the payments are being listed, and is used
   * to filter the payments returned in the response.
   * 
   * @param pageable page number and the number of payments to be displayed per page
   * for the list of payments scheduled by the admin.
   * 
   * 	- `communityId`: The ID of the community that the admin belongs to.
   * 	- `adminId`: The ID of the admin who is requesting the list of scheduled payments.
   * 	- `pageable`: A `Pageable` object representing the pagination information for the
   * list of scheduled payments. It contains the page number, page size, and total
   * number of payments.
   * 
   * @returns a `ResponseEntity` object containing a `ListAdminPaymentsResponse` body
   * with the scheduled payments for the admin.
   * 
   * 	- `payments`: A list of `AdminPayment` objects, representing the scheduled payments
   * for the given admin.
   * 	- `pageInfo`: Represents the pagination information of the payment list, including
   * the total number of payments and the current page number.
   * 
   * The function returns a `ResponseEntity` object with the `body` containing the
   * `ListAdminPaymentsResponse` object.
   */
  @Override
  public ResponseEntity<ListAdminPaymentsResponse> listAllAdminScheduledPayments(
      String communityId, String adminId, Pageable pageable) {
    log.trace("Received request to list all the payments scheduled by the admin with id[{}]",
        adminId);

    final boolean isAdminInGivenCommunity = isAdminInGivenCommunity(communityId, adminId);

    if (isAdminInGivenCommunity) {
      final Page<Payment> paymentsForAdmin = paymentService.getPaymentsByAdmin(adminId, pageable);
      final List<Payment> payments = paymentsForAdmin.getContent();
      final Set<AdminPayment> adminPayments =
          schedulePaymentApiMapper.adminPaymentSetToRestApiResponseAdminPaymentSet(
              new HashSet<>(payments));
      final ListAdminPaymentsResponse response = new ListAdminPaymentsResponse()
          .payments(adminPayments)
          .pageInfo(PageInfo.of(pageable, paymentsForAdmin));
      return ResponseEntity.ok().body(response);
    }

    return ResponseEntity.notFound().build();
  }

  /**
   * takes two parameters, `communityId` and `adminId`, and returns a boolean value
   * indicating whether the specified admin is an administrator in the given community.
   * It does so by retrieving the details of the community and its admins, then checks
   * if the specified admin is present in the list of admins for that community.
   * 
   * @param communityId identifier of the community whose details and admins are to be
   * retrieved for checking if the given `adminId` is an admin in that community.
   * 
   * @param adminId ID of the admin to check if they are an administrator in the specified
   * community.
   * 
   * @returns a boolean value indicating whether the specified admin is an administrator
   * of the given community.
   * 
   * 	- The method returns a `Boolean` value indicating whether the specified `adminId`
   * is an admin in the given `communityId` community or not.
   * 	- The method first retrieves the details of the `communityId` using
   * `communityService.getCommunityDetailsByIdWithAdmins()`, which returns a `List` of
   * `Community` objects containing the details of the community, along with a list of
   * admins associated with it.
   * 	- The method then maps each admin in the list to its `userId`, and then uses
   * `stream().anyMatch()` to check if the specified `adminId` exists in the list of admins.
   * 	- If the `adminId` is found in the list, the method returns `true`. Otherwise,
   * it returns `false`.
   * 	- If the community with the given `communityId` does not exist, the method throws
   * a `RuntimeException`.
   */
  private Boolean isAdminInGivenCommunity(String communityId, String adminId) {
    return communityService.getCommunityDetailsByIdWithAdmins(communityId)
        .map(Community::getAdmins)
        .map(admins -> admins.stream().anyMatch(admin -> admin.getUserId().equals(adminId)))
        .orElseThrow(
            () -> new RuntimeException("Community with given id not exists: " + communityId));
  }
}
