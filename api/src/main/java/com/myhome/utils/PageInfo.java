package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * is a data structure that provides information about a page of results. It includes
 * the current page number, page limit, total pages, and total elements. The class
 * also offers a static method for creating a new instance of the class with the
 * specified pageable and page parameters.
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
   * generates a `PageInfo` object from a given `Pageable` and `Page`. The object
   * contains information on the current page, size, total pages, and total elements.
   * 
   * @param pageable Pageable object that contains the information about the current
   * page of results, including the page number and the number of elements per page.
   * 
   * 	- `getPageNumber()` returns the page number of the page being processed.
   * 	- `getPageSize()` returns the number of elements in a page.
   * 	- `getTotalPages()` returns the total number of pages in the result set.
   * 	- `getTotalElements()` returns the total number of elements in the result set.
   * 
   * @param page current page being processed, providing the total number of elements
   * on that page and the page number within the overall result set.
   * 
   * 	- `pageNumber`: The number of the current page being served.
   * 	- `pageSize`: The number of elements in a single page.
   * 	- `totalPages`: The total number of pages in the result set.
   * 	- `totalElements`: The total number of elements returned by the query.
   * 
   * @returns a `PageInfo` object containing information about the page number, size,
   * total pages, and total elements of the given `Pageable` and `Page`.
   * 
   * 	- `pageNumber`: The page number where the element resides.
   * 	- `pageSize`: The number of elements per page.
   * 	- `totalPages`: The total number of pages in the result set.
   * 	- `totalElements`: The total number of elements in the result set.
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
