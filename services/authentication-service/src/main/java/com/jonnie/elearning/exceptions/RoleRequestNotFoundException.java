package com.jonnie.elearning.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleRequestNotFoundException extends RuntimeException {
    public RoleRequestNotFoundException(String message) {
        super(message);
    }
}
