package com.map.nguyennhatminh.demo2.model;

import com.map.nguyennhatminh.demo2.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryUtils {

    public static List<String> getFormattedCategories(List<Category> categories) {
        List<String> formattedCategories = new ArrayList<>();
        for (Category category : categories) {
            addCategoryWithIndentation(formattedCategories, category, 0);
        }
        return formattedCategories;
    }

    private static void addCategoryWithIndentation(List<String> formattedCategories, Category category, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("  "); // Add indentation
        }
        sb.append(category.getName());
        formattedCategories.add(sb.toString());

        if (category.getCategory() != null) {
            addCategoryWithIndentation(formattedCategories, category.getCategory(), level + 1);
        }
    }
}
