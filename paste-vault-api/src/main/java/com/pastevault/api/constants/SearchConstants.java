package com.pastevault.api.constants;

import org.springframework.data.domain.Sort;

public class SearchConstants {

    // almost identical to timestamp sort
    public static final Sort DEFAULT_DIR_SORT = Sort.by(Sort.Order.asc("id"));

}
