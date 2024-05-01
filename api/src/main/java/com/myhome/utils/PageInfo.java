package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * is a data structure that provides information about the current page and total
 * pages of a paginated dataset, including the number of elements in each page and
 * the total number of elements in the dataset. It can transform a `Pageable` object
 * and a `Page` object into a `PageInfo` object, providing information on the current
 * page number, size, total pages, and total elements.
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
   * takes a `pageable` parameter and a `page` parameter, returning a `PageInfo` object
   * containing information about the current page of elements.
   * 
   * @param pageable Pageable interface, which provides the ability to retrieve a page
   * of elements from a larger collection, allowing for pagination and querying of the
   * larger collection.
   * 
   * 	- `getPageNumber()`: The page number of the result set.
   * 	- `getPageSize()`: The number of elements in each page of the result set.
   * 	- `getTotalPages()`: The total number of pages in the result set.
   * 	- `getTotalElements()`: The total number of elements in the result set.
   * 
   * @param page current page being processed, providing the total number of elements
   * and pages available for the specified pageable.
   * 
   * 	- `pageable`: The pageable object containing information about the current page
   * being processed.
   * 	- `page`: The page object representing the data to be paginated.
   * 	- `totalPages`: The total number of pages in the dataset.
   * 	- `totalElements`: The total number of elements in the dataset.
   * 
   * @returns a `PageInfo` object containing various pagination-related metrics.
   * 
   * 1/ Page number: The page number of the resultant page, which is represented by an
   * integer value between 1 and the total number of pages.
   * 2/ Page size: The number of elements in a page, represented by an integer value.
   * 3/ Total pages: The total number of pages in the resultant page set, also represented
   * by an integer value.
   * 4/ Total elements: The total number of elements in the resultant page set, represented
   * by an integer value.
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
