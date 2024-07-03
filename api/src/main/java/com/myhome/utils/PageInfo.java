package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Is a data structure that provides information about the current page and total
 * pages of a paginated dataset, along with the total number of elements in the
 * dataset. It accepts a Pageable object and a Page object as inputs and returns a
 * PageInfo object containing information on the current page number, size, total
 * pages, and total elements.
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
   * Takes a `pageable` and a `page` argument and returns a `PageInfo` object containing
   * information about the page number, size, total pages, and total elements.
   * 
   * @param pageable Pageable interface, which provides methods for retrieving a page
   * of elements from a source of data.
   * 
   * @param page current page of data being processed, providing the total number of
   * elements on that page.
   * 
   * @returns a `PageInfo` object containing page metadata.
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
