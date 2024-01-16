package com.onlinebookstore.service.book;

import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.onlinebookstore.dto.book.BookRequestDto;
import com.onlinebookstore.dto.book.BookSearchParametersDto;
import com.onlinebookstore.exception.EntityNotFoundException;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.Category;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.book.BookSpecificationBuilder;
import com.onlinebookstore.repository.category.CategoryRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(BookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        if (!CollectionUtils.isEmpty(requestDto.getCategoryIds())) {
            book.setCategories(getCategoriesByIds(requestDto.getCategoryIds()));
        }
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
        return bookMapper.toDto(bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Couldn't find Book by id " + id)));
    }

    @Override
    public void update(Long id, BookRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Couldn't find Book by id " + id)
        );
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());
        if (!CollectionUtils.isEmpty(requestDto.getCategoryIds())) {
            book.setCategories(getCategoriesByIds(requestDto.getCategoryIds()));
        }
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

    @Override
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id) {
        return bookRepository.findAllByCategoriesId(id).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    private Set<Category> getCategoriesByIds(Set<Long> categoryIds) {
        return categoryIds.stream()
                .map(categoryRepository::findById)
                .map(c -> c.orElseThrow(() -> new EntityNotFoundException(
                        "Can't find category by ids: " + categoryIds)))
                .collect(Collectors.toSet());
    }
}
