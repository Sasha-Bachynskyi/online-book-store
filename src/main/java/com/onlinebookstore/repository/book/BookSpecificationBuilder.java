package com.onlinebookstore.repository.book;

import com.onlinebookstore.dto.book.BookSearchParametersDto;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.SpecificationBuilder;
import com.onlinebookstore.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder
        implements SpecificationBuilder<Book, BookSearchParametersDto> {
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String PRICE_FROM = "priceFrom";
    private static final String PRICE_TO = "priceTo";
    private final SpecificationProviderManager<Book,
            BookSearchParametersDto> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> spec = Specification.where(null);
        if (searchParametersDto.titles() != null && searchParametersDto.titles().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(TITLE)
                    .getSpecification(searchParametersDto));
        }
        if (searchParametersDto.authors() != null && searchParametersDto.authors().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(AUTHOR)
                    .getSpecification(searchParametersDto));
        }
        if (searchParametersDto.priceFrom() != null) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(PRICE_FROM)
                    .getSpecification(searchParametersDto));
        }
        if (searchParametersDto.priceTo() != null) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(PRICE_TO)
                    .getSpecification(searchParametersDto));
        }
        return spec;
    }
}
