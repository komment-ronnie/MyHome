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
 * the total number of elements in the dataset. The class provides methods for
 * transforming a `Pageable` object and a `Page` object into a `PageInfo` object, and
 * for returning a `PageInfo` object containing information about the current page
 * of a paginated result.
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
   * takes a `pageable` and a `page` as input, returning a `PageInfo` object containing
   * information about the page number, size, total pages, and total elements.
   * 
   * @param pageable pageable object that contains information about the current page
   * of data being processed, including the page number and size.
   * 
   * 	- `getPageNumber()` - Returns the page number of the current page being served.
   * 	- `getPageSize()` - Returns the number of elements that can be displayed on a
   * single page.
   * 	- `getTotalPages()` - Returns the total number of pages in the result set.
   * 	- `getTotalElements()` - Returns the total number of elements in the result set.
   * 
   * @param page current page being processed, providing information on its position
   * within the paginated range and the total number of pages and elements available.
   * 
   * 	- `pageable`: The pageable object containing the page number, size, and total pages.
   * 	- `page`: The actual page object representing the data to be paginated.
   * 	- `totalPages`: The total number of pages in the dataset.
   * 	- `totalElements`: The total number of elements in the dataset.
   * 
   * @returns a `PageInfo` object containing page number, size, total pages, and total
   * elements.
   * 
   * 	- The first parameter, `pageable`, represents the pageable object that contains
   * information about the current page being processed.
   * 	- The second parameter, `page`, refers to the specific page being processed,
   * containing details such as the total number of pages and elements in the collection.
   * 	- The `PageInfo` object returned by the function encapsulates these parameters
   * into a single entity, providing easy access to the relevant information.
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
