package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * is a data structure that represents the current page and total pages of a paginated
 * dataset, along with the total number of elements in the dataset. It provides a
 * convenient way to pass around this information when working with large datasets.
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
   * transforms a `Pageable` object and a `Page` object into a `PageInfo` object,
   * providing information on the current page number, size, total pages, and total elements.
   * 
   * @param pageable pageable object that contains information about the pagination of
   * the data, which is used to calculate the page number, size, and total pages and
   * elements of the resultant page info.
   * 
   * 	- `getPageNumber(): int`: The page number of the current page being processed.
   * 	- `getPageSize(): int`: The number of elements in a single page of the data set.
   * 	- `getTotalPages(): int`: The total number of pages in the data set.
   * 	- `getTotalElements(): int`: The total number of elements in the data set.
   * 
   * @param page current page of elements being processed, which is used to calculate
   * the total pages and elements in the PageInfo object returned by the function.
   * 
   * 	- `pageNumber`: The page number that contains the elements being processed.
   * 	- `pageSize`: The number of elements in each page.
   * 	- `totalPages`: The total number of pages in the entire dataset.
   * 	- `totalElements`: The total number of elements in the dataset.
   * 
   * @returns a `PageInfo` object containing information about the current page of a
   * paginated result.
   * 
   * 	- pageable.getPageNumber(): The number of the current page being displayed.
   * 	- pageable.getPageSize(): The number of elements per page in the paginated list.
   * 	- page.getTotalPages(): The total number of pages available for display.
   * 	- page.getTotalElements(): The total number of elements in the paginated list.
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
