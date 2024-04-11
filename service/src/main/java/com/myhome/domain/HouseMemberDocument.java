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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * is a Java entity class with two fields: documentFilename and documentContent, both
 * of which are persisted to the database using JPA (Java Persistence API).
 * Fields:
 * 	- documentFilename (String): in the HouseMemberDocument class represents a unique
 * name for a file containing document content, which is stored as a binary large
 * object (Lob) in the byte array format.
 * 	- documentContent (byte[]): in the HouseMemberDocument class is an array of bytes
 * with a default size of 0, indicating that it may contain any type of data, including
 * images, videos, or documents.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = {"documentFilename"}, callSuper = false)
public class HouseMemberDocument extends BaseEntity {

  @Column(unique = true)
  private String documentFilename;

  @Lob
  @Column
  private byte[] documentContent = new byte[0];
}
