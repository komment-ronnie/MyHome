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
package com.myhome.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity identifying a payment in the service. This could be an electricity bill, house rent, water
 * charge etc
 */
/**
 * represents a payment made by a user to their housing provider for various expenses,
 * including electricity bills, house rent, and water charges.
 * Fields:
 * 	- paymentId (String): represents a unique identifier for each payment transaction
 * in the service.
 * 	- charge (BigDecimal): in the Payment entity represents an amount of money to be
 * paid by a user or member of a house, depending on the value assigned to the admin
 * and member fields.
 * 	- type (String): in the Payment entity represents a category of payment, such as
 * "electricity bill" or "house rent".
 * 	- description (String): in the Payment class represents a brief narrative or
 * summary of the payment's purpose or context.
 * 	- recurring (boolean): in the Payment entity indicates whether a payment is
 * recurring or not, with a value of true for recurring and false for non-recurring
 * payments.
 * 	- dueDate (LocalDate): represents the date when the payment is expected to be made.
 * 	- admin (User): in the Payment entity represents a user who manages or oversees
 * payments made by the HouseMember associated with the payment.
 * 	- member (HouseMember): in the Payment class represents an association between a
 * payment and a member of a house.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class Payment extends BaseEntity {
  @Column(unique = true, nullable = false)
  private String paymentId;
  @Column(nullable = false)
  private BigDecimal charge;
  @Column(nullable = false)
  private String type;
  @Column(nullable = false)
  private String description;
  @Column(nullable = false)
  private boolean recurring;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dueDate;
  @ManyToOne(fetch = FetchType.LAZY)
  private User admin;
  @ManyToOne(fetch = FetchType.LAZY)
  private HouseMember member;
}
