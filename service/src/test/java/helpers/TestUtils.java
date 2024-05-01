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
 * provides utility methods for working with payments in an application, including
 * creating test payment data and handling null fields. The PaymentHelpers class
 * includes methods for creating a test payment Dto object with various parameters
 * such as charge amount, payment type, description, recurring status, due date, and
 * user and member information. Additionally, the class provides a method for creating
 * a payment instance with default values.
 */
public class TestUtils {

  /**
   * appears to provide utility methods for handling image and identifier generation
   * tasks. The getImageAsByteArray method converts a BufferedImage object into a byte
   * array in JPEG format, while the generateUniqueId method generates a unique identifier
   * based on a randomly generated UUID string and returns it as a String.
   */
  public static class General {

    /**
     * generates an image as a byte array by converting it to a JPEG file and then saving
     * it as a binary stream.
     * 
     * @param height vertical dimension of the image to be converted into a byte array.
     * 
     * @param width width of the image that is being converted to a byte array.
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
     * generates a unique identifier using the `UUID.randomUUID()` method and returns it
     * as a string.
     * 
     * @returns a unique string of 36 characters, consisting of letters and numbers.
     */
    public static String generateUniqueId() {
      return UUID.randomUUID().toString();
    }
  }

  /**
   * generates randomized `CommunityHouse` instances with unique IDs and default names
   * using a Stream API. The `getTestHouses` method limits the number of generated
   * objects to the input count, while the `getTestCommunityHouse` and `getTestCommunityHouse`
   * methods create new instances with specified `houseId` and default `name`, respectively.
   */
  public static class CommunityHouseHelpers {

    /**
     * generates a set of `CommunityHouse` objects with unique IDs and default names,
     * limited to a specified count using `Stream` and `collect`.
     * 
     * @param count number of CommunityHouse objects to be generated and returned by the
     * `getTestHouses()` method.
     * 
     * @returns a set of `CommunityHouse` objects generated randomly with unique IDs and
     * default names.
     * 
     * 	- The output is a set of `CommunityHouse` objects, generated using a stream of
     * anonymous inner classes and collected using `Collectors.toSet()`.
     * 	- Each `CommunityHouse` object in the set has a unique `houseId` attribute and a
     * default name attribute.
     * 	- The number of `CommunityHouse` objects in the set is limited to the specified
     * `count` parameter.
     * 	- The output set does not contain any duplicates.
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
     * creates a new `CommunityHouse` instance with a unique ID and a default community
     * name.
     * 
     * @returns a new `CommunityHouse` object with a unique house ID and a default community
     * name.
     * 
     * 	- The CommunityHouse object is created with a unique house ID generated by the
     * `generateUniqueId()` method.
     * 	- The name of the community is set to "default-community-name".
     */
    public static CommunityHouse getTestCommunityHouse() {
      return new CommunityHouse()
          .withHouseId(generateUniqueId())
          .withName("default-community-name");
    }

    /**
     * creates a new instance of `CommunityHouse` with an specified `houseId` and sets
     * the name to "default-community-name".
     * 
     * @param houseId ID of the community house to be created and is used to set the
     * `HouseId` property of the resulting `CommunityHouse` object.
     * 
     * @returns a new `CommunityHouse` object with an ID and default name.
     * 
     * 	- The function returns an instance of the `CommunityHouse` class.
     * 	- The instance is created with a `houseId` property set to the input parameter `houseId`.
     * 	- The `name` property of the instance is set to a default value of `"default-community-name"`.
     */
    public static CommunityHouse getTestCommunityHouse(String houseId) {
      return new CommunityHouse()
          .withHouseId(houseId)
          .withName("default-community-name");
    }
  }

  /**
   * generates instances of a custom `HouseMember` class with unique IDs and default
   * names. The `getTestHouseMembers()` method returns a set of generated `HouseMember`
   * objects, while the `getTestHouseMember()` method creates a new instance of `HouseMember`.
   */
  public static class HouseMemberHelpers {

    /**
     * generates a set of `HouseMember` objects using a stream of anonymous instances,
     * limits the number of elements to the input `count`, and returns the set.
     * 
     * @param count maximum number of `HouseMember` instances to be generated and returned
     * by the function.
     * 
     * @returns a set of `HouseMember` objects generated using a stream and collected
     * into a set.
     * 
     * 	- The output is a set of `HouseMember` objects, generated using a stream-based
     * approach that creates new house members with unique IDs and default names.
     * 	- The `Stream` generates `HouseMember` objects using a factory method that takes
     * no arguments.
     * 	- The `limit` method is used to restrict the number of elements in the stream to
     * the specified `count`.
     * 	- The `collect` method is used to aggregate the elements in the stream into a set.
     * 
     * Overall, the function returns a set of randomly generated house members with unique
     * IDs and default names.
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
     * creates a new instance of `HouseMember` with a unique identifier and a predefined
     * name.
     * 
     * @returns a new instance of the `HouseMember` class with a generated unique ID and
     * a default name.
     * 
     * 	- `memberId`: A unique identifier generated by the function for each member.
     * 	- `name`: A default name assigned to the member.
     */
    public static HouseMember getTestHouseMember() {
      return new HouseMember()
              .withMemberId(generateUniqueId())
              .withName("default-house-member-name");
    }
  }

  /**
   * is an utility class that provides various methods for creating and manipulating
   * communities in a fictional community management system. The class offers functionality
   * for generating unique community IDs, retrieving test communities, adding test
   * communities to administrators' managed communities lists, getting test communities,
   * and more.
   */
  public static class CommunityHelpers {

    /**
     * generates a set of `Community` objects using a stream of numbers, each representing
     * a unique community. The communities are created with default names and districts,
     * and the number of communities is limited to the specified count.
     * 
     * @param count number of community objects to be generated and returned by the
     * `getTestCommunities` method.
     * 
     * @returns a set of `Community` objects, each with a unique ID and name, generated
     * within a specified limit.
     * 
     * 	- The Set<Community> object contains multiple Community objects, each representing
     * a potential community for testing purposes.
     * 	- Each Community object has four attributes: id (a unique identifier), name,
     * district, and population.
     * 	- The id attribute is an integer that represents the unique identity of each
     * Community object.
     * 	- The name attribute is a string that provides a human-readable name for each
     * Community object.
     * 	- The district attribute is a string that identifies the geographical area or
     * district where each Community object is located.
     * 	- The population attribute is an integer that represents the estimated population
     * size of each Community object.
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
     * generates a new community with a unique ID and specified name, district, and
     * location (0,0).
     * 
     * @returns a `Community` object containing default community details.
     * 
     * 	- The Community object is generated using the `generateUniqueId()` method, which
     * generates a unique identifier for the community.
     * 	- The name of the community is specified in the second argument, which is a string
     * called "default-community-name".
     * 	- The district of the community is specified in the third argument, which is an
     * integer called "default-community-district".
     * 	- The `getTestCommunity` function returns a Community object with these properties.
     */
    public static Community getTestCommunity() {
      return getTestCommunity(
          generateUniqueId(),
          "default-community-name",
          "default-community-district",
          0, 0);
    }

    /**
     * retrieves a pre-defined community object, adds it to an administrator's list of
     * communities, and sets the administrator as the only admin for the community.
     * 
     * @param admin user who will have access to the `testCommunity`.
     * 
     * 	- `User admin`: This is an instance of the `User` class, representing a user in
     * the community. It has various attributes such as `id`, `username`, `password`, and
     * `role`.
     * 
     * @returns a Community object representing a test community with the specified admin
     * user as an administrator.
     * 
     * The Community object, `testCommunity`, has several attributes, including `setAdmins()`
     * method, which sets the admin of the community to a single user, `admin`. Additionally,
     * the community's membership is modified by adding it to the calling user's communities
     * list.
     */
    public static Community getTestCommunity(User admin) {
      Community testCommunity = getTestCommunity();
      admin.getCommunities().add(testCommunity);
      testCommunity.setAdmins(Collections.singleton(admin));
      return testCommunity;
    }

    /**
     * creates a new community object and populates it with houses and admins retrieved
     * from external sources. It returns the fully populated community object.
     * 
     * @param communityId unique identifier of the community being created, which is used
     * to assign the community its own set of houses and admins.
     * 
     * @param communityName name of the community being created or retrieved.
     * 
     * @param communityDistrict district of the community being created, and is used to
     * create a unique identifier for the community within that district.
     * 
     * @param adminsCount number of users who will be assigned as community administrators
     * for the newly created community.
     * 
     * @param housesCount number of houses to generate for the test community, which are
     * then added to the `communityHouses` set returned by the `getTestHouses()` method.
     * 
     * @returns a new `Community` object with houses and admins set.
     * 
     * 	- `Community testCommunity`: This is an instance of the `Community` class, which
     * represents a community in the application.
     * 	- `HashSet<>`: These are two sets that contain objects of the `CommunityHouse`
     * and `User` classes, respectively. The `CommunityHouse` set contains houses associated
     * with the community, while the `User` set contains admins of the community.
     * 	- `communityName`: This is the name of the community being returned.
     * 	- `communityId`: This is the ID of the community being returned.
     * 	- `communityDistrict`: This is the district of the community being returned.
     * 	- `adminsCount`: This is the number of admins associated with the community.
     * 	- `housesCount`: This is the number of houses associated with the community.
     * 
     * In summary, the `getTestCommunity` function returns an instance of the `Community`
     * class along with two sets of objects that represent houses and admins associated
     * with the community.
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
   * provides utility functions for working with amenities, including generating new
   * amenities and retrieving existing ones based on their unique IDs. The class offers
   * methods for creating new amenities with random IDs, names, and descriptions, as
   * well as retrieving a set of amenities based on a specified count.
   */
  public static class AmenityHelpers {

    /**
     * creates a new `Amenity` object with specified ID and description, and links it to
     * a test community.
     * 
     * @param amenityId identifier of the amenity being created.
     * 
     * @param amenityDescription description of the amenity being created, which is used
     * to set the `withDescription()` method of the returned `Amenity` object.
     * 
     * @returns a new `Amenity` object with specified `amenityId`, `amenityDescription`,
     * and `community`.
     * 
     * 	- `withAmenityId`: A String representing the amenity ID.
     * 	- `withDescription`: A String representing the amenity description.
     * 	- `withCommunity`: A reference to a Community object, which is obtained through
     * the `getTestCommunity()` method.
     */
    public static Amenity getTestAmenity(String amenityId, String amenityDescription) {
      return new Amenity()
          .withAmenityId(amenityId)
          .withDescription(amenityDescription)
          .withCommunity(CommunityHelpers.getTestCommunity());
    }

    /**
     * generates a set of `Amenity` objects with unique IDs and predetermined names and
     * descriptions, limiting the number of generated amenities based on the input count.
     * 
     * @param count number of amenities to be generated and returned by the `getTestAmenities()`
     * method.
     * 
     * @returns a set of `Amenity` objects generated randomly with unique IDs, names, and
     * descriptions.
     * 
     * 	- The output is a `Set` of `Amenity` objects.
     * 	- Each `Amenity` object has an `amenityId`, which is generated uniquely for each
     * amenity.
     * 	- Each `Amenity` object has a `name` and a `description`.
     * 	- The `name` is set to "default-amenity-name" for each amenity, while the
     * `description` is set to "default-amenity-description".
     * 	- The output is generated using a `Stream` of `Amenity` objects, with a total
     * count of `count`.
     * 	- The `Stream` is created by calling `generate()` on an empty `Supplier` of
     * `Amenity` objects.
     * 	- The `Limit` function is used to limit the number of generated amenities to `count`.
     * 	- The `Collectors.toSet()` method is used to collect the generated amenities into
     * a `Set`.
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
   * generates a set of user objects with unique properties using a recursive approach.
   * The output is a Set of User objects, each representing an individual test user
   * with various attributes such as name, email address, password, and a set of other
   * users they belong to. The function returns at most a specified number of User objects.
   */
  public static class UserHelpers {

    /**
     * iterates over a sequence of numbers, creates new `User` objects, and returns a set
     * of these objects after limiting the number to `count`.
     * 
     * @param count number of user objects to be generated and returned by the `getTestUsers()`
     * method.
     * 
     * @returns a set of `User` objects, each with a unique name and email address,
     * generated using a random ID and password.
     * 
     * 	- The output is a `Set` of `User` objects, indicating that each user in the set
     * has a unique identifier.
     * 	- Each `User` object contains several attributes, including a name, an email
     * address, a password, and two `HashSet`s representing the user's friends and followers.
     * 	- The `Stream` used to generate the output iterates over a range of values (0 to
     * `count`), where `count` is the maximum number of users to be generated.
     * 	- The `map` method transforms each iteration value into a new `User` object, using
     * a simple function that concatenates a default name and email address with an
     * index-based identifier.
     * 	- The `limit` method is used to cap the number of `User` objects returned in the
     * set, ensuring that only `count` elements are included.
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
   * provides various methods for creating and customizing `MailProperties` and
   * `EmailTemplateProperties` objects for testing purposes. These methods allow for
   * setting predefined properties or creating customized instances with custom values
   * for various attributes such as host, username, password, port, protocol, debug,
   * and dev mode. Additionally, a method is provided to create an instance of
   * `EmailTemplateLocalizationProperties` with customized properties for testing
   * purposes, including a path, encoding, and cache seconds.
   */
  public static class MailPropertiesHelper {

    /**
     * creates a new `MailProperties` object with specific properties set to simulate
     * various mail configurations for testing purposes.
     * 
     * @returns a `MailProperties` object with customized settings for testing purposes.
     * 
     * 	- Host: The hostname where the email server is located.
     * 	- Username: The username to use when connecting to the email server.
     * 	- Password: The password to use when connecting to the email server.
     * 	- Port: The port number used for the email communication.
     * 	- Protocol: The protocol used for the email communication, which can be either
     * "smtp" or "imap".
     * 	- Debug: A boolean value indicating whether debugging mode is enabled.
     * 	- DevMode: A boolean value indicating whether development mode is enabled.
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
     * creates a new `EmailTemplateProperties` object with customized properties, including
     * a path, encoding, mode, and cache status, and returns it.
     * 
     * @returns an instance of `EmailTemplateProperties` with customized properties for
     * testing purposes.
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
     * creates a new `EmailTemplateLocalizationProperties` object with customizable
     * properties for testing purposes, including path, encoding, and cache seconds.
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
   * is a utility class that provides methods for creating and manipulating `Payment`
   * objects. The class offers various methods to build a `PaymentDto` object with
   * fields such as charge amount, payment type, description, recurring status, due
   * date, and user/member information. Additionally, the class provides a method to
   * generate a payment object with all fields null except for the 'recurring' field
   * which is false.
   */
  public static class PaymentHelpers {

    /**
     * builds a `PaymentDto` object with various attributes, including charge amount,
     * payment type, description, recurring status, due date, admin, and member information.
     * 
     * @param charge amount to be charged for the payment.
     * 
     * 	- `BigDecimal charge`: This represents the monetary value of the payment being
     * processed. It is deserialized from the incoming JSON payload.
     * 
     * @param type type of payment, which determines the specific fields and values
     * included in the generated `PaymentDto`.
     * 
     * @param description description of the payment being made, which is included in the
     * resulting `PaymentDto`.
     * 
     * @param recurring boolean value whether the payment is recurring or not.
     * 
     * @param dueDate LocalDate when the payment is due, which is used to build the `PaymentDto`.
     * 
     * The `LocalDate` object `dueDate` represents the date on which the payment is due.
     * Its toString() method returns a string representation of the date in the format "YYYY-MM-DD".
     * 
     * @param admin user who made the payment, and its value is passed to the
     * `PaymentDto.builder()` method as part of the construction process.
     * 
     * 	- `admin`: A `UserDto` object containing details about the administrator who made
     * the payment. Its attributes include `id`, `username`, `email`, and `role`.
     * 
     * @param member HouseMemberDto object containing information about the member who
     * is responsible for the payment.
     * 
     * 	- `admin`: This is an instance of `UserDto`, representing the user who made the
     * payment.
     * 	- `member`: This is an instance of `HouseMemberDto`, representing the member for
     * whom the payment was made. The `member` object contains several properties, including:
     * 	+ `id`: A unique identifier for the member.
     * 	+ `name`: The member's name.
     * 	+ `email`: The member's email address.
     * 	+ `phone`: The member's phone number.
     * 
     * @returns a `PaymentDto` object built with charge, type, description, recurring,
     * due date, admin, and member parameters.
     * 
     * 	- `charge`: The BigDecimal value representing the amount to be charged.
     * 	- `type`: The string indicating the type of payment (e.g., "invoice", "credit_note").
     * 	- `description`: The string describing the payment (e.g., a brief description of
     * the transaction).
     * 	- `recurring`: A boolean value indicating whether the payment is recurring.
     * 	- `dueDate`: The LocalDate representing the date the payment is due.
     * 	- `admin`: The UserDto object representing the administrator responsible for the
     * payment.
     * 	- `member`: The HouseMemberDto object representing the member associated with the
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
     * creates a mock payment object with all fields except `recurring` set to null, and
     * `recurring` set to false.
     * 
     * @returns a `Payment` object with all fields null or false, except for the `recurring`
     * field.
     * 
     * 	- The `payment` field is null.
     * 	- The `id` field is null.
     * 	- The `amount` field is null.
     * 	- The `currency` field is null.
     * 	- The `due_date` field is null.
     * 	- The `recurring` field is false.
     * 	- The `status` field is null.
     * 	- The `created_at` field is null.
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
