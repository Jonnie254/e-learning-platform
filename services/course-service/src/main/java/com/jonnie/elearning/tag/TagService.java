package com.jonnie.elearning.tag;

import com.jonnie.elearning.Repositories.TagRepository;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.exceptions.TagNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    // method to create a tag
    public String createTag(
            TagRequest tagRequest) {
        Tag tag = tagMapper.tagRequestToTag(tagRequest);
        tagRepository.save(tag);
        return tag.getTagId();
    }

    // method to find all tags
    public PageResponse<TagResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Tag> tags = tagRepository.findAll(pageable);
        List<TagResponse> tagResponses = tags.stream()
                .map(tagMapper::tagToTagResponse)
                .toList();
        return new PageResponse<>(
                tagResponses,
                tags.getNumber(),
                tags.getSize(),
                tags.getTotalElements(),
                tags.getTotalPages(),
                tags.isLast(),
                tags.isFirst()
        );
    }

    // method to find tag by id
    public TagResponse findById(String tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag not found with id: " + tagId));
        return tagMapper.tagToTagResponse(tag);
    }

    // method to update a tag
    public void updateTag(String tagId, @Valid TagRequest tagRequest) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag not found with id: " + tagId));
        tag.setTagName(tagRequest.tagName());
        tagRepository.save(tag);
    }
}
