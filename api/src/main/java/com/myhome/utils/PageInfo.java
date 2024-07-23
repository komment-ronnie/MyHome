package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Represents information about a paginated dataset, including the current page number,
 * total pages, and total elements. It provides a convenient way to pass around this
 * information when working with large datasets. The class has a static method that
 * transforms Pageable and Page objects into a PageInfo object, providing information
 * on the current page number, size, total pages, and total elements.
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
   * Creates a new instance of `PageInfo`, taking four parameters: the current page
   * number, the size of each page, the total number of pages, and the total number of
   * elements. It returns an object containing these values.
   * 
   * @param pageable pagination criteria, providing information about the current page
   * number and page size.
   * 
   * @param page result of pagination, providing the total pages and elements.
   * 
   * @returns a `PageInfo` object containing page and size information.
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
