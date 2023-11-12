package com.onlinebookstore.service.book;

import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.BookRequestDto;
import com.onlinebookstore.dto.book.BookSearchParametersDto;
import com.onlinebookstore.exception.EntityNotFoundException;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.book.BookSpecificationBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(BookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookDto getBookById(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        return bookMapper.toDto(optionalBook.orElseThrow(
                () -> new EntityNotFoundException("Couldn't find Book by id " + id)));
    }

    @Override
    public void update(Long id, BookRequestDto requestDto) {
        getBookById(id);
        Book book = bookMapper.toModel(requestDto);
        book.setId(id);
        bookRepository.save(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto searchParametersDto) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParametersDto);
        return bookRepository.findAll(bookSpecification).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
