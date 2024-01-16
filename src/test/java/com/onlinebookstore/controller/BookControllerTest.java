package com.onlinebookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.BookRequestDto;
import com.onlinebookstore.dto.book.BookSearchParametersDto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    protected static MockMvc mockMvc;
    private static BookRequestDto requestDto;
    private static BookDto bookDto;
    private static BookDto harryBookDto;
    private static BookRequestDto updateRequestDto;
    private static BookDto gatsbyBookDto;
    private static BookRequestDto invalidRequestDto;
    private static BookSearchParametersDto searchParametersDto;
    @Autowired
    private ObjectMapper mapper;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource,
                                 @Autowired WebApplicationContext applicationContext)
            throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-two-book-to-books-table.sql")
            );
        }

        requestDto = new BookRequestDto();
        requestDto.setTitle("Frankenstein");
        requestDto.setAuthor("Mary Shelley");
        requestDto.setIsbn("0-071-81436-0");
        requestDto.setPrice(BigDecimal.valueOf(19.99));

        bookDto = new BookDto();
        bookDto.setTitle(requestDto.getTitle());
        bookDto.setAuthor(requestDto.getAuthor());
        bookDto.setIsbn(requestDto.getIsbn());
        bookDto.setPrice(requestDto.getPrice());

        invalidRequestDto = new BookRequestDto();
        invalidRequestDto.setTitle(requestDto.getTitle());
        invalidRequestDto.setAuthor(requestDto.getAuthor());

        harryBookDto = new BookDto();
        harryBookDto.setId(1L);
        harryBookDto.setTitle("Harry Potter and the Philosopher's Stone");
        harryBookDto.setAuthor("J. K. Rowling");
        harryBookDto.setIsbn("0-061-96436-0");
        harryBookDto.setPrice(BigDecimal.valueOf(11));

        updateRequestDto = new BookRequestDto();
        updateRequestDto.setTitle(harryBookDto.getTitle());
        updateRequestDto.setAuthor(harryBookDto.getAuthor());
        updateRequestDto.setIsbn("0-0061-96436-1");
        updateRequestDto.setPrice(harryBookDto.getPrice());

        gatsbyBookDto = new BookDto();
        gatsbyBookDto.setId(2L);
        gatsbyBookDto.setTitle("The Great Gatsby");
        gatsbyBookDto.setAuthor("F. Scott Fitzgerald");
        gatsbyBookDto.setIsbn("0-061-95636-0");
        gatsbyBookDto.setPrice(BigDecimal.valueOf(16));

        searchParametersDto = new BookSearchParametersDto(null,
                new String[]{"J. K. Rowling"},
                null,
                null);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/remove-two-book-from-books-table.sql")
            );
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/delete-frankenstein-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Verify save() method works")
    void save_ValidRequestDto_Success() throws Exception {
        String jsonRequest = mapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();

        BookDto actual = mapper.readValue(result.getResponse().getContentAsString(), BookDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(bookDto, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify save() method returns bad request with invalid request dto")
    void save_InvalidRequestDto_ReturnsBadRequest() throws Exception {
        String jsonRequest = mapper.writeValueAsString(invalidRequestDto);

        MvcResult result = mockMvc.perform(
                        post("/api/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        int actual = result.getResponse().getStatus();
        int expected = HttpStatus.BAD_REQUEST.value();

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getAll() method works")
    void getAll_ValidBooks_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/books"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actual = Arrays.stream(mapper.readValue(result.getResponse()
                .getContentAsString(), BookDto[].class))
                .toList();
        List<BookDto> expected = List.of(harryBookDto, gatsbyBookDto);

        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getBookById() method works")
    void getBookById_ValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/books/1"))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = mapper.readValue(result.getResponse().getContentAsString(), BookDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(harryBookDto, actual);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getBookById() method returns not found with invalid id")
    void getBookById_InvalidId_ReturnsNonFound() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/books/1000"))
                .andExpect(status().isNotFound())
                .andReturn();

        int actual = result.getResponse().getStatus();
        int expected = HttpStatus.NOT_FOUND.value();

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify update() method works")
    @Sql(scripts = "classpath:database/books/update-harry-potter-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_ValidRequestDtoAndId_Success() throws Exception {
        String jsonRequest = mapper.writeValueAsString(updateRequestDto);

        MvcResult result = mockMvc.perform(
                        put("/api/books/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        int actual = result.getResponse().getStatus();
        int expected = HttpStatus.OK.value();

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify update() method returns not found with invalid id")
    void update_InvalidId_ReturnsNotFound() throws Exception {
        String jsonRequest = mapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        put("/api/books/1000")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        int actual = result.getResponse().getStatus();
        int expected = HttpStatus.NOT_FOUND.value();

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify delete() method works")
    @Sql(scripts = "classpath:database/books/add-don-quixote-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                        delete("/api/books/3"))
                .andExpect(status().isNoContent())
                .andReturn();

        int actual = result.getResponse().getStatus();
        int expected = HttpStatus.NO_CONTENT.value();

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify search() method works")
    void search_ValidBookSearchParametersDto_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/books/search")
                                .param("authors", searchParametersDto.authors()))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actual = Arrays.stream(mapper.readValue(result.getResponse()
                        .getContentAsString(), BookDto[].class))
                .toList();
        List<BookDto> expected = List.of(harryBookDto);

        Assertions.assertEquals(expected, actual);
    }
}
