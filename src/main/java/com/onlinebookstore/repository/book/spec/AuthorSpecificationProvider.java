package com.onlinebookstore.repository.book.spec;

import com.onlinebookstore.dto.book.BookSearchParametersDto;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider
        implements SpecificationProvider<Book, BookSearchParametersDto> {
    private static final String AUTHOR = "author";

    @Override
    public String getKey() {
        return AUTHOR;
    }

    @Override
    public Specification<Book> getSpecification(BookSearchParametersDto searchParametersDto) {
        return (root, query, criteriaBuilder) -> root.get(AUTHOR)
                .in(Arrays.stream(searchParametersDto.authors()).toArray());
    }
}
