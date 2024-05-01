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

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

/**
 * represents a house within a community and contains relationships with HouseMembers
 * and Amenities.
 * Fields:
 * 	- community (Community): in the CommunityHouse class represents an association
 * between a CommunityHouse entity and a single Community instance.
 * 	- name (String): in the CommunityHouse class represents a unique identifier for
 * a specific community house within a community.
 * 	- houseId (String): represents a unique identifier for each CommunityHouse instance
 * in the system.
 * 	- houseMembers (Set<HouseMember>): in the CommunityHouse class represents a set
 * of objects that are related to a particular community house through a many-to-one
 * relationship with the HouseMember entity.
 * 	- amenities (Set<Amenity>): in the CommunityHouse class contains a set of Amenity
 * objects related to the house.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"houseId", "name"}, callSuper = false)
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "CommunityHouse.community",
        attributeNodes = {
            @NamedAttributeNode("community"),
        }
    ),
    @NamedEntityGraph(
        name = "CommunityHouse.houseMembers",
        attributeNodes = {
            @NamedAttributeNode("houseMembers"),
        }
    )
})
public class CommunityHouse extends BaseEntity {
  @With
  @ManyToOne(fetch = FetchType.LAZY)
  private Community community;
  @With
  @Column(nullable = false)
  private String name;
  @With
  @Column(unique = true, nullable = false)
  private String houseId;
  @OneToMany(fetch = FetchType.LAZY)
  private Set<HouseMember> houseMembers = new HashSet<>();
  @OneToMany(fetch = FetchType.LAZY)
  private Set<Amenity> amenities = new HashSet<>();
}
