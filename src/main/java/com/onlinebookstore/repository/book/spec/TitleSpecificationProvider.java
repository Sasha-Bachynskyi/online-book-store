package com.onlinebookstore.repository.book.spec;

import com.onlinebookstore.dto.BookSearchParametersDto;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    private static final String TITLE = "title";

    @Override
    public String getKey() {
        return TITLE;
    }

    @Override
    public Specification<Book> getSpecification(BookSearchParametersDto searchParametersDto) {
        return (root, query, criteriaBuilder) -> root.get(TITLE)
                .in(Arrays.stream(searchParametersDto.titles()).toArray());
    }
}
