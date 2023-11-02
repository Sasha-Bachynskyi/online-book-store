package com.onlinebookstore.service;

import com.onlinebookstore.dto.BookDto;
import com.onlinebookstore.dto.BookRequestDto;
import com.onlinebookstore.dto.BookSearchParametersDto;
import java.util.List;

public interface BookService {
    BookDto save(BookRequestDto requestDto);

    List<BookDto> getAll();

    BookDto getBookById(Long id);

    void update(Long id, BookRequestDto requestDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto searchParametersDto);
}
