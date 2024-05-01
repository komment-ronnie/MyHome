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

package com.myhome.controllers.request;

import com.myhome.model.SchedulePaymentRequest;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class is used to enrich the normal SchedulePaymentRequest with details relating to the admin
 * and house member in order to map to the User and HouseMember fields of payment successfully. By
 * doing this, you can avoid having to specify all the extra details in the request and just use the
 * IDs to get the data to enrich this request
 */
/**
 * extends the SchedulePaymentRequest and provides additional fields to enrich the
 * request with details relating to an admin and a house member for successful mapping
 * to user and house member fields in payment.
 * Fields:
 * 	- adminEntityId (Long): represents an identifier for an administrative entity
 * associated with the payment request.
 * 	- adminName (String): represents the name of an administrative user associated
 * with the payment request.
 * 	- adminEmail (String): represents an email address associated with an administrative
 * entity in the system.
 * 	- adminEncryptedPassword (String): represents an encrypted password for an
 * administrative user associated with the payment request.
 * 	- adminCommunityIds (Set<String>): represents an unordered set of strings that
 * identify communities to which the admin and house member belong.
 * 	- memberEntityId (Long): represents an identifier for a member entity associated
 * with the payment request.
 * 	- houseMemberDocumentName (String): represents the name of a document used to
 * identify the member in the house.
 * 	- houseMemberName (String): in the EnrichedSchedulePaymentRequest class represents
 * the name of the member of a house to whom the payment request relates.
 * 	- houseMemberHouseID (String): in the EnrichedSchedulePaymentRequest class
 * represents a unique identifier of the member's house within the system.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class EnrichedSchedulePaymentRequest extends SchedulePaymentRequest {
  private Long adminEntityId;
  private String adminName;
  private String adminEmail;
  private String adminEncryptedPassword;
  private Set<String> adminCommunityIds;
  private Long memberEntityId;
  private String houseMemberDocumentName;
  private String houseMemberName;
  private String houseMemberHouseID;

  public EnrichedSchedulePaymentRequest(String type, String description, boolean recurring,
      BigDecimal charge, String dueDate, String adminId, Long adminEntityId, String adminName,
      String adminEmail, String adminEncryptedPassword, Set<String> adminCommunityIds,
      String memberId, Long memberEntityId, String houseMemberDocumentName, String houseMemberName,
      String houseMemberHouseID) {

    super.type(type).description(description).recurring(recurring).charge(charge).dueDate(dueDate).adminId(adminId).memberId(memberId);

    this.adminName = adminName;
    this.adminEmail = adminEmail;
    this.adminEncryptedPassword = adminEncryptedPassword;
    this.adminCommunityIds = adminCommunityIds;
    this.adminEntityId = adminEntityId;
    this.memberEntityId = memberEntityId;
    this.houseMemberDocumentName = houseMemberDocumentName;
    this.houseMemberName = houseMemberName;
    this.houseMemberHouseID = houseMemberHouseID;
  }
}
