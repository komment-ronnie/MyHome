{"name":"BookingControllerTest.java","path":"service/src/test/java/com/myhome/controllers/BookingControllerTest.java","content":{"structured":{"description":"A unit test for a BookingController class that handles booking-related operations. The test cases include testing the deleteBooking method's behavior when the booking exists and when it doesn't exist. The code uses Spring Mockito to inject mock dependencies and verify their usage in the controller.","items":[{"id":"87a20912-2770-f3b9-8747-8cda175b2fae","ancestors":[],"type":"function","description":"is a unit test class for the BookingController class, with tests for deleting bookings. The class has a mocked BookingService interface injected into the BookingController, and uses Mockito to verify the calls made to the service. The tests include assertions for the response body and status code after calling the deleteBooking method.","name":"BookingControllerTest","code":"public class BookingControllerTest {\n\n  private final String TEST_AMENITY_ID = \"test-amenity-id\";\n  private static final String TEST_BOOKING_ID = \"test-booking-id\";\n\n  @Mock\n  private BookingService bookingSDJpaService;\n\n  @InjectMocks\n  private BookingController bookingController;\n\n  @BeforeEach\n  private void init() {\n    MockitoAnnotations.initMocks(this);\n  }\n\n  @Test\n  void deleteBooking() {\n    // given\n    given(bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID))\n        .willReturn(true);\n\n    // when\n    ResponseEntity<Void> response =\n        bookingController.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertNull(response.getBody());\n    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());\n    verify(bookingSDJpaService).deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n  }\n\n  @Test\n  void deleteBookingNotExists() {\n    // given\n    given(bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID))\n        .willReturn(false);\n\n    // when\n    ResponseEntity<Void> response =\n        bookingController.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertNull(response.getBody());\n    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());\n    verify(bookingSDJpaService).deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n  }\n}","location":{"start":17,"insert":17,"offset":" ","indent":0,"comment":null},"item_type":"class","length":48},{"id":"554aaeb7-f84b-80ae-1d47-e1fecbf200ed","ancestors":["87a20912-2770-f3b9-8747-8cda175b2fae"],"type":"function","description":"initializes mock objects using MockitoAnnotations, enabling the use of mocking frameworks for test purposes.","params":[],"usage":{"language":"java","code":"@BeforeEach\n  private void init() {\n    MockitoAnnotations.initMocks(this);\n  }\n","description":"\nInit is a BeforeEach annotated method that initializes the test by mocking all of the services used in the class."},"name":"init","code":"@BeforeEach\n  private void init() {\n    MockitoAnnotations.initMocks(this);\n  }","location":{"start":28,"insert":28,"offset":" ","indent":2,"comment":null},"item_type":"method","length":4},{"id":"1280aaa2-8be3-86ab-9446-7b601a254e7f","ancestors":["87a20912-2770-f3b9-8747-8cda175b2fae"],"type":"function","description":"tests the deletion of a booking by passing the ammenity ID and the booking ID to the `bookingController`. It then verifies that the response is null, the status code is NO_CONTENT, and that the `bookingSDJpaService` method was called with the correct parameters.","params":[],"usage":{"language":"java","code":"@Test\n  void deleteBooking() {\n    // given\n    given(bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID))\n        .willReturn(true);\n\n    // when\n    ResponseEntity<Void> response =\n        bookingController.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertNull(response.getBody());\n    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());\n    verify(bookingSDJpaService).deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n  }\n","description":""},"name":"deleteBooking","code":"@Test\n  void deleteBooking() {\n    // given\n    given(bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID))\n        .willReturn(true);\n\n    // when\n    ResponseEntity<Void> response =\n        bookingController.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertNull(response.getBody());\n    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());\n    verify(bookingSDJpaService).deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n  }","location":{"start":33,"insert":33,"offset":" ","indent":2,"comment":null},"item_type":"method","length":15},{"id":"8675ec74-d0df-f698-064c-c8470246b929","ancestors":["87a20912-2770-f3b9-8747-8cda175b2fae"],"type":"function","description":"verifies that a booking with the given ID does not exist in the database and throws a `HttpStatus.NOT_FOUND` response when tried to delete it through the controller method.","params":[],"usage":{"language":"java","code":"@Test\n  void deleteBookingNotExists() {\n    // given\n    given(bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID))\n        .willReturn(false);\n\n    // when\n    ResponseEntity<Void> response =\n        bookingController.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertNull(response.getBody());\n    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());\n    verify(bookingSDJpaService).deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n  }\n","description":""},"name":"deleteBookingNotExists","code":"@Test\n  void deleteBookingNotExists() {\n    // given\n    given(bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID))\n        .willReturn(false);\n\n    // when\n    ResponseEntity<Void> response =\n        bookingController.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertNull(response.getBody());\n    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());\n    verify(bookingSDJpaService).deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n  }","location":{"start":49,"insert":49,"offset":" ","indent":2,"comment":null},"item_type":"method","length":15}]}}}