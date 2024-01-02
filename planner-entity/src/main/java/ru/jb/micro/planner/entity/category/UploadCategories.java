package ru.jb.micro.planner.entity.category;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UploadCategories {

    private final CategoriesMapper categoriesMapper;

    public UploadCategories(CategoriesMapper categoriesMapper) {
        this.categoriesMapper = categoriesMapper;
    }

    public void upload() {
        Arrays.stream(Category.values()).forEach(categoriesMapper::uploadCategories);
    }
}
