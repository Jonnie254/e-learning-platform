package com.jonnie.elearning.course.services;

import com.jonnie.elearning.Repositories.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final CourseMapper courseMapper;
}
