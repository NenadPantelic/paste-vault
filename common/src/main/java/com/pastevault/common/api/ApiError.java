package com.pastevault.common.api;

import java.util.List;

public record ApiError(String message, int status, int internalCode, List<String> errors) {
}
