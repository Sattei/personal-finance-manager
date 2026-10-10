package com.syfe.personalfinancemanager.dto.category;

public class CategoryResponse {

    private String name;
    private String type;
    private boolean isCustom;

    public CategoryResponse(
            String name,
            String type,
            boolean isCustom) {

        this.name = name;
        this.type = type;
        this.isCustom = isCustom;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean getIsCustom() {
        return isCustom;
    }

    public boolean getCustom() {
        return isCustom;
    }
}