package com.onlinebookstore;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {
    private final BookService bookService;

    @Autowired
    public OnlineBookStoreApplication(BookService bookService) {
        this.bookService = bookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Harry Potter and the Philosopher's Stone");
            book.setAuthor("Joanne Rowling");
            book.setIsbn("0-19-853453");
            book.setPrice(BigDecimal.valueOf(99));
            bookService.save(book);
            System.out.println(bookService.getAll());
        };
    }
}
