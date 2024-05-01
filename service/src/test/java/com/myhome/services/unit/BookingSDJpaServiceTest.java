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
 * is a test class for the BookingSDJpaService class, which is responsible for deleting
 * bookings from a repository. The test class provides methods to delete bookings and
 * verify that they are deleted correctly. Additionally, the test class also verifies
 * that the amenity associated with the booking is not updated when the booking is deleted.
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
   * initializes mocks for the class using MockitoAnnotations.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * deletes a booking item from the repository, given its amenity booking item ID and
   * the booking ID. It also verifies the delete operation on the repository and the
   * booking item.
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
   * tests whether the booking with the given amenity ID and booking ID does not exist
   * in the repository before deleting it using the `bookingSDJpaService`.
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
   * tests whether deleting a booking with an amenity ID that does not exist in the
   * database throws expected exceptions and behaves as expected when the amenity is
   * updated before deletion.
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
   * creates a new instance of the `AmenityBookingItem` class with a pre-defined ID for
   * testing purposes.
   * 
   * @returns a new instance of the `AmenityBookingItem` class with a predefined `amenityBookingItemId`.
   * 
   * 	- `AmenityBookingItemId`: A unique identifier for this booking item, set to `TEST_BOOKING_ID`.
   */
  private AmenityBookingItem getTestBookingItem() {
    return new AmenityBookingItem()
        .withAmenityBookingItemId(TEST_BOOKING_ID);
  }
}
