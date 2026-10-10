package com.syfe.personalfinancemanager.controller;

import com.syfe.personalfinancemanager.dto.category.CategoryListResponse;
import com.syfe.personalfinancemanager.dto.category.CategoryResponse;
import com.syfe.personalfinancemanager.dto.category.CreateCategoryRequest;
import com.syfe.personalfinancemanager.dto.common.MessageResponse;
import com.syfe.personalfinancemanager.service.CategoryService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void createCategorySuccess() {

        CreateCategoryRequest request =
                new CreateCategoryRequest(
                        "Freelance",
                        "INCOME"
                );

        CategoryResponse serviceResponse =
                new CategoryResponse(
                        "Freelance",
                        "INCOME",
                        true
                );

        when(authentication.getName())
                .thenReturn("user@example.com");

        when(categoryService.createCategory(
                request,
                "user@example.com"
        )).thenReturn(serviceResponse);

        ResponseEntity<CategoryResponse> response =
                categoryController.createCategory(
                        request,
                        authentication
                );

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());

        assertEquals(
                "Freelance",
                response.getBody().getName()
        );

        assertTrue(
                response.getBody().getIsCustom()
        );
    }

    @Test
    void getCategoriesSuccess() {

        when(authentication.getName())
                .thenReturn("user@example.com");

        List<CategoryResponse> categories =
                List.of(
                        new CategoryResponse(
                                "Salary",
                                "INCOME",
                                false
                        ),
                        new CategoryResponse(
                                "Freelance",
                                "INCOME",
                                true
                        )
                );

        when(categoryService.getCategories(
                "user@example.com"
        )).thenReturn(categories);

        ResponseEntity<CategoryListResponse> response =
                categoryController.getCategories(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        assertEquals(
                2,
                response.getBody()
                        .getCategories()
                        .size()
        );
    }

    @Test
    void deleteCategorySuccess() {

        when(authentication.getName())
                .thenReturn("user@example.com");

        ResponseEntity<MessageResponse> response =
                categoryController.deleteCategory(
                        "Freelance",
                        authentication
                );

        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                "Category deleted successfully",
                response.getBody().getMessage()
        );

        verify(categoryService)
                .deleteCategory(
                        "Freelance",
                        "user@example.com"
                );
    }
}