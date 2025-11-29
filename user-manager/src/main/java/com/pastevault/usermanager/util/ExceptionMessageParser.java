package com.pastevault.usermanager.util;

import jakarta.validation.ConstraintViolationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

public class ExceptionMessageParser {

    private static final String DATA_INTEGRITY_ERR_MESSAGE_SEPARATOR_WORD = "Detail:";

    public static String getDataIntegrityViolationExceptionMessage(DataIntegrityViolationException e) {
        String rootCauseMessage = NestedExceptionUtils.getMostSpecificCause(e).getMessage();

        int errMessageStartIndex = rootCauseMessage.indexOf(DATA_INTEGRITY_ERR_MESSAGE_SEPARATOR_WORD);
        if (errMessageStartIndex == -1) {
            return "Invalid data, data integrity violated";
        }

        String errorMessage = rootCauseMessage.substring(
                errMessageStartIndex + DATA_INTEGRITY_ERR_MESSAGE_SEPARATOR_WORD.length()
        );
        return errorMessage.replace("Key", "")
                .replace("(", "")
                .replace(")", "")
                .trim();
    }

    public static List<String> getConstraintViolationMessages(ConstraintViolationException e) {
        return e.getConstraintViolations().stream()
                .map(violation -> String.format(
                        "Received an invalid value '%s': %s",
                        violation.getInvalidValue(),
                        violation.getMessage()
                ))
                .toList();
    }
}