package com.quickweather.validator;

import java.util.List;

public class ValidatorChainBuilder {

    public static Validator buildChain(List<Validator> validators) {
        if (validators == null || validators.isEmpty()) {
            throw new IllegalArgumentException("Validator can not be null or empty");
        }

        for (int i = 0; i < validators.size() - 1; i++) {
            validators.get(i).setNext(validators.get(i + 1));
        }

        return validators.get(0);
    }
}
