package com.myhome.services.unit;

import com.myhome.domain.AmenityBookingItem;
import com.myhome.repositories.AmenityBookingItemRepository;
import com.myhome.services.springdatajpa.BookingSDJpaService;
import helpers.TestUtils;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * is a test class that verifies the behavior of the BookingSDJpaService class, which
 * is responsible for managing bookings in a repository. The test class uses mocking
 * to verify the behavior of the class and its interactions with the repository. The
 * tests cover various scenarios such as deleting a non-existent booking, deleting a
 * booking that does not have an associated amenity, and deleting a booking that has
 * an invalid amenity ID.
 */
public class BookingSDJpaServiceTest {

  private static final String TEST_BOOKING_ID = "test-booking-id";
  private static final String TEST_AMENITY_ID = "test-amenity-id";
  private static final String TEST_AMENITY_ID_2 = "test-amenity-id-2";
  private final String TEST_AMENITY_DESCRIPTION = "test-amenity-description";

  @Mock
  private AmenityBookingItemRepository bookingItemRepository;

  @InjectMocks
  private BookingSDJpaService bookingSDJpaService;

  /**
   * initializes mock objects using MockitoAnnotations.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * deletes a booking item from the repository, given its amenity booking item ID and
   * the booking ID. It verifies that the booking item is deleted and updates the
   * repository with the changes.
   */
  @Test
  void deleteBookingItem() {
    // given
    AmenityBookingItem testBookingItem = getTestBookingItem();

    given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))
        .willReturn(Optional.of(testBookingItem));
    testBookingItem.setAmenity(TestUtils.AmenityHelpers
        .getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION));

    // when
    boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);

    // then
    assertTrue(bookingDeleted);
    verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);
    verify(bookingItemRepository).delete(testBookingItem);
  }

  /**
   * tests whether a booking can be deleted if it does not exist in the repository.
   */
  @Test
  void deleteBookingNotExists() {
    // given
    given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))
        .willReturn(Optional.empty());

    // when
    boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);

    // then
    assertFalse(bookingDeleted);
    verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);
    verify(bookingItemRepository, never()).delete(any());
  }

  /**
   * tests whether a booking can be deleted when the amenity associated with it does
   * not exist. It sets up a test booking item, saves it, and then tries to delete the
   * booking using the amenity ID that does not exist. The function verifies that the
   * booking is not deleted and the amenity ID is not updated.
   */
  @Test
  void deleteBookingAmenityNotExists() {
    // given
    AmenityBookingItem testBookingItem = getTestBookingItem();

    given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))
        .willReturn(Optional.of(testBookingItem));
    testBookingItem.setAmenity(TestUtils.AmenityHelpers
        .getTestAmenity(TEST_AMENITY_ID_2, TEST_AMENITY_DESCRIPTION));
    // when
    boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);

    // then
    assertFalse(bookingDeleted);
    assertNotEquals(TEST_AMENITY_ID, testBookingItem.getAmenity().getAmenityId());
    verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);
    verify(bookingItemRepository, never()).delete(any());
  }

  /**
   * creates a new instance of `AmenityBookingItem` with a predefined ID for testing purposes.
   * 
   * @returns a new instance of `AmenityBookingItem` with a pre-defined ID.
   * 
   * 	- `AmenityBookingItemId`: A unique identifier for the booking item, set to `TEST_BOOKING_ID`.
   * 	- No other attributes or properties are defined in the provided code snippet.
   */
  private AmenityBookingItem getTestBookingItem() {
    return new AmenityBookingItem()
        .withAmenityBookingItemId(TEST_BOOKING_ID);
  }
}
