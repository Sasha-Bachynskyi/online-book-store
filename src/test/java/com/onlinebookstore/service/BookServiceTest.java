package com.onlinebookstore.service;

import static org.mockito.ArgumentMatchers.anyLong;

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
import com.onlinebookstore.repository.book.spec.TitleSpecificationProvider;
import com.onlinebookstore.repository.category.CategoryRepository;
import com.onlinebookstore.service.book.BookServiceImpl;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static BookRequestDto requestDto;
    private static BookRequestDto requestDtoNonExistentCategoryIds;
    private static BookRequestDto updatedRequestDto;
    private static Book book;
    private static Book updatedBook;
    private static Category existentCategory;
    private static BookDto bookDto;
    private static BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds;
    private static Category nonExistentCategory;
    private static Pageable pageable;
    private static BookSearchParametersDto searchParametersDto;
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @Mock
    private TitleSpecificationProvider titleSpecificationProvider;

    @BeforeAll
    public static void initBeforeAll() {
        existentCategory = new Category();
        existentCategory.setId(1L);
        existentCategory.setName("Fantasy");

        requestDto = new BookRequestDto();
        requestDto.setTitle("Harry Potter and the Philosopher's Stone");
        requestDto.setAuthor("J. K. Rowling");
        requestDto.setIsbn("0-061-96436-0");
        requestDto.setPrice(BigDecimal.valueOf(10.99));
        requestDto.setCategoryIds(Set.of(existentCategory.getId()));

        book = new Book();
        book.setId(1L);
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setCategories(Set.of(existentCategory));

        bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setCategoryIds(Set.of(1L));

        bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds.setId(book.getId());
        bookDtoWithoutCategoryIds.setTitle(book.getTitle());
        bookDtoWithoutCategoryIds.setAuthor(book.getAuthor());
        bookDtoWithoutCategoryIds.setIsbn(book.getIsbn());
        bookDtoWithoutCategoryIds.setPrice(book.getPrice());

        nonExistentCategory = new Category();
        nonExistentCategory.setId(100L);

        requestDtoNonExistentCategoryIds = new BookRequestDto();
        requestDtoNonExistentCategoryIds.setTitle(requestDto.getTitle());
        requestDtoNonExistentCategoryIds.setAuthor(requestDto.getAuthor());
        requestDtoNonExistentCategoryIds.setIsbn(requestDto.getIsbn());
        requestDtoNonExistentCategoryIds.setPrice(requestDto.getPrice());
        requestDtoNonExistentCategoryIds.setCategoryIds(Set.of(nonExistentCategory.getId()));

        updatedRequestDto = new BookRequestDto();
        updatedRequestDto.setTitle(requestDto.getTitle());
        updatedRequestDto.setAuthor(requestDto.getAuthor());
        updatedRequestDto.setIsbn(requestDto.getIsbn());
        updatedRequestDto.setPrice(BigDecimal.valueOf(8.95));
        updatedRequestDto.setCategoryIds(Set.of(existentCategory.getId()));

        updatedBook = new Book();
        updatedBook.setId(book.getId());
        updatedBook.setTitle(book.getTitle());
        updatedBook.setAuthor(book.getAuthor());
        updatedBook.setIsbn(book.getIsbn());
        updatedBook.setPrice(updatedRequestDto.getPrice());
        updatedBook.setCategories(Set.of(existentCategory));

        searchParametersDto = new BookSearchParametersDto(new String[]{requestDto.getTitle()},
                null, null, null);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidBookRequestDto_Success() {
        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(categoryRepository.findById(existentCategory.getId()))
                .thenReturn(Optional.of(existentCategory));
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.save(requestDto);

        Assertions.assertEquals(bookDto, actual);
        Mockito.verifyNoMoreInteractions(bookRepository, categoryRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify save() method with non existent category should throw Exception")
    void save_InvalidCategoryIdsInBookRequestDto_ShouldThrowException() {
        Mockito.when(bookMapper.toModel(requestDtoNonExistentCategoryIds)).thenReturn(book);
        Mockito.when(categoryRepository.findById(nonExistentCategory.getId()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookService.save(requestDtoNonExistentCategoryIds)
        );

        String expected = "Can't find category by ids: "
                + requestDtoNonExistentCategoryIds.getCategoryIds();
        String actual = exception.getMessage();

        Assertions.assertEquals(expected, actual);
        Mockito.verifyNoMoreInteractions(bookMapper, categoryRepository);
    }

    @Test
    @DisplayName("Verify getAll() method works")
    void getAll_ValidBooks_Success() {
        Mockito.when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(book)));
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> expected = List.of(bookDto);
        List<BookDto> actual = bookService.getAll(pageable);

        Assertions.assertEquals(expected, actual);
        Mockito.verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify getBookById() method works")
    void getBookById_ValidBookId_Success() {
        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.getBookById(book.getId());

        Assertions.assertEquals(bookDto, actual);
        Mockito.verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify getBookById() method with non existent bookId should throw Exception")
    void getBookById_InvalidBookId_ShouldThrowException() {
        Long bookId = anyLong();
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getBookById(bookId)
        );

        String expected = "Couldn't find Book by id " + bookId;
        String actual = exception.getMessage();

        Assertions.assertEquals(expected, actual);
        Mockito.verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidBookId_Success() {
        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(categoryRepository.findById(existentCategory.getId()))
                .thenReturn(Optional.of(existentCategory));
        Mockito.when(bookRepository.save(updatedBook)).thenReturn(updatedBook);

        bookService.update(book.getId(), updatedRequestDto);

        Mockito.verify(bookRepository, Mockito.times(1)).save(updatedBook);
    }

    @Test
    @DisplayName("Verify update() method with non existent bookId should throw Exception")
    void update_InvalidBookId_ShouldThrowException() {
        Long bookId = anyLong();
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(bookId, updatedRequestDto)
        );

        String expected = "Couldn't find Book by id " + bookId;
        String actual = exception.getMessage();

        Assertions.assertEquals(expected, actual);
        Mockito.verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify deleteById() method works")
    void deleteById_ValidBookId_Success() {
        Mockito.doNothing().when(bookRepository).deleteById(book.getId());

        bookService.deleteById(book.getId());

        Mockito.verify(bookRepository, Mockito.times(1))
                .deleteById(book.getId());
    }

    @Test
    @DisplayName("Verify search() method works")
    void search_ValidBookSearchParameterDto_Success() {
        Specification<Book> titleSpecification = titleSpecificationProvider
                .getSpecification(searchParametersDto);
        Mockito.when(bookSpecificationBuilder.build(searchParametersDto))
                .thenReturn(titleSpecification);
        Mockito.when(bookRepository.findAll(titleSpecification)).thenReturn(List.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> expected = List.of(bookDto);
        List<BookDto> actual = bookService.search(searchParametersDto);

        Assertions.assertEquals(expected, actual);
        Mockito.verifyNoMoreInteractions(bookSpecificationBuilder, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify getBooksByCategoryId() method works")
    void getBooksByCategoryId_ValidCategoryId_Success() {
        Mockito.when(bookRepository.findAllByCategoriesId(existentCategory.getId()))
                .thenReturn(List.of(book));
        Mockito.when(bookMapper.toDtoWithoutCategories(book))
                .thenReturn(bookDtoWithoutCategoryIds);

        List<BookDtoWithoutCategoryIds> expected = List.of(bookDtoWithoutCategoryIds);
        List<BookDtoWithoutCategoryIds> actual = bookService
                .getBooksByCategoryId(existentCategory.getId());

        Assertions.assertEquals(expected, actual);
        Mockito.verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify getBooksByCategoryId() "
            + "with non existent category id should return empty list")
    void getBooksByCategoryId_InvalidCategoryId_ShouldReturnEmptyList() {
        Long categoryId = anyLong();
        Mockito.when(bookRepository.findAllByCategoriesId(categoryId))
                .thenReturn(Collections.emptyList());

        List<BookDtoWithoutCategoryIds> expected = Collections.emptyList();
        List<BookDtoWithoutCategoryIds> actual = bookService.getBooksByCategoryId(categoryId);

        Assertions.assertEquals(expected, actual);
        Mockito.verifyNoMoreInteractions(bookRepository);
    }
}
