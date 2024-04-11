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

package com.myhome.controllers.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * in the Java file is an immutable response class representing a payment schedule
 * with various attributes, including payment ID, charge amount, and due date.
 * Fields:
 * 	- paymentId (String): represents a unique identifier for a scheduled payment.
 * 	- charge (BigDecimal): represents a monetary value.
 * 	- type (String): represents a string value indicating the type of payment being
 * scheduled, such as "one-time" or "recurring".
 * 	- description (String): represents a brief textual explanation of the payment's
 * purpose or context.
 * 	- recurring (boolean): in the SchedulePaymentResponse class indicates whether the
 * payment is recurring or not.
 * 	- dueDate (String): represents the date on which a payment is due.
 * 	- adminId (String): represents an identifier for the administrator who manages
 * the payment schedule.
 * 	- memberId (String): represents a unique identifier for a specific member within
 * the context of the SchedulePaymentResponse class.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SchedulePaymentResponse {
  private String paymentId;
  private BigDecimal charge;
  private String type;
  private String description;
  private boolean recurring;
  private String dueDate;
  private String adminId;
  private String memberId;
}
