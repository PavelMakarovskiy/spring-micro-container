package ru.jb.micro.planner.entity.category;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final UploadCategories uploadCategories;

    public CategoryController(UploadCategories uploadCategories) {
        this.uploadCategories = uploadCategories;
    }

    @PostMapping("/upload")
    public void runUploadCategories() {
        uploadCategories.upload();
    }
}
