package com.jonnie.elearning.repositories;

import com.jonnie.elearning.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, String> {
}
