package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.persistence.OwnerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class OwnerValidator {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OwnerDao dao;

    public OwnerValidator(OwnerDao dao) {
        this.dao = dao;
    }

    /**
     * Validates an owner before creation, ensuring all fields meet constraints.
     *
     * @param owner the {@link OwnerCreateDto} to validate
     * @throws ValidationException if validation fails
     */
    public void validateForCreate(OwnerCreateDto owner) throws ValidationException {
        LOG.trace("validateForCreate({})", owner);
        List<String> validationErrors = new ArrayList<>();
        String namePattern = "^[A-Z][a-zA-Z-]*$";

        if (owner.firstName() == null || owner.firstName().isBlank()) {
            validationErrors.add("Owner first name is required");
        } else {

            if (!owner.firstName().matches(namePattern)) {
                validationErrors.add("Owner first name must start with a capital letter and contain only letters and suitable characters");
            }
            if (owner.firstName().length() > 255) {
                validationErrors.add("Owner first name too long: must be 255 characters or less");
            }
        }

        if (owner.lastName() == null || owner.lastName().isBlank()) {
            validationErrors.add("Owner last name is required");
        } else {
            if (!owner.lastName().matches(namePattern)) {
                validationErrors.add("Owner last name must start with a capital letter and contain only letters and suitable characters");
            }
            if (owner.lastName().length() > 255) {
                validationErrors.add("Owner last name too long: must be 255 characters or less");
            }
        }

        if (owner.description() != null) {
            if (owner.description().length() > 4095) {
                validationErrors.add("Owner description too long: must be 4095 characters or less");
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of owner for creation failed", validationErrors);
        }
    }
}
