package com.onlinebookstore.repository;

import com.onlinebookstore.dto.BookSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    String getKey();

    Specification<T> getSpecification(BookSearchParametersDto searchParametersDto);
}
