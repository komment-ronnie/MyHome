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
   * takes a `pageable` and a `page` object as input, and returns a `PageInfo` object
   * containing information about the page of data.
   * 
   * @param pageable pagination information for the page being processed, providing the
   * page number, page size, total pages, and total elements.
   * 
   * 	- `getPageNumber()`: Returns the page number of the input.
   * 	- `getPageSize()`: Returns the size of each page in the input.
   * 	- `getTotalPages()`: Returns the total number of pages in the input.
   * 	- `getTotalElements()`: Returns the total number of elements in the input.
   * 
   * @param page current page being processed, providing the total number of elements
   * on that page.
   * 
   * 	- `pageNumber`: The page number in the paginated sequence.
   * 	- `pageSize`: The number of elements per page in the paginated sequence.
   * 	- `totalPages`: The total number of pages in the paginated sequence.
   * 	- `totalElements`: The total number of elements in the paginated sequence.
   * 
   * @returns a `PageInfo` object containing information about the page and total number
   * of elements.
   * 
   * 	- The `pageNumber` field represents the page number of the current page being displayed.
   * 	- The `pageSize` field signifies the number of elements per page.
   * 	- The `totalPages` field indicates the total number of pages in the dataset.
   * 	- The `totalElements` field shows the overall number of elements in the dataset.
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
