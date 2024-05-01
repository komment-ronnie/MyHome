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
 * is an implementation of the UserDetailsService interface, providing methods for
 * loading and retrieving user details from a repository using a mapper. The class
 * loads a user by their username and returns a `User` object with relevant details,
 * and also provides a method to retrieve a user's details from the repository and
 * map them to a `UserDto` object using a mapper.
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  /**
   * loads a user by their username and returns a `UserDetails` object containing the
   * user's email address, encrypted password, and other attributes such as role and privileges.
   * 
   * @param username username for which the user details are to be loaded.
   * 
   * @returns a `UserDetails` object containing user information.
   * 
   * 	- `email`: The email address of the user.
   * 	- `encryptedPassword`: The encrypted password for the user.
   * 	- `active`: A boolean indicating whether the user is active (true) or inactive (false).
   * 	- `accountNonExpired`: A boolean indicating whether the user's account has not
   * expired (true) or has expired (false).
   * 	- `accountNonLocked`: A boolean indicating whether the user's account is unlocked
   * (true) or locked (false).
   * 	- `credentialsNonExpired`: A boolean indicating whether the user's credentials
   * have not expired (true) or have expired (false).
   * 	- `tokenNonExpired`: A boolean indicating whether the user's token has not expired
   * (true) or has expired (false).
   * 
   * No summary is provided at the end of the explanation.
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
   * retrieves a `User` object from the repository based on the given username, maps
   * it to a `UserDto`, and returns the resulting `UserDto`.
   * 
   * @param username email address of the user for which details are being retrieved.
   * 
   * @returns a `UserDto` object representing the user with the specified username.
   * 
   * The function returns a `UserDto` object, which represents a user in a specific
   * format suitable for further processing or display. The `UserDto` object contains
   * information about the user, such as their email and name.
   * 
   * The function takes a `username` parameter, which is used to retrieve the corresponding
   * user from the `userRepository`. If the user is not found, a `UsernameNotFoundException`
   * is thrown.
   * 
   * The function calls the `userMapper` method to map the retrieved user object to the
   * `UserDto` format. This mapping involves converting the original user data into a
   * structured format that can be easily processed or displayed.
   */
  public UserDto getUserDetailsByUsername(String username) {
    com.myhome.domain.User user = userRepository.findByEmail(username);
    if (user == null) {
      throw new UsernameNotFoundException(username);
    }
    return userMapper.userToUserDto(user);
  }
}
