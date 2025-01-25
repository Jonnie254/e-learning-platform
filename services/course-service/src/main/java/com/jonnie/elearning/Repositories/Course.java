package com.jonnie.elearning.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Course  extends JpaRepository<Course, String> {
}
