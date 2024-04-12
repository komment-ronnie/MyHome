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
 * is responsible for handling requests related to payments in the system. It provides
 * endpoints for creating, reading, updating, and deleting payments, as well as listing
 * all payments for a specific member or admin. The controller uses dependency injection
 * to inject the necessary services, such as the payment service, community service,
 * and schedule payment API mapper. It also handles exceptions and returns appropriate
 * responses based on the request method and input parameters.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentsApi {
  private final PaymentService paymentService;
  private final CommunityService communityService;
  private final SchedulePaymentApiMapper schedulePaymentApiMapper;

  /**
   * schedules a payment for a community member based on their administrator's authorization
   * and the community house they reside in.
   * 
   * @param request Schedule Payment Request sent by the user, containing information
   * such as the member ID and the administrator ID of the community house.
   * 
   * 	- `request.getMemberId()`: This is a string attribute that contains the ID of the
   * house member to be scheduled for payment.
   * 	- `request.getAdminId()`: This is a string attribute that contains the ID of the
   * community admin to be used for scheduling the payment.
   * 
   * @returns a `SchedulePaymentResponse` object containing the scheduled payment details.
   * 
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response with a status code and a body. In this case, the status code
   * is set to `HttpStatus.CREATED`, indicating that the payment has been scheduled successfully.
   * 	- `body`: This is the `SchedulePaymentResponse` object, which contains information
   * about the scheduled payment.
   * 
   * The attributes of the `SchedulePaymentResponse` object are:
   * 
   * 	- `status`: This is an integer status code that indicates whether the payment was
   * successful (201) or not (404).
   * 	- `paymentId`: This is a unique identifier for the scheduled payment.
   * 	- `memberId`: This is the ID of the member whose payment has been scheduled.
   * 	- `adminId`: This is the ID of the admin who scheduled the payment.
   * 	- `communityHouseId`: This is the ID of the community house where the payment was
   * made.
   * 	- `paymentDate`: This is the date and time when the payment was scheduled.
   * 	- `amount`: This is the amount of the payment.
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
   * determines if a specified `User` is an administrator of a given `CommunityHouse`.
   * It does so by checking if the `User` is contained within the `CommunityHouse` 's
   * admin list.
   * 
   * @param communityHouse CommunityHouse object that is being checked for admin status.
   * 
   * 	- `communityHouse`: A `CommunityHouse` object representing the community house
   * being checked for admin status.
   * 	- `getCommunity()`: A method of the `CommunityHouse` class that returns a `Community`
   * object representing the community associated with the house.
   * 	- `getAdmins()`: A method of the `Community` class that returns an array of `User`
   * objects representing the admins of the community.
   * 
   * @param admin User object to be checked if they are an administrator of the CommunityHouse.
   * 
   * 	- `communityHouse`: This is an instance of the `CommunityHouse` class, representing
   * a community house with various attributes and methods.
   * 	- `getCommunity()`: This method returns a reference to the community associated
   * with the `communityHouse` object.
   * 	- `getAdmins()`: This method returns a collection of `User` objects representing
   * the administrators of the community.
   * 
   * @returns a boolean value indicating whether the specified user is an administrator
   * of the community house.
   */
  private boolean isUserAdminOfCommunityHouse(CommunityHouse communityHouse, User admin) {
    return communityHouse.getCommunity()
        .getAdmins()
        .contains(admin);
  }

  /**
   * receives a payment ID and retrieves the corresponding payment details from the
   * payment service, then maps them to a `SchedulePaymentResponse` object using the
   * `paymentToSchedulePaymentResponse` method and returns the result as an `ResponseEntity`.
   * 
   * @param paymentId id of the payment for which details are to be retrieved.
   * 
   * @returns a `ResponseEntity` object representing a payment detail response.
   * 
   * 	- `paymentId`: The unique identifier for the payment being retrieved, which is
   * passed as a parameter in the function call.
   * 	- `paymentService`: A service that provides methods for interacting with the
   * payment system, which is used to retrieve the details of the payment.
   * 	- `schedulePaymentApiMapper`: A mapping service that converts the payment details
   * returned by the payment service into a Schedule Payment response object.
   * 	- `ResponseEntity`: An object that represents the overall response to the function
   * call, including any errors or exceptions that may occur during the execution of
   * the function. In this case, the response entity is created with an `ok` status
   * code if the payment details are successfully retrieved.
   * 
   * Overall, the `listPaymentDetails` function provides a way to retrieve the details
   * of a specific payment by its unique identifier, using a combination of logging and
   * mapping services to handle the response from the payment service.
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
   * list all payments for a specified member ID using the Payment Service and map the
   * response to a `ListMemberPaymentsResponse` object.
   * 
   * @param memberId 13-digit unique identifier of the member for whom all payments are
   * to be listed.
   * 
   * @returns a `ResponseEntity` object containing a list of `Payment` objects representing
   * the member's payments.
   * 
   * 	- `ResponseEntity<ListMemberPaymentsResponse>`: This is the top-level class that
   * represents the response to the list all member payments request. It contains a
   * list of `Payment` objects inside a `List`.
   * 	- `List MemberPaymentsResponse`: This inner class represents the list of payments
   * for a specific member. It has several attributes, including the payment date,
   * amount, and status.
   * 	- `paymentService.getHouseMember(memberId)`: This method returns a `HouseMember`
   * object representing the member whose payments are to be listed.
   * 	- `paymentService.getPaymentsByMember(memberId)`: This method returns a list of
   * `Payment` objects associated with the specified member.
   * 	- `schedulePaymentApiMapper.memberPaymentSetToRestApiResponseMemberPaymentSet`:
   * This method maps the `Payment` object to a corresponding `MemberPayment` object,
   * which is then included in the response.
   * 
   * Overall, the `listAllMemberPayments` function returns a list of payments associated
   * with a specific member, along with additional information about each payment.
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
   * receives a request to list all payments scheduled by an admin, checks if the admin
   * is in the given community, retrieves the payments, and returns them in a response
   * entity.
   * 
   * @param communityId community that the admin belongs to, which is used to filter
   * the list of payments to be returned in the response.
   * 
   * @param adminId ID of the administrator for whom scheduled payments are to be listed.
   * 
   * @param pageable pagination information for the list of payments, allowing the
   * function to filter and limit the result set accordingly.
   * 
   * 	- `communityId`: A string representing the community ID.
   * 	- `adminId`: A string representing the admin ID.
   * 	- `pageable`: An instance of the `Pageable` class, which provides a way to page
   * large data sets. Its properties include:
   * 	+ `pageNumber`: The current page number being requested.
   * 	+ `pageSize`: The number of elements per page.
   * 	+ `totalPages`: The total number of pages in the data set.
   * 	+ `totalElements`: The total number of elements in the data set.
   * 
   * @returns a `ResponseEntity` object containing a `ListAdminPaymentsResponse` object
   * with the scheduled payments and page information.
   * 
   * 	- `payments`: A list of `AdminPayment` objects representing the scheduled payments
   * for the given admin ID in the specified community.
   * 	- `pageInfo`: A `PageInfo` object containing information about the page of payments
   * returned, including the total number of payments and the total number of pages.
   * 
   * The function returns a `ResponseEntity` with the list of scheduled payments and
   * the page information in the body of the response. The status code of the response
   * is set to `200 OK`.
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
   * returns a Boolean value indicating whether a specified administrator is an admin
   * of a given community based on the community's details and admins stream.
   * 
   * @param communityId id of the community whose details are to be retrieved.
   * 
   * @param adminId 12-digit ID of an admin user within the specified community, which
   * is used as a filter to determine if the admin exists within that community.
   * 
   * @returns a boolean value indicating whether the specified administrator is an admin
   * in the given community.
   * 
   * 	- The function returns a `Boolean` value indicating whether an admin with the
   * given `adminId` exists in the specified `communityId`.
   * 	- The `communityService.getCommunityDetailsByIdWithAdmins()` method is called to
   * retrieve the details of the community with the given `communityId`, including its
   * admins.
   * 	- The `map()` methods are used to transform the `List<Admin>` returned by
   * `getCommunityDetailsByIdWithAdmins()` into a stream of `Admin` objects.
   * 	- The `stream().anyMatch()` method is used to check if any admin in the community
   * has the given `adminId`. If no such admin exists, the function returns `false`.
   * 	- If an admin with the given `adminId` exists in the community, the function
   * returns `true`.
   * 	- If the community with the given `communityId` does not exist, the function
   * throws a `RuntimeException`.
   */
  private Boolean isAdminInGivenCommunity(String communityId, String adminId) {
    return communityService.getCommunityDetailsByIdWithAdmins(communityId)
        .map(Community::getAdmins)
        .map(admins -> admins.stream().anyMatch(admin -> admin.getUserId().equals(adminId)))
        .orElseThrow(
            () -> new RuntimeException("Community with given id not exists: " + communityId));
  }
}
