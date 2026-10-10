package com.syfe.personalfinancemanager.service;

import com.syfe.personalfinancemanager.dto.category.CategoryResponse;
import com.syfe.personalfinancemanager.dto.category.CreateCategoryRequest;
import com.syfe.personalfinancemanager.entity.Category;
import com.syfe.personalfinancemanager.entity.User;
import com.syfe.personalfinancemanager.exception.BadRequestException;
import com.syfe.personalfinancemanager.exception.ConflictException;
import com.syfe.personalfinancemanager.exception.ForbiddenException;
import com.syfe.personalfinancemanager.exception.ResourceNotFoundException;
import com.syfe.personalfinancemanager.repository.CategoryRepository;
import com.syfe.personalfinancemanager.repository.TransactionRepository;
import com.syfe.personalfinancemanager.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategorySuccess() {

        User user = mock(User.class);

        CreateCategoryRequest request =
                new CreateCategoryRequest(
                        "Freelance",
                        "INCOME"
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.existsByNameAndUser(
                "Freelance",
                user
        )).thenReturn(false);

        when(categoryRepository.existsByNameAndIsDefaultTrue(
                "Freelance"
        )).thenReturn(false);

        Category savedCategory =
                new Category(
                        "Freelance",
                        "INCOME",
                        false,
                        user
                );

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);

        CategoryResponse response =
                categoryService.createCategory(
                        request,
                        "user@example.com"
                );

        assertEquals("Freelance", response.getName());
        assertEquals("INCOME", response.getType());
        assertTrue(response.getIsCustom());

        verify(categoryRepository)
                .save(any(Category.class));
    }

    @Test
    void createCategoryDuplicate() {

        User user = mock(User.class);

        CreateCategoryRequest request =
                new CreateCategoryRequest(
                        "Food",
                        "EXPENSE"
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.existsByNameAndUser(
                "Food",
                user
        )).thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> categoryService.createCategory(
                        request,
                        "user@example.com"
                )
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void getCategoriesSuccess() {

        User user = mock(User.class);

        Category salary =
                new Category(
                        "Salary",
                        "INCOME",
                        true,
                        null
                );

        Category food =
                new Category(
                        "Food",
                        "EXPENSE",
                        true,
                        null
                );

        Category freelance =
                new Category(
                        "Freelance",
                        "INCOME",
                        false,
                        user
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findByIsDefaultTrue())
                .thenReturn(List.of(salary, food));

        when(categoryRepository.findByUser(user))
                .thenReturn(List.of(freelance));

        List<CategoryResponse> result =
                categoryService.getCategories(
                        "user@example.com"
                );

        assertEquals(3, result.size());

        assertFalse(result.get(0).getIsCustom());
        assertFalse(result.get(1).getIsCustom());
        assertTrue(result.get(2).getIsCustom());
    }

    @Test
    void deleteCategorySuccess() {

        User user = mock(User.class);

        Category category =
                new Category(
                        "Freelance",
                        "INCOME",
                        false,
                        user
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.existsByNameAndIsDefaultTrue(
                "Freelance"
        )).thenReturn(false);

        when(categoryRepository.findByNameAndUser(
                "Freelance",
                user
        )).thenReturn(Optional.of(category));

        when(transactionRepository.existsByCategory(category))
                .thenReturn(false);

        categoryService.deleteCategory(
                "Freelance",
                "user@example.com"
        );

        verify(categoryRepository)
                .delete(category);
    }

    @Test
    void deleteDefaultCategoryForbidden() {

        User user = mock(User.class);

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.existsByNameAndIsDefaultTrue(
                "Salary"
        )).thenReturn(true);

        assertThrows(
                ForbiddenException.class,
                () -> categoryService.deleteCategory(
                        "Salary",
                        "user@example.com"
                )
        );
    }

    @Test
    void deleteCategoryUsedByTransaction() {

        User user = mock(User.class);

        Category category =
                new Category(
                        "Freelance",
                        "INCOME",
                        false,
                        user
                );

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.existsByNameAndIsDefaultTrue(
                "Freelance"
        )).thenReturn(false);

        when(categoryRepository.findByNameAndUser(
                "Freelance",
                user
        )).thenReturn(Optional.of(category));

        when(transactionRepository.existsByCategory(category))
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> categoryService.deleteCategory(
                        "Freelance",
                        "user@example.com"
                )
        );

        verify(categoryRepository, never())
                .delete(category);
    }

    @Test
    void deleteCategoryNotFound() {

        User user = mock(User.class);

        when(userRepository.findByUsername("user@example.com"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.existsByNameAndIsDefaultTrue(
                "Unknown"
        )).thenReturn(false);

        when(categoryRepository.findByNameAndUser(
                "Unknown",
                user
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(
                        "Unknown",
                        "user@example.com"
                )
        );
    }
}