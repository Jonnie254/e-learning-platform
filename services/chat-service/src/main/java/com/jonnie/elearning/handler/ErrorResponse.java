package com.jonnie.elearning.handler;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {
}
