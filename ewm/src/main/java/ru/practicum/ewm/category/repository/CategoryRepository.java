package ru.practicum.ewm.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.category.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsCategoryByName(String name);

    Boolean existsCategoryByNameAndIdNot(String name, Long categoryId);
}
