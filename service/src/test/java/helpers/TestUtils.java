package helpers;

import com.myhome.configuration.properties.mail.EmailTemplateLocalizationProperties;
import com.myhome.configuration.properties.mail.EmailTemplateProperties;
import com.myhome.configuration.properties.mail.MailProperties;
import com.myhome.controllers.dto.PaymentDto;
import com.myhome.controllers.dto.UserDto;
import com.myhome.domain.Amenity;
import com.myhome.domain.Community;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.domain.Payment;
import com.myhome.domain.User;
import com.myhome.model.HouseMemberDto;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

import static helpers.TestUtils.CommunityHouseHelpers.getTestHouses;
import static helpers.TestUtils.General.generateUniqueId;
import static helpers.TestUtils.UserHelpers.getTestUsers;

/**
 * in the provided codebase provides various methods for creating and manipulating
 * `Payment` objects. These methods include building a `PaymentDto` object with charge
 * amount, payment type, description, recurring status, due date, admin, and member
 * information, as well as generating a payment object with all fields null except
 * for the 'recurring' field which is false. Additionally, there is a method to create
 * a mock payment object with all fields null or false, except for the 'recurring' field.
 */
public class TestUtils {

  /**
   * appears to contain various methods for generating images and unique identifiers.
   * The getImageAsByteArray() method converts an image into a byte array in JPEG format,
   * while the generateUniqueId() method generates a unique identifier as a string using
   * the UUID.randomUUID() method.
   */
  public static class General {

    /**
     * converts an image represented by a `BufferedImage` object into a byte array.
     * 
     * @param height height of the image to be converted into a byte array.
     * 
     * @param width width of the resulting image.
     * 
     * @returns a byte array containing the image data in JPEG format.
     */
    public static byte[] getImageAsByteArray(int height, int width) throws IOException {
      BufferedImage documentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      try (ByteArrayOutputStream imageBytesStream = new ByteArrayOutputStream()) {
        ImageIO.write(documentImage, "jpg", imageBytesStream);
        return imageBytesStream.toByteArray();
      }
    }

    /**
     * generates a unique identifier based on a randomly generated UUID string, returning
     * it as a string.
     * 
     * @returns a unique, randomly generated string of characters.
     */
    public static String generateUniqueId() {
      return UUID.randomUUID().toString();
    }
  }

  /**
   * is a utility class that provides various methods for generating and manipulating
   * `CommunityHouse` objects. These methods include generating a set of `CommunityHouse`
   * objects with unique IDs and default names, creating a new `CommunityHouse` instance
   * with a unique ID and a default community name, and creating a new instance of
   * `CommunityHouse` with an specified ID and sets the name to "default-community-name".
   */
  public static class CommunityHouseHelpers {

    /**
     * generates `count` instances of `CommunityHouse`, each with a unique ID and default
     * name, and returns them as a set.
     * 
     * @param count maximum number of CommunityHouse objects to be generated and returned
     * by the function.
     * 
     * @returns a set of `CommunityHouse` objects generated randomly with unique IDs and
     * default names.
     * 
     * 	- The output is a `Set` data structure containing `CommunityHouse` objects.
     * 	- Each `CommunityHouse` object has a unique `houseId` attribute generated using
     * the `generateUniqueId()` method.
     * 	- Each `CommunityHouse` object has a default name attribute set to "default-house-name".
     * 	- The total number of elements in the `Set` is determined by the `count` parameter
     * passed to the function.
     */
    public static Set<CommunityHouse> getTestHouses(int count) {
      return Stream
          .generate(() -> new CommunityHouse()
              .withHouseId(generateUniqueId())
              .withName("default-house-name")
          )
          .limit(count)
          .collect(Collectors.toSet());
    }

    /**
     * creates a new `CommunityHouse` instance with a unique identifier and a default name.
     * 
     * @returns a new instance of `CommunityHouse` with a unique identifier and a default
     * name.
     * 
     * 	- The CommunityHouse object is generated using a constructor and passed back as
     * the result.
     * 	- The HouseId property of the CommunityHouse object is set to a unique identifier
     * generated by the `generateUniqueId()` method.
     * 	- The Name property of the CommunityHouse object is set to a default value of "default-community-name".
     */
    public static CommunityHouse getTestCommunityHouse() {
      return new CommunityHouse()
          .withHouseId(generateUniqueId())
          .withName("default-community-name");
    }

    /**
     * creates a new `CommunityHouse` object with an assigned house ID and default community
     * name.
     * 
     * @param houseId unique identifier of a community house.
     * 
     * @returns a new `CommunityHouse` instance with the specified house ID and default
     * community name.
     * 
     * The function returns a new instance of `CommunityHouse`, which has two primary
     * attributes - `houseId` and `name`. The `houseId` is a string that represents the
     * unique identifier of the community house, while the `name` is a default value
     * assigned to all community houses.
     */
    public static CommunityHouse getTestCommunityHouse(String houseId) {
      return new CommunityHouse()
          .withHouseId(houseId)
          .withName("default-community-name");
    }
  }

  /**
   * is a utility class that provides various methods for working with house members
   * in a fictional household. The class generates sets of random house members, creates
   * new instances of house member objects, and returns the generated set or individual
   * object.
   */
  public static class HouseMemberHelpers {

    /**
     * generates a set of `HouseMember` objects using a Stream API, limiting the number
     * of generated elements to the input `count`.
     * 
     * @param count maximum number of HouseMembers to generate and return in the set.
     * 
     * @returns a set of `HouseMember` objects generated randomly with unique IDs and
     * default names.
     * 
     * 	- The output is a `Set` of `HouseMember` objects, indicating that each house
     * member is unique and distinct within the set.
     * 	- The `Stream` generated using the `generate()` method creates an infinite number
     * of house members, which are then collected into a set using the `collect()` method.
     * 	- The `limit()` method is used to restrict the number of house members returned
     * in the set, which can be any positive integer value.
     * 
     * Overall, the output of the `getTestHouseMembers` function is a collection of a
     * fixed number of randomly generated house members.
     */
    public static Set<HouseMember> getTestHouseMembers(int count) {
      return Stream
          .generate(() -> new HouseMember()
              .withMemberId(generateUniqueId())
              .withName("default-house-member-name")
          )
          .limit(count)
          .collect(Collectors.toSet());
    }
    /**
     * creates a new instance of `HouseMember`, generating a unique identifier and setting
     * the name to a default value.
     * 
     * @returns a new `HouseMember` instance with a generated unique ID and a default name.
     * 
     * 	- `memberId`: A unique identifier generated by the function for the house member.
     * 	- `name`: The default name assigned to the house member.
     */
    public static HouseMember getTestHouseMember() {
      return new HouseMember()
              .withMemberId(generateUniqueId())
              .withName("default-house-member-name");
    }
  }

  /**
   * provides several methods for creating and manipulating communities in an application.
   * These methods include:
   * 
   * 	- `getTestCommunity`: Generates a new community with default details and returns
   * it fully populated with houses and admins retrieved from external sources.
   * 	- `getTestHouses`: Retrieves a set of houses to generate for the test community,
   * which are then added to the `communityHouses` set returned by the method.
   * 	- `getTestUsers`: Retrieves a set of users to assign as admins for the newly
   * created community, and sets them as admins of the community.
   */
  public static class CommunityHelpers {

    /**
     * iterates over a range of numbers and returns a set of `Community` objects, each
     * with a unique ID, name, district, and population of 0.
     * 
     * @param count maximum number of community objects to be generated and returned by
     * the `getTestCommunities()` method.
     * 
     * @returns a set of `Community` objects, each with a unique ID and name, generated
     * using a stream of indices from 0 to the specified `count`.
     * 
     * 	- The output is a `Set` of `Community` objects.
     * 	- Each element in the set represents a unique community generated through the
     * Stream.iterate method.
     * 	- The `Community` objects have three attributes: `id`, `name`, and `district`.
     * 	- The `id` attribute is a unique integer value for each community.
     * 	- The `name` attribute is a string value that is generated using the `generateUniqueId()`
     * method and appended with an index value (e.g., "default-community-name0").
     * 	- The `district` attribute is also a string value that is generated using the
     * `generateUniqueId()` method and appended with an index value (e.g., "default-community-district0").
     * 
     * The purpose of this function is to generate a set of communities with unique IDs,
     * names, and districts, based on a specified count.
     */
    public static Set<Community> getTestCommunities(int count) {
      return Stream.iterate(0, n -> n + 1)
          .map(index -> getTestCommunity(
              generateUniqueId(),
              "default-community-name" + index,
              "default-community-district" + index,
              0, 0)
          )
          .limit(count)
          .collect(Collectors.toSet());
    }

    /**
     * generates a new community instance with a unique ID and predefined name, district,
     * and population size.
     * 
     * @returns a `Community` object representing a fictional community with a unique ID,
     * name, and district.
     * 
     * 	- The `generateUniqueId()` method is used to generate a unique identifier for the
     * community.
     * 	- The `default-community-name`, `default-community-district`, and `0`, `0`
     * parameters are used to set default values for the community name, district, and
     * location coordinates, respectively.
     */
    public static Community getTestCommunity() {
      return getTestCommunity(
          generateUniqueId(),
          "default-community-name",
          "default-community-district",
          0, 0);
    }

    /**
     * retrieves a pre-defined community object, adds it to the user's communities list,
     * and sets the user as the only admin for the community.
     * 
     * @param admin user who is being added as an administrator to the `testCommunity`.
     * 
     * 	- `User admin`: This object represents a user with unknown properties, as it is
     * not specified in the code snippet provided. However, based on its name, it may
     * contain attributes such as username, password, email, and other personal information.
     * 
     * @returns a `Community` object representing a mock community for testing purposes.
     * 
     * 	- The Community object `testCommunity` contains information about a fictional community.
     * 	- The `admin` parameter passed to the function is added as an administrator of
     * the community.
     * 	- The community's admin list includes only the `admin` instance.
     */
    public static Community getTestCommunity(User admin) {
      Community testCommunity = getTestCommunity();
      admin.getCommunities().add(testCommunity);
      testCommunity.setAdmins(Collections.singleton(admin));
      return testCommunity;
    }

    /**
     * creates a new community object and populates it with houses and admins retrieved
     * from calls to other functions. It returns the constructed community object.
     * 
     * @param communityId identifier of the community to be created or retrieved, which
     * is used to identify the community in the database.
     * 
     * @param communityName name of the community being created or retrieved, which is
     * used to set the name of the new Community object.
     * 
     * @param communityDistrict district of the community being created, which is used
     * to set the appropriate name for the community.
     * 
     * @param adminsCount number of users who will be assigned as administrators for the
     * generated community, and it is used to create a set of users with the appropriate
     * size.
     * 
     * @param housesCount number of houses to be generated and added to the community.
     * 
     * @returns a new `Community` object representing a fictional community with houses
     * and admins.
     * 
     * 	- `testCommunity`: A new instance of the `Community` class, created with an empty
     * set of houses and admins.
     * 	- `housesCount`: The number of houses to be added to the community, which is
     * obtained from the function parameter.
     * 	- `house`: An instance of the `CommunityHouse` class, created with an empty set
     * of neighbors. Each house is added to the community's set of houses.
     * 	- `adminsCount`: The number of admins to be added to the community, which is
     * obtained from the function parameter.
     * 	- `user`: An instance of the `User` class, created with an empty set of communities.
     * Each admin is added to the community's set of admins.
     * 	- `CommunityHouse` and `User`: These classes represent houses and users in the
     * community, respectively. They have various attributes and methods that describe
     * their properties and behaviors.
     * 
     * These are the essential properties and attributes of the output returned by the
     * `getTestCommunity` function.
     */
    public static Community getTestCommunity(String communityId, String communityName, String communityDistrict, int adminsCount, int housesCount) {
      Community testCommunity = new Community(
          new HashSet<>(),
          new HashSet<>(),
          communityName,
          communityId,
          communityDistrict,
          new HashSet<>()
      );
      Set<CommunityHouse> communityHouses = getTestHouses(housesCount);
      communityHouses.forEach(house -> house.setCommunity(testCommunity));
      Set<User> communityAdmins = getTestUsers(adminsCount);
      communityAdmins.forEach(user -> user.getCommunities().add(testCommunity));

      testCommunity.setHouses(communityHouses);
      testCommunity.setAdmins(communityAdmins);
      return testCommunity;
    }
  }

  /**
   * provides methods for creating and manipulating amenities within a test environment.
   * These methods include generating new amenities with unique IDs, names, and
   * descriptions, as well as linking them to a test community. Additionally, the class
   * provides a method for retrieving a set of generated amenities with a limited number.
   * Overall, the class is used for testing purposes and helps in creating and manipulating
   * amenity data within a controlled environment.
   */
  public static class AmenityHelpers {

    /**
     * creates a new `Amenity` object with the given `amenityId` and `amenityDescription`,
     * and sets its `community` to a test `Community` object using `CommunityHelpers.getTestCommunity()`.
     * 
     * @param amenityId unique identifier of the amenity being created, which is used to
     * establish its identity within the system.
     * 
     * @param amenityDescription description of an amenity.
     * 
     * @returns a new `Amenity` object with specified `amenityId`, `amenityDescription`,
     * and `community`.
     * 
     * 	- `withAmenityId`: This attribute is set to a string representing the amenity ID.
     * 	- `withDescription`: This attribute is set to a string representing the amenity
     * description.
     * 	- `withCommunity`: This attribute is set to a `Community` object, which represents
     * the community where the amenity belongs. This is retrieved using the `getTestCommunity()`
     * function.
     */
    public static Amenity getTestAmenity(String amenityId, String amenityDescription) {
      return new Amenity()
          .withAmenityId(amenityId)
          .withDescription(amenityDescription)
          .withCommunity(CommunityHelpers.getTestCommunity());
    }

    /**
     * generates a set of `Amenity` objects with unique IDs, names, and descriptions using
     * a stream of anonymous objects generated by a lambda expression. The number of
     * generated amenities is limited to the input `count`.
     * 
     * @param count maximum number of amenities to be generated and returned by the
     * `getTestAmenities()` method.
     * 
     * @returns a set of `Amenity` objects generated randomly with unique identifiers,
     * names, and descriptions within a specified count limit.
     * 
     * 	- The output is a `Set` of `Amenity` objects.
     * 	- Each `Amenity` object has an `amenityId`, which is generated uniquely by the
     * `generateUniqueId()` method.
     * 	- Each `Amenity` object has a `name` and a `description`, which are hardcoded
     * with default values.
     * 	- The output is generated using a `Stream` of `Amenity` objects, which are created
     * and added to the stream using the `generate()` method.
     * 	- The `limit()` method is used to limit the number of `Amenity` objects in the
     * stream to the specified `count`.
     * 	- The `collect()` method is used to collect the stream of `Amenity` objects into
     * a `Set`, which is returned as the output.
     */
    public static Set<Amenity> getTestAmenities(int count) {
      return Stream
          .generate(() -> new Amenity()
              .withAmenityId(generateUniqueId())
              .withName("default-amenity-name")
              .withDescription("default-amenity-description")
          )
          .limit(count)
          .collect(Collectors.toSet());
    }

  }

  /**
   * generates random user objects with unique names and emails, using a simple function
   * to concatenate a default name and email address with an index-based identifier.
   * The method takes the maximum number of users to generate as input and returns a
   * set of these objects with a unique identifier for each one.
   */
  public static class UserHelpers {

    /**
     * iteratively generates `count` user objects with unique IDs, emails, and passwords,
     * and collects them into a set.
     * 
     * @param count number of users to be generated and returned by the `getTestUsers()`
     * function.
     * 
     * @returns a set of `User` objects, each with unique identifying information and no
     * overlap with any other user.
     * 
     * 	- The Set of User objects contains `count` number of elements, each representing
     * a test user.
     * 	- Each User object is created with a unique name, generated using the
     * `generateUniqueId()` method.
     * 	- The email address for each User object is also unique and consists of a prefix
     * followed by an incrementing index.
     * 	- The password for each User object is also unique and consists of a random string
     * of characters.
     * 	- The User objects have no roles assigned to them, represented by an empty HashSet.
     * 	- The User objects have no permissions granted to them, represented by an empty
     * HashSet.
     */
    public static Set<User> getTestUsers(int count) {
      return Stream.iterate(0, n -> n + 1)
          .map(index -> new User(
              "default-user-name" + index,
              generateUniqueId(),
              "default-user-email" + index,
              false,
              "default-user-password" + index,
              new HashSet<>(),
              new HashSet<>())
          )
          .limit(count)
          .collect(Collectors.toSet());
    }
  }

  /**
   * appears to provide various ways to create customized email properties for testing
   * purposes, including creating new instances of `MailProperties`, `EmailTemplateProperties`,
   * and `EmailTemplateLocalizationProperties`. These properties can be customized with
   * specific values for host, username, password, port, protocol, debug, devMode, path,
   * encoding, mode, and cache seconds.
   */
  public static class MailPropertiesHelper {

    /**
     * creates a new instance of `MailProperties` with customized properties for testing
     * purposes.
     * 
     * @returns a `MailProperties` object with customized properties for testing purposes.
     * 
     * 	- `host`: The value of this property is "test host". This specifies the hostname
     * or IP address of the mail server to connect to.
     * 	- `username`: The value of this property is "test username". This specifies the
     * login username for the mail server.
     * 	- `password`: The value of this property is "test password". This specifies the
     * password for the login credentials.
     * 	- `port`: The value of this property is 0. This specifies the port number to use
     * when connecting to the mail server.
     * 	- `protocol`: The value of this property is "test protocol". This specifies the
     * mail transfer protocol (MTP) to use when sending emails.
     * 	- `debug`: The value of this property is false. This specifies whether or not to
     * enable debug mode for the mail client.
     * 	- `devMode`: The value of this property is false. This specifies whether or not
     * to enable developer mode for the mail client.
     */
    public static MailProperties getTestMailProperties() {
      MailProperties testMailProperties = new MailProperties();
      testMailProperties.setHost("test host");
      testMailProperties.setUsername("test username");
      testMailProperties.setPassword("test password");
      testMailProperties.setPort(0);
      testMailProperties.setProtocol("test protocol");
      testMailProperties.setDebug(false);
      testMailProperties.setDevMode(false);
      return testMailProperties;
    }

    /**
     * creates a new `EmailTemplateProperties` instance with customized properties,
     * including path, encoding, mode, and cache status.
     * 
     * @returns an instance of `EmailTemplateProperties` with custom properties set.
     * 
     * 	- The `path` attribute is set to "test path".
     * 	- The `encoding` attribute is set to "test encoding".
     * 	- The `mode` attribute is set to "test mode".
     * 	- The `cache` attribute is set to `false`.
     */
    public static EmailTemplateProperties getTestMailTemplateProperties() {
      EmailTemplateProperties testMailTemplate = new EmailTemplateProperties();
      testMailTemplate.setPath("test path");
      testMailTemplate.setEncoding("test encoding");
      testMailTemplate.setMode("test mode");
      testMailTemplate.setCache(false);
      return testMailTemplate;
    }

    /**
     * creates a new instance of `EmailTemplateLocalizationProperties` and sets the path,
     * encoding, and cache seconds to specified values.
     * 
     * @returns an instance of `EmailTemplateLocalizationProperties` with customized path,
     * encoding, and cache seconds settings.
     * 
     * 	- The `setPath()` method sets the path to the test localization files.
     * 	- The `setEncoding()` method sets the encoding of the test localization files.
     * 	- The `setCacheSeconds()` method sets the cache time for the test localization
     * files in seconds.
     */
    public static EmailTemplateLocalizationProperties getTestLocalizationMailProperties() {
      EmailTemplateLocalizationProperties testTemplatesLocalization = new EmailTemplateLocalizationProperties();
      testTemplatesLocalization.setPath("test path");
      testTemplatesLocalization.setEncoding("test encodig");
      testTemplatesLocalization.setCacheSeconds(0);
      return testTemplatesLocalization;
    }
  }

  /**
   * is used to build a `PaymentDto` object with various attributes, including charge
   * amount, payment type, description, recurring status, due date, admin, and member
   * information. The class provides methods for creating a mock payment object with
   * all fields except 'recurring' set to null, and 'recurring' set to false.
   */
  public static class PaymentHelpers {

    /**
     * builds a `PaymentDto` object with various parameters such as charge amount, payment
     * type, description, recurring status, due date, and admin and member information.
     * 
     * @param charge amount to be charged for the payment.
     * 
     * The `BigDecimal` charge represents an amount of money.
     * 
     * @param type payment type, which determines how the payment will be processed and
     * recorded in the system.
     * 
     * @param description a brief description of the payment, which is added to the
     * `PaymentDto` object as a string field.
     * 
     * @param recurring whether the payment is recurring or not.
     * 
     * @param dueDate date when the payment is due, which is converted to a string and
     * included in the `PaymentDto` object.
     * 
     * 	- `toString()` is called to convert the `LocalDate` object into a string
     * representation in the format "YYYY-MM-DD" or "YYYY-MM-DD HH:MM:SS", depending on
     * the context.
     * 
     * @param admin UserDto object containing information about the administrator who
     * made the payment.
     * 
     * 	- `admin`: A `UserDto` object representing an administrator who made the payment.
     * 	+ Properties: `username`, `fullName`, `email`, `phoneNumber`, `role` (e.g., "Admin").
     * 
     * @param member HouseMemberDto object containing information about the member whose
     * payment is being processed.
     * 
     * 	- `admin`: The `UserDto` object representing the admin user associated with the
     * payment.
     * 
     * @returns a `PaymentDto` object with pre-populated fields.
     * 
     * 	- charge: The BigDecimal value representing the amount to be charged.
     * 	- type: The string value indicating the payment type (e.g., "invoice", "credit_card").
     * 	- description: The string value providing a brief description of the payment.
     * 	- recurring: A boolean value indicating whether the payment is recurring.
     * 	- dueDate: A LocalDate object representing the date when the payment is due.
     * 	- admin: The UserDto object representing the administrator associated with the payment.
     * 	- member: The HouseMemberDto object representing the member associated with the
     * payment.
     */
    public static PaymentDto getTestPaymentDto(BigDecimal charge, String type, String description, boolean recurring, LocalDate dueDate, UserDto admin, HouseMemberDto member) {

      return PaymentDto.builder()
          .charge(charge)
          .type(type)
          .description(description)
          .recurring(recurring)
          .dueDate(dueDate.toString())
          .admin(admin)
          .member(member)
          .build();
    }
    /**
     * creates a `Payment` instance with all fields nullable except for the recurring
     * field, which is false.
     * 
     * @returns a `Payment` object with all fields null except for the `recurring` field,
     * which is set to false.
     * 
     * 	- `payment`: The Payment object itself, which is empty and has no fields set.
     * 	- `recurring`: A boolean field indicating whether the payment is recurring or
     * not. In this case, it is false.
     * 	- `amount`: The amount of the payment, which is null.
     * 	- `currency`: The currency of the payment, which is also null.
     * 	- `description`: A string field providing a brief description of the payment,
     * which is null.
     * 	- `due_date`: The date the payment is due, which is null.
     * 	- `paid`: A boolean field indicating whether the payment has been made, which is
     * also null.
     * 	- `status`: The status of the payment, which is null.
     */
    public static Payment getTestPaymentNullFields() {
      //Only 'recurring' field will be not null, but false
      return new Payment(
          null,
          null,
          null,
          null,
          false,
          null,
          null,
          null);
    }
  }
}
