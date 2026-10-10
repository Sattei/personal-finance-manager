package com.syfe.personalfinancemanager.config;

import com.syfe.personalfinancemanager.entity.Category;
import com.syfe.personalfinancemanager.repository.CategoryRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Ensures the predefined income and expense categories exist at application startup.
 */
@Component
public class CategoryInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public CategoryInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates any default categories that are not already stored.
     *
     * @param args application startup arguments
     */
    @Override
    public void run(String... args) {

        createDefault("Salary", "INCOME");

        createDefault("Food", "EXPENSE");
        createDefault("Rent", "EXPENSE");
        createDefault("Transportation", "EXPENSE");
        createDefault("Entertainment", "EXPENSE");
        createDefault("Healthcare", "EXPENSE");
        createDefault("Utilities", "EXPENSE");
    }

    private void createDefault(String name, String type) {

        if (!categoryRepository.existsByNameAndIsDefaultTrue(name)) {

            Category category =
                    new Category(name, type, true, null);

            categoryRepository.save(category);
        }
    }
}
