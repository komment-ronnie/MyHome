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
 * is responsible for managing payments in a Java-based application. It provides
 * methods for retrieving and creating payments, as well as mapping between payment
 * data and the corresponding `PaymentDto` objects. The service uses JPA (Java
 * Persistence API) to interact with the database and perform CRUD (Create, Read,
 * Update, Delete) operations on payments. Additionally, it provides a method for
 * generating unique payment IDs.
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
   * generates a payment ID and creates a payment record in the repository.
   * 
   * @param request PaymentDto object containing information related to payment scheduling,
   * which is used to generate a unique payment ID and create a new payment record in
   * the repository.
   * 
   * 	- `generatePaymentId`: The first step is to generate a unique payment ID for the
   * scheduled payment.
   * 	- `createPaymentInRepository`: The second step is to create a new payment instance
   * in the repository using the provided details from `request`.
   * 
   * @returns a payment DTO object containing the scheduled payment details.
   * 
   * 	- `generatePaymentId`: This method generates a unique payment ID for the scheduled
   * payment.
   * 	- `createPaymentInRepository`: This method creates a new payment object in the
   * repository, where the payment details are stored.
   * 
   * The output of the function is a `PaymentDto` object that contains the generated
   * payment ID and the created payment object in the repository.
   */
  @Override
  public PaymentDto schedulePayment(PaymentDto request) {
    generatePaymentId(request);
    return createPaymentInRepository(request);
  }

  /**
   * retrieves a payment's details from the repository, maps them to a `PaymentDto`
   * object using a mapper, and returns an optional instance of `PaymentDto`.
   * 
   * @param paymentId identifier of a payment, which is used to retrieve the corresponding
   * payment details from the repository.
   * 
   * @returns an Optional<PaymentDto> containing the payment details for the specified
   * payment ID.
   * 
   * 	- `Optional<PaymentDto>` represents an optional payment details object, which
   * means that if no payment details are found, the function will return an empty Optional.
   * 	- `paymentRepository.findByPaymentId(paymentId)` is a method that retrieves a
   * Payment object based on its payment ID.
   * 	- `map(paymentMapper::paymentToPaymentDto)` is a method that maps the retrieved
   * Payment object to a PaymentDto object, which contains additional attributes and
   * properties not present in the Payment object.
   */
  @Override
  public Optional<PaymentDto> getPaymentDetails(String paymentId) {
    return paymentRepository.findByPaymentId(paymentId)
        .map(paymentMapper::paymentToPaymentDto);
  }

  /**
   * retrieves a `HouseMember` object from the repository based on the input `memberId`.
   * 
   * @param memberId ID of the House Member to be retrieved from the repository.
   * 
   * @returns an optional instance of `HouseMember`.
   * 
   * Optional<HouseMember>: This is an instance of the Optional class, which represents
   * either an existing HouseMember or none (absent). The presence of an element in the
   * Optional indicates whether a HouseMember exists with the provided memberId.
   * HouseMember: This class represents a member of a house, containing properties such
   * as the member's ID, name, and address.
   */
  @Override
  public Optional<HouseMember> getHouseMember(String memberId) {
    return houseMemberRepository.findByMemberId(memberId);
  }

  /**
   * retrieves a set of payments associated with a given member ID from the payment repository.
   * 
   * @param memberId member ID of the payments to be retrieved.
   * 
   * @returns a set of payment objects retrieved from the database based on the member
   * ID provided.
   * 
   * 	- The Set<Payment> is constructed by calling the `findAll()` method on the `paymentRepository`.
   * 	- The method takes an `Example<Payment>` as its argument, which is created using
   * the `Example.of()` method and passed to the `ignoringMatcher` method.
   * 	- The `ignoringMatcher` method returns a new `ExampleMatcher` instance that ignores
   * certain properties of the Payment objects. In this case, it ignores the `paymentId`,
   * `charge`, `type`, `description`, `recurring`, `dueDate`, and `admin` properties.
   * 	- The resulting `Example<Payment>` is then passed to the `findAll()` method, which
   * returns a Set of Payment objects that match the specified example.
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
   * retrieves a page of payments for an administrator based on their ID, using a custom
   * matcher to ignore certain fields.
   * 
   * @param adminId user ID of the administrator for whom the payments are being retrieved.
   * 
   * @param pageable pagination information for the query, allowing the function to
   * retrieve a subset of the payments matching the specified criteria in a specific
   * page or set of pages.
   * 
   * 	- `Pageable`: This is an interface in Java that defines a page-oriented iteration
   * over a collection. It has several methods to define how to iterate over the
   * collection based on various criteria such as size, index, and sorted order.
   * 	- `size()`: This method returns the number of elements in the collection.
   * 	- `getNumber()`: This method returns the index of the current page in the iteration.
   * 	- `isLast()`: This method returns a boolean indicating whether the current page
   * is the last page in the iteration.
   * 	- `getSort()`: This method returns the sorting order of the pages in the iteration.
   * 
   * In summary, `pageable` is an interface that provides methods to iterate over a
   * collection based on various criteria, and it is used in the `getPaymentsByAdmin`
   * function to define how to iterate over the payment data.
   * 
   * @returns a page of Payment objects filtered based on the admin ID and ignoring
   * certain fields.
   * 
   * 	- `Page<Payment>`: This is a page of payments, where each payment is represented
   * by an instance of the `Payment` class.
   * 	- `payments`: This is a list of payments that match the specified admin ID.
   * 	- `pageable`: This is the page request, which contains information about the
   * number of payments to display on each page and the total number of payments in the
   * result set.
   * 
   * The `ExampleMatcher` used in the function is responsible for defining the matching
   * criteria for the payments. It ignores certain fields in the `Payment` class, such
   * as `paymentId`, `charge`, `type`, `description`, `recurring`, and `dueDate`. The
   * remaining fields are matched using a combination of exact matches and startsWith()
   * method.
   * 
   * The `paymentRepository` is responsible for fetching the payments from the database
   * based on the matching criteria defined by the `ExampleMatcher`.
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
   * creates a new payment entity and saves it to the appropriate repositories, returning
   * the corresponding payment DTO.
   * 
   * @param request PaymentDto object containing the necessary information for creating
   * a payment instance, which is then converted into a corresponding payment entity
   * and saved in the repository.
   * 
   * 	- `paymentMapper`: This is an instance of `PaymentMapper`, which maps between a
   * payment DTO and a payment entity.
   * 	- `paymentRepository`: This is an instance of `PaymentRepository`, which manages
   * payment entities.
   * 	- `adminRepository`: This is an instance of `AdminRepository`, which manages admin
   * entities.
   * 
   * The function takes in a `PaymentDto` object as input, and performs the following
   * operations:
   * 
   * 1/ Deserializes the `request` into a `Payment` entity using the `paymentMapper`.
   * 2/ Saves the admin entity associated with the payment entity to the `adminRepository`.
   * 3/ Saves the payment entity itself to the `paymentRepository`.
   * 4/ Maps the saved payment entity back to a `PaymentDto` object using the
   * `paymentMapper`, and returns it as output.
   * 
   * @returns a `PaymentDto` object representing the created payment.
   * 
   * 	- The PaymentDto object is converted into a Payment object using the
   * `paymentMapper.paymentDtoToPayment()` method.
   * 	- The admin and payment objects are saved in the repository using the
   * `adminRepository.save()` and `paymentRepository.save()` methods, respectively.
   * 	- The returned output is the Payment object that has been converted back to a
   * PaymentDto format using the `paymentMapper.paymentToPaymentDto()` method.
   */
  private PaymentDto createPaymentInRepository(PaymentDto request) {
    Payment payment = paymentMapper.paymentDtoToPayment(request);

    adminRepository.save(payment.getAdmin());
    paymentRepository.save(payment);

    return paymentMapper.paymentToPaymentDto(payment);
  }

  /**
   * generates a unique UUID string as the payment ID for a given `PaymentDto` request.
   * 
   * @param request `PaymentDto` object that contains information about the payment,
   * and it is used to set the `paymentId` field of the object to a randomly generated
   * UUID string.
   * 
   * 	- `request`: A `PaymentDto` object that contains the required information for
   * generating a payment ID.
   */
  private void generatePaymentId(PaymentDto request) {
    request.setPaymentId(UUID.randomUUID().toString());
  }
}
