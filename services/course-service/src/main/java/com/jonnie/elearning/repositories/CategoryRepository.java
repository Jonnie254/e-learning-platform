package com.jonnie.elearning.repositories;

import com.jonnie.elearning.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {

}
