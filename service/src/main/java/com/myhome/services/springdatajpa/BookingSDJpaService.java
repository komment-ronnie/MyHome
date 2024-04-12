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
 * class has a single method, `deleteBooking`, which deletes a booking based on its
 * amenity ID and booking ID. The method uses the `Optional` class to check if a
 * booking exists for the given amenity ID, and then deletes it from the repository
 * if found.
 */
@Service
@RequiredArgsConstructor
public class BookingSDJpaService implements BookingService {

  private final AmenityBookingItemRepository bookingRepository;

  /**
   * deletes a booking from the repository based on the amenity ID and the booking ID.
   * 
   * @param amenityId ID of the amenity for which the booking is to be deleted, and it
   * is used to filter the booking items in the repository to find the appropriate
   * booking to delete.
   * 
   * @param bookingId id of a booking that needs to be deleted.
   * 
   * @returns a boolean value indicating whether the booking item was successfully deleted.
   * 
   * 	- `Optional<AmenityBookingItem>` represents an optional booking item that may be
   * present or not in the repository.
   * 	- `map()` method is used to transform the `Optional` into a boolean value by
   * checking if the booking item has the specified amenity ID.
   * 	- `getAmenityId()` method of the `AmenityBookingItem` returns the amenity ID of
   * the booking item.
   * 	- `equals()` method of the `Amenity` class compares the amenity ID of the booking
   * item with the given amenity ID.
   * 	- `delete()` method of the `bookingRepository` deletes the booking item from the
   * repository if the amenity ID matches.
   * 	- The `orElse()` method returns a boolean value indicating whether the booking
   * item was found and deleted successfully or not.
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
