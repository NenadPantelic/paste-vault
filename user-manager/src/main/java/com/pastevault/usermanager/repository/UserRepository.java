package com.pastevault.usermanager.repository;

import com.pastevault.common.exception.ApiException;
import com.pastevault.common.exception.ErrorReport;
import com.pastevault.usermanager.model.User;
import com.pastevault.usermanager.util.ExceptionMessageParser;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    default User findOrNotFound(int userId) {
        return this.findById(userId).orElseThrow(() -> new ApiException(ErrorReport.NOT_FOUND));
    }

    default User doSave(User user) {
        try {
            return this.save(user);
        } catch (DataIntegrityViolationException e) {
            String errMessage = ExceptionMessageParser.getDataIntegrityViolationExceptionMessage(e);
            throw new ApiException(ErrorReport.CONFLICT.withErrors(errMessage));
        } catch (ConstraintViolationException e) {
            List<String> errors = ExceptionMessageParser.getConstraintViolationMessages(e);
            throw new ApiException(ErrorReport.CONFLICT.withErrors(errors));
        }
    }
}
