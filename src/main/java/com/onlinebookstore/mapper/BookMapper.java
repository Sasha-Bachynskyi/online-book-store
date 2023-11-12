package com.onlinebookstore.mapper;

import com.onlinebookstore.config.MapperConfig;
import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.BookRequestDto;
import com.onlinebookstore.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(BookRequestDto requestDto);
}
