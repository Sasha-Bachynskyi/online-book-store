package com.onlinebookstore.repository.book.spec;

import com.onlinebookstore.dto.BookSearchParametersDto;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceFromSpecificationProvider implements SpecificationProvider<Book> {
    private static final String PRICE = "price";
    private static final String PRICE_FROM = "priceFrom";

    @Override
    public String getKey() {
        return PRICE_FROM;
    }

    @Override
    public Specification<Book> getSpecification(BookSearchParametersDto searchParametersDto) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(
                root.get(PRICE), searchParametersDto.priceFrom());
    }
}
