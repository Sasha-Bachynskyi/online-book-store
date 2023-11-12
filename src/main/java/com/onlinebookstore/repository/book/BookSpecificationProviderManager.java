package com.onlinebookstore.repository.book;

import com.onlinebookstore.dto.book.BookSearchParametersDto;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.SpecificationProvider;
import com.onlinebookstore.repository.SpecificationProviderManager;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager
        implements SpecificationProviderManager<Book, BookSearchParametersDto> {
    private final List<SpecificationProvider<Book,
            BookSearchParametersDto>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book, BookSearchParametersDto> getSpecificationProvider(
            String key) {
        return bookSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Can't find correct "
                        + "specification provider for key " + key));
    }
}
