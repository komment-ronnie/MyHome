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

package com.myhome.controllers.dto;

import com.myhome.model.HouseMemberDto;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * represents a payment record with relevant details such as payment ID, charge amount,
 * payment type, description, recurrence status, due date, and user and member information.
 * Fields:
 * 	- paymentId (String): represents a unique identifier for a payment transaction
 * within the PaymentDto class.
 * 	- charge (BigDecimal): in the PaymentDto class represents a monetary value,
 * specifically a BigDecimal object, which is used to represent a payment amount.
 * 	- type (String): in the PaymentDto class represents a categorical label or category
 * for the payment, such as "rent", "utility bill", or "invoice".
 * 	- description (String): in the PaymentDto class represents a brief textual
 * description of the payment.
 * 	- recurring (boolean): indicates whether the payment is a recurring one.
 * 	- dueDate (String): in the PaymentDto class represents the date on which a payment
 * is due or has to be made.
 * 	- admin (UserDto): represents an entity of type `UserDto` in the PaymentDto class.
 * 	- member (HouseMemberDto): in the PaymentDto class represents an association with
 * a HouseMemberDto object containing information about a member of a household.
 */
@Builder
@Getter
@Setter
public class PaymentDto {
  private String paymentId;
  private BigDecimal charge;
  private String type;
  private String description;
  private boolean recurring;
  private String dueDate;
  private UserDto admin;
  private HouseMemberDto member;
}
