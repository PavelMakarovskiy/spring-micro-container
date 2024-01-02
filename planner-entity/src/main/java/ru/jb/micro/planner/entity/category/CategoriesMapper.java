package ru.jb.micro.planner.entity.category;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoriesMapper {
    @Insert("insert into categories(category) values (#{category}) on conflict (category) do nothing")
    void uploadCategories(Category category);

}
