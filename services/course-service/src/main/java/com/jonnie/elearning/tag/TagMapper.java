package com.jonnie.elearning.tag;

import org.springframework.stereotype.Service;

@Service
public class TagMapper {
    public Tag tagRequestToTag(TagRequest tagRequest) {
        return Tag.builder()
                .tagName(tagRequest.tagName())
                .build();
    }
}
