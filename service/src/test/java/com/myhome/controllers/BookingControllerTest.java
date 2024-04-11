package com.myhome.controllers;

import com.myhome.services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * is a unit test class for the BookingController class, with tests for deleting
 * bookings. The class has a mocked BookingService interface injected into the
 * BookingController, and uses Mockito to verify the calls made to the service. The
 * tests include assertions for the response body and status code after calling the
 * deleteBooking method.
 */
public class BookingControllerTest {

  private final String TEST_AMENITY_ID = "test-amenity-id";
  private static final String TEST_BOOKING_ID = "test-booking-id";

  @Mock
  private BookingService bookingSDJpaService;

  @InjectMocks
  private BookingController bookingController;

  /**
   * initializes mock objects using MockitoAnnotations, enabling the use of mocking
   * frameworks for test purposes.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * tests the deletion of a booking by passing the ammenity ID and the booking ID to
   * the `bookingController`. It then verifies that the response is null, the status
   * code is NO_CONTENT, and that the `bookingSDJpaService` method was called with the
   * correct parameters.
   */
  @Test
  void deleteBooking() {
    // given
    given(bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID))
        .willReturn(true);

    // when
    ResponseEntity<Void> response =
        bookingController.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);

    // then
    assertNull(response.getBody());
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(bookingSDJpaService).deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);
  }

  /**
   * verifies that a booking with the given ID does not exist in the database and throws
   * a `HttpStatus.NOT_FOUND` response when tried to delete it through the controller
   * method.
   */
  @Test
  void deleteBookingNotExists() {
    // given
    given(bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID))
        .willReturn(false);

    // when
    ResponseEntity<Void> response =
        bookingController.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);

    // then
    assertNull(response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(bookingSDJpaService).deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);
  }
}
