package com.onlinebookstore.service.book;

import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.BookRequestDto;
import com.onlinebookstore.dto.book.BookSearchParametersDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(BookRequestDto requestDto);

    List<BookDto> getAll(Pageable pageable);

    BookDto getBookById(Long id);

    void update(Long id, BookRequestDto requestDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto searchParametersDto);
}
