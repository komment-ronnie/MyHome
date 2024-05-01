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
 * is responsible for handling requests related to payments in the application. It
 * provides functions to list all payments for a specified member ID, list all payments
 * scheduled by an admin, and check if an administrator is an admin of a given community
 * based on their details and admins stream. These functions return response entities
 * containing lists of payment objects or Boolean values indicating whether an
 * administrator exists in the given community.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentsApi {
  private final PaymentService paymentService;
  private final CommunityService communityService;
  private final SchedulePaymentApiMapper schedulePaymentApiMapper;

  /**
   * processes a schedule payment request from a community member, verifies the user's
   * admin role in the community house, and schedules the payment if the user is an admin.
   * 
   * @param request SchedulePaymentRequest object containing information necessary for
   * scheduling a payment, such as the member ID and admin ID of the community house.
   * 
   * 	- `request.getMemberId()` - returns the member ID of the house member to be paid.
   * 	- `request.getAdminId()` - returns the ID of the community admin who is authorizing
   * the payment.
   * 
   * The function then checks if the user admin of the community house is the same as
   * the one specified in the request, and proceeds accordingly.
   * 
   * @returns a `SchedulePaymentResponse` object containing the scheduled payment details.
   * 
   * 	- `ResponseEntity`: This is an instance of `ResponseEntity`, which represents a
   * response message with a status code and a body. In this case, the status code is
   * set to `HttpStatus.CREATED`, indicating that the payment has been scheduled successfully.
   * 	- `body`: The `body` attribute contains the `SchedulePaymentResponse` object,
   * which provides information about the scheduled payment.
   * 	- `SchedulePaymentResponse`: This class represents the response message for the
   * scheduled payment. It contains fields for the payment ID, the member ID, the
   * community house ID, and a flag indicating whether the payment was successful or not.
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
   * verifies if a specified User is an admin of a CommunityHouse by checking if the
   * User's name appears in the CommunityHouse's Admin list.
   * 
   * @param communityHouse community house whose admins are being checked against to
   * see if the given user is an admin of that community house.
   * 
   * 	- `communityHouse`: A `CommunityHouse` object that represents a community house
   * with various attributes and methods.
   * 	- `getCommunity()`: Returns the community associated with the `communityHouse` object.
   * 	- `getAdmins()`: Returns a list of users who are admins of the community associated
   * with the `communityHouse` object.
   * 
   * @param admin User object to be checked for admin status in relation to the CommunityHouse.
   * 
   * 	- `communityHouse`: This is an instance of the `CommunityHouse` class, which
   * likely contains information about a community and its administration.
   * 	- `getAdmins()`: This method returns a list of `User` objects, representing the
   * admins of the community.
   * 	- `contains()`: This method checks if a specific `User` object is present in the
   * list returned by `getAdmins()`.
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
   * receives a payment ID and retrieves payment details from the service, mapping them
   * to a `SchedulePaymentResponse` object and returning it as an `ResponseEntity`.
   * 
   * @param paymentId id of the payment to retrieve details about.
   * 
   * @returns a `ResponseEntity` object representing a payment detail response.
   * 
   * 	- `paymentId`: The identifier of the payment being retrieved.
   * 	- `paymentService`: An instance of the `PaymentService` class, which is responsible
   * for managing payments.
   * 	- `schedulePaymentApiMapper`: A mapping function that converts a `Payment` object
   * to a `SchedulePaymentResponse` object.
   * 	- `ResponseEntity`: A class that represents a response entity, which can be either
   * `ok` or `notFound`.
   * 
   * The function returns an `Optional` instance of `ResponseEntity`, which contains
   * either the converted `SchedulePaymentResponse` object or an error message indicating
   * that the payment does not exist.
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
   * retrieves all payments for a specified house member and maps them to a response
   * entity with a list of `MemberPayment` objects.
   * 
   * @param memberId unique identifier of the house member for whom the payments are
   * to be listed.
   * 
   * @returns a `ResponseEntity` object containing a list of member payments.
   * 
   * 	- `ResponseEntity<ListMemberPaymentsResponse>`: This is the overall response
   * entity that contains the list of member payments.
   * 	- `paymentService.getHouseMember(memberId)`: This method returns a `Optional<HouseMember>`
   * object, which contains information about the house member with the given `memberId`.
   * 	- `paymentService.getPaymentsByMember(memberId)`: This method returns a list of
   * `Payment` objects, which represent the payments made by the house member with the
   * given `memberId`.
   * 	- `schedulePaymentApiMapper.memberPaymentSetToRestApiResponseMemberPaymentSet()`:
   * This method maps the `Payment` objects to a list of `MemberPayment` objects, which
   * are then included in the response entity.
   * 	- `new ListMemberPaymentsResponse().payments(memberPayments)`: This method creates
   * a new instance of the `ListMemberPaymentsResponse` class and sets the `payments`
   * field to the list of `MemberPayment` objects returned by the previous method calls.
   * 	- `ResponseEntity.ok()`: This method builds a response entity with a status code
   * of 200 (OK) and returns it as part of the overall response entity.
   * 	- `orElseGet(() -> ResponseEntity.notFound().build())`: This method provides an
   * alternative way to handle non-existent house members, by returning a response
   * entity with a status code of 404 (NOT FOUND).
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
   * receives a community ID and an admin ID, and lists all payments scheduled by the
   * admin with the given ID.
   * 
   * @param communityId community that the admin belongs to, which is used to filter
   * the list of payments scheduled by the admin.
   * 
   * @param adminId identifier of the admin for whom the scheduled payments are to be
   * listed.
   * 
   * @param pageable pagination information for the payments, which allows the function
   * to retrieve only a subset of the payments that match the given criteria and to
   * provide the page number and size of the response.
   * 
   * 	- `communityId`: A string representing the ID of the community for which the
   * payments are to be listed.
   * 	- `adminId`: A string representing the ID of the admin for whom the payments are
   * to be listed.
   * 	- `isAdminInGivenCommunity`: A boolean indicating whether the given admin is
   * present in the specified community.
   * 
   * The `pageable` object has several properties and attributes, including:
   * 
   * 	- `pageNumber`: An integer representing the current page number being accessed.
   * 	- `pageSize`: An integer representing the number of payments to be listed per page.
   * 	- `sort`: A string representing the field by which the payments are sorted.
   * 	- `direction`: A string representing the sorting direction (either "asc" or "desc").
   * 
   * @returns a `ResponseEntity` object containing a `ListAdminPaymentsResponse` body
   * with a list of `AdminPayment` objects and pagination metadata.
   * 
   * 	- `payments`: A list of `AdminPayment` objects representing the scheduled payments
   * for the specified admin.
   * 	- `pageInfo`: A `PageInfo` object containing information about the pagination of
   * the results, such as the total number of results and the current page being displayed.
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
   * determines if a user is an admin in a specified community by querying the community
   * details and admins, and then checking if the user ID matches that of an admin.
   * 
   * @param communityId id of the community whose details and admins are to be retrieved
   * for checking if the given `adminId` is an admin in that community.
   * 
   * @param adminId 12-digit unique identifier of an admin to be checked if they are
   * an admin in the given community.
   * 
   * @returns a boolean value indicating whether the specified admin is an administrator
   * of the given community.
   * 
   * 	- `communityId`: The ID of the community being checked for admin status.
   * 	- `adminId`: The ID of the admin being checked for membership in the community.
   * 	- `Boolean`: Returns a boolean value indicating whether the admin is an administrator
   * in the given community.
   */
  private Boolean isAdminInGivenCommunity(String communityId, String adminId) {
    return communityService.getCommunityDetailsByIdWithAdmins(communityId)
        .map(Community::getAdmins)
        .map(admins -> admins.stream().anyMatch(admin -> admin.getUserId().equals(adminId)))
        .orElseThrow(
            () -> new RuntimeException("Community with given id not exists: " + communityId));
  }
}
