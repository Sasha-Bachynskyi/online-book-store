package com.onlinebookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.onlinebookstore.dto.category.CategoryDto;
import com.onlinebookstore.dto.category.CategoryRequestDto;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private static CategoryRequestDto requestDto;
    private static CategoryRequestDto invalidRequestDto;
    private static CategoryRequestDto updateRequestDto;
    private static CategoryDto horrorDto;
    private static CategoryDto fantasyDto;
    private static BookDtoWithoutCategoryIds bookDto;

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
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/add-two-book-to-books-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/add-one-category-to-categories-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books_categories/add-one-book-and-one-category-to-books-categories-table.sql"));
        }

        requestDto = new CategoryRequestDto();
        requestDto.setName("Horror");
        requestDto.setDescription("Horror");

        fantasyDto = new CategoryDto(1L, "Fantasy", null);

        horrorDto = new CategoryDto(null, "Horror", "Horror");

        invalidRequestDto = new CategoryRequestDto();

        updateRequestDto = new CategoryRequestDto();
        updateRequestDto.setName("Romance");

        bookDto = new BookDtoWithoutCategoryIds();
        bookDto.setId(1L);
        bookDto.setTitle("Harry Potter and the Philosopher's Stone");
        bookDto.setAuthor("J. K. Rowling");
        bookDto.setIsbn("0-061-96436-0");
        bookDto.setPrice(BigDecimal.valueOf(11));
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books_categories/remove-one-book-and-one-category-from-books-categories-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/remove-two-book-from-books-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/remove-one-category-from-categories-table.sql"));
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/categories/delete-horror-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Verify save() method works")
    void save_ValidRequestDto_Success() throws Exception {
        String jsonRequest = mapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/api/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = mapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(horrorDto, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify save() method returns bad request with invalid request dto")
    void save_InvalidRequestDto_ReturnsBadRequest() throws Exception {
        String jsonRequest = mapper.writeValueAsString(invalidRequestDto);

        MvcResult result = mockMvc.perform(
                        post("/api/categories")
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
    void getAll_ValidCategories_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> actual = Arrays.stream(mapper.readValue(result.getResponse()
                        .getContentAsString(), CategoryDto[].class))
                .toList();
        List<CategoryDto> expected = List.of(fantasyDto);

        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getCategoryById() method works")
    void getCategoryById_ValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/categories/1"))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = mapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        Assertions.assertEquals(fantasyDto, actual);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getCategoryById() method returns not found with invalid id")
    void getCategoryById_InvalidId_ReturnsNonFound() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/categories/1000"))
                .andExpect(status().isNotFound())
                .andReturn();

        int actual = result.getResponse().getStatus();
        int expected = HttpStatus.NOT_FOUND.value();

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify update() method works")
    @Sql(scripts = "classpath:database/categories/update-from-romance-to-fantasy-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_ValidRequestDtoAndId_Success() throws Exception {
        String jsonRequest = mapper.writeValueAsString(updateRequestDto);

        MvcResult result = mockMvc.perform(
                        put("/api/categories/1")
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
        String jsonRequest = mapper.writeValueAsString(updateRequestDto);

        MvcResult result = mockMvc.perform(
                        put("/api/categories/1000")
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
    @Sql(scripts = "classpath:database/categories/add-detective-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                        delete("/api/categories/2"))
                .andExpect(status().isNoContent())
                .andReturn();

        int actual = result.getResponse().getStatus();
        int expected = HttpStatus.NO_CONTENT.value();

        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getBooksByCategoryId() method works")
    void getBooksByCategoryId_ValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/api/categories/1/books"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> actual = Arrays.stream(mapper.readValue(result.getResponse()
                        .getContentAsString(), BookDtoWithoutCategoryIds[].class))
                .toList();
        List<BookDtoWithoutCategoryIds> expected = List.of(bookDto);

        Assertions.assertEquals(expected, actual);
    }
}
