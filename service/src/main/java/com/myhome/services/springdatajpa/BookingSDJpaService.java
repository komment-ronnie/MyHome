package com.myhome.services.springdatajpa;

import com.myhome.domain.AmenityBookingItem;
import com.myhome.repositories.AmenityBookingItemRepository;
import com.myhome.services.BookingService;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * is a Java class that provides booking-related services using Spring Data JPA. The
 * deleteBooking method deletes a booking item from the repository based on its amenity
 * booking item ID, returning true if the amenity is found and deleted successfully,
 * or false otherwise.
 */
@Service
@RequiredArgsConstructor
public class BookingSDJpaService implements BookingService {

  private final AmenityBookingItemRepository bookingRepository;

  /**
   * retrieves an amenity booking item from the repository based on its id, checks if
   * the amenity id matches the given amenity id, and deletes it from the repository
   * if it is a match.
   * 
   * @param amenityId id of the amenity that is associated with the booking to be deleted.
   * 
   * @param bookingId identifier of a booking to be deleted.
   * 
   * @returns a boolean value indicating whether the booking was successfully deleted.
   * 
   * 	- The function returns a boolean value indicating whether the booking item was
   * successfully deleted or not.
   * 	- The function uses the `Optional` class to handle the case where the booking
   * item is not found. If the booking item is not found, the function returns `false`.
   * 	- The function calls the `delete` method on the `bookingRepository` to delete the
   * booking item.
   * 	- The function uses a ternary operator to simplify the conditionals and make the
   * code more readable.
   */
  @Transactional
  @Override
  public boolean deleteBooking(String amenityId, String bookingId) {
    Optional<AmenityBookingItem> booking =
        bookingRepository.findByAmenityBookingItemId(bookingId);
    return booking.map(bookingItem -> {
      boolean amenityFound =
          bookingItem.getAmenity().getAmenityId().equals(amenityId);
      if (amenityFound) {
        bookingRepository.delete(bookingItem);
        return true;
      } else {
        return false;
      }
    }).orElse(false);
  }
}
