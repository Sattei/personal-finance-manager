package com.syfe.personalfinancemanager.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(
            max = 255,
            message = "Category name must not exceed 255 characters"
    )
    private String name;

    @NotBlank(message = "Category type is required")
    @Pattern(
            regexp = "INCOME|EXPENSE",
            message = "Category type must be INCOME or EXPENSE"
    )
    private String type;

    public CreateCategoryRequest() {
    }

    public CreateCategoryRequest(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}