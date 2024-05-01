package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * from the file provides a structure for storing and manipulating pagination-related
 * metrics. It contains four fields: currentPage, pageLimit, totalPages, and
 * totalElements. The class offers a `of()` method for constructing a `PageInfo`
 * object based on a `Pageable` and a `Page` parameter.
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
   * creates a `PageInfo` object containing information about the number of pages, page
   * size, total pages, and total elements for a given `Pageable` and `Page`.
   * 
   * @param pageable pageable object that contains information about the current page
   * of data to be processed, including the page number and size.
   * 
   * 	- `getPageNumber()` - Returns the current page number being rendered.
   * 	- `getPageSize()` - Returns the maximum number of elements that can be displayed
   * per page.
   * 	- `getTotalPages()` - Returns the total number of pages in the dataset.
   * 	- `getTotalElements()` - Returns the total number of elements in the dataset.
   * 
   * @param page current page being processed, providing information on its position
   * within the overall set of pages and the total number of pages and elements available.
   * 
   * 	- The first property is `pageable.getPageNumber()`, which represents the page
   * number of the paginated result.
   * 	- The second property is `pageable.getPageSize()`, which signifies the number of
   * elements per page in the paginated result.
   * 	- The third property is `page.getTotalPages()`, which indicates the total number
   * of pages in the result set.
   * 	- Finally, the fourth property is `page.getTotalElements()` which represents the
   * total number of elements in the result set.
   * 
   * @returns a `PageInfo` object containing information about the page number, size,
   * total pages, and total elements of a given `Pageable` and `Page`.
   * 
   * 	- The `PageNumber` field represents the current page being accessed.
   * 	- The `PageSize` field denotes the number of elements displayed per page.
   * 	- The `TotalPages` field indicates the total number of pages available in the collection.
   * 	- The `TotalElements` field displays the sum of all elements in the collection.
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
