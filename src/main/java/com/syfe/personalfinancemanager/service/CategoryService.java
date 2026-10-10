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

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides business logic for default and custom categories.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository) {

        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Creates a custom category for the specified user.
     *
     * @param request category information
     * @param username category owner's username
     * @return created category
     * @throws ConflictException if the category name already exists
     */
    public CategoryResponse createCategory(
            CreateCategoryRequest request,
            String username) {

        User user = getUser(username);

        if (categoryRepository.existsByNameAndUser(request.getName(), user)
                || categoryRepository.existsByNameAndIsDefaultTrue(request.getName())) {

            throw new ConflictException(
                    "Category already exists"
            );
        }

        Category category = new Category(
                request.getName(),
                request.getType(),
                false,
                user
        );

        try {

            Category savedCategory =
                    categoryRepository.save(category);

            categoryRepository.flush();

            return new CategoryResponse(
                    savedCategory.getName(),
                    savedCategory.getType(),
                    true
            );

        } catch (DataIntegrityViolationException exception) {

            throw new ConflictException(
                    "Category already exists"
            );
        }
    }

    /**
     * Returns all default categories together with
     * the user's custom categories.
     *
     * @param username category owner's username
     * @return categories available to the user
     */
    public List<CategoryResponse> getCategories(
            String username) {

        User user = getUser(username);

        List<CategoryResponse> categories =
                new ArrayList<>();

        categoryRepository.findByIsDefaultTrue()
                .forEach(category ->
                        categories.add(
                                new CategoryResponse(
                                        category.getName(),
                                        category.getType(),
                                        false
                                )
                        )
                );

        categoryRepository.findByUser(user)
                .forEach(category ->
                        categories.add(
                                new CategoryResponse(
                                        category.getName(),
                                        category.getType(),
                                        true
                                )
                        )
                );

        return categories;
    }

    /**
     * Deletes an unused custom category belonging to the user.
     *
     * @param name category name
     * @param username category owner's username
     * @throws ForbiddenException if the category is a default category
     * @throws ResourceNotFoundException if the custom category does not exist
     * @throws BadRequestException if the category is currently used
     */
    public void deleteCategory(
            String name,
            String username) {

        User user = getUser(username);

        if (categoryRepository.existsByNameAndIsDefaultTrue(name)) {

            throw new ForbiddenException(
                    "Default categories cannot be deleted"
            );
        }

        Category category =
                categoryRepository
                        .findByNameAndUser(name, user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                )
                        );

        if (transactionRepository.existsByCategory(category)) {

            throw new BadRequestException(
                    "Category is currently used by a transaction"
            );
        }

        categoryRepository.delete(category);
    }

    private User getUser(String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }
}