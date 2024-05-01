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
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

/**
 * Entity identifying a valid user in the service.
 */
/**
 * represents a valid user in the service with various associations to other entities,
 * including users, houses, and amenities.
 * Fields:
 * 	- admins (Set<User>): represents a set of user objects related to the community
 * through their association with the community as admins.
 * 	- houses (Set<CommunityHouse>): in the Community class represents a set of House
 * objects related to the community through their association with the community as
 * houses, as defined in the Java code provided.
 * 	- name (String): in the Community entity represents a unique identifier for a
 * community within a specific district.
 * 	- communityId (String): in the Community class represents a unique identifier for
 * a specific community within the application's domain.
 * 	- district (String): in the Community class represents a geographical area where
 * the community is located.
 * 	- amenities (Set<Amenity>): in the Community class represents a set of Amenity
 * objects related to the community through the mappedBy attribute in the OneToMany
 * annotation.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false, of = {"communityId", "name", "district"})
@Entity
@With
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "Community.amenities",
        attributeNodes = {
            @NamedAttributeNode("amenities"),
        }
    ),
    @NamedEntityGraph(
        name = "Community.admins",
        attributeNodes = {
            @NamedAttributeNode("admins"),
        }
    ),
    @NamedEntityGraph(
        name = "Community.houses",
        attributeNodes = {
            @NamedAttributeNode("houses"),
        }
    )
})
public class Community extends BaseEntity {
  @ToString.Exclude
  @ManyToMany(fetch = FetchType.LAZY)
  private Set<User> admins = new HashSet<>();
  @ToString.Exclude
  @OneToMany(fetch = FetchType.LAZY)
  private Set<CommunityHouse> houses = new HashSet<>();
  @Column(nullable = false)
  private String name;
  @Column(unique = true, nullable = false)
  private String communityId;
  @Column(nullable = false)
  private String district;
  @ToString.Exclude
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "community", orphanRemoval = true)
  private Set<Amenity> amenities = new HashSet<>();
}
