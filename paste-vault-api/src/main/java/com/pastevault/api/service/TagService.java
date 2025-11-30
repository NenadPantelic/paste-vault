package com.pastevault.api.service;

import java.util.List;

public interface TagService {

    void updateTagCounters(List<String> addedTags, List<String> removedTags);
}
