package com.onlinebookstore.service;

import com.onlinebookstore.dto.category.CategoryDto;
import com.onlinebookstore.dto.category.CategoryRequestDto;
import com.onlinebookstore.exception.EntityNotFoundException;
import com.onlinebookstore.mapper.CategoryMapper;
import com.onlinebookstore.model.Category;
import com.onlinebookstore.repository.category.CategoryRepository;
import com.onlinebookstore.service.category.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
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

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    private static final Pageable pageable = PageRequest.of(0, 10);
    private static final CategoryRequestDto requestDto = new CategoryRequestDto();
    private static final Category category = new Category();
    private static CategoryDto categoryDto;
    private static final CategoryRequestDto updatedRequestDto = new CategoryRequestDto();
    private static final Category updatedCategory = new Category();


    @BeforeAll
    public static void beforeAll() {
        requestDto.setName("Fantasy");

        category.setId(1L);
        category.setName(requestDto.getName());

        categoryDto = new CategoryDto(category.getId(), category.getName(), null);

        updatedRequestDto.setName("Novel");

        updatedCategory.setId(category.getId());
        updatedCategory.setName(updatedRequestDto.getName());
    }

    @Test
    @DisplayName("Verify getAll() method works")
    void getAll_ValidCategories_ShouldReturnCategoryDtoList() {
        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(category)));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        List<CategoryDto> expected = List.of(categoryDto);
        List<CategoryDto> actual = categoryService.getAll(pageable);

        Assertions.assertEquals(expected, actual);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify getById() method works")
    void getById_ValidCategoryId_ShouldReturnCategoryDto() {
        Mockito.when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.getById(category.getId());

        Assertions.assertEquals(categoryDto, actual);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify getById() method with non existent categoryId should throw Exception")
    void getById_InvalidCategoryId_ShouldThrowException() {
        Long categoryId = anyLong();
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(categoryId)
        );

        String expected = "Couldn't find Category by id " + categoryId;
        String actual = exception.getMessage();

        Assertions.assertEquals(expected, actual);
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidCategoryRequestDto_ReturnsCategoryDto() {
        Mockito.when(categoryMapper.toModel(requestDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.save(requestDto);

        Assertions.assertEquals(categoryDto, actual);
        Mockito.verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidCategoryId_Success() {
        Mockito.when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);

        categoryService.update(category.getId(), updatedRequestDto);

        Mockito.verify(categoryRepository, Mockito.times(1)).save(updatedCategory);
    }

    @Test
    @DisplayName("Verify update() method with non existent categoryId should throw Exception")
    void update_InvalidCategoryId_ShouldThrowException() {
        Long categoryId = anyLong();
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(categoryId, updatedRequestDto)
        );

        String expected = "Couldn't find Category by id " + categoryId;
        String actual = exception.getMessage();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify deleteById() method works")
    void deleteById_ValidCategoryId_Success() {
        Mockito.doNothing().when(categoryRepository).deleteById(category.getId());

        categoryService.deleteById(category.getId());

        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(category.getId());
    }
}
