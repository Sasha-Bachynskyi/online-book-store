package com.onlinebookstore.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T, P> {
    String getKey();

    Specification<T> getSpecification(P parameters);
}
