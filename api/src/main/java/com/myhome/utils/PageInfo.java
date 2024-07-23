package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Represents paginated data and provides information about the current page of a
 * dataset. It is used to encapsulate pagination metadata such as the current page
 * number, total pages, total elements, and page size. The class has a static factory
 * method that creates a PageInfo object from a Pageable and a Page objects.
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
   * Creates an instance of the `PageInfo` class, initializing it with parameters from
   * a `Pageable` object and a `Page` object. The resulting `PageInfo` contains page
   * number, page size, total pages, and total elements from the given pagination data.
   * 
   * @param pageable pagination information, providing methods to retrieve the current
   * page number and size.
   * 
   * @param page result of the pagination operation, providing information about the
   * total number of pages and elements.
   * 
   * @returns a `PageInfo` object containing pagination details.
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
