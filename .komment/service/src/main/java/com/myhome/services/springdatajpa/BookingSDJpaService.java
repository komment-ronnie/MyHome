{"name":"BookingSDJpaService.java","path":"service/src/main/java/com/myhome/services/springdatajpa/BookingSDJpaService.java","content":{"structured":{"description":"A `BookingSDJpaService` class that implements a `BookingService` interface using Spring Data JPA. The service provides a method `deleteBooking()` that deletes an amenity booking item from the repository based on its amenity ID and booking ID. The method is transactional and uses the `Optional` class to retrieve the amenity booking item, then delete it if it exists.","image":"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<!-- Generated by graphviz version 2.43.0 (0)\n -->\n<!-- Title: com.myhome.services.springdatajpa.BookingSDJpaService Pages: 1 -->\n<svg width=\"208pt\" height=\"104pt\"\n viewBox=\"0.00 0.00 208.00 104.00\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n<g id=\"graph0\" class=\"graph\" transform=\"scale(1 1) rotate(0) translate(4 100)\">\n<title>com.myhome.services.springdatajpa.BookingSDJpaService</title>\n<!-- Node1 -->\n<g id=\"Node000001\" class=\"node\">\n<title>Node1</title>\n<g id=\"a_Node000001\"><a xlink:title=\" \">\n<polygon fill=\"#999999\" stroke=\"#666666\" points=\"200,-30 0,-30 0,0 200,0 200,-30\"/>\n<text text-anchor=\"start\" x=\"8\" y=\"-18\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.services.springdatajpa.</text>\n<text text-anchor=\"middle\" x=\"100\" y=\"-7\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">BookingSDJpaService</text>\n</a>\n</g>\n</g>\n<!-- Node2 -->\n<g id=\"Node000002\" class=\"node\">\n<title>Node2</title>\n<g id=\"a_Node000002\"><a xlink:href=\"interfacecom_1_1myhome_1_1services_1_1BookingService.html\" target=\"_top\" xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"185,-96 15,-96 15,-66 185,-66 185,-96\"/>\n<text text-anchor=\"start\" x=\"23\" y=\"-84\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.services.Booking</text>\n<text text-anchor=\"middle\" x=\"100\" y=\"-73\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">Service</text>\n</a>\n</g>\n</g>\n<!-- Node2&#45;&gt;Node1 -->\n<g id=\"edge1_Node000001_Node000002\" class=\"edge\">\n<title>Node2&#45;&gt;Node1</title>\n<g id=\"a_edge1_Node000001_Node000002\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M100,-55.54C100,-46.96 100,-37.61 100,-30.16\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"96.5,-55.8 100,-65.8 103.5,-55.8 96.5,-55.8\"/>\n</a>\n</g>\n</g>\n</g>\n</svg>\n","items":[{"id":"30a38543-fa1d-c69b-ab46-298d7fd8513d","ancestors":[],"type":"function","description":"is a Java class that provides booking-related services using Spring Data JPA. The class has a single method, `deleteBooking`, which deletes a booking based on its amenity ID and booking ID. The method uses the `Optional` class to check if a booking exists for the given amenity ID, and then deletes it from the repository if found.","name":"BookingSDJpaService","code":"@Service\n@RequiredArgsConstructor\npublic class BookingSDJpaService implements BookingService {\n\n  private final AmenityBookingItemRepository bookingRepository;\n\n  @Transactional\n  @Override\n  public boolean deleteBooking(String amenityId, String bookingId) {\n    Optional<AmenityBookingItem> booking =\n        bookingRepository.findByAmenityBookingItemId(bookingId);\n    return booking.map(bookingItem -> {\n      boolean amenityFound =\n          bookingItem.getAmenity().getAmenityId().equals(amenityId);\n      if (amenityFound) {\n        bookingRepository.delete(bookingItem);\n        return true;\n      } else {\n        return false;\n      }\n    }).orElse(false);\n  }\n}","location":{"start":11,"insert":11,"offset":" ","indent":0,"comment":null},"item_type":"class","length":23},{"id":"dc3afde3-8383-059e-824f-7be81d68296a","ancestors":["30a38543-fa1d-c69b-ab46-298d7fd8513d"],"type":"function","description":"deletes a booking from the repository based on the amenity ID and the booking ID.","params":[{"name":"amenityId","type_name":"String","description":"ID of the amenity for which the booking is to be deleted, and it is used to filter the booking items in the repository to find the appropriate booking to delete.","complex_type":false},{"name":"bookingId","type_name":"String","description":"id of a booking that needs to be deleted.","complex_type":false}],"returns":{"type_name":"Boolean","description":"a boolean value indicating whether the booking item was successfully deleted.\n\n* `Optional<AmenityBookingItem>` represents an optional booking item that may be present or not in the repository.\n* `map()` method is used to transform the `Optional` into a boolean value by checking if the booking item has the specified amenity ID.\n* `getAmenityId()` method of the `AmenityBookingItem` returns the amenity ID of the booking item.\n* `equals()` method of the `Amenity` class compares the amenity ID of the booking item with the given amenity ID.\n* `delete()` method of the `bookingRepository` deletes the booking item from the repository if the amenity ID matches.\n* The `orElse()` method returns a boolean value indicating whether the booking item was found and deleted successfully or not.","complex_type":true},"usage":{"language":"java","code":"@Transactional\npublic void deleteAmenity(String amenityId, String bookingId) {\n    boolean deleted = bookingService.deleteBooking(amenityId, bookingId);\n    if (deleted) {\n        System.out.println(\"Successfully deleted booking\");\n    } else {\n        System.out.println(\"Failed to delete booking\");\n    }\n}\n","description":""},"name":"deleteBooking","code":"@Transactional\n  @Override\n  public boolean deleteBooking(String amenityId, String bookingId) {\n    Optional<AmenityBookingItem> booking =\n        bookingRepository.findByAmenityBookingItemId(bookingId);\n    return booking.map(bookingItem -> {\n      boolean amenityFound =\n          bookingItem.getAmenity().getAmenityId().equals(amenityId);\n      if (amenityFound) {\n        bookingRepository.delete(bookingItem);\n        return true;\n      } else {\n        return false;\n      }\n    }).orElse(false);\n  }","location":{"start":17,"insert":17,"offset":" ","indent":2,"comment":null},"item_type":"method","length":16}]}}}