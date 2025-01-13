package com.jonnie.elearning.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvalidRoleRequestException extends RuntimeException {
    public InvalidRoleRequestException(String message) {
        super(message);
    }
}
