package com.myhome.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Encapsulates pagination metadata, providing a representation of paginated data.
 * It has a static factory method that creates an instance from Pageable and Page
 * objects, containing page number, page size, total pages, and total elements. This
 * class is designed to encapsulate pagination information.
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
   * Constructs a `PageInfo` object from provided parameters, including the page number,
   * page size, total pages, and total elements. It takes two inputs: a `Pageable`
   * instance and a `Page` object. The resulting object contains metadata about the
   * pagination result.
   * 
   * @param pageable pager parameters for pagination, providing the current page number
   * and size of elements per page to be used for generating the PageInfo object.
   * 
   * @param page 1-based paged results, providing total pages and total elements for
   * pagination purposes.
   * 
   * @returns a `PageInfo` object with four properties.
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
