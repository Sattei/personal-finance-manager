package com.syfe.personalfinancemanager.controller;

import com.syfe.personalfinancemanager.dto.category.CategoryListResponse;
import com.syfe.personalfinancemanager.dto.category.CategoryResponse;
import com.syfe.personalfinancemanager.dto.category.CreateCategoryRequest;
import com.syfe.personalfinancemanager.dto.common.MessageResponse;
import com.syfe.personalfinancemanager.service.CategoryService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes category operations for authenticated users.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Creates a custom category for the authenticated user.
     *
     * @param request validated category data
     * @param authentication current authenticated user
     * @return the created category
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            Authentication authentication) {

        CategoryResponse response =
                categoryService.createCategory(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(201)
                .body(response);
    }

    /**
     * Lists default categories and custom categories available to the authenticated user.
     *
     * @param authentication current authenticated user
     * @return the available categories
     */
    @GetMapping
    public ResponseEntity<CategoryListResponse> getCategories(
            Authentication authentication) {

        List<CategoryResponse> categories =
                categoryService.getCategories(
                        authentication.getName()
                );

        CategoryListResponse response =
                new CategoryListResponse(categories);

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a custom category owned by the authenticated user.
     *
     * @param name category name
     * @param authentication current authenticated user
     * @return a successful deletion response
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<MessageResponse> deleteCategory(
            @PathVariable String name,
            Authentication authentication) {

        categoryService.deleteCategory(
                name,
                authentication.getName()
        );

        return ResponseEntity.ok(
                new MessageResponse(
                        "Category deleted successfully"
                )
        );
    }
}
