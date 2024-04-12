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

import com.myhome.controllers.dto.CommunityDto;
import com.myhome.controllers.dto.PaymentDto;
import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.mapper.SchedulePaymentApiMapper;
import com.myhome.controllers.request.EnrichedSchedulePaymentRequest;
import com.myhome.domain.Community;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.domain.HouseMemberDocument;
import com.myhome.domain.Payment;
import com.myhome.domain.User;
import com.myhome.model.AdminPayment;
import com.myhome.model.HouseMemberDto;
import com.myhome.model.ListAdminPaymentsResponse;
import com.myhome.model.ListMemberPaymentsResponse;
import com.myhome.model.MemberPayment;
import com.myhome.services.CommunityService;
import com.myhome.services.PaymentService;
import com.myhome.utils.PageInfo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * tests the listAllAdminScheduledPayments method of the PaymentController class. The
 * test cases cover various scenarios such as successful retrieval of payments for
 * an admin, retrieval of payments for an admin who is not in the community, and
 * thrown exceptions when the community does not exist. The test classes use PowerMockito
 * to stub and verify interactions with other services and methods.
 */
class PaymentControllerTest {

  private static final String TEST_TYPE = "WATER BILL";
  private static final String TEST_DESCRIPTION = "This is your excess water bill";
  private static final boolean TEST_RECURRING = false;
  private static final BigDecimal TEST_CHARGE = BigDecimal.valueOf(50.00);
  private static final String TEST_DUE_DATE = "2020-08-15";
  private static final String TEST_MEMBER_NAME = "Test Name";
  private static final String TEST_COMMUNITY_NAME = "Test Community";
  private static final String TEST_COMMUNITY_DISTRICT = "Wonderland";
  private static final String TEST_ADMIN_ID = "1";
  private static final String TEST_ADMIN_NAME = "test_admin_name";
  private static final String TEST_ADMIN_EMAIL = "test_admin_email@myhome.com";
  private static final String TEST_ADMIN_PASSWORD = "password";
  private static final String COMMUNITY_ADMIN_NAME = "Test Name";
  private static final String COMMUNITY_ADMIN_EMAIL = "testadmin@myhome.com";
  private static final String COMMUNITY_ADMIN_PASSWORD = "testpassword@myhome.com";
  private static final String COMMUNITY_HOUSE_NAME = "Test House";
  private static final String COMMUNITY_HOUSE_ID = "5";
  private static final String TEST_MEMBER_ID = "2";
  private static final String TEST_ID = "3";
  private static final String TEST_COMMUNITY_ID = "4";

  private static final Pageable TEST_PAGEABLE = PageRequest.of(1, 10);

  @Mock
  private PaymentService paymentService;

  @Mock
  private SchedulePaymentApiMapper paymentApiMapper;

  @Mock
  private CommunityService communityService;

  @InjectMocks
  private PaymentController paymentController;

  /**
   * initializes MockitoAnnotations for testing purposes by mocking dependencies and
   * setting up the Mockito runtime environment.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * creates a PaymentDto object with test data, including an ID, type, description,
   * charge, due date, and recurring status, as well as reference to a UserDto and
   * HouseMemberDto objects representing the admin and member associated with the payment.
   * 
   * @returns a `PaymentDto` object with mock data for a payment.
   * 
   * 	- paymentId: A unique identifier for the payment.
   * 	- type: The type of payment (e.g., invoice, subscription).
   * 	- description: A brief description of the payment.
   * 	- charge: The amount to be charged to the user.
   * 	- dueDate: The date by which the payment must be made.
   * 	- recurring: Whether the payment is recurring or not.
   * 	- admin: The user who created the payment (represented as a `UserDto`).
   * 	- member: The member associated with the payment (represented as a `HouseMemberDto`).
   */
  private PaymentDto createTestPaymentDto() {
    UserDto userDto = UserDto.builder()
        .userId(TEST_ADMIN_ID)
        .communityIds(new HashSet<>(Collections.singletonList(TEST_COMMUNITY_ID)))
        .id(Long.valueOf(TEST_ADMIN_ID))
        .encryptedPassword(TEST_ADMIN_PASSWORD)
        .name(TEST_ADMIN_NAME)
        .email(TEST_ADMIN_EMAIL)
        .build();
    HouseMemberDto houseMemberDto = new HouseMemberDto()
        .memberId(TEST_MEMBER_ID)
        .name(TEST_MEMBER_NAME)
        .id(Long.valueOf(TEST_MEMBER_ID));

    return PaymentDto.builder()
        .paymentId(TEST_ID)
        .type(TEST_TYPE)
        .description(TEST_DESCRIPTION)
        .charge(TEST_CHARGE)
        .dueDate(TEST_DUE_DATE)
        .recurring(TEST_RECURRING)
        .admin(userDto)
        .member(houseMemberDto)
        .build();
  }

  /**
   * creates a new instance of the `CommunityDto` class with test data for a community,
   * including its name, district, and ID.
   * 
   * @returns a `CommunityDto` object with test data.
   * 
   * 	- `name`: A string representing the name of the community.
   * 	- `district`: A string representing the district where the community is located.
   * 	- `communityId`: An integer representing the unique identifier of the community.
   */
  private CommunityDto createTestCommunityDto() {
    CommunityDto communityDto = new CommunityDto();
    communityDto.setName(TEST_COMMUNITY_NAME);
    communityDto.setDistrict(TEST_COMMUNITY_DISTRICT);
    communityDto.setCommunityId(TEST_COMMUNITY_ID);
    return communityDto;
  }

  /**
   * creates a new community object and adds admins to it, then links the admin to the
   * community via a house object, finally returning the created community object.
   * 
   * @param admins set of users who will be administrators for the generated community,
   * and it is used to create an instance of `User` objects that will be added as admins
   * to the community.
   * 
   * 	- `Set<User> admins`: This is a set of `User` objects, representing the community
   * administrators.
   * 	- `new HashSet<>()`: An empty set, used as a placeholder for the community's administrators.
   * 	- `TEST_COMMUNITY_NAME`, `TEST_COMMUNITY_ID`, and `TEST_COMMUNITY_DISTRICT`: These
   * are constant strings representing the name, ID, and district of the mock community
   * being created.
   * 	- `new HashSet<>()`: An empty set, used to store the community's houses.
   * 	- `User admin`: A single `User` object representing the first administrator for
   * the community. Its properties are explained below:
   * 	+ `COMMUNITY_ADMIN_NAME`: A constant string representing the name of the community
   * administrator.
   * 	+ `TEST_ADMIN_ID`: An integer ID representing the ID of the community administrator.
   * 	+ `COMMUNITY_ADMIN_EMAIL`: An email address representing the email address of the
   * community administrator.
   * 	+ `false`: A boolean value indicating whether the administrator is an owner of
   * the community.
   * 	+ `COMMUNITY_ADMIN_PASSWORD`: A password representing the password of the community
   * administrator.
   * 	+ `new HashSet<>()`: An empty set, used to store the communities that the
   * administrator belongs to.
   * 	+ `new HashSet<>()`: An empty set, used to store the houses that the administrator
   * owns.
   * 
   * @returns a mock community object containing admins and houses.
   * 
   * 	- The `Community` object represents a mock community with a set of admins, a name,
   * an ID, a district, and a set of houses.
   * 	- The `admins` field is a set of users who are administrators of the community.
   * 	- The `name`, `id`, and `district` fields represent the name, ID, and district
   * of the community, respectively.
   * 	- The `houses` field is a set of `CommunityHouse` objects that represent the
   * houses in the community.
   * 	- The `User` object represents an admin user of the community with a name, ID,
   * email, and password.
   * 	- The `getAdmins()` method returns a set of admins for the community.
   * 	- The `getHouses()` method returns a set of houses in the community.
   * 	- The `setCommunity()` method sets the community object for the given house.
   */
  private Community getMockCommunity(Set<User> admins) {
    Community community =
        new Community(admins, new HashSet<>(), TEST_COMMUNITY_NAME, TEST_COMMUNITY_ID,
            TEST_COMMUNITY_DISTRICT, new HashSet<>());
    User admin = new User(COMMUNITY_ADMIN_NAME, TEST_ADMIN_ID, COMMUNITY_ADMIN_EMAIL, false,
        COMMUNITY_ADMIN_PASSWORD, new HashSet<>(), new HashSet<>());
    community.getAdmins().add(admin);
    admin.getCommunities().add(community);

    CommunityHouse communityHouse = getMockCommunityHouse();
    communityHouse.setCommunity(community);
    community.getHouses().add(communityHouse);

    return community;
  }

  /**
   * creates a new instance of `CommunityHouse` and sets its name, ID, and member list
   * to empty. It returns the created `CommunityHouse` object.
   * 
   * @returns a mock CommunityHouse object.
   * 
   * 	- `CommunityHouse communityHouse`: This is an instance of the `CommunityHouse`
   * class, which contains information about a mock community house.
   * 	- `name`: The name of the community house, set to `COMMUNITY_HOUSE_NAME`.
   * 	- `houseId`: The ID of the community house, set to `COMMUNITY_HOUSE_ID`.
   * 	- `houseMembers`: A set containing no members, indicating that the community house
   * has no members.
   */
  private CommunityHouse getMockCommunityHouse() {
    CommunityHouse communityHouse = new CommunityHouse();
    communityHouse.setName(COMMUNITY_HOUSE_NAME);
    communityHouse.setHouseId(COMMUNITY_HOUSE_ID);
    communityHouse.setHouseMembers(new HashSet<>());

    return communityHouse;
  }

  /**
   * creates a mock payment object containing a user, community, and payment details.
   * The user is created with admin privileges, and the community is generated with the
   * user as an admin. The function returns the mock payment object.
   * 
   * @returns a mock payment object containing various details.
   * 
   * 	- `id`: The unique identifier for this payment.
   * 	- `charge`: The amount charged to the customer.
   * 	- `type`: The type of payment (e.g., one-time or recurring).
   * 	- `description`: A brief description of the payment.
   * 	- `dueDate`: The date by which the payment is due.
   * 	- `admin`: The user who created the payment.
   * 	- `houseMember`: The member associated with this payment.
   * 	- `communityHouse`: The community house associated with this payment.
   */
  private Payment getMockPayment() {
    User admin =
        new User(TEST_ADMIN_NAME, TEST_ADMIN_ID, TEST_ADMIN_EMAIL, false, TEST_ADMIN_PASSWORD,
            new HashSet<>(), new HashSet<>());
    Community community = getMockCommunity(new HashSet<>());
    community.getAdmins().add(admin);
    admin.getCommunities().add(community);
    return new Payment(TEST_ID, TEST_CHARGE, TEST_TYPE, TEST_DESCRIPTION, TEST_RECURRING,
        LocalDate.parse(TEST_DUE_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd")), admin,
        new HouseMember(TEST_MEMBER_ID, new HouseMemberDocument(), TEST_MEMBER_NAME,
            new CommunityHouse()));
  }

  /**
   * tests the payment API endpoint for scheduling a payment successfully. It creates
   * a test request, enriches it with additional data, and then verifies that the
   * response is successful and the data is correct.
   */
  @Test
  void shouldSchedulePaymentSuccessful() {
    // given
    com.myhome.model.SchedulePaymentRequest request =
        new com.myhome.model.SchedulePaymentRequest()
            .type(TEST_TYPE)
            .description(TEST_DESCRIPTION)
            .recurring(TEST_RECURRING)
            .charge(TEST_CHARGE)
            .dueDate(TEST_DUE_DATE)
            .adminId(TEST_ADMIN_ID)
            .memberId(TEST_MEMBER_ID);

    EnrichedSchedulePaymentRequest enrichedRequest =
        new EnrichedSchedulePaymentRequest(TEST_TYPE, TEST_DESCRIPTION, TEST_RECURRING, TEST_CHARGE,
            TEST_DUE_DATE, TEST_ADMIN_ID, 1L, TEST_ADMIN_NAME, TEST_ADMIN_EMAIL,
            TEST_ADMIN_PASSWORD, new HashSet<>(Collections.singletonList(TEST_COMMUNITY_ID)),
            TEST_MEMBER_ID,
            2L, "", TEST_MEMBER_NAME, COMMUNITY_HOUSE_ID);
    PaymentDto paymentDto = createTestPaymentDto();
    com.myhome.model.SchedulePaymentResponse response =
        new com.myhome.model.SchedulePaymentResponse()
            .paymentId(TEST_ID)
            .charge(TEST_CHARGE)
            .type(TEST_TYPE)
            .description(TEST_DESCRIPTION)
            .recurring(TEST_RECURRING)
            .dueDate(TEST_DUE_DATE)
            .adminId(TEST_ADMIN_ID)
            .memberId(TEST_MEMBER_ID);

    Community community = getMockCommunity(new HashSet<>());

    HouseMember member = new HouseMember(TEST_MEMBER_ID, null, TEST_MEMBER_NAME,
        community.getHouses().iterator().next());

    community.getHouses().iterator().next().getHouseMembers().add(member);

    User admin = community.getAdmins().iterator().next();

    given(paymentApiMapper.enrichSchedulePaymentRequest(request, admin, member))
        .willReturn(enrichedRequest);
    given(paymentApiMapper.enrichedSchedulePaymentRequestToPaymentDto(enrichedRequest))
        .willReturn(paymentDto);
    given(paymentService.schedulePayment(paymentDto))
        .willReturn(paymentDto);
    given(paymentApiMapper.paymentToSchedulePaymentResponse(paymentDto))
        .willReturn(response);
    given(paymentService.getHouseMember(TEST_MEMBER_ID))
        .willReturn(Optional.of(member));
    given(communityService.findCommunityAdminById(TEST_ADMIN_ID))
        .willReturn(Optional.of(community.getAdmins().iterator().next()));

    //when
    ResponseEntity<com.myhome.model.SchedulePaymentResponse> responseEntity =
        paymentController.schedulePayment(request);

    //then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(paymentApiMapper).enrichSchedulePaymentRequest(request, admin, member);
    verify(paymentApiMapper).enrichedSchedulePaymentRequestToPaymentDto(enrichedRequest);
    verify(paymentService).schedulePayment(paymentDto);
    verify(paymentApiMapper).paymentToSchedulePaymentResponse(paymentDto);
    verify(paymentService).getHouseMember(TEST_MEMBER_ID);
  }

  /**
   * tests whether the payment controller throws a `RuntimeException` when a member
   * with the given ID does not exist in the database.
   */
  @Test
  void shouldNotScheduleIfMemberDoesNotExist() {
    // given
    com.myhome.model.SchedulePaymentRequest request =
        new com.myhome.model.SchedulePaymentRequest()
            .type(TEST_TYPE)
            .description(TEST_DESCRIPTION)
            .recurring(TEST_RECURRING)
            .charge(TEST_CHARGE)
            .dueDate(TEST_DUE_DATE)
            .adminId(TEST_ADMIN_ID)
            .memberId(TEST_MEMBER_ID);
    PaymentDto paymentDto = createTestPaymentDto();
    String expectedExceptionMessage = "House member with given id not exists: " + TEST_MEMBER_ID;

    given(paymentApiMapper.schedulePaymentRequestToPaymentDto(request))
        .willReturn(paymentDto);
    given(paymentService.schedulePayment(paymentDto))
        .willReturn(paymentDto);
    given(paymentService.getHouseMember(TEST_MEMBER_ID))
        .willReturn(Optional.empty());

    // when
    final RuntimeException runtimeException =
        assertThrows(RuntimeException.class, () -> paymentController.schedulePayment(request));
    // then
    final String exceptionMessage = runtimeException.getMessage();
    assertEquals(expectedExceptionMessage, exceptionMessage);
    verifyNoInteractions(paymentApiMapper);
  }

  /**
   * tests the scenario where an admin with the given ID does not exist, and it should
   * throw a `RuntimeException` with a specific message.
   */
  @Test
  void shouldNotScheduleIfAdminDoesntExist() {
    // given
    com.myhome.model.SchedulePaymentRequest request =
        new com.myhome.model.SchedulePaymentRequest()
            .type(TEST_TYPE)
            .description(TEST_DESCRIPTION)
            .recurring(TEST_RECURRING)
            .charge(TEST_CHARGE)
            .dueDate(TEST_DUE_DATE)
            .adminId(TEST_ADMIN_ID)
            .memberId(TEST_MEMBER_ID);
    PaymentDto paymentDto = createTestPaymentDto();
    String expectedExceptionMessage = "Admin with given id not exists: " + TEST_ADMIN_ID;
    com.myhome.model.SchedulePaymentResponse response =
        new com.myhome.model.SchedulePaymentResponse()
            .paymentId(TEST_ID)
            .charge(TEST_CHARGE)
            .type(TEST_TYPE)
            .description(TEST_DESCRIPTION)
            .recurring(TEST_RECURRING)
            .dueDate(TEST_DUE_DATE)
            .adminId(TEST_ADMIN_ID)
            .memberId(TEST_MEMBER_ID);

    HouseMember member = new HouseMember(TEST_MEMBER_ID, null, TEST_MEMBER_NAME, null);

    given(paymentApiMapper.schedulePaymentRequestToPaymentDto(request))
        .willReturn(paymentDto);
    given(paymentService.schedulePayment(paymentDto))
        .willReturn(paymentDto);
    given(paymentApiMapper.paymentToSchedulePaymentResponse(paymentDto))
        .willReturn(response);
    given(paymentService.getHouseMember(TEST_MEMBER_ID))
        .willReturn(Optional.of(member));
    given(communityService.findCommunityAdminById(TEST_ADMIN_ID))
        .willReturn(Optional.empty());

    // when
    final RuntimeException runtimeException =
        assertThrows(RuntimeException.class, () -> paymentController.schedulePayment(request));
    // then
    final String exceptionMessage = runtimeException.getMessage();
    assertEquals(expectedExceptionMessage, exceptionMessage);
    verifyNoInteractions(paymentApiMapper);
  }

  /**
   * checks if a payment request should be scheduled when the admin associated with the
   * member is not part of the community. If the admin is not in the community, the
   * function returns a ResponseEntity with a status code of NOT_FOUND and an empty body.
   */
  @Test
  void shouldNotScheduleIfAdminIsNotInCommunity() {
    // given
    com.myhome.model.SchedulePaymentRequest request =
        new com.myhome.model.SchedulePaymentRequest()
            .type(TEST_TYPE)
            .description(TEST_DESCRIPTION)
            .recurring(TEST_RECURRING)
            .charge(TEST_CHARGE)
            .dueDate(TEST_DUE_DATE)
            .adminId(TEST_ADMIN_ID)
            .memberId(TEST_MEMBER_ID);
    PaymentDto paymentDto = createTestPaymentDto();
    com.myhome.model.SchedulePaymentResponse response =
        new com.myhome.model.SchedulePaymentResponse()
            .paymentId(TEST_ID)
            .charge(TEST_CHARGE)
            .type(TEST_TYPE)
            .description(TEST_DESCRIPTION)
            .recurring(TEST_RECURRING)
            .dueDate(TEST_DUE_DATE)
            .adminId(TEST_ADMIN_ID)
            .memberId(TEST_MEMBER_ID);

    Community community = getMockCommunity(new HashSet<>());
    Set<User> admins = community.getAdmins();
    User admin = admins.iterator().next();
    admins.remove(admin);

    CommunityHouse communityHouse = community.getHouses().iterator().next();

    HouseMember member = new HouseMember(TEST_MEMBER_ID, null, TEST_MEMBER_NAME, communityHouse);

    given(paymentApiMapper.schedulePaymentRequestToPaymentDto(request))
        .willReturn(paymentDto);
    given(paymentService.schedulePayment(paymentDto))
        .willReturn(paymentDto);
    given(paymentApiMapper.paymentToSchedulePaymentResponse(paymentDto))
        .willReturn(response);
    given(paymentService.getHouseMember(TEST_MEMBER_ID))
        .willReturn(Optional.of(member));
    given(communityService.findCommunityAdminById(TEST_ADMIN_ID))
        .willReturn(Optional.of(admin));

    //when
    ResponseEntity<com.myhome.model.SchedulePaymentResponse> responseEntity =
        paymentController.schedulePayment(request);

    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(paymentService).getHouseMember(TEST_MEMBER_ID);
    verifyNoInteractions(paymentApiMapper);
    verify(communityService).findCommunityAdminById(TEST_ADMIN_ID);
  }

  /**
   * tests the payment controller's method `listPaymentDetails`, which retrieves payment
   * details for a given ID and maps them to a `SchedulePaymentResponse` object. The
   * function verifies that the response status code is `OK` and that the mapped response
   * matches the expected response.
   */
  @Test
  void shouldGetPaymentDetailsSuccess() {
    // given
    PaymentDto paymentDto = createTestPaymentDto();

    com.myhome.model.SchedulePaymentResponse expectedResponse =
        new com.myhome.model.SchedulePaymentResponse()
            .paymentId(TEST_ID)
            .charge(TEST_CHARGE)
            .type(TEST_TYPE)
            .description(TEST_DESCRIPTION)
            .recurring(TEST_RECURRING)
            .dueDate(TEST_DUE_DATE)
            .adminId(TEST_ADMIN_ID)
            .memberId(TEST_MEMBER_ID);
    given(paymentService.getPaymentDetails(TEST_ID))
        .willReturn(Optional.of(paymentDto));
    given(paymentApiMapper.paymentToSchedulePaymentResponse(paymentDto))
        .willReturn(expectedResponse);

    // when
    ResponseEntity<com.myhome.model.SchedulePaymentResponse> responseEntity =
        paymentController.listPaymentDetails(TEST_ID);

    // then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedResponse, responseEntity.getBody());
    verify(paymentService).getPaymentDetails(TEST_ID);
    verify(paymentApiMapper).paymentToSchedulePaymentResponse(paymentDto);
  }

  /**
   * tests the `listPaymentDetails` method of a payment controller by providing a test
   * ID and verifying that the method returns a `HttpStatus.NOT_FOUND` status code and
   * no payment details when no payment details are available for the given test ID.
   */
  @Test
  void shouldListNoPaymentDetailsSuccess() {
    //given
    given(paymentService.getPaymentDetails(TEST_ID))
        .willReturn(Optional.empty());

    //when
    ResponseEntity<com.myhome.model.SchedulePaymentResponse> responseEntity =
        paymentController.listPaymentDetails(TEST_ID);

    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(paymentService).getPaymentDetails(TEST_ID);
    verifyNoInteractions(paymentApiMapper);
  }

  /**
   * tests whether the `listAllMemberPayments` method returns a response with a status
   * code of `HttpStatus.NOT_FOUND` and an empty list when no member payments are found
   * for the given member ID.
   */
  @Test
  void shouldGetNoMemberPaymentsSuccess() {
    //given
    given(paymentService.getHouseMember(TEST_MEMBER_ID))
        .willReturn(Optional.empty());

    //when
    ResponseEntity<ListMemberPaymentsResponse> responseEntity =
        paymentController.listAllMemberPayments(TEST_MEMBER_ID);

    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verifyNoInteractions(paymentApiMapper);
  }

  /**
   * tests the `listAllMemberPayments` method of a payment controller by providing a
   * member ID and verifying that the response contains the expected payments in the
   * correct format.
   */
  @Test
  void shouldGetMemberPaymentsSuccess() {
    // given
    PaymentDto paymentDto = createTestPaymentDto();

    given(paymentService.schedulePayment(paymentDto))
        .willReturn(paymentDto);

    HouseMember member = new HouseMember(TEST_MEMBER_ID, null, TEST_MEMBER_NAME, null);
    given(paymentService.getHouseMember(TEST_MEMBER_ID))
        .willReturn(Optional.of(member));

    Set<Payment> payments = new HashSet<>();
    Payment mockPayment = getMockPayment();
    payments.add(mockPayment);

    given(paymentService.getPaymentsByMember(TEST_MEMBER_ID))
        .willReturn(payments);

    Set<MemberPayment> paymentResponses = new HashSet<>();
    paymentResponses.add(
        new MemberPayment()
            .memberId(TEST_MEMBER_ID)
            .paymentId(TEST_ID)
            .charge(TEST_CHARGE)
            .dueDate(TEST_DUE_DATE));

    ListMemberPaymentsResponse expectedResponse =
        new ListMemberPaymentsResponse().payments(paymentResponses);

    given(paymentApiMapper.memberPaymentSetToRestApiResponseMemberPaymentSet(payments))
        .willReturn(paymentResponses);

    // when
    ResponseEntity<ListMemberPaymentsResponse> responseEntity =
        paymentController.listAllMemberPayments(TEST_MEMBER_ID);

    // then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(responseEntity.getBody(), expectedResponse);
    verify(paymentService).getPaymentsByMember(TEST_MEMBER_ID);
    verify(paymentApiMapper).memberPaymentSetToRestApiResponseMemberPaymentSet(payments);
  }

  /**
   * tests the listAllAdminScheduledPayments endpoint, where it retrieves all scheduled
   * payments for a given admin and community, and returns them in a paginated response.
   */
  @Test
  void shouldGetAdminPaymentsSuccess() {
    // given
    com.myhome.model.SchedulePaymentRequest request =
        new com.myhome.model.SchedulePaymentRequest()
            .type(TEST_TYPE)
            .description(TEST_DESCRIPTION)
            .recurring(TEST_RECURRING)
            .charge(TEST_CHARGE)
            .dueDate(TEST_DUE_DATE)
            .adminId(TEST_ADMIN_ID)
            .memberId(TEST_MEMBER_ID);
    PaymentDto paymentDto = createTestPaymentDto();

    given(paymentService.schedulePayment(paymentDto))
        .willReturn(paymentDto);

    List<Payment> payments = new ArrayList<>();
    Payment mockPayment = getMockPayment();
    payments.add(mockPayment);

    Set<String> adminIds = new HashSet<>();
    adminIds.add(TEST_ADMIN_ID);

    Set<User> admins = new HashSet<>();

    Community community = getMockCommunity(admins);

    CommunityDto communityDto = createTestCommunityDto();

    given(communityService.createCommunity(communityDto))
        .willReturn(community);
    given(communityService.getCommunityDetailsByIdWithAdmins(TEST_ID))
        .willReturn(Optional.of(community));
    given(paymentService.getPaymentsByAdmin(TEST_ADMIN_ID, TEST_PAGEABLE))
        .willReturn(new PageImpl<>(payments));
    given(communityService.addAdminsToCommunity(TEST_ID, adminIds))
        .willReturn(Optional.of(community));

    Set<AdminPayment> responsePayments = new HashSet<>();
    responsePayments.add(
        new AdminPayment().adminId(TEST_ADMIN_ID)
            .paymentId(TEST_ID)
            .charge(TEST_CHARGE)
            .dueDate(TEST_DUE_DATE)
    );

    ListAdminPaymentsResponse expectedResponse =
        new ListAdminPaymentsResponse()
            .payments(responsePayments)
            .pageInfo(PageInfo.of(TEST_PAGEABLE, new PageImpl<>(payments)));

    given(paymentApiMapper.adminPaymentSetToRestApiResponseAdminPaymentSet(new HashSet<>(payments)))
        .willReturn(responsePayments);

    //when
    ResponseEntity<ListAdminPaymentsResponse> responseEntity =
        paymentController.listAllAdminScheduledPayments(TEST_ID, TEST_ADMIN_ID,
            TEST_PAGEABLE);

    //then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedResponse, responseEntity.getBody());
    verify(communityService).getCommunityDetailsByIdWithAdmins(TEST_ID);
    verify(paymentService).getPaymentsByAdmin(TEST_ADMIN_ID, TEST_PAGEABLE);
    verify(paymentApiMapper).adminPaymentSetToRestApiResponseAdminPaymentSet(
        new HashSet<>(payments));
  }

  /**
   * verifies that when an admin is not in a community, the listAllAdminScheduledPayments
   * method returns a `HttpStatus.NOT_FOUND` response and no admins are returned in the
   * body of the response.
   */
  @Test
  void shouldReturnNotFoundWhenAdminIsNotInCommunity() {
    //given
    final String notAdminFromCommunity = "2";
    Community community = getMockCommunity(new HashSet<>());
    given(communityService.getCommunityDetailsByIdWithAdmins(TEST_ID))
        .willReturn(Optional.of(community));

    //when
    ResponseEntity<ListAdminPaymentsResponse> responseEntity =
        paymentController.listAllAdminScheduledPayments(TEST_ID, notAdminFromCommunity,
            TEST_PAGEABLE);

    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(communityService).getCommunityDetailsByIdWithAdmins(TEST_ID);
    verifyNoInteractions(paymentService);
  }

  /**
   * tests whether a `RuntimeException` is thrown when the community with the given ID
   * does not exist.
   */
  @Test
  void shouldThrowExceptionWhenCommunityNotExists() {
    //given
    String expectedExceptionMessage = "Community with given id not exists: " + TEST_ID;

    given(communityService.getCommunityDetailsByIdWithAdmins(TEST_ID))
        .willReturn(Optional.empty());

    //when
    final RuntimeException runtimeException = assertThrows(
        RuntimeException.class,
        () -> paymentController.listAllAdminScheduledPayments(TEST_ID, TEST_ADMIN_ID,
            TEST_PAGEABLE)
    );

    //then
    assertEquals(expectedExceptionMessage, runtimeException.getMessage());
    verify(communityService).getCommunityDetailsByIdWithAdmins(TEST_ID);
    verifyNoInteractions(paymentService);
    verifyNoInteractions(paymentApiMapper);
  }
}
