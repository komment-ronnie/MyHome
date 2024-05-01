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
   * deletes a booking based on its amenity ID and booking ID, returning a HTTP status
   * code indicating the result of the operation.
   * 
   * @param amenityId ID of an amenity that is associated with the booking to be deleted.
   * 
   * @param bookingId ID of the booking to be deleted.
   * 
   * @returns a HTTP NO_CONTENT status code indicating that the booking was successfully
   * deleted.
   * 
   * 	- `HttpStatus.NO_CONTENT`: indicates that the booking was successfully deleted
   * 	- `HttpStatus.NOT_FOUND`: indicates that the booking could not be found
   * 
   * The function returns a `ResponseEntity` object with the status code as its property.
   * The `build()` method is used to create the response entity.
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
