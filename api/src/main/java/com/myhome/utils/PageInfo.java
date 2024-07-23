package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Encapsulates pagination metadata, providing an object that represents paginated
 * data. It has a static factory method to create an instance from Pageable and Page
 * objects, containing page number, page size, total pages, and total elements. This
 * class is used to provide information about the current page of a dataset.
 */
@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PageInfo {
  private final int currentPage;
  private final int pageLimit;
  private final int totalPages;
  private final long totalElements;

  /**
   * Constructs a `PageInfo` object, which encapsulates pagination metadata from a given
   * `Pageable` and `Page` instance. The object contains information about the current
   * page number, page size, total pages, and total elements.
   * 
   * @param pageable page and size of the data being paginated, allowing for retrieval
   * of specific pages of data from a larger dataset.
   * 
   * @param page result of a query executed against the data store, providing information
   * about the total number of elements and pages.
   * 
   * @returns an instance of `PageInfo` with specific page and total element details.
   */
  public static PageInfo of(Pageable pageable, Page<?> page) {
    return new PageInfo(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        page.getTotalPages(),
        page.getTotalElements()
    );
  }
}
