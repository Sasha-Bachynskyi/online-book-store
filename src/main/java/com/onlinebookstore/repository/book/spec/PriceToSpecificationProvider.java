package com.onlinebookstore.repository.book.spec;

import com.onlinebookstore.dto.BookSearchParametersDto;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceToSpecificationProvider implements SpecificationProvider<Book> {
    public static final String PRICE = "price";
    public static final String PRICE_TO = "priceTo";

    @Override
    public String getKey() {
        return PRICE_TO;
    }

    @Override
    public Specification<Book> getSpecification(BookSearchParametersDto searchParametersDto) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(
                root.get(PRICE), searchParametersDto.priceTo());
    }
}
