package com.pastevault.common.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record ErrorReport(String message, int status, int internalCode, List<String> errors) {

    public static ErrorReport of(String message, int status, int internalCode) {
        return new ErrorReport(message, status, internalCode, new ArrayList<>());
    }

    // Bad request - 1000
    public static final ErrorReport BAD_REQUEST = ErrorReport.of("Bad request.", 400, 1000);
    // Unauthorized - 2000
    public static final ErrorReport UNAUTHORIZED = ErrorReport.of("Unauthorized.", 401, 2000);
    // Forbidden - 3000
    public static final ErrorReport FORBIDDEN = ErrorReport.of("Forbidden.", 403, 3000);
    // Not found - 4000
    public static final ErrorReport NOT_FOUND = ErrorReport.of("Not found.", 404, 4000);
    // Conflict - 5000
    public static final ErrorReport CONFLICT = ErrorReport.of("Conflict.", 409, 5000);
    // Unprocessable entity - 6000
    public static final ErrorReport UNPROCESSABLE_ENTITY = ErrorReport.of("Unprocessable entity.", 422, 6000);
    // Internal server error - 6000
    public static final ErrorReport INTERNAL_SERVER_ERROR = ErrorReport.of("Internal server error.", 500, 7000);
    // Service unavailable - 7000
    public static final ErrorReport SERVICE_UNAVAILABLE = ErrorReport.of("Service unavailable.", 503, 8000);

    public ErrorReport withErrors(String error, String... errors) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(error);
        errorMessages.addAll(Arrays.asList(errors));
        return new ErrorReport(this.message, this.status, this.internalCode, errorMessages);
    }

    public ErrorReport withErrors(List<String> errors) {
        return new ErrorReport(this.message, this.status, this.internalCode, errors);
    }

    public ErrorReport withInternalCode(int internalCode) {
        return new ErrorReport(this.message, this.status, internalCode, this.errors);
    }
}