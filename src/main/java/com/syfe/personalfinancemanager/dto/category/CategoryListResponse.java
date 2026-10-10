package com.syfe.personalfinancemanager.dto.category;

import java.util.List;

public class CategoryListResponse {

    private List<CategoryResponse> categories;

    public CategoryListResponse(List<CategoryResponse> categories) {
        this.categories = categories;
    }

    public List<CategoryResponse> getCategories() {
        return categories;
    }
}