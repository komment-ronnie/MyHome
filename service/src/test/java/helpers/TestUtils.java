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
 * provides various helpers for testing purposes in the context of an API for managing
 * a community and its members, including:
 * 
 * 	- Helpers for creating test data sets of users, houses, communities, and amenities
 * 	- Methods for generating unique IDs and passwords
 * 	- Amenity helpers for creating test amenities and linking them to a community
 * 	- User helpers for creating test users and linking them to a community
 * 	- Mail properties helper for creating test mail properties
 * 	- Payment helpers for creating test payments with different fields filled or left
 * empty.
 */
public class TestUtils {

  /**
   * is a utility class that provides various functionality for image processing and
   * unique identifier generation. The getImageAsByteArray method takes height and width
   * parameters and returns the image as a byte array, while the generateUniqueId method
   * generates a unique identifier using the UUID random generator.
   */
  public static class General {

    /**
     * converts a `BufferedImage` object to a byte array, representing an image as a JPEG
     * file.
     * 
     * @param height vertical dimension of the image to be converted into a byte array.
     * 
     * @param width horizontal resolution of the resulting byte array, which is the size
     * of the output image.
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
     * generates a unique identifier based on a randomly generated UUID string, returned
     * as a String.
     * 
     * @returns a unique, randomly generated string of characters.
     */
    public static String generateUniqueId() {
      return UUID.randomUUID().toString();
    }
  }

  /**
   * provides utility methods for creating and manipulating CommunityHouses in a Spring
   * Boot application. The class offers several methods for generating sets of
   * CommunityHouses with different parameters, as well as methods for creating individual
   * CommunityHouses with customized values.
   */
  public static class CommunityHouseHelpers {

    /**
     * generates a set of `CommunityHouse` objects using a Stream API, with each object
     * having a unique ID and default name. The function limits the number of generated
     * objects to the input `count`.
     * 
     * @param count number of CommunityHouse instances to be generated and returned by
     * the function.
     * 
     * @returns a set of `CommunityHouse` objects generated randomly with unique IDs and
     * default names.
     * 
     * 	- The output is a `Set` of `CommunityHouse` objects.
     * 	- Each element in the set represents a unique `CommunityHouse` instance with its
     * own `houseId` and `name`.
     * 	- The `houseId` for each element is generated uniquely using `generateUniqueId()`.
     * 	- The `name` for each element is set to a default value of "default-house-name".
     * 	- The total number of elements in the set is limited to the specified `count`.
     * 
     * The output of the function can be described as a collection of randomized
     * `CommunityHouse` instances with unique identifiers and default names.
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
     * creates a new `CommunityHouse` instance with a unique ID and default name.
     * 
     * @returns a new instance of the `CommunityHouse` class with a unique ID and a default
     * community name.
     * 
     * The function returns a new instance of the `CommunityHouse` class with a unique `houseId`.
     * 
     * The `CommunityHouse` object has a `name` attribute set to a default value.
     */
    public static CommunityHouse getTestCommunityHouse() {
      return new CommunityHouse()
          .withHouseId(generateUniqueId())
          .withName("default-community-name");
    }

    /**
     * creates a new instance of `CommunityHouse` with a specified `houseId` and default
     * `name`.
     * 
     * @param houseId ID of the community house to be created or retrieved.
     * 
     * @returns a new `CommunityHouse` object with an assigned `houseId` and a default `name`.
     * 
     * 	- The function returns a new instance of `CommunityHouse`.
     * 	- The `withHouseId` method is called on the newly created instance, passing in
     * the `houseId` parameter.
     * 	- The `withName` method is called on the instance, passing in the default community
     * name.
     */
    public static CommunityHouse getTestCommunityHouse(String houseId) {
      return new CommunityHouse()
          .withHouseId(houseId)
          .withName("default-community-name");
    }
  }

  /**
   * provides utility methods for working with house members in a housing platform.
   * These methods include generating sets of test house members and creating new
   * individual house members with randomized IDs, names, and other properties.
   */
  public static class HouseMemberHelpers {

    /**
     * generates `count` instances of a custom `HouseMember` class, each with a unique
     * identifier and a default name, and returns them as a set.
     * 
     * @param count number of house members to be generated, and it determines the size
     * of the set returned by the `getTestHouseMembers()` method.
     * 
     * @returns a set of `HouseMember` objects generated randomly.
     * 
     * 1/ The output is a `Set` of `HouseMember` objects.
     * 2/ Each `HouseMember` object has a unique `memberId`.
     * 3/ Each `HouseMember` object has a default name of "default-house-member-name".
     * 4/ The total number of `HouseMember` objects in the set is determined by the input
     * parameter `count`.
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
     * generates a new instance of `HouseMember` with a unique ID and a default name.
     * 
     * @returns a new instance of the `HouseMember` class with a randomly generated ID
     * and a predetermined name.
     * 
     * The `HouseMember` object is created with a unique `memberId` generated by the
     * function itself.
     * The `name` attribute of the `HouseMember` object is set to a default value, "default-house-member-name".
     * Both these properties are essential for the proper functioning of the code and
     * cannot be changed or altered in any way.
     */
    public static HouseMember getTestHouseMember() {
      return new HouseMember()
              .withMemberId(generateUniqueId())
              .withName("default-house-member-name");
    }
  }

  /**
   * provides various utility methods for creating and manipulating communities in the
   * application. These methods include generating test communities, creating new
   * communities with admins and houses, and getting test communities of various counts.
   * The class also provides methods for creating amenities, users, and payments.
   */
  public static class CommunityHelpers {

    /**
     * iteratively creates `Community` objects based on a unique id, name, and district,
     * and limits the number of generated communities to the provided count.
     * 
     * @param count maximum number of community objects to return in the Set.
     * 
     * @returns a set of `Community` objects, each with a unique ID and name generated
     * using a prefix based on the index.
     * 
     * The return value is a `Set` of `Community` objects, containing `n` communities
     * where `n` is the input parameter `count`. Each community object represents a unique
     * community instance with a randomly generated ID, name, and district. The
     * `Stream.iterate` method is used to generate the community instances in a sequence,
     * starting from 0 and incrementing by 1 for each iteration. The `map` method is used
     * to apply a transformation to each community instance, which involves calling the
     * `getTestCommunity` function with a unique ID, name, and district for each instance.
     * Finally, the `limit` method is used to limit the number of community instances
     * returned to `count`.
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
     * generates a test community with a unique ID, name, and district, and returns it
     * as an object of type `Community`.
     * 
     * @returns a ` Community` object representing a test community with a unique ID,
     * name, district, and population of 0.
     * 
     * 	- The function returns a `Community` object, which represents a community in the
     * system.
     * 	- The `generateUniqueId()` method is called to generate an unique ID for the community.
     * 	- The `default-community-name`, `default-community-district`, and `0`, `0`
     * parameters are used to set default values for various attributes of the community.
     */
    public static Community getTestCommunity() {
      return getTestCommunity(
          generateUniqueId(),
          "default-community-name",
          "default-community-district",
          0, 0);
    }

    /**
     * retrieves and returns a pre-created community object, adds it to the administrator's
     * communities list, sets the administrator as the sole admin of the community, and
     * returns the community.
     * 
     * @param admin User who is adding the test community to their list of managed communities.
     * 
     * 	- `User`: A class that represents a user in the community. It has properties such
     * as `id`, `username`, `email`, and `role`.
     * 	- `admin`: A property of the `User` class that indicates whether the user is an
     * administrator or not.
     * 
     * @returns a new `Community` object with the specified admin user added as an administrator.
     * 
     * 	- The Community object, `testCommunity`, is created by calling the `getTestCommunity()`
     * method.
     * 	- The `admin` parameter's `getCommunities()` method adds the `testCommunity` to
     * its list of communities.
     * 	- The `setAdmins()` method sets the `testCommunity` as the sole admin for the
     * community, using the `Collections.singleton()` method to provide a single instance
     * of the `Admin` class.
     * 
     * No summary is provided at the end of this response.
     */
    public static Community getTestCommunity(User admin) {
      Community testCommunity = getTestCommunity();
      admin.getCommunities().add(testCommunity);
      testCommunity.setAdmins(Collections.singleton(admin));
      return testCommunity;
    }

    /**
     * creates a new community with given name, ID and district, and then links it to a
     * set of houses and administrators.
     * 
     * @param communityId unique identifier of the community being created, which is used
     * to assign the correct name and district to the new community.
     * 
     * @param communityName name of the community being created or retrieved, which is
     * used to set the name of the new `Community` object returned by the function.
     * 
     * @param communityDistrict district of the community being created, which is used
     * to set the `communityDistrict` field of the generated `Community` object.
     * 
     * @param adminsCount number of administrators to be associated with the community
     * generated by the function, and it is used to set the `Admins` field of the generated
     * `Community` object.
     * 
     * @param housesCount number of houses to generate for the created community, and it
     * is used to populate the `communityHouses` set with the generated houses.
     * 
     * @returns a new `Community` object representing a test community with houses and admins.
     * 
     * 	- `Community testCommunity`: This is an instance of the `Community` class, which
     * represents a fictional community with various attributes and memberships.
     * 	- `HashSet<>`: The two `HashSet` instances represent the sets of houses and users
     * that belong to the community.
     * 	- `communityName`: The name of the community, which is provided as an input parameter.
     * 	- `communityId`: The ID of the community, which is also provided as an input parameter.
     * 	- `communityDistrict`: The district where the community is located, which is also
     * provided as an input parameter.
     * 	- `adminsCount`: The number of administrators for the community, which is provided
     * as an input parameter.
     * 	- `housesCount`: The number of houses in the community, which is provided as an
     * input parameter.
     * 	- `Set<CommunityHouse> communityHouses`: A set of `CommunityHouse` instances that
     * represent the houses in the community. Each house is associated with the community
     * through its membership in the `communityHouses` set.
     * 	- `Set<User> communityAdmins`: A set of `User` instances that represent the
     * administrators of the community. Each administrator is associated with the community
     * through its membership in the `communityAdmins` set.
     * 
     * The function creates these objects and sets their properties based on the input
     * parameters provided. The resulting `Community` instance represents a fictional
     * community with houses and users, as well as the relationships between them.
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
   * provides utility methods for creating and manipulating amenities in a housing
   * platform. The class offers two main methods: `getTestAmenity()` for creating a
   * single amenity instance, and `getTestAmenities()` for generating a set of amenities.
   * These methods are used to create and populate the amenities database.
   */
  public static class AmenityHelpers {

    /**
     * creates a new `Amenity` object with specified `amenityId` and `amenityDescription`,
     * and also assigns it to a community object retrieved from a helper class.
     * 
     * @param amenityId identifier of the amenity being created, which is used to uniquely
     * identify the amenity within the given community.
     * 
     * @param amenityDescription description of the amenity being created.
     * 
     * @returns a new instance of the `Amenity` class with specified `amenityId`,
     * `amenityDescription`, and `community`.
     * 
     * 	- `withAmenityId`: A string representing the unique identifier for the amenity.
     * 	- `withDescription`: A string describing the amenity.
     * 	- `withCommunity`: A reference to a test community object that the amenity belongs
     * to.
     */
    public static Amenity getTestAmenity(String amenityId, String amenityDescription) {
      return new Amenity()
          .withAmenityId(amenityId)
          .withDescription(amenityDescription)
          .withCommunity(CommunityHelpers.getTestCommunity());
    }

    /**
     * generates a set of `Amenity` objects with unique IDs, names, and descriptions,
     * limited to a specified count using a streaming API.
     * 
     * @param count number of amenities to be generated and returned by the `getTestAmenities()`
     * method.
     * 
     * @returns a set of `Amenity` objects generated randomly with unique IDs, names, and
     * descriptions.
     * 
     * 	- The output is a `Set` of `Amenity` objects, indicating that each call to the
     * function will return a unique set of amenities.
     * 	- The `Stream` used in the function generates a new `Amenity` object for each
     * iteration, using a combination of an `identity()` function and a `limit()` operation
     * to specify the number of amenities to generate.
     * 	- Each generated `Amenity` object is assigned a unique `amenityId`, `name`, and
     * `description`, which are generated using random values.
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
   * is a utility class that provides various methods to generate and manipulate test
   * data for users in a system. The class provides methods to generate sets of users
   * with customizable fields such as names, emails, passwords, and admin status.
   * Additionally, the class offers methods to create test users with specific properties.
   */
  public static class UserHelpers {

    /**
     * iterates over a range of numbers, generates new user objects using a template, and
     * collects the results in a set with a maximum size of `count`.
     * 
     * @param count number of test users to be generated by the `getTestUsers` function.
     * 
     * @returns a set of `User` objects, each with unique properties, generated within a
     * limited range based on an incrementing index.
     * 
     * 	- The output is a `Set` of `User` objects.
     * 	- Each `User` object represents an individual test user.
     * 	- The `User` objects are generated using a recursive approach, where each new
     * user is created by modifying the previous one.
     * 	- The `User` objects have various attributes, including a unique name, email
     * address, and password.
     * 	- The `User` objects also have a `HashSet` of other users that they belong to
     * (i.e., their "followers").
     * 	- The `getTestUsers` function returns at most `count` `User` objects.
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
   * is a utility class that provides helpers for creating and manipulating email
   * properties, including host, username, password, port, protocol, debug, and dev
   * mode. Additionally, it provides methods for creating email template properties and
   * localization properties. Overall, the class offers useful functionality for working
   * with email settings in a Java application.
   */
  public static class MailPropertiesHelper {

    /**
     * creates a new `MailProperties` instance with various properties set to test values,
     * including host, username, password, port, protocol, debug, and dev mode.
     * 
     * @returns a `MailProperties` object with predefined properties for testing purposes.
     * 
     * 	- `host`: The hostname or IP address of the mail server.
     * 	- `username`: The username to use when connecting to the mail server.
     * 	- `password`: The password for the specified username.
     * 	- `port`: The port number used to connect to the mail server (zero means default).
     * 	- `protocol`: The protocol used to connect to the mail server (e.g., "smtp").
     * 	- `debug`: A boolean indicating whether debugging mode is enabled.
     * 	- `devMode`: A boolean indicating whether the mail server is in development mode.
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
     * creates a new `EmailTemplateProperties` object with customizable properties for
     * testing purposes.
     * 
     * @returns an EmailTemplateProperties object with customized properties.
     * 
     * 	- Path: The path to the email template file.
     * 	- Encoding: The encoding used for the email template.
     * 	- Mode: The mode in which the email template is used.
     * 	- Cache: A Boolean value indicating whether the email template should be cached
     * or not.
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
     * creates a new `EmailTemplateLocalizationProperties` object with customized properties
     * for testing purposes, including a path, encoding, and cache seconds.
     * 
     * @returns an instance of `EmailTemplateLocalizationProperties` with customized
     * properties for testing purposes.
     * 
     * 	- The `path` attribute is set to "test path".
     * 	- The `encoding` attribute is set to "test encoding".
     * 	- The `cacheSeconds` attribute is set to 0.
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
   * provides utility methods for working with payments in an application. The class
   * includes methods for creating test payment data and handling null fields. Additionally,
   * the class provides a method for creating a payment instance with default values.
   */
  public static class PaymentHelpers {

    /**
     * builds a `PaymentDto` object with various parameters such as charge amount, payment
     * type, description, recurring status, due date, and user and member information.
     * 
     * @param charge amount of money to be charged or deducted from the user's account
     * when creating a new payment.
     * 
     * The BigDecimal object `charge` represents the amount to be charged or paid, with
     * a precision of up to 10 decimal places.
     * 
     * @param type payment type of the Dto, which can be either `RECURRING`, `ONCE`, or
     * `OTHER`.
     * 
     * @param description description of the payment in the PaymentDto object that is
     * being built.
     * 
     * @param recurring whether the payment is recurring or not.
     * 
     * @param dueDate date when the payment is due, which is converted to a string and
     * included in the PaymentDto object.
     * 
     * 	- `toString()` method is used to convert the `LocalDate` object into a string
     * representation, which can be used in further processing or serialization.
     * 
     * @param admin user who made the payment.
     * 
     * 	- `admin`: A `UserDto` object representing the administrator who created or updated
     * the payment. It contains attributes such as `id`, `username`, `email`, and `role`.
     * 
     * @param member HouseMemberDto object containing information about the member whose
     * payment is being processed.
     * 
     * 	- `admin`: The `UserDto` object represents the administrator who made the payment.
     * It contains information such as username, email address, and any other relevant details.
     * 	- `member`: The `HouseMemberDto` object represents the member for whom the payment
     * is being made. It contains information such as name, address, and any other relevant
     * details related to membership in a household.
     * 
     * @returns a `PaymentDto` object containing various attributes related to a payment.
     * 
     * 	- charge: A BigDecimal object representing the amount to be charged.
     * 	- type: A string representing the type of payment (e.g., "invoice", "payment").
     * 	- description: A string providing a brief description of the payment.
     * 	- recurring: A boolean indicating whether the payment is recurring or not.
     * 	- dueDate: A LocalDate object representing the date when the payment is due.
     * 	- admin: A UserDto object representing the administrator who created/modified the
     * payment.
     * 	- member: A HouseMemberDto object representing the member for whom the payment
     * is made.
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
     * generates a payment object with all fields null except for the 'recurring' field
     * which is false.
     * 
     * @returns a Payment object with all fields null except for the recurring field,
     * which is false.
     * 
     * 	- `payment`: The Payment object itself is null.
     * 	- `amount`: The amount field is null.
     * 	- `currency`: The currency field is null.
     * 	- `description`: The description field is null.
     * 	- `recurring`: The recurring field is false, indicating that the payment is not
     * a recurring payment.
     * 	- `paymentDate`: The payment date field is null.
     * 	- `nextPaymentDate`: The next payment date field is null.
     * 	- `paymentMethod`: The payment method field is null.
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
