package com.jonnie.gatewayms.handler;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {
}
