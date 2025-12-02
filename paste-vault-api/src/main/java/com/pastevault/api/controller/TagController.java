package com.pastevault.api.controller;

import com.pastevault.api.dto.request.NewTag;
import com.pastevault.api.service.TagService;
import com.pastevault.api.dto.response.TagDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDTO createTag(@Valid @RequestBody NewTag newTag) {
        log.info("Received a request to create new tag");
        return tagService.createTag(newTag);
    }

    @DeleteMapping("/{tag}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable("tag") String tag) {
        log.info("Received a request to create new tag");
        tagService.deleteTag(tag);
    }

    // TODO: think about updating tag (means that all vault nodes associated with that tag would have to be updated)
}
