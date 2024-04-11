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

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

/**
 * has a set of attributes (id, amenityId, name, description, price, communityId) and
 * is annotated with various constructors, builders, and data annotations.
 * Fields:
 * 	- id (Long): in the AmenityDto class represents a unique identifier for each amenity.
 * 	- amenityId (String): represents a unique identifier for a specific amenity within
 * a community.
 * 	- name (String): in the AmenityDto class is used to store the name of an amenity.
 * 	- description (String): in the AmenityDto class represents a string value that
 * provides a brief summary or description of the amenity.
 * 	- price (BigDecimal): represents a decimal value representing the cost of an amenity.
 * 	- communityId (String): represents a unique identifier for a specific community
 * or neighborhood associated with the amenity.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Data
public class AmenityDto {
  private Long id;
  private String amenityId;
  private String name;
  private String description;
  private BigDecimal price;
  private String communityId;
}
