package com.myhome.services;

/**
 * provides a method for deleting bookings based on amenity ID and booking ID.
 */
public interface BookingService {

  boolean deleteBooking(String amenityId, String bookingId);

}
