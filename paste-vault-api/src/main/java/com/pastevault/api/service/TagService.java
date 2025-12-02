package com.pastevault.api.service;

import com.pastevault.api.dto.request.NewTag;
import com.pastevault.api.dto.response.TagDTO;

import java.util.List;

public interface TagService {

    TagDTO createTag(NewTag newTag);

    void updateTagCounters(List<String> addedTags, List<String> removedTags);

    void deleteTag(String tag);
}
