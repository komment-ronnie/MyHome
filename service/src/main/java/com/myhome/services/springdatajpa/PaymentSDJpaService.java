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
 * is responsible for handling payment related operations in the application. It
 * provides methods to create a new payment entity and save it to the appropriate
 * repositories, as well as generate a unique UUID string as the payment ID for a
 * given `PaymentDto` request. Additionally, it defines an interface `pageable` that
 * is used in the `getPaymentsByAdmin` method to iterate over the payment data based
 * on various criteria.
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
   * generates a payment ID and creates a new payment record in the repository using
   * the provided request details.
   * 
   * @param request payment details to be scheduled, which includes the amount and other
   * relevant information.
   * 
   * 	- `generatePaymentId`: A method that generates a unique payment ID for the request.
   * 	- `createPaymentInRepository`: A method that creates a new payment entity in the
   * repository based on the provided `request`.
   * 
   * @returns a payment DTO containing the scheduled payment details.
   * 
   * 	- `PaymentDto`: This is the type of the output parameter, which represents a
   * payment request.
   * 	- `generatePaymentId(request)`: This is a method call that generates a unique
   * identifier for the payment request.
   * 	- `createPaymentInRepository(request)`: This is a method call that creates a new
   * payment record in the repository. The exact properties of the payment record depend
   * on the implementation of the `createPaymentInRepository` method, but it typically
   * includes information such as the payment amount, payment date, and payment status.
   */
  @Override
  public PaymentDto schedulePayment(PaymentDto request) {
    generatePaymentId(request);
    return createPaymentInRepository(request);
  }

  /**
   * retrieves a `PaymentDto` object from the payment repository based on the provided
   * `paymentId`. It maps the retrieved payment data to a `PaymentDto` object using the
   * `paymentMapper` function.
   * 
   * @param paymentId identifier of a payment to be retrieved from the repository.
   * 
   * @returns an Optional<PaymentDto> containing the payment details of the specified
   * payment ID.
   * 
   * 	- `Optional<PaymentDto>` represents an optional object of type `PaymentDto`. This
   * indicates that the function may return `None` if no payment details are found for
   * the given payment ID.
   * 	- `paymentRepository.findByPaymentId(paymentId)` is a call to the repository's
   * `findByPaymentId` method, which retrieves a `Payment` object based on the provided
   * payment ID.
   * 	- `map(paymentMapper::paymentToPaymentDto)` applies a mapping function to the
   * retrieved `Payment` object, transforming it into an instance of `PaymentDto`.
   */
  @Override
  public Optional<PaymentDto> getPaymentDetails(String paymentId) {
    return paymentRepository.findByPaymentId(paymentId)
        .map(paymentMapper::paymentToPaymentDto);
  }

  /**
   * retrieves a `HouseMember` object based on its `memberId`. It delegates the task
   * to the `houseMemberRepository` and returns an optional instance of `HouseMember`.
   * 
   * @param memberId unique identifier of a HouseMember that is being retrieved.
   * 
   * @returns an optional instance of `HouseMember`.
   * 
   * 	- `Optional<HouseMember>` is a type-safe wrapper class that represents an optional
   * value of type `HouseMember`. It provides a safe way to handle null or non-existent
   * values.
   * 	- `houseMemberRepository` is a database or data storage component used to retrieve
   * the `House Member` object.
   * 	- `findByMemberId(memberId)` is a method that retrieves a `House Member` object
   * based on its `memberId`.
   */
  @Override
  public Optional<HouseMember> getHouseMember(String memberId) {
    return houseMemberRepository.findByMemberId(memberId);
  }

  /**
   * queries the payment repository to retrieve a set of payments associated with a
   * specific member ID.
   * 
   * @param memberId unique identifier of the member whose payments are to be retrieved.
   * 
   * @returns a set of `Payment` objects that match the specified member ID.
   * 
   * 	- `Set<Payment>`: This is the type of the returned output, which is a set of
   * payment objects.
   * 	- `Payment`: This is the type of each element in the set, which represents a
   * payment made by a member.
   * 	- `memberId`: This is the attribute of each payment object that matches the input
   * parameter `memberId`. It represents the ID of the member who made the payment.
   * 	- `ExampleMatcher`: This is an instance of `ExampleMatcher`, which is used to
   * filter the payments based on their member ID. The matcher defines a set of matching
   * rules for the `memberId` attribute, including ignoring cases and matching only
   * elements that start with the input value.
   * 	- `paymentRepository`: This is the repository responsible for storing and retrieving
   * payment objects. It is used to find all payments that match the input parameter `memberId`.
   * 
   * Overall, the function returns a set of payments made by a specific member, filtered
   * based on their ID.
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
   * retrieves a list of payments for a specific administrator by matching on the
   * `adminId`. It ignores certain fields such as `paymentId`, `charge`, `type`, and
   * `description`. The resulting list is returned pageable.
   * 
   * @param adminId ID of the administrator who is authorized to view the payments.
   * 
   * @param pageable pagination information for the query, allowing the function to
   * retrieve a specific page of results from the database.
   * 
   * 	- `Pageable`: This interface provides methods for navigating and manipulating
   * pages of data. The `pageable` argument is used to specify the pagination settings
   * for the query.
   * 	- `pageNumber`: The current page number being queried, which determines which
   * subset of data is returned.
   * 	- `pageSize`: The number of items to be retrieved per page.
   * 	- `sort`: The field by which the data should be sorted.
   * 	- `direction`: The sort order (ascending or descending).
   * 
   * @returns a page of Payment objects filtered based on the admin ID.
   * 
   * 	- `Page<Payment>`: This is the type of the return value, which is a pageable list
   * of payments filtered by the admin ID.
   * 	- `paymentExample`: This is an example instance of the Payment entity, used to
   * match the desired fields in the database query.
   * 	- `paymentRepository`: This is the repository responsible for storing and retrieving
   * Payment entities from the database.
   * 	- `pageable`: This is a Pageable object, which provides a way to page the list
   * of payments based on the admin ID.
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
   * takes a `PaymentDto` object as input and creates a new `Payment` entity by mapping
   * the provided `PaymentDto` fields to the corresponding `Payment` fields. It then
   * saves both the `Admin` and `Payment` entities in separate repositories, and finally
   * maps the created `Payment` back to a `PaymentDto` object for return.
   * 
   * @param request PaymentDto object that contains the details of the payment to be processed.
   * 
   * 	- PaymentDto request is transformed into a Payment object by calling the
   * `paymentMapper.paymentDtoToPayment()` method.
   * 	- An admin and payment objects are created using the `adminRepository.save()` and
   * `paymentRepository.save()` methods, respectively.
   * 	- The created payment object is then transformed back into a PaymentDto object
   * using the `paymentMapper.paymentToPaymentDto()` method.
   * 
   * @returns a `PaymentDto` object representing the created payment.
   * 
   * 	- The PaymentDto object is transformed into a Payment object using the `paymentMapper`.
   * 	- The admin and payment objects are saved in the repository using the
   * `adminRepository.save()` and `paymentRepository.save()` methods, respectively.
   * 	- The PaymentDto object is transformed back into a Payment object using the `paymentMapper`.
   */
  private PaymentDto createPaymentInRepository(PaymentDto request) {
    Payment payment = paymentMapper.paymentDtoToPayment(request);

    adminRepository.save(payment.getAdmin());
    paymentRepository.save(payment);

    return paymentMapper.paymentToPaymentDto(payment);
  }

  /**
   * generates a unique payment ID for a given `PaymentDto` request using the
   * `UUID.randomUUID()` method and returns it as a string.
   * 
   * @param request PaymentDto class and is used to set the payment ID of the request.
   * 
   * 	- `UUID.randomUUID().toString()` generates a unique random ID for payment.
   */
  private void generatePaymentId(PaymentDto request) {
    request.setPaymentId(UUID.randomUUID().toString());
  }
}
