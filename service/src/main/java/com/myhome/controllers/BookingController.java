package com.myhome.controllers;

import com.myhome.api.BookingsApi;
import com.myhome.services.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * is a Spring Boot RESTful controller that implements the BookingsApi interface. It
 * has a single method, deleteBooking(), which takes two path variables (amenityId
 * and bookingId) and deletes a booking based on those IDs. The method returns a
 * ResponseEntity with either NO_CONTENT or NOT_FOUND status code depending on whether
 * the booking was successfully deleted or not.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class BookingController implements BookingsApi {

  private final BookingService bookingSDJpaService;

  /**
   * deletes a booking based on its amenity ID and ID, returning a HTTP response code
   * indicating whether the deletion was successful or not.
   * 
   * @param amenityId identifier of an amenity for which a booking is to be deleted.
   * 
   * @param bookingId identifier of the booking to be deleted.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the booking was successfully deleted.
   * 
   * 	- `HttpStatus.NO_CONTENT`: This status code indicates that the booking was
   * successfully deleted, and no content was returned in response.
   * 	- `HttpStatus.NOT_FOUND`: This status code indicates that the booking could not
   * be found, and therefore it was not deleted.
   */
  @Override
  public ResponseEntity<Void> deleteBooking(@PathVariable String amenityId,
      @PathVariable String bookingId) {
    boolean isBookingDeleted = bookingSDJpaService.deleteBooking(amenityId, bookingId);
    if (isBookingDeleted) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
