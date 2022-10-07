package com.tyfff.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListIntegerValueConstraintValidator implements ConstraintValidator<ListIntegerValue,Integer> {
    private final Set<Integer> set = new HashSet<>();

    @Override
    public void initialize(ListIntegerValue constraintAnnotation) {
        for (int val : constraintAnnotation.vals()) {
            set.add(val);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
