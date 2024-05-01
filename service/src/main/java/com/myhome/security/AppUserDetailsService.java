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

package com.myhome.security;

import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.dto.mapper.UserMapper;
import com.myhome.repositories.UserRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom {@link UserDetailsService} catering to the need of service logic.
 */
/**
 * is an implementation of the UserDetailsService interface, providing functionality
 * for loading user details by username and mapping them to a UserDto format. The
 * class includes methods for retrieving a user object from the repository based on
 * a given username, and then mapping it to a UserDto format using the `userMapper`
 * method.
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  /**
   * retrieves a user from the repository based on their username and returns a
   * `UserDetails` object with the user's email, encrypted password, and other metadata.
   * 
   * @param username username for which the user details are to be loaded.
   * 
   * @returns a `UserDetails` object containing the user's email, encrypted password,
   * and various other attributes.
   * 
   * 	- `email`: The email address of the user.
   * 	- `encryptedPassword`: The encrypted password for the user.
   * 	- `active`: A boolean indicating whether the user is active (true) or inactive (false).
   * 	- `accountNonExpired`: A boolean indicating whether the user's account is non-expired
   * (true) or expired (false).
   * 	- `credentialsNonExpired`: A boolean indicating whether the user's credentials
   * are non-expired (true) or expired (false).
   * 	- `accountDisabled`: A boolean indicating whether the user's account is disabled
   * (true) or enabled (false).
   * 	- `userDetailsList`: An empty list.
   */
  @Override public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {

    com.myhome.domain.User user = userRepository.findByEmail(username);
    if (user == null) {
      throw new UsernameNotFoundException(username);
    }

    return new User(user.getEmail(),
        user.getEncryptedPassword(),
        true,
        true,
        true,
        true,
        Collections.emptyList());
  }

  /**
   * retrieves a `User` object from the repository based on the given username and maps
   * it to a `UserDto` object using the provided mapper.
   * 
   * @param username username for which the user details are being retrieved.
   * 
   * @returns a `UserDto` object containing the details of the user with the provided
   * username.
   * 
   * The function returns a `UserDto` object, which represents a user entity in a more
   * accessible and usable format for client-side applications. The `UserDto` class
   * contains attributes such as `id`, `username`, `email`, `firstName`, `lastName`,
   * and `password`, which are mapped from the corresponding fields in the `User` entity.
   * 
   * The function first retrieves a `User` object from the `userRepository` using the
   * `findByEmail` method, passing in the `username` parameter as a search criterion.
   * If the user is not found, a `UsernameNotFoundException` is thrown.
   * 
   * Once the user is retrieved, the `userMapper` class maps the user entity to a
   * `UserDto` object, which is then returned as the function output.
   */
  public UserDto getUserDetailsByUsername(String username) {
    com.myhome.domain.User user = userRepository.findByEmail(username);
    if (user == null) {
      throw new UsernameNotFoundException(username);
    }
    return userMapper.userToUserDto(user);
  }
}
