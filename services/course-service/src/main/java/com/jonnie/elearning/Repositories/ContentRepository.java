package com.jonnie.elearning.Repositories;

import com.jonnie.elearning.course.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, String> {
}
