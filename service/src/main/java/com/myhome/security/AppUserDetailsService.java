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
 * is a custom implementation of Spring Security's UserDetailsService interface. It
 * retrieves user details from a repository and maps them to a UserDto object using
 * a mapper. The loadUserByUsername method loads a user by username and returns a
 * UserDetails object, while the getUserDetailsByUsername method returns a UserDto
 * object for the given username.
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  /**
   * loads a user by their username and returns a `User` object with relevant details.
   * 
   * @param username username for which the user details are being loaded.
   * 
   * @returns a `UserDetails` object representing a user with various attributes and
   * authentication capabilities.
   * 
   * 	- The first element is an instance of `com.myhome.domain.User`.
   * 	- The `email` field of the user object is the username passed in the function.
   * 	- The `encryptedPassword` field represents the encrypted password for the user.
   * 	- The fifth element, `true`, indicates that the user is activated.
   * 	- The sixth element, `true`, indicates that the user is confirmed.
   * 	- The seventh element, `true`, indicates that the user is locked.
   * 	- The eighth element, `Collections.emptyList()`, represents an empty list of roles
   * associated with the user.
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
   * retrieves a user's details from the repository and maps them to a `UserDto` object
   * using a mapper.
   * 
   * @param username username for which the user details are to be retrieved.
   * 
   * @returns a `UserDto` object containing the details of the user with the specified
   * username.
   * 
   * 	- The function returns a `UserDto` object representing the user details.
   * 	- The `user` variable is of type `com.myhome.domain.User`, which contains information
   * about the user, such as their email and name.
   * 	- The `userMapper` is responsible for mapping the `User` object to the `UserDto`
   * object, which provides a more convenient and consumable representation of the user
   * data.
   */
  public UserDto getUserDetailsByUsername(String username) {
    com.myhome.domain.User user = userRepository.findByEmail(username);
    if (user == null) {
      throw new UsernameNotFoundException(username);
    }
    return userMapper.userToUserDto(user);
  }
}
