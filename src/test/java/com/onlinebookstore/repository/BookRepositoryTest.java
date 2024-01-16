package com.onlinebookstore.repository;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.book.BookRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Verify findAllByCategoriesId() method "
            + "with valid category id should return book list")
    @Sql(scripts = {
            "classpath:database/books/add-two-book-to-books-table.sql",
            "classpath:database/categories/add-one-category-to-categories-table.sql",
            "classpath:database/books_categories/"
                    + "add-one-book-and-one-category-to-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books_categories/"
                    + "remove-one-book-and-one-category-from-books-categories-table.sql",
            "classpath:database/categories/remove-one-category-from-categories-table.sql",
            "classpath:database/books/remove-two-book-from-books-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoriesId_ValidCategoryId_ShouldReturnOneBook() {
        List<Book> actual = bookRepository.findAllByCategoriesId(1L);

        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals("Harry Potter and the Philosopher's Stone",
                actual.get(0).getTitle());
    }

    @Test
    @DisplayName("Verify findAllByCategoriesId() method "
            + "with invalid category id should return empty list")
    @Sql(scripts = {
            "classpath:database/books/add-two-book-to-books-table.sql",
            "classpath:database/categories/add-one-category-to-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-two-book-from-books-table.sql",
            "classpath:database/categories/remove-one-category-from-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoriesId_InvalidCategoryId_ShouldReturnEmptyList() {
        List<Book> actual = bookRepository.findAllByCategoriesId(1L);

        Assertions.assertEquals(0, actual.size());
    }
}
