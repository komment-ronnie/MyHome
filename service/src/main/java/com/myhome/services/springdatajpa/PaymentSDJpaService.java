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

import com.myhome.controllers.dto.PaymentDto;
import com.myhome.controllers.dto.mapper.PaymentMapper;
import com.myhome.domain.HouseMember;
import com.myhome.domain.Payment;
import com.myhome.domain.User;
import com.myhome.repositories.HouseMemberRepository;
import com.myhome.repositories.PaymentRepository;
import com.myhome.repositories.UserRepository;
import com.myhome.services.PaymentService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implements {@link PaymentService} and uses Spring Data JPA Repository to do its work
 */
/**
 * is an implementation of the PaymentService interface that provides various methods
 * for managing payments in a Java Persistent Architecture (JPA) environment. The
 * class performs functions such as scheduling payments, retrieving payment details,
 * getting house members, and getting payments by member or administrator. It utilizes
 * dependencies on other repositories and mappers to perform these operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentSDJpaService implements PaymentService {
  private final PaymentRepository paymentRepository;
  private final UserRepository adminRepository;
  private final PaymentMapper paymentMapper;
  private final HouseMemberRepository houseMemberRepository;

  /**
   * generates a payment ID and creates a new payment in the repository using the
   * provided request.
   * 
   * @param request payment details required for scheduling a payment.
   * 
   * 	- `generatePaymentId`: generates a unique payment ID for the request.
   * 	- `createPaymentInRepository`: creates a new payment instance in the repository.
   * 
   * @returns a payment DTO containing the generated payment ID and created payment instance.
   * 
   * 	- `PaymentDto`: This is the type of the object that is being scheduled for payment.
   * 	- `generatePaymentId(request)`: This method generates a unique identifier for the
   * payment request.
   * 	- `createPaymentInRepository(request)`: This method creates a new payment object
   * in the repository, which represents the scheduled payment.
   */
  @Override
  public PaymentDto schedulePayment(PaymentDto request) {
    generatePaymentId(request);
    return createPaymentInRepository(request);
  }

  /**
   * retrieves payment details from a repository and maps them to a `PaymentDto` object
   * using a provided mapper.
   * 
   * @param paymentId identifier of the payment for which the user seeks to retrieve details.
   * 
   * @returns an Optional<PaymentDto> containing the payment details of the specified
   * payment ID.
   * 
   * 	- `Optional<PaymentDto>`: The output is an optional object of type `PaymentDto`,
   * indicating that the function may return `None` if no payment details are found for
   * the provided payment ID.
   * 	- `paymentRepository.findByPaymentId(paymentId)`: This method calls the
   * `paymentRepository` to retrieve a `List` of `Payment` objects based on the provided
   * `paymentId`.
   * 	- `map(paymentMapper::paymentToPaymentDto)`: This line maps each `Payment` object
   * in the `List` to an instance of `PaymentDto`, using the `paymentMapper` function.
   */
  @Override
  public Optional<PaymentDto> getPaymentDetails(String paymentId) {
    return paymentRepository.findByPaymentId(paymentId)
        .map(paymentMapper::paymentToPaymentDto);
  }

  /**
   * retrieves a House Member entity from the repository based on the given member ID.
   * 
   * @param memberId unique identifier of a member within the house, which is used to
   * retrieve the corresponding HouseMember object from the repository.
   * 
   * @returns an Optional object containing a HouseMember object if found, otherwise None.
   * 
   * 	- The output is an `Optional` object, which means it may contain some information
   * about the `HouseMember` or be empty if no match is found.
   * 	- The `findByMemberId` method of the `houseMemberRepository` returns a `List` of
   * `HouseMember` objects that match the given `memberId`.
   * 	- If multiple matches are found, the `Optional` object will contain a single
   * `HouseMember` object representing the first match.
   * 	- If no match is found, the `Optional` object will be empty.
   */
  @Override
  public Optional<HouseMember> getHouseMember(String memberId) {
    return houseMemberRepository.findByMemberId(memberId);
  }

  /**
   * retrieves a set of payments associated with a given member ID from the payment repository.
   * 
   * @param memberId member whose payments are to be retrieved.
   * 
   * @returns a set of Payment objects that match the specified member ID.
   * 
   * 	- `Set<Payment>`: The function returns a set of payments that match the given
   * member ID.
   * 	- `paymentRepository`: This is the repository responsible for storing and retrieving
   * payment objects.
   * 	- `findAll(Example)`: This method queries the database using an example object
   * to retrieve all payments that match the given criteria.
   * 	- `ExampleMatcher`: This class defines a set of matchers that are used to filter
   * the results based on the member ID.
   * 	- `ignoringMatcher`: This is an example matcher that ignores the "admin" property
   * when matching payments.
   * 
   * The output of the function is a set of payments that have been retrieved from the
   * database using the given member ID as a criteria.
   */
  @Override
  public Set<Payment> getPaymentsByMember(String memberId) {
    ExampleMatcher ignoringMatcher = ExampleMatcher.matchingAll()
        .withMatcher("memberId",
            ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase())
        .withIgnorePaths("paymentId", "charge", "type", "description", "recurring", "dueDate",
            "admin");

    Example<Payment> paymentExample =
        Example.of(new Payment(null, null, null, null, false, null, null,
                new HouseMember().withMemberId(memberId)),
            ignoringMatcher);

    return new HashSet<>(paymentRepository.findAll(paymentExample));
  }

  /**
   * retrieves a paginated list of payments from the repository based on the administrator's
   * ID using Example Matcher to ignore irrelevant fields.
   * 
   * @param adminId user ID of the administrator whose payments are to be retrieved.
   * 
   * @param pageable pagination information for the payment data, allowing the function
   * to retrieve a specific subset of the data within the larger dataset.
   * 
   * 	- `pageable`: It is an instance of the `Pageable` interface, which allows for
   * navigating through a collection of objects using a set of predefined methods.
   * 	- `getNumberOfElements()`: This method returns the total number of elements in
   * the collection.
   * 	- `getPageIndex()`: This method returns the current page index, which is used to
   * determine the position of the current element in the collection.
   * 	- `getPageSize()`: This method returns the number of elements that can be displayed
   * on a single page.
   * 	- `getTotalElements()`: This method returns the total number of elements in the
   * collection, including all pages.
   * 	- `getTotalPages()`: This method returns the total number of pages that contain
   * elements from the collection.
   * 
   * @returns a page of Payment instances filtered based on the admin ID.
   * 
   * 1/ `Page<Payment>`: This represents a pageable list of payments returned by the function.
   * 2/ `payments`: This is the list of payments contained within the page.
   * 3/ `pageable`: This is the page request parameter, which specifies the page number
   * and size.
   * 4/ `adminId`: This is the ID of the admin for whom the payments are being retrieved.
   * 5/ `ExampleMatcher`: This is an object that defines the matching criteria for the
   * payments. It ignores certain fields such as "paymentId", "charge", "type",
   * "description", "recurring", "dueDate", and "memberId".
   * 6/ `paymentRepository`: This is the repository responsible for retrieving the
   * payments based on the given criteria.
   */
  @Override
  public Page<Payment> getPaymentsByAdmin(String adminId, Pageable pageable) {
    ExampleMatcher ignoringMatcher = ExampleMatcher.matchingAll()
        .withMatcher("adminId",
            ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase())
        .withIgnorePaths("paymentId", "charge", "type", "description", "recurring", "dueDate",
            "memberId");

    Example<Payment> paymentExample =
        Example.of(
            new Payment(null, null, null, null, false, null, new User().withUserId(adminId), null),
            ignoringMatcher);

    return paymentRepository.findAll(paymentExample, pageable);
  }

  /**
   * creates a new payment object by mapping a `PaymentDto` request, saves it to both
   * an admin repository and a payment repository, and returns the corresponding `PaymentDto`.
   * 
   * @param request PaymentDto object that contains the necessary information for
   * creating a new payment.
   * 
   * 1/ PaymentDto request contains the following attributes:
   * 		- `id`: The unique identifier for the payment
   * 		- `amount`: The amount of the payment in the local currency
   * 		- `currency`: The currency in which the payment is made
   * 		- `description`: A brief description of the payment
   * 		- `admin`: The administrative information of the user who made the payment
   * 2/ The function first maps the `request` to a `Payment` object using the `paymentMapper`.
   * 3/ Then, it saves the `Admin` object associated with the `Payment` object in the
   * `adminRepository`.
   * 4/ Subsequently, it saves the `Payment` object itself in the `paymentRepository`.
   * 5/ Finally, the function maps the saved `Payment` object back to a `PaymentDto`
   * object using the `paymentMapper`, and returns it.
   * 
   * @returns a `PaymentDto` object containing the saved `Payment` data.
   * 
   * 	- The payment object is created by mapping the `PaymentDto` request to a `Payment`
   * object using the `paymentMapper`.
   * 	- The `admin` property of the `Payment` object is saved in the `adminRepository`.
   * 	- The `Payment` object itself is saved in the `paymentRepository`.
   * 
   * The output of the function is a mapped `PaymentDto` object, which represents the
   * created payment.
   */
  private PaymentDto createPaymentInRepository(PaymentDto request) {
    Payment payment = paymentMapper.paymentDtoToPayment(request);

    adminRepository.save(payment.getAdmin());
    paymentRepository.save(payment);

    return paymentMapper.paymentToPaymentDto(payment);
  }

  /**
   * generates a unique payment ID for a given `PaymentDto` request using the
   * `UUID.randomUUID()` method and converts it to a string.
   * 
   * @param request PaymentDto object that contains information about the payment, and
   * its `setPaymentId()` method sets the payment ID to a unique randomly generated
   * UUID string.
   * 
   * 	- `UUID`: A random UUID generator is used to generate a unique payment ID.
   * 	- `request.setPaymentId()`: Sets the `paymentId` property of the `request` object
   * to a randomly generated string.
   */
  private void generatePaymentId(PaymentDto request) {
    request.setPaymentId(UUID.randomUUID().toString());
  }
}
