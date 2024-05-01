package com.myhome.services.springdatajpa;

import com.myhome.domain.AmenityBookingItem;
import com.myhome.repositories.AmenityBookingItemRepository;
import com.myhome.services.BookingService;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * is a Java class that provides booking-related services using Spring Data JPA. It
 * has a single method, `deleteBooking`, which deletes a booking from the repository
 * based on its amenity ID and booking ID. The method uses Optional to check if a
 * booking exists for the given amenity ID and then deletes it from the repository
 * if found.
 */
@Service
@RequiredArgsConstructor
public class BookingSDJpaService implements BookingService {

  private final AmenityBookingItemRepository bookingRepository;

  /**
   * deletes a booking item from the repository based on its amenity booking item ID,
   * returning `true` if the amenity is found and deleted successfully, or `false` otherwise.
   * 
   * @param amenityId id of an amenity that is associated with the booking to be deleted.
   * 
   * @param bookingId identifier of a booking item to be deleted, which is used to
   * locate the corresponding booking item in the repository for deletion.
   * 
   * @returns a boolean value indicating whether the booking was successfully deleted.
   * 
   * 	- The function returns a boolean value indicating whether the booking item was
   * successfully deleted.
   * 	- The function uses the `findByAmenityBookingItemId` method from the `bookingRepository`
   * to locate the booking item with the specified `bookingId`. This method returns an
   * `Optional` object containing the booking item if it exists, or an empty `Optional`
   * if it does not.
   * 	- The function maps the booking item to a boolean value using the `map` method.
   * If the `amenityFound` variable is set to `true`, it means that the booking item
   * corresponds to the specified `amenityId`. Otherwise, it means that the booking
   * item does not correspond to the specified `amenityId`.
   * 	- The function then calls the `delete` method on the `bookingRepository` to delete
   * the booking item.
   * 
   * Overall, the `deleteBooking` function provides a convenient way to delete booking
   * items based on their amenity and booking Id.
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
