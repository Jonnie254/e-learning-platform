package com.jonnie.elearning.tag;

import com.jonnie.elearning.Repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public String createTag(
            TagRequest tagRequest,
            String userId,
            String userRole) {
        Tag tag = tagMapper.tagRequestToTag(tagRequest);
        tagRepository.save(tag);
        return tag.getTagId();
    }
}
